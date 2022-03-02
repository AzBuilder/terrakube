# Terrakube UI

## Running the UI

Define the following environment variables:

|Name                         | Description                                                          |
|:----------------------------|----------------------------------------------------------------------|
|REACT_CONFIG_TERRAKUBE_URL   | Terrakube API URL Example: https://someURL/terrakube/api/v1/         | 
|REACT_CONFIG_CLIENT_ID       | Azure Active Directory Application Id                                |
|REACT_CONFIG_AUTHORITY       | https://login.microsoftonline.com/(Azure Active Directory Tenant ID) |
|REACT_CONFIG_REDIRECT        | Redirct URL. Example: http://localhost:3000                          |
|REACT_CONFIG_REGISTRY_URI    | Terrakube Registry url. Example: https://someurl/open-registry/      |
|REACT_CONFIG_SCOPE           | Azure Ad API Scope Example: api://azbuilder/Builder.Default          |

Run the script:

```bash
./setupEnv.sh
```

Build/Start the application:

```bash
yarn install
yarn start
```

## Docker Build Support

```bash
docker build --build-arg TERRAKUBE_API_URL=$REACT_CONFIG_TERRAKUBE_URL --build-arg CLIENT_ID=$REACT_CONFIG_CLIENT_ID --build-arg AUTHORITY=$REACT_CONFIG_AUTHORITY --build-arg REDIRECT_URI=$REACT_CONFIG_REDIRECT -t terrakube/ui:latest  .
```
