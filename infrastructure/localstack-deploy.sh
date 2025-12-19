#!/bin/bash
set -e # Stops the script if any command fails

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
export AWS_REGION=us-east-1

ENDPOINT_URL="http://localhost:4566"

cdk synth --output ./cdk.out

aws --endpoint-url=$ENDPOINT_URL cloudformation delete-stack \
    --stack-name AutoInsurance 2>/dev/null || echo "Stack didn't existed or was deleted"

if aws --endpoint-url=$ENDPOINT_URL cloudformation describe-stacks \
    --stack-name AutoInsurance 2>/dev/null; then
    echo "Waiting for delete"
    aws --endpoint-url=$ENDPOINT_URL cloudformation wait stack-delete-complete \
        --stack-name AutoInsurance
fi

echo "Creating new stack"
aws --endpoint-url=$ENDPOINT_URL cloudformation create-stack \
    --stack-name AutoInsurance \
    --template-body file://./cdk.out/localstack.template.json \
    --capabilities CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND

aws --endpoint-url=$ENDPOINT_URL cloudformation wait stack-create-complete \
    --stack-name AutoInsurance

echo "Successfully created"
echo ""
echo "Information"
echo "======================"

aws --endpoint-url=$ENDPOINT_URL cloudformation describe-stacks \
    --stack-name AutoInsurance \
    --query 'Stacks[0].[StackName, StackStatus, CreationTime]' \
    --output table


echo "================="
aws --endpoint-url=$ENDPOINT_URL cloudformation describe-stack-resources \
    --stack-name AutoInsurance \
    --query 'StackResources[*].[LogicalResourceId, ResourceType, ResourceStatus]' \
    --output table


echo "==============="

CLUSTER_NAME="InsuranceManagementCluster"
aws --endpoint-url=$ENDPOINT_URL ecs describe-clusters \
    --clusters $CLUSTER_NAME \
    --query 'clusters[0].[clusterName, status, registeredContainerInstancesCount]' \
    --output table 2>/dev/null || echo "ECS classter not created yet"

echo "==================="

aws --endpoint-url=$ENDPOINT_URL rds describe-db-instances \
    --query 'DBInstances[*].[DBInstanceIdentifier, DBInstanceStatus, Endpoint.Address, Endpoint.Port]' \
    --output table 2>/dev/null || echo "Db not created yet"

echo "================="
aws --endpoint-url=$ENDPOINT_URL ec2 describe-vpcs \
    --filters "Name=tag:Name,Values=InsuranceManagementVPC" \
    --query 'Vpcs[0].[VpcId, CidrBlock, State]' \
    --output table 2>/dev/null || echo "VPC not created yet"

echo ""
echo "🔗 Endpoints:"
echo "======================"
echo "API Gateway: http://localhost:4004"
echo "Auth Service: http://localhost:4005"
echo "Insurance Service: http://localhost:4000"
echo ""

aws --endpoint-url=$ENDPOINT_URL elbv2 describe-load-balancers \
    --query "LoadBalancers[0].[DNSName, Scheme, Type]" \
    --output table 2>/dev/null || echo "Load Balancer not created"

echo ""
echo "Deployed successfully"
echo "- API Gateway: http://localhost:4004"
echo "- Auth Service: http://localhost:4005"
echo "- Insurance Service: http://localhost:4000"
echo ""
echo "for API Gateway:"
echo "curl http://localhost:4004/health"
echo ""
echo "To login:"
echo "curl -X POST http://localhost:4004/auth/login \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"email\":\"testuser@test.com\",\"password\":\"password123\"}'"