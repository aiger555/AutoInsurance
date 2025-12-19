package com.pm.stack;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
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
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        this.vpc = Vpc.Builder
                .create(this, "InsuranceManagementVPC")
                .vpcName("InsuranceManagementVPC")
                .maxAzs(2)
                .build();

        DatabaseInstance authServiceDb = createDatabase("AuthServiceDB", "auth-service-db");
        DatabaseInstance insuranceServiceDb = createDatabase("InsuranceServiceDB", "insurance-service-db");

        CfnHealthCheck authDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");
        CfnHealthCheck insuranceDbHealthCheck = createDbHealthCheck(insuranceServiceDb, "InsuranceServiceDBHealthCheck");

        this.ecsCluster = Cluster.Builder.create(this, "InsuranceManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("AutoInsurance.local")
                        .build())
                .build();

        ContainerImage authServiceImage = ContainerImage.fromAsset(
                Paths.get("..", "auth-service").toAbsolutePath().toString()
        );

        ContainerImage insuranceServiceImage = ContainerImage.fromAsset(
                Paths.get("..", "insurance-service").toAbsolutePath().toString()
        );

        ContainerImage apiGatewayImage = ContainerImage.fromAsset(
                Paths.get("..", "api-gateway").toAbsolutePath().toString()
        );

        FargateService authService = createFargateService(
                "AuthService",
                "auth-service",
                authServiceImage,
                List.of(4005),
                authServiceDb,
                Map.of(
                        "JWT_SECRET", "c3VwZXJTZWNyZXRLZXlGb3JKV1RDb25maWd1cmF0aW9uMTIz=",
                        "SERVER_PORT", "4005",
                        "SPRING_APPLICATION_NAME", "auth-service"
                )
        );

        FargateService insuranceService = createFargateService(
                "InsuranceService",
                "insurance-service",
                insuranceServiceImage,
                List.of(4000),
                insuranceServiceDb,
                Map.of(
                        "SERVER_PORT", "4000",
                        "SPRING_APPLICATION_NAME", "insurance-service"
                )
        );

        FargateService apiGatewayService = createFargateService(
                "ApiGatewayService",
                "api-gateway",
                apiGatewayImage,
                List.of(4004),
                null,
                Map.of(
                        "SERVER_PORT", "4004",
                        "SPRING_APPLICATION_NAME", "api-gateway",
                        "SPRING_PROFILES_ACTIVE", "prod"
                )
        );

        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);

        insuranceService.getNode().addDependency(insuranceDbHealthCheck);
        insuranceService.getNode().addDependency(insuranceServiceDb);

        apiGatewayService.getNode().addDependency(authService);
        apiGatewayService.getNode().addDependency(insuranceService);
    }

    private DatabaseInstance createDatabase(String id, String dbName) {
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

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
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

    private FargateService createFargateService(String id,
                                                String serviceName,
                                                ContainerImage image,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> additionalEnvVars) {

        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(image)
                .containerName(serviceName)
                .portMappings(ports.stream()
                        .map(port -> PortMapping.builder()
                                .containerPort(port)
                                .protocol(Protocol.TCP)
                                .build())
                        .toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                .logGroupName("/ecs/" + serviceName)
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .streamPrefix(serviceName)
                        .build()));

        Map<String, String> envVars = new HashMap<>();

        envVars.put("SPRING_PROFILES_ACTIVE", "prod");

        if (db != null) {
            envVars.put("SPRING_DATASOURCE_URL", String.format("jdbc:postgresql://%s:%s/%s",
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    serviceName.equals("auth-service") ? "auth-service-db" : "insurance-service-db"));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
        }

        if (additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars);
        }

        if (serviceName.equals("api-gateway")) {
            envVars.put("AUTH_SERVICE_URL", "http://auth-service:4005");
            envVars.put("INSURANCE_SERVICE_URL", "http://insurance-service:4000");
        }

        containerOptions.environment(envVars);

        taskDefinition.addContainer(serviceName + "Container", containerOptions.build());

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(true)
                .serviceName(serviceName)
                .desiredCount(1)
                .build();
    }

    public static void main(final String[] args) {
        App app = new App(AppProps.builder()
                .outdir("./cdk.out")
                .build());

        StackProps props = StackProps.builder()
//                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("CDK synthesis completed!");
    }
}