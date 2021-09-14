import { React, useState } from 'react';
import { Form, Input, Button, Breadcrumb, Layout, Steps, Card, Space } from "antd";
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import { BiTerminal, BiBookBookmark, BiUpload } from "react-icons/bi";
import { IconContext } from "react-icons";
import { GithubOutlined, GitlabOutlined } from '@ant-design/icons';
import { SiGit } from "react-icons/si";
const { Content } = Layout;
const { Step } = Steps;
const validateMessages = {
  required: '${label} is required!'
}




export const CreateWorkspace = () => {
  const handleClick = e => {
    setCurrent(1);
  };

  const handleGitClick = e => {
    setCurrent(2);
  };
  const [current, setCurrent] = useState(0);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);

  const onFinish = (values) => {
    const body = {
      data: {
        type: "workspace",
        attributes: values
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/workspace`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
      })
  };

  const handleChange = current => {
    setCurrent(current);

  };


  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>organization-name</Breadcrumb.Item>
        <Breadcrumb.Item>Workspaces</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createWorkspace">
          <h2>Create a new Workspace</h2>
          <div className="App-text">
            Workspaces determine how Terraform Cloud organizes infrastructure. A workspace contains your Terraform configuration (infrastructure as code), shared variable values, your current and historical Terraform state, and run logs.
          </div>
          <Steps direction="horizontal" size="small" current={current} onChange={handleChange}>
            <Step title="Choose Type" />
            <Step title="Connect to VCS" />
            <Step title="Choose a repository" />
            <Step title="Configure Settings" />
          </Steps>
          {current == 0 && (
            <Space className="chooseType" direction="vertical">
              <h3>Choose your workflow </h3>
              <Card hoverable onClick={handleClick}>

                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiBookBookmark />
                </IconContext.Provider>
                <span className="workflowType">Version control workflow</span>
                <div className="workflowDescription App-text">
                  Store your Terraform configuration in a git repository, and trigger runs based on pull requests and merges.
                </div>
                <div className="workflowSelect">
                </div>


              </Card>
              <Card hoverable>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiTerminal />
                </IconContext.Provider>
                <span className="workflowType">CLI-driven workflow</span>
                <div className="workflowDescription App-text">
                  Trigger remote Terraform runs from your local command line.
                </div>
              </Card>
              <Card hoverable>
                <IconContext.Provider value={{ size: "1.3em" }}>
                  <BiUpload />
                </IconContext.Provider>
                <span className="workflowType">API-driven workflow</span>
                <div className="workflowDescription App-text">
                  A more advanced option. Integrate Terraform into a larger pipeline using the Terraform API.
                </div>
              </Card>
            </Space>
          )}


          {current == 1 && (
            <Space className="chooseType" direction="vertical">
              <h3>Connect to a version control provider</h3>
              <div className="workflowDescription2 App-text">
                Choose the version control provider that hosts the Terraform configuration for this workspace.
              </div>
              <Space direction="horizontal">
                <Button icon={<SiGit />} onClick={handleGitClick} size="large">&nbsp;Git</Button>
                <Button icon={<GithubOutlined />} size="large" >Github</Button>
                <Button icon={<GitlabOutlined />} size="large" >Gitlab</Button>
              </Space>
            </Space>

          )}

          {current == 2 && (
            <Space className="chooseType" direction="vertical">
              <h3>Choose a repository</h3>
              <div className="workflowDescription2 App-text">
                Choose the repository that hosts your Terraform source code.
              </div>
            </Space>
          )}


          {current == 3 && (
            <Form name="create-workspace" onFinish={onFinish} validateMessages={validateMessages}>
              <Form.Item name="name" label="Name" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="source" label="Source" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="branch" label="Branch" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="terraformVersion" label="Terraform Version" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  Submit
                </Button>
              </Form.Item>
            </Form>
          )}
        </div>
      </div>
    </Content>
  )
}