# AzBuilder UI

## Running the UI

Define the following environment variables:

|Name                         | Description                                                          |
|:----------------------------|----------------------------------------------------------------------|
|REACT_CONFIG_AZBUILDER_URL   | AzBuilder API URL Example: https://someURL/azbuilder/api/v1/         | 
|REACT_CONFIG_CLIENT_ID       | Azure Active Directory Application Id                                |
|REACT_CONFIG_AUTHORITY       | https://login.microsoftonline.com/(Azure Active Directory Tenant ID) |
|REACT_CONFIG_REDIRECT        | Redirct URL. Example: http://localhost:3000                          |

Run the script:

```bash
./setupEnv.sh
```

Build/Start the application:

```bash
yarn isntall
yarn start
```

## Docker Build Support

```bash
docker build --build-arg BUILDER_API_URL=$REACT_CONFIG_AZBUILDER_URL --build-arg CLIENT_ID=$REACT_CONFIG_CLIENT_ID --build-arg AUTHORITY=$REACT_CONFIG_AUTHORITY --build-arg REDIRECT_URI=$REACT_CONFIG_REDIRECT -t azbuilder/ui:latest  .
```
