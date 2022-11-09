import { React, useState,useEffect } from 'react';
import { Form, Input, Button, Breadcrumb, Layout, Steps, Space ,Select} from "antd";
import { ORGANIZATION_ARCHIVE,ORGANIZATION_NAME } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import { GithubOutlined, GitlabOutlined } from '@ant-design/icons';
import { SiBitbucket, SiAzuredevops } from "react-icons/si";
import { IconContext } from "react-icons";
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



export const CreateModule = () => {
  const [current, setCurrent] = useState(0);
  const [step3Hidden, setStep3Hidden] = useState(true);
  const [step2Hidden, setStep2Hidden] = useState(true);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [vcs, setVCS] = useState([]);
  const [loading, setLoading] = useState(false);
  const history = useHistory();
  const [vcsId, setVcsId] = useState("");
  const [vcsButtonsVisible, setVCSButtonsVisible] = useState(true);
  const [sshKeys, setSSHKeys] = useState([]);
  const [sshKeysVisible,setSSHKeysVisible] = useState(false);

  useEffect(() => {
    loadSSHKeys();
    loadVCSProviders();
  },[organizationId]);

 const handleGitClick = (id) => {

   if(id ==="git"){
      setSSHKeysVisible(true);
   }
   else{
     setSSHKeysVisible(false);
     setVcsId(id);
   }
    setCurrent(1);
    setStep2Hidden(false);
  };

  const handleGitContinueClick = e => {
    setCurrent(2);
    setStep3Hidden(false);
    setStep2Hidden(true);
    var source = form.getFieldValue("source");

    if(source!= null){
    var providerValue = source.match('terraform-(.*)-');
    if(providerValue!=null && providerValue.length > 0){
      form.setFieldsValue({ provider:providerValue[1]});
      var nameValue = source.match(providerValue[1] + '-(.*).git');
      if(nameValue!=null && nameValue.length > 0){
        form.setFieldsValue({ name:nameValue[1]});
      }
    }
  }
  };

  const handleVCSClick = (vcsType) => {
    history.push(`/organizations/${organizationId}/settings/vcs/new/${vcsType}`)
 };

  const handleDifferent = () => {
    setVCSButtonsVisible(false);
  };

  const handleExisting = () => {
     setVCSButtonsVisible(true);
  };

  const renderVCSLogo = (vcs) => {
    switch (vcs) {
      case 'GITLAB':
        return <GitlabOutlined style={{ fontSize: '20px' }} />;
      case 'BITBUCKET':
        return <IconContext.Provider value={{ size: "20px" }}><SiBitbucket />&nbsp;&nbsp;</IconContext.Provider>;
      case 'AZURE_DEVOPS':
        return <IconContext.Provider value={{ size: "20px" }}><SiAzuredevops />&nbsp;&nbsp;</IconContext.Provider>;
      default:
        return <GithubOutlined style={{ fontSize: '20px' }} />;

    }
  }

  const loadVCSProviders = () => {
    axiosInstance.get(`organization/${organizationId}/vcs`)
      .then(response => {
        console.log(response);
        setVCS(response.data);
        setLoading(false);
      });
  }

  const loadSSHKeys = () => {
    axiosInstance.get(`organization/${organizationId}/ssh`)
      .then(response => {
        console.log(response.data.data);
        setSSHKeys(response.data.data);
      });
  }

  const onFinish = (values) => {
    let body = {
      data: {
        type: "module",
        attributes: { 
          name: values.name,
          description: values.description,
          provider: values.provider,
          source: values.source
        }
      }
    }

    if (vcsId !== "") {
      body = {
        data: {
          type: "module",
          attributes: { 
            name: values.name,
            description: values.description,
            provider: values.provider,
            source: values.source
          },
          relationships: {
            vcs: {
              data: {
                type: "vcs",
                id: vcsId
              }
            }
          }
        }
      }

    }

    if (values.sshKey){
      body = {
        data: {
          type: "module",
          attributes: {
            name: values.name,
            description: values.description,
            provider: values.provider,
            source: values.source
          },
          relationships: {
            ssh: {
              data: {
                type: "ssh",
                id: values.sshKey
              }
            }
          }
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
        if(response.status === 201)
        {
          history.push(`/organizations/${organizationId}/registry/${response.data.data.id}`);
        }
      })
  };
  const [form] = Form.useForm();

  const handleChange = currentVal => {
    setCurrent(currentVal);
    if (currentVal === 1){
      setStep2Hidden(false);
      setStep3Hidden(true);
    }

    if (currentVal === 2){
      setStep3Hidden(false);
      setStep2Hidden(true);
    }

  };


  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{localStorage.getItem(ORGANIZATION_NAME)}</Breadcrumb.Item>
        <Breadcrumb.Item><Link to={`/organizations/${organizationId}/registry`}>Modules</Link></Breadcrumb.Item>
        <Breadcrumb.Item>New Module</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createWorkspace">
          <h2>Add Module</h2>
          <div className="App-text">
            This module will be created under the current organization, {localStorage.getItem(ORGANIZATION_NAME)}. 
          </div>
          <Steps direction="horizontal" size="small" current={current} onChange={handleChange}>
            <Step title="Connect to VCS" />
            <Step title="Choose a repository" />
            <Step title="Confirm selection" />
          </Steps>
          


          {current === 0 && (
            <Space className="chooseType" direction="vertical">
              <h3>Connect to a version control provider</h3>
              <div className="workflowDescription2 App-text">
              Choose the version control provider that hosts your module source code.
              </div>
              {vcsButtonsVisible ?  (
              <div>
              <Space direction="horizontal">
                <Button icon={<SiGit />} onClick={() => { handleGitClick("git"); }}  size="large">&nbsp;Git</Button>
                {loading || !vcs.data ? (
                  <p>Data loading...</p>
                ) : (
                  vcs.data.map(function (item, i) {
                    return <Button icon={renderVCSLogo(item.attributes.vcsType)} onClick={() => { handleGitClick(item.id); }} size="large">&nbsp;{item.attributes.name}</Button>;
                  }))}
              </Space>
              <br/>
              <Button onClick={handleDifferent} className="link" type="link">
                Connect to a different VCS
              </Button>
              </div>):

              (<div>
              <Space direction="horizontal">
                  <Button icon={<GithubOutlined />} onClick={() => {handleVCSClick("GITHUB");}} size="large" >Github</Button>
                  <Button icon={<GitlabOutlined />} onClick={() => {handleVCSClick("GITLAB");}} size="large" >Gitlab</Button>
                  <Button icon={<SiBitbucket />} onClick={() => {handleVCSClick("BITBUCKET");}} size="large" >&nbsp;&nbsp;Bitbucket</Button>
                  <Button icon={<SiAzuredevops />} onClick={() => {handleVCSClick("AZURE_DEVOPS");}} size="large" >&nbsp;&nbsp;Azure Devops</Button>
              </Space><br/>
              <Button onClick={handleExisting} className="link" type="link">
                Use an existing VCS connection
              </Button>
              </div>)}
            </Space>

          )}

         <Form  form={form} name="create-module" layout="vertical" onFinish={onFinish} validateMessages={validateMessages}> 
     
            <Space hidden={step2Hidden} className="chooseType" direction="vertical">
              <h3>Choose a repository</h3>
              <div className="workflowDescription2 App-text">
              Choose the repository that hosts your module source code. The format of your repository name should be <b>{"terraform-<PROVIDER>-<NAME>"}</b>.
              </div>
              <Form.Item name="source" label="Git repo" tooltip="e.g. https://github.com/Terrakube/terraform-sample-repository.git or git@github.com:AzBuilder/terraform-azurerm-webapp-sample.git" extra=" Git repo must be a valid git url using either https or ssh protocol." rules={[{ required: true , pattern: "((git|ssh|http(s)?)|(git@[\\w\\.]+))(:(//)?)([\\w\\.@\\:/\\-~]+)(\\.git)(/)?" }]}>
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
              <Form.Item hidden={!sshKeysVisible} name="sshKey" label="SSH Key" tooltip="Select an SSH Key that will be used to clone this repo." extra="To use the SSH support in modules the source should be used like git@github.com:AzBuilder/terrakube-docker-compose.git" rules={[{ required: false }]}>
               <Select placeholder="select SSH Key" style={{ width: 250 }} >
                  {sshKeys.map(function (sshKey, index) {
                    return <Select.Option key={sshKey?.id}>{sshKey?.attributes?.name}</Select.Option>;
                  })}
                </Select>
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