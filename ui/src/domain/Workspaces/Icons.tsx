const AWSIcons = [
    // AWS Compute
    { type: "aws_ec2_", path: "/providers/aws/Arch_Compute/32/Arch_Amazon-EC2_32.svg"},
    { type: "aws_instance", path: "/providers/aws/Arch_Compute/32/Arch_Amazon-EC2_32.svg"},
    { type: "aws_autoscaling_", path:"/providers/aws/Arch_Compute/32/Arch_Amazon-EC2-Auto-Scaling_32.svg"},
    { type: "aws_imagebuilder_", path:"/providers/aws/Arch_Compute/32/Arch_Amazon-EC2-Image-Builder_32.svg"},
    { type: "aws_lightsail_", path:"/providers/aws/Arch_Compute/32/Arch_Amazon-Lightsail_32.svg"},
    { type: "aws_apprunner_", path:"/providers/aws/Arch_Compute/32/Arch_Amazon-App-Runner_32.svg"},
    { type: "aws_batch_", path:"/providers/aws/Arch_Compute/32/Arch_AWS-Batch_32.svg"},
    { type: "aws_elastic_beanstalk_", path:"/providers/aws/Arch_Compute/32/Arch_AWS-Elastic-Beanstalk_32.svg"},
    { type: "aws_lambda_", path:"/providers/aws/Arch_Compute/32/Arch_AWS-Lambda_32.svg"},
    { type: "aws_outposts_", path:"/providers/aws/Arch_Compute/32/Arch_AWS-Outposts_32.svg"},
    { type: "aws_serverlessapplicationrepository", path:"/providers/aws/Arch_Compute/32/Arch_AWS-Serverless-Application-Repository_32.svg"},

    // AWS Networking
    {type:"aws_appmesh", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_AWS-App-Mesh_32.svg"},
    {type:"aws_cloudfront", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_Amazon-CloudFront_32.svg"},
    {type:"aws_dx", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_AWS-Direct-Connect_32.svg"},
    {type:"aws_globalaccelerator", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_AWS-Global-Accelerator_32.svg"},
    {type:"aws_lb", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_Elastic-Load-Balancing_32.svg"},
    {type:"aws_route53", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_Amazon-Route-53_32.svg"},
    {type:"aws_s2_transit_gateway", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_AWS-Transit-Gateway_32.svg"},
    {type:"aws_vpc", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_Amazon-Virtual-Private-Cloud_32.svg"},
    {type:"aws_default_vpc", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_Amazon-Virtual-Private-Cloud_32.svg"},
    {type:"aws_vpn", path:"/providers/aws/Arch_Networking-Content-Delivery/32/Arch_AWS-Site-to-Site-VPN_32.svg"},

    // AWS Storage
    {type:"aws_backup", path:"/providers/aws/Arch_Storage/32/Arch_AWS-Backup_32.svg"},
    {type:"aws_efs", path:"/providers/aws/Arch_Storage/32/Arch_Amazon-EFS_32.svg"},
    {type:"aws_fsx", path:"/providers/aws/Arch_Storage/32/Arch_Amazon-FSX_32.svg"},
    {type:"aws_s3", path:"/providers/aws/Arch_Storage/32/Arch_Amazon-Simple-Storage-Service_32.svg"},
    {type:"aws_storagegateway", path:"/providers/aws/Arch_Storage/32/Arch_AWS-Storage-Gateway_32.svg"},

    // AWS Database
    {type:"aws_docdb", path:"/providers/aws/Arch_Database/32/Arch_Amazon-DocumentDB_32.svg"},
    {type:"aws_dynamodb", path:"/providers/aws/Arch_Database/32/Arch_Amazon-DynamoDB_32.svg"},
    {type:"aws_elasticache", path:"/providers/aws/Arch_Database/32/Arch_Amazon-ElastiCache_32.svg"},
    {type:"aws_neptune", path:"/providers/aws/Arch_Database/32/Arch_Amazon-Neptune_32.svg"},
    {type:"aws_rds", path:"/providers/aws/Arch_Database/32/Arch_Amazon-RDS_32.svg"},
    {type:"aws_memoydb", path:"/providers/aws/Arch_Database/32/Arch_Amazon-MemoryDB-for-Redis_32.svg"},
    {type:"aws_timestream", path:"/providers/aws/Arch_Database/32/Arch_Amazon-Timestream_32.svg"},

    // AWS Security, Identity, & Compliance
    {type:"aws_acm",path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Certificate-Manager_32.svg"},
    {type:"aws_acmpca", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Certificate-Manager_32.svg"},
    {type:"aws_auditmanager", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Audit-Manager_32.svg"},
    {type:"aws_directory_service", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Directory-Service_32.svg"},
    {type:"aws_cognito", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_Amazon-Cognito_32.svg"},
    {type:"aws_detective", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_Amazon-Detective_32.svg"},
    {type:"aws_directory_service", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Directory-Service_32.svg"},
    {type:"aws_fms", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Firewall-Manager_32.svg"},
    {type:"aws_guardduty", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_Amazon-GuardDuty_32.svg"},
    {type:"aws_iam", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Identity-and-Access-Management_32.svg"},
    {type:"aws_inspector", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_Amazon-Inspector_32.svg"},
    {type:"aws_kms", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Key-Management-Service_32.svg"},
    {type:"aws_secretmanager", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Secrets-Manager_32.svg"},
    {type:"aws_securityhub", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Security-Hub_32.svg"},
    {type:"aws_shield", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Shield_32.svg"},
    {type:"aws_signer", path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Signer_32.svg"},
    {type:"aws_waf",path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-WAF_32.svg"},
    {type:"aws_macie",path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_Amazon-Macie_32.svg"},
    {type:"aws_ram",path:"/providers/aws/Arch_Security-Identity-Compliance/32/Arch_AWS-Resource-Access-Manager_32.svg"},

    // AWS Tools
    {type: "aws_budgets",path:"providers/aws/Arch_Cloud-Financial-Management/32/Arch_AWS-Budgets_32.png"},
    {type:"aws_cloud9",path:"/providers/aws/Arch_Developer-Tools/32/Arch_AWS-Cloud9_32.svg"},
    {type:"aws_codebuild",path:"/providers/aws/Arch_Developer-Tools/32/Arch_AWS-CodeBuild_32.svg"},
    {type:"aws_codecommit",path:"/providers/aws/Arch_Developer-Tools/32/Arch_AWS-CodeCommit_32.svg"},
    {type:"aws_codedeploy",path:"/providers/aws/Arch_Developer-Tools/32/Arch_AWS-CodeDeploy_32.svg"},
    {type:"aws_codestar",path:"/providers/aws/Arch_Developer-Tools/32/Arch_AWS-CodeStar_32.svg"},
    {type:"aws_pinpoint",path:"/providers/aws/Arch_Mobile-Services/32/Arch_Amazon-Pinpoint_32.svg"},
    {type:"aws_prometheus",path:"/providers/aws/Arch_Management-Governance/32/Arch_Amazon-Managed-Service-for-Prometheus_32.svg"},
    {type:"aws_xray",path:"/providers/aws/Arch_Developer-Tools/32/Arch_AWS-X-Ray_32.svg"},

    // AWS AI
    {type:"aws_comprehend",path:"/providers/aws/Arch_Machine-Learning/32/Arch_Amazon-Comprehend_32.svg"},
    {type:"aws_kendra",path:"/providers/aws/Arch_Machine-Learning/32/Arch_Amazon-Kendra_32.svg"},
    {type:"aws_lex",path:"/providers/aws/Arch_Machine-Learning/32/Arch_Amazon-Lex_32.svg"},
    {type:"aws_sagemaker",path:"/providers/aws/Arch_Machine-Learning/32/Arch_Amazon-SageMaker_32.svg"},

    // AWS Analytics
    {type:"aws_athena",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-Athena_32.svg"},
    {type:"aws_cloudsearch",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-CloudSearch_32.svg"},
    {type:"aws_dataexchange",path:"/providers/aws/Arch_Analytics/32/Arch_AWS-Data-Pipeline_32.svg"},
    {type:"aws_datapipeline",path:"/providers/aws/Arch_Analytics/32/Arch_AWS-Data-Pipeline_32.svg"},
    {type:"aws_elasticsearch",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-OpenSearch-Service_32.svg"},
    {type:"aws_emr",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-EMR_32.svg"},
    {type:"aws_glue",path:"/providers/aws/Arch_Analytics/32/Arch_AWS-Glue_32.svg"},
    {type:"aws_kinesis",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-Kinesis_32.svg"},
    {type:"aws_lakeformation",path:"/providers/aws/Arch_Analytics/32/Arch_AWS-Lake-Formation_32.svg"},
    {type:"aws_quicksight",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-QuickSight_32.svg"},
    {type:"aws_redshift",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-Redshift_32.svg"},
    {type:"aws_msk",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-Managed-Streaming-for-Apache-Kafka_32.svg"},
    {type:"aws_opensearch",path:"/providers/aws/Arch_Analytics/32/Arch_Amazon-OpenSearch-Service_32.svg"},

    // AWS Application
    {type:"aws_amplify",path:"/providers/aws/Arch_Front-End-Web-Mobile/32/Arch_AWS-Amplify_32.svg"},
    {type:"aws_appconfig",path:"/providers/aws/Arch_Management-Governance/32/Arch_AWS-App-Config_32.svg"},
    {type:"aws_appflow",path:"/providers/aws/Arch_App-Integration/32/Arch_Amazon-AppFlow_32.svg"},
    {type:"aws_mwaa",path:"/providers/aws/Arch_App-Integration/32/Arch_Amazon-Managed-Workflows-for-Apache-Airflow_32.svg"},
    {type:"aws_appstream",path:"/providers/aws/Arch_End-User-Computing/32/Arch_Amazon-AppStream_32.svg"},
    {type:"aws_appsync",path:"/providers/aws/Arch_App-Integration/32/Arch_AWS-AppSync_32.svg"},
    {type:"aws_chime",path:"/providers/aws/Arch_Business-Applications/32/Arch_Amazon-Chime_32.svg"},
    {type:"aws_connect",path:"/providers/aws/Arch_Business-Applications/32/Arch_Amazon-Connect_32.svg"},
    {type:"aws_devicefarm",path:"/providers/aws/Arch_Front-End-Web-Mobile/32/Arch_AWS-Device-Farm_32.svg"},
    {type:"aws_elastictranscoder",path:"/providers/aws/Arch_Media-Services/32/Arch_Amazon-Elastic-Transcoder_32.svg"},
    {type:"aws_medialive",path:"/providers/aws/Arch_Media-Services/32/Arch_AWS-Elemental-MediaConvert_32.svg"},
    {type:"aws_gamelift",path:"/providers/aws/Arch_Games/32/Arch_Amazon-GameLift_32.svg"},
    {type:"aws_ivs",path:"/providers/aws/Arch_Media-Services/32/Arch_Amazon-Interactive-Video-Service_32.svg"},
    {type:"aws_media_convert_queue",path:"/providers/aws/Arch_Media-Services/32/Arch_AWS-Elemental-MediaConvert_32.svg"},
    {type:"aws_media_package",path:"/providers/aws/Arch_Media-Services/32/Arch_AWS-Elemental-MediaPackage_32.svg"},
    {type:"aws_media_store",path:"/providers/aws/Arch_Media-Services/32/Arch_AWS-Elemental-MediaStore_32.svg"},
    {type:"aws_location",path:"/providers/aws/Arch_Front-End-Web-Mobile/32/Arch_Amazon-Location-Service_32.svg"},
    {type:"aws_ses",path:"/providers/aws/Arch_Business-Applications/32/Arch_Amazon-Simple-Email-Service_32.svg"},
    {type:"aws_sns",path:"/providers/aws/Arch_App-Integration/32/Arch_Amazon-Simple-Notification-Service_32.svg"},
    {type:"aws_sqs",path:"/providers/aws/Arch_App-Integration/32/Arch_Amazon-Simple-Queue-Service_32.svg"},
    {type:"aws_sfn",path:"/providers/aws/Arch_App-Integration/32/Arch_AWS-Step-Functions_32.svg"},
    {type:"aws_api_gateway",path:"/providers/aws/Arch_App-Integration/32/Arch_Amazon-API-Gateway_32.svg"},

    // AWS Containers
    {type:"aws_ecr",path:"/providers/aws/Arch_Containers/32/Arch_Amazon-Elastic-Container-Registry_32.svg"},
    {type:"aws_ecs",path:"/providers/aws/Arch_Containers/32/Arch_Amazon-ECS-Anywhere_32.svg"},
    {type:"aws_eks",path:"/providers/aws/Arch_Containers/32/Arch_Amazon-EKS-Cloud_32.svg"},
    {type:"aws_eks_fargate_profile",path:"/providers/aws/Arch_Containers/32/Arch_AWS-Fargate_32.svg"},

    // AWS Other
    {type:"aws_iot",path:"/providers/aws/Arch_Internet-of-Things/32/Arch_AWS-IoT-Analytics_32.svg"},
    {type:"aws_ssm",path:"/providers/aws/Arch_Management-Governance/32/Arch_AWS-Systems-Manager_32.svg"},

]

const AzureIcons = [
    { type: "azurerm_aadb2c_directory", path: "/providers/azurerm/Identity/10228-icon-service-Azure-AD-B2C.svg"},
    { type: "azurerm_api_management", path: "/providers/azurerm/App Services/10042-icon-service-API-Management-Services.svg"},
    { type: "azurerm_app_service", path:"/providers/azurerm/App Services/10035-icon-service-App-Services.svg"},
    { type: "azurerm_app_service_plan", path:"providers/azurerm/App Services/00046-icon-service-App-Service-Plans.svg"},
    { type: "azurerm_resource_group", path:"/providers/azurerm/General/10007-icon-service-Resource-Groups.svg"},
    { type: "azurerm_virtual_network", path:"/providers/azurerm/Networking/10061-icon-service-Virtual-Networks.svg"},
    { type: "azurerm_network_interface", path:"/providers/azurerm/Networking/10080-icon-service-Network-Interfaces.svg"},
    { type: "azurerm_subnet", path:"/providers/azurerm/Networking/10061-icon-service-Virtual-Networks.svg"},
    { type: "azurerm_virtual_machine", path:"/providers/azurerm/Compute/10021-icon-service-Virtual-Machine.svg"},
    { type: "azurerm_sql_server", path:"/providers/azurerm/Databases/02390-icon-service-Azure-SQL.svg"},
]

export function getServiceIcon (providerType,resourceType){
    switch (providerType) {
        case "registry.terraform.io/hashicorp/aws":
            return getAWSIcon(resourceType);
        case "registry.terraform.io/hashicorp/azurerm":
            return getAzureIcon(resourceType);
        default:
            return "/providers/terraform.svg";
    }
};


const getAWSIcon = (resourceType) => {
    // search exact match
    var icon = AWSIcons.find(icon => resourceType === icon.type);
    if (icon) return icon.path;

    // search partial match
    icon = AWSIcons.find(icon => resourceType.includes(icon.type));
    return icon ? icon.path : "/providers/aws/AWS.svg";
}

const getAzureIcon = (resourceType) => {
    // search exact match
    var icon = AzureIcons.find(icon => resourceType === icon.type);
    if (icon) return icon.path;

    // search partial match
    icon = AzureIcons.find(icon => resourceType.includes(icon.type));
    return icon ? icon.path : "/providers/azurerm/Azure.svg";
}