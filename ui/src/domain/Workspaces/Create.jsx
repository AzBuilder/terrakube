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
  Dropdown,
  List,
} from "antd";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import { BiTerminal, BiBookBookmark, BiUpload } from "react-icons/bi";
import { compareVersions } from "./Workspaces";
import { IconContext } from "react-icons";
import {
  GithubOutlined,
  GitlabOutlined,
  DownOutlined,
} from "@ant-design/icons";
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
  const [orgTemplates, setOrgTemplates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [vcsButtonsVisible, setVCSButtonsVisible] = useState(true);
  const [vcsId, setVcsId] = useState("");
  const [current, setCurrent] = useState(0);
  const [step3Hidden, setStep4Hidden] = useState(true);
  const [step2Hidden, setStep3Hidden] = useState(true);
  const [sshKeysVisible, setSSHKeysVisible] = useState(false);
  const [versionControlFlow, setVersionControlFlow] = useState(true);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [iacTypes, setIacTypes] = useState([]);
  const [iacType, setIacType] = useState({
    id: "terraform",
    name: "Terraform",
  });
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
        handleVCSClick("GITLAB_COMMUNITY");
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
  const history = useHistory();
  useEffect(() => {
    setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
    setLoading(true);
    loadVersions(iacType);
    loadSSHKeys();
    loadOrgTemplates();
    loadVCS();
    getIacTypes();
  }, []);
  const handleClick = (e) => {
    setCurrent(2);
    setVersionControlFlow(true);
    form.setFieldsValue({ source: "", branch: "" });
  };

  const handleIacTypeClick = (iacType) => {
    setCurrent(1);
    setIacType(iacType);
    loadVersions(iacType);
  };

  const handleGitClick = (id) => {
    if (id === "git") {
      setSSHKeysVisible(true);
    } else {
      setSSHKeysVisible(false);
      setVcsId(id);
    }
    setCurrent(3);
    setStep3Hidden(false);
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
            &nbsp;
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

  const loadOrgTemplates = () => {
    axiosInstance
      .get(`organization/${organizationId}/template`)
      .then((response) => {
        console.log(response.data.data);
        setOrgTemplates(response.data.data);
      });
  };

  const [form] = Form.useForm();
  const handleGitContinueClick = (e) => {
    setCurrent(3);
    setStep4Hidden(false);
    setStep3Hidden(true);
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
    setCurrent(2);
    setStep4Hidden(false);
    setSSHKeysVisible(false);
    form.setFieldsValue({ source: "empty", branch: "remote-content" });
  };

  const onFinishFailed = (values, errorFields) => {
    console.log(values);
    console.log(errorFields);
  };

  const loadVersions = (iacType) => {
    const versionsApi = `${
      new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
    }/${iacType.id}/index.json`;
    axiosInstance.get(versionsApi).then((resp) => {
      console.log(resp);
      const tfVersions = [];
      if (iacType.id === "tofu") {
        resp.data.forEach((release) => {
          if (!release.tag_name.includes("-"))
            tfVersions.push(release.tag_name.replace("v", ""));
        });
      } else {
        for (const version in resp.data.versions) {
          if (!version.includes("-")) tfVersions.push(version);
        }
      }
      setTerraformVersions(tfVersions.sort(compareVersions).reverse());
      console.log(tfVersions);
    });
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
          iacType: iacType.id,
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
            iacType: iacType.id,
            defaultTemplate: values.defaultTemplate,
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
            iacType: iacType.id,
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
      })
      .catch((error) => {
        if (error.response) {
          if (error.response.status === 403) {
            message.error(
              <span>
                You are not authorized to create workspaces. <br /> Please
                contact your administrator and request the{" "}
                <b>Manage Workspaces</b> permission. <br /> For more
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

  const handleChange = (currentVal) => {
    setCurrent(currentVal);
    if (currentVal === 3) {
      setStep3Hidden(false);
      setStep4Hidden(true);
    }

    if (currentVal === 4) {
      setStep4Hidden(false);
      setStep3Hidden(true);
    }

    if (currentVal === 2 || currentVal === 1 || currentVal === 0) {
      setStep4Hidden(true);
      setStep3Hidden(true);
      setVersionControlFlow(true);
    }
  };

  const getIacTypes = () => {
    let iacTypes = [
      {
        id: "terraform",
        name: "Terraform",
        description:
          "Create an empty template. So you can define your template from scratch.",
        icon: "/providers/terraform.svg",
      },
      { id: "tofu", name: "OpenTofu", icon: "/providers/opentofu.png" },
    ];

    setIacTypes(iacTypes);
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: organizationName,
          },
          {
            title: (
              <Link to={`/organizations/${organizationId}/workspaces`}>
                Workspaces
              </Link>
            ),
          },
          {
            title: "New Workspace",
          },
        ]}
      />

      <div className="site-layout-content">
        <div className="createWorkspace">
          <h2>Create a new Workspace</h2>
          <div className="App-text">
            Workspaces determine how Terrakube organizes infrastructure. A
            workspace contains your configuration (infrastructure as code),
            shared variable values, your current and historical state, and run
            logs.
          </div>
          <Steps
            direction="horizontal"
            size="small"
            current={current}
            onChange={handleChange}
          >
            <Step title="Choose IaC Type" />
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
          {current == 0 && (
            <Space className="chooseType" direction="vertical">
              <h3>Choose your IaC type </h3>
              <List
                grid={{ gutter: 5, column: 5 }}
                dataSource={iacTypes}
                renderItem={(item) => (
                  <List.Item>
                    <Card
                      style={{ width: "150px", textAlign: "center" }}
                      hoverable
                      onClick={() => handleIacTypeClick(item)}
                    >
                      <Space direction="vertical">
                        <img
                          style={{
                            padding: "10px",
                            backgroundColor: item.color,
                            width: "60px",
                          }}
                          alt="example"
                          src={item.icon}
                        />
                        <span style={{ fontWeight: "bold" }}>{item.name}</span>
                      </Space>
                    </Card>
                  </List.Item>
                )}
              />
            </Space>
          )}

          {current === 1 && (
            <Space className="chooseType" direction="vertical">
              <h3>Choose your workflow </h3>
              <Card hoverable onClick={handleClick}>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiBookBookmark />
                </IconContext.Provider>
                <span className="workflowType">Version control workflow</span>
                <div className="workflowDescription App-text">
                  Store your {iacType?.name} configuration in a git repository,
                  and trigger runs based on pull requests and merges.
                </div>
                <div className="workflowSelect"></div>
              </Card>
              <Card hoverable onClick={handleCliDriven}>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiTerminal />
                </IconContext.Provider>
                <span className="workflowType">CLI-driven workflow</span>
                <div className="workflowDescription App-text">
                  Trigger remote {iacType?.name} runs from your local command
                  line.
                </div>
              </Card>
              <Card hoverable onClick={handleCliDriven}>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiUpload />
                </IconContext.Provider>
                <span className="workflowType">API-driven workflow</span>
                <div className="workflowDescription App-text">
                  A more advanced option. Integrate {iacType?.name} into a
                  larger pipeline using the {iacType?.name} API.
                </div>
              </Card>
            </Space>
          )}

          {current === 2 && versionControlFlow && (
            <Space className="chooseType" direction="vertical">
              <h3>Connect to a version control provider</h3>
              <div className="workflowDescription2 App-text">
                Choose the version control provider that hosts the{" "}
                {iacType?.name}&nbsp; configuration for this workspace.
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
                Choose the repository that hosts your {iacType?.name} source
                code.
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
                rules={[
                  { required: true },
                  {
                    pattern: /^[A-Za-z0-9_-]+$/,
                    message:
                      "Only dashes, underscores, and alphanumeric characters are permitted.",
                  },
                ]}
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
                label={iacType?.name + " Working Directory"}
                placeholder="/"
                extra=" Default workspace directory. Use / for the root folder"
                rules={[{ required: true }]}
                hidden={!versionControlFlow}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="terraformVersion"
                label={iacType?.name + " Version"}
                rules={[{ required: true }]}
                extra={
                  "The version of " +
                  iacType?.name +
                  " to use for this workspace. It will not upgrade automatically."
                }
              >
                <Select placeholder="select version" style={{ width: 250 }}>
                  {terraformVersions.map(function (name, index) {
                    return <Option key={name}>{name}</Option>;
                  })}
                </Select>
              </Form.Item>
              <Form.Item
                name="defaultTemplate"
                label="Default template (VCS Push)"
                tooltip="Template that will be executed by default when doing a git push to the repository."
                rules={[{ required: false }]}
                hidden={!versionControlFlow}
              >
                <Select placeholder="Select Template" style={{ width: 250 }}>
                  {orgTemplates.map(function (template, index) {
                    return (
                      <Option key={template?.id}>
                        {template?.attributes?.name}
                      </Option>
                    );
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
