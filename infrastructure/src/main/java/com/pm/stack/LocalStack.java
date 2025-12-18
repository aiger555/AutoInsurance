package com.pm.stack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ecr.assets.DockerImageAsset;
import software.amazon.awscdk.services.ecr.assets.DockerImageAssetProps;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;

    private DockerImageAsset authServiceImage;
    private DockerImageAsset insuranceServiceImage;
    private DockerImageAsset apiGatewayImage;

    public LocalStack(final App scope, final String id, final StackProps props){
        super(scope, id, props);

        this.authServiceImage = new DockerImageAsset(this, "AuthServiceImage",
                DockerImageAssetProps.builder()
                        .directory("./auth-service")
                        .build());

        this.insuranceServiceImage = new DockerImageAsset(this, "InsuranceServiceImage",
                DockerImageAssetProps.builder()
                        .directory("./insurance-service")
                        .build());

        this.apiGatewayImage = new DockerImageAsset(this, "ApiGatewayImage",
                DockerImageAssetProps.builder()
                        .directory("./api-gateway")
                        .build());

        this.vpc = createVpc();

        DatabaseInstance authServiceDb =
                createDatabase("AuthServiceDB", "auth-service-db");

        DatabaseInstance insuranceServiceDb =
                createDatabase("InsuranceServiceDB", "insurance-service-db");

        CfnHealthCheck authDbHealthCheck =
                createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");

        CfnHealthCheck insuranceDbHealthCheck =
                createDbHealthCheck(insuranceServiceDb, "InsuranceServiceDBHealthCheck");

//        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();

        FargateService authService =
                createFargateService("AuthService",
                        "auth-service",
                        List.of(4005),
                        authServiceDb,
                        Map.of("JWT_SECRET", "c3VwZXJTZWNyZXRLZXlGb3JKV1RDb25maWd1cmF0aW9uMTIz="));

        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);

//        FargateService billingService =
//                createFargateService("BillingService",
//                        "billing-service",
//                        List.of(4001,9001),
//                        null,
//                        null);

//        FargateService analyticsService =
//                createFargateService("AnalyticsService",
//                        "analytics-service",
//                        List.of(4002),
//                        null,
//                        null);

//        analyticsService.getNode().addDependency(mskCluster);

        FargateService insuranceService = createFargateService(
                "InsuranceService",
                "insurance-service",
                List.of(4000),
                insuranceServiceDb,
                null  // Нет дополнительных env vars для insurance-service
        );
        insuranceService.getNode().addDependency(insuranceServiceDb);
        insuranceService.getNode().addDependency(insuranceDbHealthCheck);

        FargateService apiGatewayService = createFargateService(
                "ApiGatewayService",
                "api-gateway",
                List.of(4004),
                null,  // Нет базы данных
                Map.of(
                        "SPRING_PROFILES_ACTIVE", "prod",
                        "AUTH_SERVICE_URL", "http://auth-service:4005",
                        "INSURANCE_SERVICE_URL", "http://insurance-service:4000"
                )
        );

        apiGatewayService.getNode().addDependency(authService);
        apiGatewayService.getNode().addDependency(insuranceService);

    }

    private Vpc createVpc(){
        return Vpc.Builder
                .create(this, "InsuranceManagementVPC")
                .vpcName("InsuranceManagementVPC")
                .maxAzs(2)
                .build();
    }

    private DatabaseInstance createDatabase(String id, String dbName){
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id){
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

//    private CfnCluster createMskCluster(){
//        return CfnCluster.Builder.create(this, "MskCluster")
//                .clusterName("kafa-cluster")
//                .kafkaVersion("2.8.0")
//                .numberOfBrokerNodes(1)
//                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
//                        .instanceType("kafka.m5.xlarge")
//                        .clientSubnets(vpc.getPrivateSubnets().stream()
//                                .map(ISubnet::getSubnetId)
//                                .collect(Collectors.toList()))
//                        .brokerAzDistribution("DEFAULT")
//                        .build())
//                .build();
//    }

    private Cluster createEcsCluster(){
        return Cluster.Builder.create(this, "InsuranceManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("AutoInsurance.local")
                        .build())
                .build();
    }

    private FargateService createFargateService(String id,
                                                String imageName,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> additionalEnvVars) {

        FargateTaskDefinition taskDefinition =
                FargateTaskDefinition.Builder.create(this, id + "Task")
                        .cpu(256)
                        .memoryLimitMiB(512)
                        .build();

        ContainerImage containerImage;
        if (imageName.equals("auth-service")) {
            containerImage = ContainerImage.fromDockerImageAsset(authServiceImage);
        } else if (imageName.equals("insurance-service")) {
            containerImage = ContainerImage.fromDockerImageAsset(insuranceServiceImage);
        } else if (imageName.equals("api-gateway")) {
            containerImage = ContainerImage.fromDockerImageAsset(apiGatewayImage);
        } else {
            containerImage = ContainerImage.fromRegistry(imageName); // для других сервисов
        }

        ContainerDefinitionOptions.Builder containerOptions =
                ContainerDefinitionOptions.builder()
                        .image(containerImage)  // ✅ ИСПОЛЬЗУЙ ЛОКАЛЬНЫЙ ASSET
                        .portMappings(ports.stream()
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList())
                        .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                        .logGroupName("/ecs/" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix(imageName)
                                .build()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        if(additionalEnvVars != null){
            envVars.putAll(additionalEnvVars);
        }

        if(db != null){
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD",
                    db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }

        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build());

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    private void createApiGatewayService() {
        FargateTaskDefinition taskDefinition =
                FargateTaskDefinition.Builder.create(this, "APIGatewayTaskDefinition")
                        .cpu(256)
                        .memoryLimitMiB(512)
                        .build();

        ContainerDefinitionOptions containerOptions =
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromDockerImageAsset(apiGatewayImage))
                        .environment(Map.of(
                                "SPRING_PROFILES_ACTIVE", "prod",
                                "AUTH_SERVICE_URL", "http://auth-service:4005",
                                "INSURANCE_SERVICE_URL", "http://insurance-service:4000"
                        ))
                        .portMappings(List.of(PortMapping.builder()
                .containerPort(4004)
                .hostPort(4004)
                .protocol(Protocol.TCP)
                .build()))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                .logGroupName("/ecs/api-gateway")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .streamPrefix("api-gateway")
                        .build()))
                .build();


        taskDefinition.addContainer("APIGatewayContainer", containerOptions);

        ApplicationLoadBalancedFargateService apiGateway =
                ApplicationLoadBalancedFargateService.Builder.create(this, "APIGatewayService")
                        .cluster(ecsCluster)
                        .serviceName("api-gateway")
                        .taskDefinition(taskDefinition)
                        .desiredCount(1)
                        .healthCheckGracePeriod(Duration.seconds(60))
                        .build();
    }

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }
}
