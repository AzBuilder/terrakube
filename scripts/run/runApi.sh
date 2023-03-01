ApiDataSourceType=H2
GroupValidationType=DEX
UserValidationType=DEX
AuthenticationValidationType=DEX
TerrakubeHostname=8080-azbuilder-terrakube-d49wenb6aff.ws-us87.gitpod.io
AzBuilderExecutorUrl=https://8090-azbuilder-terrakube-d49wenb6aff.ws-us87.gitpod.io/api/v1/terraform-rs
PatSecret=ejZRSFgheUBOZXAyUURUITUzdmdINDNeUGpSWHlDM1g=
InternalSecret=S2JeOGNNZXJQTlpWNmhTITkha2NEKkt1VVBVQmFeQjM=
DexIssuerUri=https://5556-azbuilder-terrakube-d49wenb6aff.ws-us87.gitpod.io/dex
StorageType=LOCAL
TerrakubeUiURL=https://3000-azbuilder-terrakube-d49wenb6aff.ws-us87.gitpod.io
spring_profiles_active=demo
DexClientId=example-app

cd /projects/terrakube/api

mvn spring-boot:run -Dspring-boot.run.profiles=demo -Dserver.port=8080