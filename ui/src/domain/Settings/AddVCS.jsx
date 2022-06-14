import { React, useState } from "react";
import './Settings.css';
import { Steps, Space, Button,Form ,Input,Row,Col} from "antd";
import { GithubOutlined, GitlabOutlined } from '@ant-design/icons';
import { SiBitbucket, SiAzuredevops } from "react-icons/si";
import { HiOutlineExternalLink } from "react-icons/hi";
import axiosInstance from "../../config/axiosConfig";
import {useParams} from "react-router-dom";
import { ORGANIZATION_NAME } from '../../config/actionTypes';
const { Step } = Steps;
const validateMessages = {
  required: '${label} is required!'
}
export const AddVCS = ({ setMode ,loadVCS}) => {
  const { orgid,vcsName } = useParams();
  const [current, setCurrent] = useState(vcsName ? 1:0);
  const [vcsType, setVcsType] = useState(vcsName? vcsName:"GITHUB");
  const handleChange = currentVal => {
    setCurrent(currentVal);
  };
  const handleClick = (vcs) => {
    setCurrent(1);
    setVcsType(vcs);
  };
  const renderVCSType = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return "GitLab";
      case 'BITBUCKET':
        return "BitBucket";
      case 'AZURE_DEVOPS':
        return "Azure Devops";
      default:
        return "GitHub";
    }
  }

  const getDocsUrl = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return "https://docs.terrakube.org/vcs/gitlab.com";
      case 'BITBUCKET':
        return "https://docs.terrakube.org/vcs/bitbucket.com";
      case 'AZURE_DEVOPS':
        return "https://docs.terrakube.org/vcs/azure-devops";
      default:
        return "https://docs.terrakube.org/vcs/github.com";
    }
  }

  const getClientIdName = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return "Application ID";
      case 'BITBUCKET':
        return "Key";
      case 'AZURE_DEVOPS':
        return "App ID";
      default:
        return "Client ID";
    }
  }

  const getSecretIdName = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return "Secret";
      case 'BITBUCKET':
        return "Secret";
      case 'AZURE_DEVOPS':
        return "Client Secret";
      default:
        return "Client Secret";
    }
  }

  const renderStep1 = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return <div>
        <p className="paragraph">1. On Gitlab, <Button className="link" target="_blank" href="https://gitlab.com/-/profile/applications" type="link">register a new OAuth Application&nbsp; <HiOutlineExternalLink/></Button>. Enter the following information:</p>
        <div className="paragraph">
          <p></p>
          <Row><Col span={6}><b>Name:</b> </Col><Col span={6}>Terrakube ({localStorage.getItem(ORGANIZATION_NAME)})</Col></Row>
          <Row><Col span={6}><b>Redirect URI:</b> </Col><Col span={6}>http://localhost</Col></Row>
          <Row><Col span={6}><b>Scopes:</b> </Col><Col span={6}>Only the following should be checked:<br/>api</Col></Row>
          <p></p>
        </div>
      </div>;
      case 'BITBUCKET':
        return <div>
        <p className="paragraph">1. On Bitbucket Cloud, logged in as whichever account you want Terrakube to act as, add a new OAuth Consumer. You can find the OAuth Consumer settings page under your workspace settings. Enter the following information:</p>
        <div className="paragraph">
          <p></p>
          <Row><Col span={6}><b>Name:</b> </Col><Col span={6}>Terrakube ({localStorage.getItem(ORGANIZATION_NAME)})</Col></Row>
          <Row><Col span={6}><b>Description:</b> </Col><Col span={6}>Any description of your choice</Col></Row>
          <Row><Col span={6}><b>Callback URL:</b> </Col><Col span={6}>http://localhost</Col></Row>
          <Row><Col span={6}><b>URL:</b> </Col><Col span={6}>http://localhost</Col></Row>
          <Row><Col span={6}><b>This is a private consumer (checkbox):</b> </Col><Col span={6}>Checked</Col></Row>
          <Row><Col span={6}><b>Permissions (checkboxes):</b> </Col><Col span={6}>The following should be checked:<br/>Account: Write<br/>Repositories: Admin<br/>Pull requests: Write<br/>Webhooks: Read and write</Col></Row>
          <p></p>
        </div>
      </div>;
      case 'AZURE_DEVOPS':
        return <div>
        <p className="paragraph">1. On Azure DevOps, <Button className="link" target="_blank" href="https://aex.dev.azure.com/app/register?mkt=en-US" type="link">register a new OAuth Application&nbsp; <HiOutlineExternalLink/></Button>. Enter the following information:</p>
        <div className="paragraph">
          <p></p>
          <Row><Col span={6}><b>Company Name:</b> </Col><Col span={6}>Terrakube</Col></Row>
          <Row><Col span={6}><b>Application name:</b> </Col><Col span={6}>Terrakube ({localStorage.getItem(ORGANIZATION_NAME)})</Col></Row>
          <Row><Col span={6}><b>Application website:</b> </Col><Col span={6}>http://localhost</Col></Row>
          <Row><Col span={6}><b>Callback URL:</b> </Col><Col span={6}>http://localhost</Col></Row>
          <Row><Col span={6}><b>Authorized scopes (checkboxes):</b> </Col><Col span={6}>Only the following should be checked: <br/>Code (read)<br/>Code (status)</Col></Row>
          <p></p>
        </div>
      </div>;
      default:
        return <div>
          <p className="paragraph">1. On GitHub, <Button className="link" target="_blank" href="https://github.com/settings/applications/new" type="link">register a new OAuth Application&nbsp; <HiOutlineExternalLink/></Button>. Enter the following information:</p>
          <div className="paragraph">
            <p></p>
            <Row><Col span={6}><b>Application Name:</b> </Col><Col span={6}>Terrakube ({localStorage.getItem(ORGANIZATION_NAME)})</Col></Row>
            <Row><Col span={6}><b>Homepage URL:</b> </Col><Col span={6}>http://localhost</Col></Row>
            <Row><Col span={6}><b>Application description:</b> </Col><Col span={6}>Any description of your choice</Col></Row>
            <Row><Col span={6}><b>Authorization callback URL:</b> </Col><Col span={6}>http://localhost</Col></Row>
            <p></p>
          </div>
        </div>;
    }
  }

  const renderStep2 = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return  <p className="paragraph">2. After clicking the "Save application" button, you'll be taken to the new application's page. Enter the Application ID and Secret below:</p>;
      case 'BITBUCKET':
        return <p className="paragraph">2. After clicking the "Save" button, you'll be taken to the OAuth settings page. Find your new OAuth consumer under the "OAuth Consumers" heading, and click its name to reveal its details. Enter the Key and Secret below:</p>;
      case 'AZURE_DEVOPS':
        return <p className="paragraph">2. Create the application. On the following page, you'll find its details. Enter the App ID and Client Secret below:</p>;
      default:
        return <p className="paragraph">2. After clicking the "Register application" button, you'll be taken to the new application's page. Enter the Client ID below:</p>;
    }
  }

  const renderStep3 = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return  null;
      case 'BITBUCKET':
        return null;
      case 'AZURE_DEVOPS':
        return null;
      default:
        return <div><p className="paragraph">3. Next, generate a new client secret and enter the value below:</p><br/></div>;
    }
  }

  const onFinish = (values) => {
    const body = {
      data: {
        type: "vcs",
        attributes: { 
          name: values.name,
          description:values.name,
          vcsType: vcsType,
          clientId: values.clientId,
          clientSecret: values.clientSecret
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${orgid}/vcs`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        if(response.status =="201")
        {
          loadVCS();
          setMode("list");

        }
      })
  };
  return (
    <div>
      <h1>Add VCS Provider</h1>
      <div className="App-text">
        To connect workspaces, modules, and policy sets to git repositories containing Terraform configurations, Terrakube needs access to your version control system (VCS) provider. Use this page to configure OAuth authentication with your VCS provider.
      </div>
      <Steps direction="horizontal" size="small" current={current} onChange={handleChange}>
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
            <Button icon={<GithubOutlined />} onClick={() => {handleClick("GITHUB");}} size="large" >Github</Button>
            <Button icon={<GitlabOutlined />} onClick={() => {handleClick("GITLAB");}} size="large" >Gitlab</Button>
            <Button icon={<SiBitbucket />} onClick={() => {handleClick("BITBUCKET");}} size="large" >&nbsp;&nbsp;Bitbucket</Button>
            <Button icon={<SiAzuredevops />} onClick={() => {handleClick("AZURE_DEVOPS");}} size="large" >&nbsp;&nbsp;Azure Devops</Button>
          </Space>
        </Space>

      )}
      {current == 1 && (
        <Space className="chooseType" direction="vertical">
          <h3>Set up provider</h3>
          <p className="paragraph">
          For additional information about connecting to {renderVCSType(vcsType)} to Terrakube, please read our <Button className="link" target="_blank" href={getDocsUrl(vcsType)} type="link">documentation&nbsp; <HiOutlineExternalLink/>.</Button>
          </p>
          {renderStep1(vcsType)}
          {renderStep2(vcsType)}
          <Form  onFinish={onFinish} validateMessages={validateMessages} name="create-vcs" layout="vertical"> 
              <Form.Item name="name" label="Name"  extra=" A name for your VCS Provider. This is helpful if you will be configuring multiple instances of the same provider." rules={[{ required: true }]}>
                <Input placeholder={renderVCSType(vcsType)} />
              </Form.Item>
              <Form.Item name="clientId" label={getClientIdName(vcsType)} rules={[{ required: true }]}>
                <Input placeholder="ex. 824ff023a7136981f322" />
              </Form.Item>
              {renderStep3(vcsType)}
              <Form.Item name="clientSecret" label={getSecretIdName(vcsType)}  rules={[{ required: true }]}>
                <Input placeholder="ex. db55545bd64e851dc298ba900dd197a02b42bb3s" />
              </Form.Item>
              <Button type="primary"  htmlType="submit">Connect and Continue</Button>
          </Form>
       
        </Space>
      )}
    </div>
  );
}