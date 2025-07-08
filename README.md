<div id="terrakube" align="center">
    <br />
    <img src="https://avatars.githubusercontent.com/u/80990539?s=200&v=4" alt="Terrakube Logo" width="100"/>
    <h2 border="0">Terrakube</h2>
    <p>Open source IaC Automation and Collaboration Software.</p>
</div>

<div id="badges" align="center">

[![gitpod](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod&style=flat-square)](https://gitpod.io/#https://github.com/terrakube-io/terrakube)
[![Build](https://github.com/terrakube-io/terrakube/actions/workflows/pull_request.yml/badge.svg)](https://github.com/terrakube-io/terrakube/actions/workflows/pull_request.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AzBuilder_azb-server&metric=coverage)](https://sonarcloud.io/dashboard?id=AzBuilder_azb-server)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/terrakube-io/terrakube/blob/main/LICENSE)
[![gitbook](https://raw.githubusercontent.com/aleen42/badges/master/src/gitbook_2.svg)](https://gitpod.io/#https://github.com/terrakube-io/terrakube)
[![Slack](https://img.shields.io/badge/Join%20Our%20Community-Slack-blue)](https://join.slack.com/t/terrakubeworkspace/shared_invite/zt-2cx6yn95t-2CTBGvsQhBQJ5bfbG4peFg)


</div>



### Features
The key features of Terrakube are:
- **Private Registry:** Publish your Terraform Modules and Providers privately.   <br/>

<img src="https://github.com/terrakube-io/terrakube/assets/27365102/66fbd39c-0a40-43d8-94d9-4c2a0976051c" width="1080"/>  <br/>

- **Organizations and Workspaces:** Use Organizations and Workspaces to manage your infrastructure in a structured and efficient way. Workspaces also support Tags, which let you group, organize, and filter your workspaces based on the tags you assign to them.    <br/>

<img src="https://github.com/terrakube-io/terrakube/assets/27365102/f36953f7-0dbd-4877-be8d-ba2bf7704f2b" width="1080"/>  <br/>

- **Version Control Integration:** Terrakube integrates with Github (Cloud and  Enterprise), GitLab (EE and CE), Bitbucket and Azure DevOps to retrieve your terraform/opentofu code. <br/>

<img src="https://github.com/terrakube-io/terrakube/assets/27365102/d9102910-41af-42be-b154-1257108f688b" width="1080"/>  <br/>

- **Terraform Workflow:** Terrakube allows you to run the Terraform / OpenTofu workflow from anywhere, anytime. You can perform actions such as terraform apply, terraform plan, and terraform destroy. You can also manage variables and access the Terraform state files.

<img src="https://github.com/terrakube-io/terrakube/assets/27365102/9e0421be-576c-4206-a29a-c1d62238681e" width="1080"/>  <br/>

- **Custom Workflows:** Enhance your IaC workflow with OPA, Infracost, or any other tool of your choice. You can use Terrakube extensions to integrate them seamlessly, or create your own custom integration using the Terrakube API. This way, you can automate compliance checks, cost estimates, security scans, and more for your Terraform projects.

- **Access Control:** You can use [DEX](https://github.com/dexidp/dex) to authenticate in Terrakube with various identity providers, such as Azure Active Directory, Amazon Cognito, Github, SAML, and more. You can also leverage your existing groups to assign granular permissions to Workspaces, Modules, VCS, and other resources.

- **Remote Backend:** Terrakube supports both `remote backend` and `cloud` block so you can run your workflow directly from the Terraform / OpenTofu CLI.

### Getting Started

### Installation

- [Install Terrakube using Helm](https://docs.terrakube.io/getting-started/deployment/docker-compose)
- [Install Terrakube using Docker Compose](https://docs.terrakube.io/getting-started/docker-compose)
- [Test Terrakube using Minikube](https://docs.terrakube.io/getting-started/deployment/minikube-+-https)
- [Test Terrakube using Gitpod](https://docs.terrakube.io/getting-started/getting-started)
- [Develop Terrakube using VS Code Devcontainers](.devcontainer/README.md)

### Documentation
To learn more about Terrakube [go to the complete documentation.](https://docs.terrakube.io/) 

### Contributing 
Terrakube welcomes any idea or feedback from the community. If you want to contribute to this project, please read our [Contribution Guide](CONTRIBUTING.md) for more details.

### Sponsors

| Sponsor  | Thanks |
| ------------- | ------------- |
| [<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" alt="JetBrains" width="32"> JetBrains](https://jb.gg/OpenSource)  | For providing with free licenses to their great tools.   |
| [<img src="https://uploads-ssl.webflow.com/5c349f90a3cd4515d0564552/5c66e5b48238e30e170da3be_logo.svg" alt="Gitbook" width="32"> Gitbook](https://www.gitbook.com/)   | For providing us with free OSS Plan. |
| [<img src="https://github.com/terrakube-io/terrakube/assets/27365102/e5977550-eb4f-4519-9aa8-293e5660f873" width="32"> Docker](https://www.docker.com/) | For providing us with free OSS Plan.|
| [<img src="https://github.com/user-attachments/assets/c094496d-ff2d-4501-8416-8185b1abe45a" width="32"> Tuta](https://tuta.com/) | For providing us with free email service.|


### Terraform BSL License
Hashicorp confirmed that Terrakube is compatible with the new Terraform BSL License, more information can be found in the following [discussion](https://github.com/orgs/terrakube-io/discussions/467).
