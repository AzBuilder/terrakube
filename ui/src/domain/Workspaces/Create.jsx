import { React, useState, useEffect } from "react";
import {
  Form,
  Input,
  Button,
  Breadcrumb,
  Layout,
  Steps,
  Card,
  Space,
  Select,
  message,
} from "antd";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import { BiTerminal, BiBookBookmark, BiUpload } from "react-icons/bi";
import { compareVersions } from "./Workspaces";
import { IconContext } from "react-icons";
import { GithubOutlined, GitlabOutlined } from "@ant-design/icons";
import { SiBitbucket, SiAzuredevops } from "react-icons/si";
import { SiGit } from "react-icons/si";
import { useHistory, Link } from "react-router-dom";
const { Content } = Layout;
const { Step } = Steps;
const validateMessages = {
  required: "${label} is required!",
  types: {
    url: "${label} is not a valid git url",
  },
};
const { Option } = Select;

export const CreateWorkspace = () => {
  const [organizationName, setOrganizationName] = useState([]);
  const [terraformVersions, setTerraformVersions] = useState([]);
  const [vcs, setVCS] = useState([]);
  const [sshKeys, setSSHKeys] = useState([]);
  const [loading, setLoading] = useState(false);
  const [vcsButtonsVisible, setVCSButtonsVisible] = useState(true);
  const [vcsId, setVcsId] = useState("");
  const terraformVersionsApi = `${
    new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
  }/terraform/index.json`;
  const [current, setCurrent] = useState(0);
  const [step3Hidden, setStep3Hidden] = useState(true);
  const [step2Hidden, setStep2Hidden] = useState(true);
  const [sshKeysVisible, setSSHKeysVisible] = useState(false);
  const [versionControlFlow, setVersionControlFlow] = useState(true);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const history = useHistory();
  useEffect(() => {
    setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
    setLoading(true);
    axiosClient.get(terraformVersionsApi).then((resp) => {
      console.log(resp);
      const tfVersions = [];
      for (const version in resp.data.versions) {
        if (!version.includes("-")) tfVersions.push(version);
      }
      setTerraformVersions(tfVersions.sort(compareVersions).reverse());
      console.log(tfVersions);
    });
    loadSSHKeys();
    loadVCS();
  }, [terraformVersionsApi]);
  const handleClick = (e) => {
    setCurrent(1);
    setVersionControlFlow(true);
    form.setFieldsValue({ source: "", branch: "" });
  };

  const handleGitClick = (id) => {
    if (id === "git") {
      setSSHKeysVisible(true);
    } else {
      setSSHKeysVisible(false);
      setVcsId(id);
    }
    setCurrent(2);
    setStep2Hidden(false);
  };

  const handleVCSClick = (vcsType) => {
    history.push(
      `/organizations/${organizationId}/settings/vcs/new/${vcsType}`
    );
  };

  const handleConnectDifferent = () => {
    setVCSButtonsVisible(false);
  };

  const handleConnectExisting = () => {
    setVCSButtonsVisible(true);
  };

  const renderVCSLogo = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return <GitlabOutlined style={{ fontSize: "20px" }} />;
      case "BITBUCKET":
        return (
          <IconContext.Provider value={{ size: "20px" }}>
            <SiBitbucket />
            &nbsp;&nbsp;
          </IconContext.Provider>
        );
      case "AZURE_DEVOPS":
        return (
          <IconContext.Provider value={{ size: "20px" }}>
            <SiAzuredevops />
            &nbsp;&nbsp;
          </IconContext.Provider>
        );
      default:
        return <GithubOutlined style={{ fontSize: "20px" }} />;
    }
  };

  const loadVCS = () => {
    axiosInstance.get(`organization/${organizationId}/vcs`).then((response) => {
      console.log(response);
      setVCS(response.data);
      setLoading(false);
    });
  };

  const loadSSHKeys = () => {
    axiosInstance.get(`organization/${organizationId}/ssh`).then((response) => {
      console.log(response.data.data);
      setSSHKeys(response.data.data);
    });
  };

  const [form] = Form.useForm();
  const handleGitContinueClick = (e) => {
    setCurrent(3);
    setStep3Hidden(false);
    setStep2Hidden(true);
    var source = form.getFieldValue("source");

    if (source != null) {
      var nameValue = source.match("/([^/]+)/?$");
      if (nameValue != null && nameValue.length > 0) {
        form.setFieldsValue({ name: nameValue[1].replace(".git", "") });
      }
    }
  };

  const handleCliDriven = (e) => {
    setVersionControlFlow(false);
    setCurrent(1);
    setStep3Hidden(false);
    setSSHKeysVisible(false);
    form.setFieldsValue({ source: "empty", branch: "remote-content" });
  };

  const onFinishFailed = (values, errorFields) => {
    console.log(values);
    console.log(errorFields);
  };

  const onFinish = (values) => {
    let body = {
      data: {
        type: "workspace",
        attributes: {
          source: values.source,
          folder: values.folder,
          name: values.name,
          terraformVersion: values.terraformVersion,
          branch: values.branch,
        },
      },
    };

    if (vcsId !== "") {
      body = {
        data: {
          type: "workspace",
          attributes: {
            source: values.source,
            folder: values.folder,
            name: values.name,
            terraformVersion: values.terraformVersion,
            branch: values.branch,
          },
          relationships: {
            vcs: {
              data: {
                type: "vcs",
                id: vcsId,
              },
            },
          },
        },
      };
    }
    if (values.sshKey) {
      body = {
        data: {
          type: "workspace",
          attributes: {
            source: values.source,
            folder: values.folder,
            name: values.name,
            terraformVersion: values.terraformVersion,
            branch: values.branch,
          },
          relationships: {
            ssh: {
              data: {
                type: "ssh",
                id: values.sshKey,
              },
            },
          },
        },
      };
    }

    axiosInstance
      .post(`organization/${organizationId}/workspace`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response.status);
        if (response.status === 201) {
          console.log("/workspaces/" + response.data.data.id);
          history.push("/workspaces/" + response.data.data.id);
        }

      }).catch(error => {
         if (error.response) {
          if(error.response.status === 403){
          message.error(<span>You are not authorized to create workspaces. <br/> Please contact your administrator and request the <b>Manage Workspaces</b> permission. <br/> For more information, visit the <a target="_blank" href="https://docs.terrakube.org/user-guide/organizations/team-management">Terrakube documentation</a>.</span>);
          }
        }
      });
  };

  const handleChange = (currentVal) => {
    setCurrent(currentVal);
    if (currentVal === 2) {
      setStep2Hidden(false);
      setStep3Hidden(true);
    }

    if (currentVal === 3) {
      setStep3Hidden(false);
      setStep2Hidden(true);
    }

    if (currentVal === 1 || currentVal === 0) {
      setStep3Hidden(true);
      setStep2Hidden(true);
      setVersionControlFlow(true);
    }
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item>
          <Link to={`/organizations/${organizationId}/workspaces`}>
            Workspaces
          </Link>
        </Breadcrumb.Item>
        <Breadcrumb.Item>New Workspace</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createWorkspace">
          <h2>Create a new Workspace</h2>
          <div className="App-text">
            Workspaces determine how Terrakube organizes infrastructure. A
            workspace contains your Terraform configuration (infrastructure as
            code), shared variable values, your current and historical Terraform
            state, and run logs.
          </div>
          <Steps
            direction="horizontal"
            size="small"
            current={current}
            onChange={handleChange}
          >
            <Step title="Choose Type" />
            {versionControlFlow ? (
              <>
                <Step title="Connect to VCS" />
                <Step title="Choose a repository" />
              </>
            ) : (
              ""
            )}
            <Step title="Configure Settings" />
          </Steps>
          {current === 0 && (
            <Space className="chooseType" direction="vertical">
              <h3>Choose your workflow </h3>
              <Card hoverable onClick={handleClick}>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiBookBookmark />
                </IconContext.Provider>
                <span className="workflowType">Version control workflow</span>
                <div className="workflowDescription App-text">
                  Store your Terraform configuration in a git repository, and
                  trigger runs based on pull requests and merges.
                </div>
                <div className="workflowSelect"></div>
              </Card>
              <Card hoverable onClick={handleCliDriven}>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiTerminal />
                </IconContext.Provider>
                <span className="workflowType">CLI-driven workflow</span>
                <div className="workflowDescription App-text">
                  Trigger remote Terraform runs from your local command line.
                </div>
              </Card>
              <Card hoverable onClick={handleCliDriven}>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiUpload />
                </IconContext.Provider>
                <span className="workflowType">API-driven workflow</span>
                <div className="workflowDescription App-text">
                  A more advanced option. Integrate Terraform into a larger
                  pipeline using the Terraform API.
                </div>
              </Card>
            </Space>
          )}

          {current === 1 && versionControlFlow && (
            <Space className="chooseType" direction="vertical">
              <h3>Connect to a version control provider</h3>
              <div className="workflowDescription2 App-text">
                Choose the version control provider that hosts the Terraform
                configuration for this workspace.
              </div>

              {vcsButtonsVisible ? (
                <div>
                  <Space direction="horizontal">
                    <Button
                      icon={<SiGit />}
                      onClick={() => {
                        handleGitClick("git");
                      }}
                      size="large"
                    >
                      &nbsp;Git
                    </Button>
                    {loading || !vcs.data ? (
                      <p>Data loading...</p>
                    ) : (
                      vcs.data.map(function (item, i) {
                        return (
                          <Button
                            icon={renderVCSLogo(item.attributes.vcsType)}
                            onClick={() => {
                              handleGitClick(item.id);
                            }}
                            size="large"
                          >
                            &nbsp;{item.attributes.name}
                          </Button>
                        );
                      })
                    )}
                  </Space>{" "}
                  <br />
                  <Button
                    onClick={handleConnectDifferent}
                    className="link"
                    type="link"
                  >
                    Connect to a different VCS
                  </Button>
                </div>
              ) : (
                <div>
                  <Space direction="horizontal">
                    <Button
                      icon={<GithubOutlined />}
                      onClick={() => {
                        handleVCSClick("GITHUB");
                      }}
                      size="large"
                    >
                      Github
                    </Button>
                    <Button
                      icon={<GitlabOutlined />}
                      onClick={() => {
                        handleVCSClick("GITLAB");
                      }}
                      size="large"
                    >
                      Gitlab
                    </Button>
                    <Button
                      icon={<SiBitbucket />}
                      onClick={() => {
                        handleVCSClick("BITBUCKET");
                      }}
                      size="large"
                    >
                      &nbsp;&nbsp;Bitbucket
                    </Button>
                    <Button
                      icon={<SiAzuredevops />}
                      onClick={() => {
                        handleVCSClick("AZURE_DEVOPS");
                      }}
                      size="large"
                    >
                      &nbsp;&nbsp;Azure Devops
                    </Button>
                  </Space>
                  <br />
                  <Button
                    onClick={handleConnectExisting}
                    className="link"
                    type="link"
                  >
                    Use an existing VCS connection
                  </Button>
                </div>
              )}
            </Space>
          )}

          <Form
            form={form}
            name="create-workspace"
            layout="vertical"
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            validateMessages={validateMessages}
            initialValues={{ folder: "/" }}
          >
            <Space
              hidden={step2Hidden}
              className="chooseType"
              direction="vertical"
            >
              <h3>Choose a repository</h3>
              <div className="workflowDescription2 App-text">
                Choose the repository that hosts your Terraform source code.
              </div>
              <Form.Item
                name="source"
                label="Git repo"
                tooltip="e.g. https://github.com/Terrakube/terraform-sample-repository.git or git@github.com:AzBuilder/terraform-azurerm-webapp-sample.git"
                extra=" Git repo must be a valid git url using either https or ssh protocol."
                rules={[
                  {
                    required: true,
                    pattern:
                      "(empty)|(((git|ssh|http(s)?)|(git@[\\w\\.]+))(:(//)?)([\\w\\.@\\:/\\-~]+)(\\.git)?(/)?)",
                  },
                ]}
              >
                <Input />
              </Form.Item>
              <Form.Item>
                <Button onClick={handleGitContinueClick} type="primary">
                  Continue
                </Button>
              </Form.Item>
            </Space>

            <Space
              hidden={step3Hidden}
              className="chooseType"
              direction="vertical"
            >
              <h3>Configure settings</h3>
              <Form.Item
                name="name"
                label="Workspace Name"
                rules={[{ required: true }]}
                extra="The name of your workspace is unique and used in tools, routing, and UI. Dashes, underscores, and alphanumeric characters are permitted."
              >
                <Input />
              </Form.Item>

              <Form.Item
                name="branch"
                label="VCS branch"
                placeholder="(default branch)"
                extra=" The branch from which to import new versions. This defaults to the value your version control provides as the default branch for this repository."
                rules={[{ required: true }]}
                hidden={!versionControlFlow}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="folder"
                label="Terraform Working Directory"
                placeholder="/"
                extra=" Default workspace directory. Use / for the root folder"
                rules={[{ required: true }]}
                hidden={!versionControlFlow}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="terraformVersion"
                label="Terraform Version"
                rules={[{ required: true }]}
                extra="The version of Terraform to use for this workspace. It will not upgrade automatically."
              >
                <Select placeholder="select version" style={{ width: 250 }}>
                  {terraformVersions.map(function (name, index) {
                    return <Option key={name}>{name}</Option>;
                  })}
                </Select>
              </Form.Item>
              <Form.Item
                hidden={!sshKeysVisible}
                name="sshKey"
                label="SSH Key"
                tooltip="Select an SSH Key that will be used to clone this repo."
                extra="To use the SSH support in modules the source should be used like git@github.com:AzBuilder/terrakube-docker-compose.git"
                rules={[{ required: false }]}
              >
                <Select placeholder="select SSH Key" style={{ width: 250 }}>
                  {sshKeys.map(function (sshKey, index) {
                    return (
                      <Option key={sshKey?.id}>
                        {sshKey?.attributes?.name}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  Create Workspace
                </Button>
              </Form.Item>
            </Space>
          </Form>
        </div>
      </div>
    </Content>
  );
};
