import { React, useState } from 'react';
import { Form, Input, Button, Breadcrumb, Layout, Steps, Space ,Select} from "antd";
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
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



export const CreateModule = () => {
 
 const handleGitClick = e => {
    setCurrent(1);
    setStep2Hidden(false);
  };

  const handleGitContinueClick = e => {
    setCurrent(2);
    setStep3Hidden(false);
    setStep2Hidden(true);
    var source = form.getFieldValue("source");
    var providerValue = source.match('terraform-(.*)-');
    if(providerValue!=null && providerValue.length > 0){
      form.setFieldsValue({ provider:providerValue[1]});
      var nameValue = source.match(providerValue[1] + '-(.*).git');
      if(nameValue!=null && nameValue.length > 0){
        form.setFieldsValue({ name:nameValue[1]});
      }
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
        type: "module",
        attributes: { 
          name: values.name,
          description: values.description,
          provider: values.provider,
          source: values.source,
          sourceSample: values.source
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${organizationId}/module`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        if(response.status =="201")
        {
          history.push(`/organizations/${organizationId}/registry/${response.data.data.id}`);
        }
      })
  };
  const [form] = Form.useForm();

  const handleChange = currentVal => {
    setCurrent(currentVal);
    if (currentVal == 1){
      setStep2Hidden(false);
      setStep3Hidden(true);
    }

    if (currentVal == 2){
      setStep3Hidden(false);
      setStep2Hidden(true);
    }

  };


  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>organization-name</Breadcrumb.Item>
        <Breadcrumb.Item><Link to={`/organizations/${organizationId}/registry`}>Modules</Link></Breadcrumb.Item>
        <Breadcrumb.Item>New Module</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createWorkspace">
          <h2>Add Module</h2>
          <div className="App-text">
            This module will be created under the current organization, devops-mindset. 
          </div>
          <Steps direction="horizontal" size="small" current={current} onChange={handleChange}>
            <Step title="Connect to VCS" />
            <Step title="Choose a repository" />
            <Step title="Confirm selection" />
          </Steps>
          


          {current == 0 && (
            <Space className="chooseType" direction="vertical">
              <h3>Connect to a version control provider</h3>
              <div className="workflowDescription2 App-text">
              Choose the version control provider that hosts your module source code.
              </div>
              <Space direction="horizontal">
                <Button icon={<SiGit />} onClick={handleGitClick} size="large">&nbsp;Git</Button>
                <Button icon={<GithubOutlined />} size="large" >Github</Button>
                <Button icon={<GitlabOutlined />} size="large" >Gitlab</Button>
              </Space>
            </Space>

          )}

         <Form  form={form} name="create-module" layout="vertical" onFinish={onFinish} validateMessages={validateMessages}> 
     
            <Space hidden={step2Hidden} className="chooseType" direction="vertical">
              <h3>Choose a repository</h3>
              <div className="workflowDescription2 App-text">
              Choose the repository that hosts your module source code. The format of your repository name should be <b>{"terraform-<PROVIDER>-<NAME>"}</b>.
              </div>
              <Form.Item name="source" label="Git repo" tooltip="e.g. https://github.com/Terrakube/terraform-sample-repository.git" extra=" Git repo must be a valid git url using either https or ssh protocol." rules={[{ required: true },{ type: 'url'}]}>
                <Input />
              </Form.Item>
              <Form.Item>
                <Button onClick={handleGitContinueClick} type="primary">
                  Continue
                </Button>
              </Form.Item>
              
            </Space>
        


          
           <Space hidden={step3Hidden} className="chooseType" direction="vertical">
              <h3>Confirm selection</h3>
              <Form.Item name="name" label="Module Name" rules={[{ required: true }]} extra="The name of your module generally names the abstraction that the module is intending to create.">
                <Input />
              </Form.Item>
             
              <Form.Item name="description" label="Module Description" placeholder="(description)" rules={[{ required: true }]}>
                 <Input.TextArea />
              </Form.Item>
              <Form.Item name="provider" tooltip="e.g. azurerm,aws,google"  label="Provider" rules={[{ required: true }]} extra="The name of a remote system that the module is primarily written to target.">
                  <Input />
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  Publish Module
                </Button>
              </Form.Item>
              </Space>
            
        
          </Form>
        </div>
      </div>
    </Content>
  )
}