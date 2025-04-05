import { DownOutlined, GithubOutlined, GitlabOutlined } from "@ant-design/icons";
import { Breadcrumb, Button, Card, Dropdown, Form, Input, Layout, List, Select, Space, Steps, message } from "antd";
import { useEffect, useState } from "react";
import { IconContext } from "react-icons";
import { BiBookBookmark } from "react-icons/bi";
import { SiBitbucket, SiTerraform } from "react-icons/si";
import { RiAliensLine } from "react-icons/ri";
import { VscAzureDevops } from "react-icons/vsc";
import { Link, useNavigate } from "react-router-dom";
import { ORGANIZATION_ARCHIVE, ORGANIZATION_NAME } from "../../config/actionTypes";
import { VcsModel, VcsType, VcsTypeExtended } from "../types";
import { CreateStackForm, ToolType } from "@/modules/stacks/types";
import PageWrapper from "@/modules/layout/PageWrapper/PageWrapper";
import useApiRequest from "@/modules/api/useApiRequest";
import { apiGet, apiPost } from "@/modules/api/apiWrapper";

const { Content } = Layout;
const { Step } = Steps;
const { Option } = Select;

const validateMessages = {
  required: "${label} is required!",
  types: {
    url: "${label} is not a valid git url",
  },
};

export function CreateStack() {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [current, setCurrent] = useState(0);
  const [vcs, setVCS] = useState<VcsModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [vcsButtonsVisible, setVCSButtonsVisible] = useState(true);
  const [vcsId, setVcsId] = useState("");
  const [organizationName, setOrganizationName] = useState<string>(
    sessionStorage.getItem(ORGANIZATION_NAME) || ""
  );
  const organizationId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);

  const [toolTypes] = useState<ToolType[]>([
    {
      id: "terraform",
      name: "Terraform Stacks",
      icon: <SiTerraform style={{ fontSize: '32px', color: '#000000' }} />,
    },
    {
      id: "atmos",
      name: "Atmos",
      icon: <RiAliensLine style={{ fontSize: '32px', color: '#4A4A4A' }} />,
    },
  ]);

  const [selectedToolType, setSelectedToolType] = useState<ToolType>(toolTypes[0]);

  const gitlabItems = [
    {
      label: "Gitlab.com",
      key: "1",
      onClick: () => handleVCSClick(VcsTypeExtended.GITLAB),
    },
    {
      label: "Gitlab Community Edition",
      key: "2",
      onClick: () => handleVCSClick(VcsTypeExtended.GITLAB_COMMUNITY),
    },
    {
      label: "Gitlab Enterprise Edition",
      key: "3",
      onClick: () => handleVCSClick(VcsTypeExtended.GITLAB_ENTERPRISE),
    },
  ];

  const githubItems = [
    {
      label: "Github.com",
      key: "1",
      onClick: () => handleVCSClick(VcsTypeExtended.GITHUB),
    },
    {
      label: "Github Enterprise",
      key: "2",
      onClick: () => handleVCSClick(VcsTypeExtended.GITHUB_ENTERPRISE),
    },
  ];

  const bitBucketItems = [
    {
      label: "Bitbucket Cloud",
      key: "1",
      onClick: () => handleVCSClick(VcsTypeExtended.BITBUCKET),
    },
  ];

  const azDevOpsItems = [
    {
      label: "Azure DevOps Services",
      key: "1",
      onClick: () => handleVCSClick(VcsTypeExtended.AZURE_DEVOPS),
    },
  ];

  useEffect(() => {
    loadVCS();
  }, []);

  const loadVCS = async () => {
    setLoading(true);
    try {
      const response = await apiGet<{ data: VcsModel[] }>(`/api/v1/organization/${organizationId}/vcs`, {
        dataWrapped: true,
      });
      if (!response.isError && response.data) {
        setVCS(response.data.data);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleToolTypeClick = (toolType: ToolType) => {
    setSelectedToolType(toolType);
    setCurrent(1);
  };

  const handleVCSClick = (vcsType: VcsTypeExtended) => {
    navigate(`/organizations/${organizationId}/settings/vcs/new/${vcsType}`);
  };

  const handleGitClick = (id: string) => {
    setVcsId(id);
    setCurrent(2);
  };

  const handleConnectDifferent = () => {
    setVCSButtonsVisible(false);
  };

  const handleConnectExisting = () => {
    setVCSButtonsVisible(true);
  };

  const renderVCSLogo = (vcsType: VcsType) => {
    switch (vcsType) {
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
            <VscAzureDevops />
            &nbsp;
          </IconContext.Provider>
        );
      default:
        return <GithubOutlined style={{ fontSize: "20px" }} />;
    }
  };

  const onFinish = async (values: CreateStackForm) => {
    setLoading(true);
    try {
      const stackData = {
        ...values,
        toolType: selectedToolType.id,
        vcsId,
      };

      const response = await apiPost<{ data: CreateStackForm }, any>(`/api/v1/organization/${organizationId}/stacks`, {
        data: stackData,
      }, {
        dataWrapped: true,
        contentType: "application/vnd.api+json",
      });

      if (!response.isError) {
        message.success("Stack created successfully");
        navigate(`/organizations/${organizationId}/stacks`);
      } else {
        message.error("Failed to create stack");
      }
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    {
      title: "Select Tool",
      content: (
        <div style={{ maxWidth: 600, margin: "0 auto" }}>
          <h3>Choose your IaC stack tool</h3>
          <p style={{ color: '#666', marginBottom: '24px' }}>Select the Infrastructure as Code tool you'll use to manage your infrastructure.</p>
          <List
            grid={{ gutter: 16, column: 2 }}
            dataSource={toolTypes}
            renderItem={(item) => (
              <List.Item>
                <Card
                  hoverable
                  onClick={() => handleToolTypeClick(item)}
                  style={{ textAlign: "center", cursor: "pointer", padding: "12px" }}
                  bodyStyle={{ padding: "12px" }}
                >
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    {typeof item.icon === 'string' ? (
                      <img 
                        src={item.icon} 
                        alt={item.name} 
                        style={{ width: 32, height: 32 }} 
                      />
                    ) : (
                      item.icon
                    )}
                    <h4 style={{ margin: 0 }}>{item.name}</h4>
                  </Space>
                </Card>
              </List.Item>
            )}
          />
        </div>
      ),
    },
    {
      title: "Connect to VCS",
      content: (
        <div style={{ maxWidth: 800, margin: "0 auto" }}>
          <h3>Connect to a version control provider</h3>
          <p style={{ color: '#666', marginBottom: '24px' }}>Choose the version control provider that hosts the {selectedToolType.name} configuration for this stack.</p>
          {vcsButtonsVisible ? (
            <>
              <List
                grid={{ gutter: 16, column: 2 }}
                dataSource={vcs}
                renderItem={(item) => (
                  <List.Item>
                    <Card 
                      hoverable 
                      onClick={() => handleGitClick(item.id)}
                      style={{ padding: '12px' }}
                      bodyStyle={{ padding: '12px' }}
                    >
                      <Space>
                        {renderVCSLogo(item.attributes.vcsType)}
                        <span>{item.attributes.name}</span>
                      </Space>
                    </Card>
                  </List.Item>
                )}
              />
              <div style={{ marginTop: 24, textAlign: "center" }}>
                <Button type="link" onClick={handleConnectDifferent}>
                  Connect to a different VCS provider
                </Button>
              </div>
            </>
          ) : (
            <div>
              <Space direction="horizontal" style={{ width: "100%", justifyContent: "center", gap: "16px" }}>
                <Dropdown menu={{ items: githubItems }}>
                  <Button style={{ width: '150px', height: '40px' }}>
                    <Space>
                      <GithubOutlined />
                      Github
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
                <Dropdown menu={{ items: gitlabItems }}>
                  <Button style={{ width: '150px', height: '40px' }}>
                    <Space>
                      <GitlabOutlined />
                      Gitlab
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
                <Dropdown menu={{ items: bitBucketItems }}>
                  <Button style={{ width: '150px', height: '40px' }}>
                    <Space>
                      <SiBitbucket />
                      Bitbucket
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
                <Dropdown menu={{ items: azDevOpsItems }}>
                  <Button style={{ width: '150px', height: '40px' }}>
                    <Space>
                      <VscAzureDevops />
                      Azure DevOps
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
              </Space>
              <div style={{ marginTop: 24, textAlign: "center" }}>
                <Button type="link" onClick={handleConnectExisting}>
                  Connect to an existing VCS provider
                </Button>
              </div>
            </div>
          )}
        </div>
      ),
    },
    {
      title: "Configure Stack",
      content: (
        <div style={{ maxWidth: 800, margin: "0 auto" }}>
          <h3>Configure settings</h3>
          <p style={{ color: '#666', marginBottom: '24px' }}>Configure the basic settings for your stack.</p>
          <Form
            form={form}
            layout="vertical"
            onFinish={onFinish}
            validateMessages={validateMessages}
            initialValues={{
              toolType: selectedToolType.id,
              vcsId,
            }}
          >
            <Form.Item
              name="repoUrl"
              label="Repository URL"
              rules={[{ required: true }, { type: "url" }]}
              extra={`The URL of the repository containing your ${selectedToolType.name} configuration.`}
            >
              <Input placeholder="https://github.com/organization/repository" />
            </Form.Item>
            <Form.Item
              name="defaultBranch"
              label="Branch"
              rules={[{ required: true }]}
              extra="The branch of the repository to use for this stack."
            >
              <Input placeholder="main" />
            </Form.Item>
            <Form.Item
              name="name"
              label="Stack Name"
              rules={[{ required: true }]}
              extra="The name of your stack is unique and used in tools, routing, and UI. Dashes, underscores, and alphanumeric characters are permitted."
            >
              <Input placeholder="my-stack" />
            </Form.Item>
            <Form.Item
              name="description"
              label="Description"
              extra="A description helps other users understand the purpose of this stack."
            >
              <Input.TextArea rows={4} placeholder="Describe your stack" />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading}>
                Create Stack
              </Button>
            </Form.Item>
          </Form>
        </div>
      ),
    },
  ];

  return (
    <PageWrapper
      title="Create Stack"
      subTitle="Stacks organize related deployments under a single configuration. Use stacks to manage infrastructure consistently across environments like dev, staging, and production."
      loadingText="Loading..."
      loading={loading}
      breadcrumbs={[
        { label: organizationName, path: "/" },
        { label: "Stacks", path: `/organizations/${organizationId}/stacks` },
        { label: "Create", path: `/organizations/${organizationId}/stacks/create` },
      ]}
    >
      <div style={{ padding: "24px 0" }}>
        <Steps 
          current={current} 
          items={steps.map(s => ({ title: s.title }))} 
          onChange={setCurrent}
        />
        <div style={{ marginTop: 24 }}>{steps[current].content}</div>
      </div>
    </PageWrapper>
  );
} 