# Terrakube UI

## Compile

Define the following environment variables in your local machine:

|Name                         | Description                                                          |
|:----------------------------|----------------------------------------------------------------------|
|REACT_CONFIG_TERRAKUBE_URL   | Terrakube API URL Example: https://someURL/terrakube-api/api/v1/     | 
|REACT_CONFIG_CLIENT_ID       | Azure Active Directory Application Id                                |
|REACT_CONFIG_AUTHORITY       | https://login.microsoftonline.com/(Azure Active Directory Tenant ID) |
|REACT_CONFIG_REDIRECT        | Redirct URL. Example: http://localhost:3000                          |
|REACT_CONFIG_REGISTRY_URI    | Terrakube Registry url. Example: https://someurl/open-registry/      |
|REACT_CONFIG_SCOPE           | Azure Ad API Scope Example: api://azbuilder/Builder.Default          |

Run the script to generate .env file in your local

```bash
./setupEnv.sh
```

The above script will generate a .env file with the following structure:

```
REACT_APP_TERRAKUBE_API_URL=https://someURL.com/XXXX/api/v1/
REACT_APP_CLIENT_ID=7a567a36-e18b-4754-a9fb-03a97e082fc4
REACT_APP_AUTHORITY=https://login.microsoftonline.com/59b1b67-5a8d-4060-b3e2-9d7b849527a8
REACT_APP_REDIRECT_URI=http://localhost:8080
REACT_APP_REGISTRY_URI=https://registry.someURL.com
REACT_APP_SCOPE=api://azbuilder/Builder.Default
```

> Values are only examples

Run the script env.sh to generate static configuration file env-config.js inside the public folder, the UI will take the values from that static file at run time.

> For a better explanation of how env-config.js and env.sh load the values at running time please refer to the following [documentation](https://www.freecodecamp.org/news/how-to-implement-runtime-environment-variables-with-create-react-app-docker-and-nginx-7f9d42a91d70/)

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

> If you are a Windows user and face the following error when building the UI ***Bash script and /bin/bash^M: bad interpreter: No such file or directory*** run the following command to fix the issue ***sed -i -e 's/\r$//' env.sh***

### Run Image

```bash
docker run -p 127.0.0.1:8080:8080/tcp -it --env-file=.env terrakube-ui:latest
```

> Make sure to run the script  **setupEnv.sh** first to generate the **.env** file

### Docker Compose

```dockerfile

version: "3.8"
services:
  terrakube-ui:
    image: azbuilder/terrakube-ui:latest
    container_name: terrakube-ui
    environment:
      - REACT_APP_TERRAKUBE_API_URL=https://XXXXXXXX/api/v1/
      - REACT_APP_CLIENT_ID=XXXXXX
      - REACT_APP_AUTHORITY=https://login.microsoftonline.com/XXXXX
      - REACT_APP_REDIRECT_URI=http://localhost:8080
      - REACT_APP_REGISTRY_URI=https://registry.XXXX.com
      - REACT_APP_SCOPE=api://XXXX/XXX
    ports:
      - 8080:8080 
```