import { React, useState } from "react";
import "./Settings.css";
import {
  Steps,
  Space,
  Button,
  Form,
  Input,
  Row,
  Col,
  Typography,
  message,
  Dropdown,
} from "antd";
import {
  GithubOutlined,
  GitlabOutlined,
  DownOutlined,
} from "@ant-design/icons";
import { SiBitbucket, SiAzuredevops } from "react-icons/si";
import { HiOutlineExternalLink } from "react-icons/hi";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import { ORGANIZATION_NAME } from "../../config/actionTypes";
import { v1 as uuidv1 } from "uuid";

const { Paragraph } = Typography;
const { Step } = Steps;
const validateMessages = {
  required: "${label} is required!",
};
export const AddVCS = ({ setMode, loadVCS }) => {
  const { orgid, vcsName } = useParams();
  const [current, setCurrent] = useState(vcsName ? 1 : 0);
  const [vcsType, setVcsType] = useState(vcsName ? vcsName : "GITHUB");
  const [uuid, setUUID] = useState(uuidv1());
  const handleChange = (currentVal) => {
    setCurrent(currentVal);
  };
  const handleVCSClick = (vcs) => {
    setCurrent(1);
    setVcsType(vcs);
  };

  const getCallBackUrl = () => {
    return `${
      new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
    }/callback/v1/vcs/${uuid}`;
  };

  const renderVCSType = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return "GitLab";
      case "GITLAB_ENTERPRISE":
        return "GitLab Enterprise";
      case "GITLAB_COMMUNITY":
        return "GitLab Community Edition";
      case "BITBUCKET":
        return "BitBucket";
      case "BITBUCKET_SERVER":
        return "BitBucket Server";
      case "AZURE_DEVOPS":
        return "Azure Devops";
      case "AZURE_DEVOPS_SERVER":
        return "Azure Devops Server";
      case "GITHUB_ENTERPRISE":
        return "GitHub Enterprise";
      default:
        return "GitHub";
    }
  };
  const gitlabItems = [
    {
      label: "Gitlab.com",
      key: "1",
      onClick: () => {
        handleVCSClick("GITLAB");
      },
    },
    {
      label: "Gitlab Community Edition",
      key: "2",
      onClick: () => {
        handleVCSClick("GITLAB_ENTERPRISE");
      },
    },
    {
      label: "Gitlab Enterprise Edition",
      key: "3",
      onClick: () => {
        handleVCSClick("GITLAB_ENTERPRISE");
      },
    },
  ];

  const githubItems = [
    {
      label: "Github.com",
      key: "1",
      onClick: () => {
        handleVCSClick("GITHUB");
      },
    },
    {
      label: "Github Enterprise",
      key: "2",
      onClick: () => {
        handleVCSClick("GITHUB_ENTERPRISE");
      },
    },
  ];

  const bitBucketItems = [
    {
      label: "Bitbucket Cloud",
      key: "1",
      onClick: () => {
        handleVCSClick("BITBUCKET");
      },
    },
  ];

  const azDevOpsItems = [
    {
      label: "Azure DevOps Services",
      key: "1",
      onClick: () => {
        handleVCSClick("AZURE_DEVOPS");
      },
    },
  ];
  const getDocsUrl = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return "https://docs.terrakube.io/user-guide/vcs-providers/gitlab.com";
      case "GITLAB_ENTERPRISE":
      case "GITLAB_COMMUNITY":  
        return "https://docs.terrakube.io/user-guide/vcs-providers/gitlab-ee-and-ce";
      case "BITBUCKET":
        return "https://docs.terrakube.io/user-guide/vcs-providers/bitbucket.com";
      case "BITBUCKET_SERVER":
        return "https://docs.terrakube.io/user-guide/vcs-providers/bitbucket-server";
      case "AZURE_DEVOPS":
        return "https://docs.terrakube.io/user-guide/vcs-providers/azure-devops";
      case "AZURE_DEVOPS_SERVER":
        return "https://docs.terrakube.io/user-guide/vcs-providers/azure-devops";
      case "GITHUB_ENTERPRISE":
        return "https://docs.terrakube.io/user-guide/vcs-providers/github-enterprise";
      default:
        return "https://docs.terrakube.io/user-guide/vcs-providers/github.com";
    }
  };

  const getClientIdName = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "GITLAB_ENTERPRISE":
      case "GITLAB_COMMUNITY":
        return "Application ID";
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        return "Key";
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        return "App ID";
      default:
        return "Client ID";
    }
  };

  const getVcsType = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "GITLAB_ENTERPRISE":
      case "GITLAB_COMMUNITY":
        return "GITLAB";
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        return "BITBUCKET";
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        return "AZURE_DEVOPS";
      default:
        return "GITHUB";
    }
  };

  const getAPIUrl = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return "https://gitlab.com/api/v4";
      case "BITBUCKET":
        return "https://api.bitbucket.org/2.0";
      case "AZURE_DEVOPS":
        return "https://dev.azure.com";
      case "GITHUB":
        return "https://api.github.com";
      default:
        return "";
    }
  };

  const getAPIUrlPlaceholder = (vcs) => {
    switch (vcs) {
      case "GITLAB_ENTERPRISE":
      case "GITLAB_COMMUNITY":
        return "ex. https://<GITLAB INSTANCE HOSTNAME>/api/v4";
      case "BITBUCKET_SERVER":
        return "ex. https://<BITBUCKET INSTANCE HOSTNAME>/context-path/rest/api/1.0";
      case "AZURE_DEVOPS_SERVER":
        return "ex. https://<AZURE DEVOPS INSTANCE HOSTNAME>";
      case "GITHUB_ENTERPRISE":
        return "ex. https://<GITHUB INSTANCE HOSTNAME>/api/v3";
      default:
        return "";
    }
  };

  const getHttpsPlaceholder = (vcs) => {
    switch (vcs) {
      case "GITLAB_ENTERPRISE":
      case "GITLAB_COMMUNITY":
        return "ex. https://<GITLAB INSTANCE HOSTNAME>";
      case "BITBUCKET_SERVER":
        return "ex. https://<BITBUCKET INSTANCE HOSTNAME>/<CONTEXT PATH>";
      case "AZURE_DEVOPS_SERVER":
        return "ex. https://<AZURE DEVOPS INSTANCE HOSTNAME>";
      case "GITHUB_ENTERPRISE":
        return "ex. https://<GITHUB INSTANCE HOSTNAME>";
      default:
        return "";
    }
  };

  const httpsHidden = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "BITBUCKET":
      case "AZURE_DEVOPS":
      case "GITHUB":
        return true;
      default:
        return false;
    }
  };

  const apiUrlHidden = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "BITBUCKET":
      case "AZURE_DEVOPS":
      case "GITHUB":
        return true;
      default:
        return false;
    }
  };

  const getSecretIdName = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "GITLAB_ENTERPRISE":
      case "GITLAB_COMMUNITY":
        return "Secret";
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        return "Secret";
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        return "Client Secret";
      default:
        return "Client Secret";
    }
  };

  const renderStep1 = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "GITLAB_ENTERPRISE":
        return (
          <div>
            <p className="paragraph">
              1. On {renderVCSType(vcsType)},{" "}
              {vcsType === "GITLAB" ? (
                <>
                  <Button
                    className="link"
                    target="_blank"
                    href="https://gitlab.com/-/profile/applications"
                    type="link"
                  >
                    register a new OAuth Application&nbsp;{" "}
                    <HiOutlineExternalLink />
                  </Button>
                  . Enter the following information:
                </>
              ) : (
                <span>
                  navigate to User Settings → Application and register a new
                  OAuth Application with the following information.
                </span>
              )}
            </p>
            <div className="paragraph">
              <p></p>
              <Row>
                <Col span={6}>
                  <b>Name:</b>{" "}
                </Col>
                <Col span={18}>
                  Terrakube ({sessionStorage.getItem(ORGANIZATION_NAME)})
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Redirect URI:</b>{" "}
                </Col>
                <Col span={18}>
                  {" "}
                  <Paragraph copyable>{getCallBackUrl()}</Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Scopes:</b>{" "}
                </Col>
                <Col span={18}>
                  Only the following should be checked:
                  <br />
                  api
                </Col>
              </Row>
              <p></p>
            </div>
          </div>
        );
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        return (
          <div>
            <p className="paragraph">
              1. On {renderVCSType(vcsType)}, logged in as whichever account you
              want Terrakube to act as, add a new OAuth Consumer. You can find
              the OAuth Consumer settings page under your workspace settings.
              Enter the following information:
            </p>
            <div className="paragraph">
              <p></p>
              <Row>
                <Col span={6}>
                  <b>Name:</b>{" "}
                </Col>
                <Col span={18}>
                  Terrakube ({sessionStorage.getItem(ORGANIZATION_NAME)})
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Description:</b>{" "}
                </Col>
                <Col span={18}>Any description of your choice</Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Callback URL:</b>{" "}
                </Col>
                <Col span={18}>
                  {" "}
                  <Paragraph copyable>{getCallBackUrl()}</Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>URL:</b>{" "}
                </Col>
                <Col span={18}>
                  {" "}
                  <Paragraph copyable>
                    {new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}
                  </Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>This is a private consumer (checkbox):</b>{" "}
                </Col>
                <Col span={18}>Checked</Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Permissions (checkboxes):</b>{" "}
                </Col>
                <Col span={18}>
                  The following should be checked:
                  <br />
                  Account: Write
                  <br />
                  Repositories: Admin
                  <br />
                  Pull requests: Write
                  <br />
                  Webhooks: Read and write
                </Col>
              </Row>
              <p></p>
            </div>
          </div>
        );
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        return (
          <div>
            <p className="paragraph">
              1. On {renderVCSType(vcsType)},{" "}
              <Button
                className="link"
                target="_blank"
                href="https://aex.dev.azure.com/app/register?mkt=en-US"
                type="link"
              >
                register a new OAuth Application&nbsp; <HiOutlineExternalLink />
              </Button>
              . Enter the following information:
            </p>
            <div className="paragraph">
              <p></p>
              <Row>
                <Col span={6}>
                  <b>Company Name:</b>{" "}
                </Col>
                <Col span={6}>Terrakube</Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Application name:</b>{" "}
                </Col>
                <Col span={18}>
                  Terrakube ({sessionStorage.getItem(ORGANIZATION_NAME)})
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Application website:</b>{" "}
                </Col>
                <Col span={18}>
                  {" "}
                  <Paragraph copyable>
                    {new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}
                  </Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Callback URL:</b>{" "}
                </Col>
                <Col span={18}>
                  {" "}
                  <Paragraph copyable>{getCallBackUrl()}</Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Authorized scopes (checkboxes):</b>{" "}
                </Col>
                <Col span={18}>
                  Only the following should be checked: <br />
                  Code (read)
                  <br />
                  Code (status)
                </Col>
              </Row>
              <p></p>
            </div>
          </div>
        );
      default:
        return (
          <div>
            <p className="paragraph">
              1. On {renderVCSType(vcsType)},{" "}
              {vcsType === "GITHUB" ? (
                <Button
                  className="link"
                  target="_blank"
                  href="https://github.com/settings/applications/new"
                  type="link"
                >
                  register a new OAuth Application&nbsp;{" "}
                  <HiOutlineExternalLink />
                </Button>
              ) : (
                <span>
                  register a new OAuth Application using the link https://
                  <i>yourdomain.com</i>/settings/applications/new
                </span>
              )}
              . Enter the following information:
            </p>
            <div className="paragraph">
              <p></p>
              <Row>
                <Col span={6}>
                  <b>Application Name:</b>{" "}
                </Col>
                <Col span={18}>
                  <Paragraph copyable>
                    Terrakube ({sessionStorage.getItem(ORGANIZATION_NAME)})
                  </Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Homepage URL:</b>{" "}
                </Col>
                <Col span={18}>
                  <Paragraph copyable>
                    {new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin}
                  </Paragraph>
                </Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Application description:</b>{" "}
                </Col>
                <Col span={18}>Any description of your choice</Col>
              </Row>
              <Row>
                <Col span={6}>
                  <b>Authorization callback URL:</b>{" "}
                </Col>
                <Col span={18}>
                  {" "}
                  <Paragraph copyable>{getCallBackUrl()}</Paragraph>
                </Col>
              </Row>
              <p></p>
            </div>
          </div>
        );
    }
  };

  const renderStep2 = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return (
          <p className="paragraph">
            2. After clicking the "Save application" button, you'll be taken to
            the new application's page. Enter the Application ID and Secret
            below:
          </p>
        );
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        return (
          <p className="paragraph">
            2. After clicking the "Save" button, you'll be taken to the OAuth
            settings page. Find your new OAuth consumer under the "OAuth
            Consumers" heading, and click its name to reveal its details. Enter
            the Key and Secret below:
          </p>
        );
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        return (
          <p className="paragraph">
            2. Create the application. On the following page, you'll find its
            details. Enter the App ID and Client Secret below:
          </p>
        );
      default:
        return (
          <p className="paragraph">
            2. After clicking the "Register application" button, you'll be taken
            to the new application's page. Enter the Client ID below:
          </p>
        );
    }
  };

  const renderStep3 = (vcs) => {
    switch (vcs) {
      case "GITLAB":
      case "GITLAB_ENTERPRISE":
        return null;
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        return null;
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        return null;
      default:
        return (
          <div>
            <p className="paragraph">
              3. Next, generate a new client secret and enter the value below:
            </p>
            <br />
          </div>
        );
    }
  };

  const getConnectUrl = (vcs, clientId, callbackUrl, endpoint) => {
    switch (vcs) {
      case "GITLAB":
      case "GITLAB_ENTERPRISE":
        if (endpoint != null)
          return `${endpoint}/oauth/authorize?client_id=${clientId}&response_type=code&scope=api&&redirect_uri=${callbackUrl}`;
        else
          return `https://gitlab.com/oauth/authorize?client_id=${clientId}&response_type=code&scope=api&&redirect_uri=${callbackUrl}`;
      case "BITBUCKET":
      case "BITBUCKET_SERVER":
        if (endpoint != null)
          return `${endpoint}/site/oauth2/authorize?client_id=${clientId}&response_type=code&response_type=code&scope=repository`;
        else
          return `https://bitbucket.org/site/oauth2/authorize?client_id=${clientId}&response_type=code&response_type=code&scope=repository`;
      case "AZURE_DEVOPS":
      case "AZURE_DEVOPS_SERVER":
        if (endpoint != null)
          return `${endpoint}/oauth2/authorize?client_id=${clientId}&redirect_uri=${callbackUrl}&response_type=Assertion&scope=vso.code+vso.code_status`;
        else
          return `https://app.vssps.visualstudio.com/oauth2/authorize?client_id=${clientId}&redirect_uri=${callbackUrl}&response_type=Assertion&scope=vso.code+vso.code_status`;
      default:
        if (endpoint != null)
          return `${endpoint}/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=repo`;
        else
          return `https://github.com/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=repo`;
    }
  };

  const getDefaultHttps = (vcsType) => {
    switch (vcsType) {
      case "GITLAB":
        return `https://gitlab.com`;
      case "BITBUCKET":
        return `https://bitbucket.org`;
      case "AZURE_DEVOPS":
        return `https://app.vssps.visualstudio.com`;
      case "GITHUB":
        return `https://github.com`;
      default:
        return ``;
    }
  };

  const onFinish = (values) => {
    console.log(`Using endpoint ${values.endpoint}`);
    const body = {
      data: {
        type: "vcs",
        attributes: {
          name: values.name,
          description: values.name,
          vcsType: getVcsType(vcsType),
          clientId: values.clientId,
          clientSecret: values.clientSecret,
          callback: uuid,
          endpoint: values.endpoint,
          apiUrl: values.apiUrl,
          redirectUrl: `${window._env_.REACT_APP_REDIRECT_URI}/organizations/${orgid}/settings/vcs`,
        },
      },
    };
    console.log(body);

    axiosInstance
      .post(`organization/${orgid}/vcs`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log("created");
        console.log(response);
        if (response.status == "201") {
          window.location.replace(
            getConnectUrl(
              vcsType,
              response.data.data.attributes.clientId,
              getCallBackUrl(),
              response.data.data.attributes.endpoint
            )
          );
          loadVCS();
          setMode("list");
        }
      })
      .catch((error) => {
        if (error.response) {
          if (error.response.status === 403) {
            message.error(
              <span>
                You are not authorized to create VCS Settings. <br /> Please
                contact your administrator and request the{" "}
                <b>Manage VCS Settings</b> permission. <br /> For more
                information, visit the{" "}
                <a
                  target="_blank"
                  href="https://docs.terrakube.io/user-guide/organizations/team-management"
                >
                  Terrakube documentation
                </a>
                .
              </span>
            );
          }
        }
      });
  };
  return (
    <div>
      <h1>Add VCS Provider</h1>
      <div className="App-text">
        To connect workspaces, modules, and policy sets to git repositories
        containing Terraform configurations, Terrakube needs access to your
        version control system (VCS) provider. Use this page to configure OAuth
        authentication with your VCS provider.
      </div>
      <Steps
        direction="horizontal"
        size="small"
        current={current}
        onChange={handleChange}
      >
        <Step title="Connect to VCS" />
        <Step title="Set up provider" />
      </Steps>
      {current == 0 && (
        <Space className="chooseType" direction="vertical">
          <h3>Choose a version control provider to connect</h3>
          <div className="workflowDescription2 App-text">
            Choose the version control provider you would like to connect.
          </div>
          <Space direction="horizontal">
            <Dropdown menu={{ items: githubItems }}>
              <Button size="large">
                <Space>
                  <GithubOutlined /> Github <DownOutlined />
                </Space>
              </Button>
            </Dropdown>
            <Dropdown menu={{ items: gitlabItems }}>
              <Button size="large">
                <Space>
                  <GitlabOutlined />
                  Gitlab <DownOutlined />
                </Space>
              </Button>
            </Dropdown>
            <Dropdown menu={{ items: bitBucketItems }}>
              <Button size="large">
                <SiBitbucket /> &nbsp; Bitbucket <DownOutlined />
              </Button>
            </Dropdown>
            <Dropdown menu={{ items: azDevOpsItems }}>
              <Button size="large">
                <Space>
                  <SiAzuredevops /> Azure Devops <DownOutlined />
                </Space>
              </Button>
            </Dropdown>
          </Space>
        </Space>
      )}
      {current == 1 && (
        <Space className="chooseType" direction="vertical">
          <h3>Set up provider</h3>
          <p className="paragraph">
            For additional information about connecting to{" "}
            {renderVCSType(vcsType)} to Terrakube, please read our{" "}
            <Button
              className="link"
              target="_blank"
              href={getDocsUrl(vcsType)}
              type="link"
            >
              documentation&nbsp; <HiOutlineExternalLink />.
            </Button>
          </p>
          {renderStep1(vcsType)}
          {renderStep2(vcsType)}
          <Form
            onFinish={onFinish}
            validateMessages={validateMessages}
            name="create-vcs"
            layout="vertical"
            initialValues={{
              endpoint: getDefaultHttps(vcsType),
              apiUrl: getAPIUrl(vcsType),
            }}
          >
            <Form.Item
              name="name"
              label="Name"
              extra=" A name for your VCS Provider. This is helpful if you will be configuring multiple instances of the same provider."
              rules={[{ required: true }]}
            >
              <Input placeholder={renderVCSType(vcsType)} />
            </Form.Item>
            <Form.Item
              name="endpoint"
              label="HTTPS URL"
              rules={[{ required: true }]}
              hidden={httpsHidden(vcsType)}
            >
              <Input placeholder={getHttpsPlaceholder(vcsType)} />
            </Form.Item>
            <Form.Item
              name="apiUrl"
              label="API URL"
              rules={[{ required: true }]}
              hidden={apiUrlHidden(vcsType)}
            >
              <Input placeholder={getAPIUrlPlaceholder(vcsType)} />
            </Form.Item>
            <Form.Item
              name="clientId"
              label={getClientIdName(vcsType)}
              rules={[{ required: true }]}
            >
              <Input placeholder="ex. 824ff023a7136981f322" />
            </Form.Item>
            {renderStep3(vcsType)}
            <Form.Item
              name="clientSecret"
              label={getSecretIdName(vcsType)}
              rules={[{ required: true }]}
            >
              <Input placeholder="ex. db55545bd64e851dc298ba900dd197a02b42bb3s" />
            </Form.Item>
            <Button type="primary" htmlType="submit">
              Connect and Continue
            </Button>
          </Form>
        </Space>
      )}
    </div>
  );
};
