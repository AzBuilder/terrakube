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

Run the script to generate .env file in your local

```bash
./setupEnv.sh
```

Run the script env.sh to generate static configuration file env-config.js that will be loaded when the ui starts

Build/Start the application:

```bash
yarn install
yarn dev
```

## Docker Support

### Build Image

```bash
docker build -t terrakube-ui:latest  .
```

### Run Image

```bash
docker run -p 127.0.0.1:8080:8080/tcp -it --env-file=.env terrakube-ui:latest
```

> Make sure to run the script  **setupEnv.sh** first to generate the **.env** file

