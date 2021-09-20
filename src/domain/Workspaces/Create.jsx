import { React, useState,useEffect } from 'react';
import { Form, Input, Button, Breadcrumb, Layout, Steps, Card, Space ,Select} from "antd";
import { ORGANIZATION_ARCHIVE,ORGANIZATION_NAME } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import { BiTerminal, BiBookBookmark, BiUpload } from "react-icons/bi";
import { IconContext } from "react-icons";
import { GithubOutlined, GitlabOutlined } from '@ant-design/icons';
import { SiGit } from "react-icons/si";
import { useHistory,Link } from "react-router-dom";
const { Content } = Layout;
const { Step } = Steps;
const validateMessages = {
  required: '${label} is required!',
  types: {
    url: '${label} is not a valid git url',
  }
}
const { Option } = Select;



export const CreateWorkspace = () => {
  const [organizationName, setOrganizationName] = useState([]);
  useEffect(() => {
    setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
  });
  const handleClick = e => {
    setCurrent(1);
  };

  const handleGitClick = e => {
    setCurrent(2);
    setStep2Hidden(false);
  };
  const [form] = Form.useForm();
  const handleGitContinueClick = e => {
    setCurrent(3);
    setStep3Hidden(false);
    setStep2Hidden(true);
    var source = form.getFieldValue("source");
    var nameValue = source.match('\/([^\/]+)\/?$');
    if(nameValue!=null && nameValue.length > 0){
      form.setFieldsValue({ name:nameValue[1].replace(".git","")});
    }
  };
  const [current, setCurrent] = useState(0);
  const [step3Hidden, setStep3Hidden] = useState(true);
  const [step2Hidden, setStep2Hidden] = useState(true);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const history = useHistory();
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
        if(response.status =="201")
        {
          history.push('/workspaces/' + response.data.data.id);
        }
      })
  };

  

  const handleChange = currentVal => {
    setCurrent(currentVal);
    if (currentVal == 2){
      setStep2Hidden(false);
      setStep3Hidden(true);
    }

    if (currentVal == 3){
      setStep3Hidden(false);
      setStep2Hidden(true);
    }

  };


  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item><Link to={`/organizations/${organizationId}/workspaces`}>Workspaces</Link></Breadcrumb.Item>
        <Breadcrumb.Item>New Workspace</Breadcrumb.Item>
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

         <Form form={form} name="create-workspace" layout="vertical" onFinish={onFinish} validateMessages={validateMessages}> 
     
            <Space hidden={step2Hidden} className="chooseType" direction="vertical">
              <h3>Choose a repository</h3>
              <div className="workflowDescription2 App-text">
                Choose the repository that hosts your Terraform source code.
              </div>
              <Form.Item name="source" label="Git repo" tooltip="e.g. https://github.com/AzBuilder/terraform-sample-repository.git" extra=" Git repo must be a valid git url using either https or ssh protocol." rules={[{ required: true },{ type: 'url'}]}>
                <Input />
              </Form.Item>
              <Form.Item>
                <Button onClick={handleGitContinueClick} type="primary">
                  Continue
                </Button>
              </Form.Item>
              
            </Space>
        


          
           <Space hidden={step3Hidden} className="chooseType" direction="vertical">
              <h3>Configure settings</h3>
              <Form.Item name="name" label="Workspace Name" rules={[{ required: true }]} extra="The name of your workspace is unique and used in tools, routing, and UI. Dashes, underscores, and alphanumeric characters are permitted.">
                <Input />
              </Form.Item>
             
              <Form.Item name="branch" label="VCS branch" placeholder="(default branch)" extra=" The branch from which to import new versions. This defaults to the value your version control provides as the default branch for this repository." rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="terraformVersion" label="Terraform Version" rules={[{ required: true }]} extra="The version of Terraform to use for this workspace. It will not upgrade automatically.">
              <Select placeholder="select version"  style={{ width: 250 }} >
                          <Option value="0.13.0">0.13.0</Option>
                          <Option value="0.14.0">0.14.0</Option>
                          <Option value="0.14.1">0.14.1</Option>
                          <Option value="0.14.2">0.14.2</Option>
                          <Option value="0.14.3">0.14.3</Option>
                          <Option value="0.15.0">0.15.0</Option>
                          <Option value="0.15.1">0.15.1</Option>
                          <Option value="0.15.2">0.15.2</Option>
                          <Option value="0.15.3">0.15.3</Option>
                          <Option value="1.0.0">1.0.0</Option>
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
  )
}