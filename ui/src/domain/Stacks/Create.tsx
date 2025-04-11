import { DownOutlined, GithubOutlined, GitlabOutlined } from "@ant-design/icons";
import { Button, Card, Dropdown, Form, Input, Layout, List, Select, Space, Steps, message, Empty } from "antd";
import { useEffect, useState } from "react";
import { IconContext } from "react-icons";
import { SiBitbucket, SiTerraform } from "react-icons/si";
import { RiAliensLine } from "react-icons/ri";
import { VscAzureDevops } from "react-icons/vsc";
import { useNavigate } from "react-router-dom";
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
    if (organizationId) {
      loadVCS();
    }
  }, [organizationId]);

  const loadVCS = async () => {
    if (!organizationId) {
      console.error('Organization ID is missing');
      return;
    }

    setLoading(true);
    try {
      const response = await apiGet<{ data: VcsModel[] }>(`/api/v1/organization/${organizationId}/vcs`, {
        dataWrapped: true,
      });
      
      console.log('VCS API Response:', response);
      
      if (!response.isError && response.data) {
        console.log('Setting VCS data:', response.data.data);
        setVCS(response.data.data);
      } else {
        console.error('Failed to load VCS providers:', response);
        message.error('Failed to load VCS providers');
      }
    } catch (error) {
      console.error('Error loading VCS providers:', error);
      message.error('Failed to load VCS providers');
    } finally {
      setLoading(false);
    }
  };

  // Add debug log for vcs state changes
  useEffect(() => {
    console.log('Current VCS state:', vcs);
  }, [vcs]);

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
        return <GitlabOutlined style={{ fontSize: "24px" }} />;
      case "BITBUCKET":
        return (
          <IconContext.Provider value={{ size: "24px" }}>
            <SiBitbucket />
          </IconContext.Provider>
        );
      case "AZURE_DEVOPS":
        return (
          <IconContext.Provider value={{ size: "24px" }}>
            <VscAzureDevops />
          </IconContext.Provider>
        );
      default:
        return <GithubOutlined style={{ fontSize: "24px" }} />;
    }
  };

  // Add debug log in the render section
  const renderVCSList = () => {
    console.log('Rendering VCS list with data:', vcs);
    return (
      <List
        grid={{ gutter: 16, column: 2 }}
        dataSource={vcs}
        locale={{
          emptyText: (
            <div style={{ padding: '32px' }}>
              <Empty 
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                imageStyle={{ margin: '0 auto' }}
                description={false}
              />
              <div style={{ marginTop: '16px', textAlign: 'center' }}>
                <h4 style={{ margin: 0 }}>No VCS Providers Found</h4>
              </div>
            </div>
          )
        }}
        renderItem={(item) => {
          console.log('Rendering VCS item:', item);
          return (
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
          );
        }}
      />
    );
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
        <div style={{ width: '100%', maxWidth: '1200px', margin: "0 auto" }}>
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
                  style={{ padding: '12px' }}
                  bodyStyle={{ padding: '12px' }}
                >
                  <Space>
                    {item.icon}
                    <span>{item.name}</span>
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
        <div style={{ width: '100%', maxWidth: '1200px', margin: "0 auto" }}>
          <h3>Connect to a version control provider</h3>
          <p style={{ color: '#666', marginBottom: '24px' }}>Choose the version control provider that hosts the {selectedToolType.name} configuration for this stack.</p>
          {vcsButtonsVisible ? (
            <>
              {renderVCSList()}
              <div style={{ marginTop: 24 }}>
                <Button type="link" onClick={handleConnectDifferent} style={{ paddingLeft: 0 }}>
                  Connect to a different VCS provider
                </Button>
              </div>
            </>
          ) : (
            <div>
              <Space size="middle" style={{ display: 'flex', flexWrap: 'wrap', gap: '12px' }}>
                <Dropdown menu={{ items: githubItems }}>
                  <Button style={{ width: '200px', height: '48px', display: 'flex', alignItems: 'center' }}>
                    <Space style={{ flex: 1, justifyContent: 'space-between' }}>
                      <Space>
                        <GithubOutlined style={{ fontSize: '24px' }} />
                        Github
                      </Space>
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
                <Dropdown menu={{ items: gitlabItems }}>
                  <Button style={{ width: '200px', height: '48px', display: 'flex', alignItems: 'center' }}>
                    <Space style={{ flex: 1, justifyContent: 'space-between' }}>
                      <Space>
                        <GitlabOutlined style={{ fontSize: '24px' }} />
                        Gitlab
                      </Space>
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
                <Dropdown menu={{ items: bitBucketItems }}>
                  <Button style={{ width: '200px', height: '48px', display: 'flex', alignItems: 'center' }}>
                    <Space style={{ flex: 1, justifyContent: 'space-between' }}>
                      <Space>
                        <SiBitbucket style={{ fontSize: '24px' }} />
                        Bitbucket
                      </Space>
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
                <Dropdown menu={{ items: azDevOpsItems }}>
                  <Button style={{ width: '200px', height: '48px', display: 'flex', alignItems: 'center' }}>
                    <Space style={{ flex: 1, justifyContent: 'space-between' }}>
                      <Space>
                        <VscAzureDevops style={{ fontSize: '24px' }} />
                        Azure DevOps
                      </Space>
                      <DownOutlined />
                    </Space>
                  </Button>
                </Dropdown>
              </Space>
              <div style={{ marginTop: 24 }}>
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
        <div style={{ width: '100%', maxWidth: '1200px', margin: "0 auto" }}>
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
            style={{ width: '100%' }}
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
      fluid
    >
      <div style={{ width: '100%', padding: "24px 0" }}>
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