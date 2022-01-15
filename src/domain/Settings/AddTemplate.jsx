import { React, useState,useRef } from "react";
import './Settings.css';
import { Steps, Space, Button, Form, Input,Card,message } from "antd";
import { IconContext } from "react-icons";
import { HiOutlineExternalLink } from "react-icons/hi";
import {  BiFileBlank, BiMobileLandscape } from "react-icons/bi";
import { AiOutlineAppstoreAdd } from "react-icons/ai";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import Editor from "@monaco-editor/react";
import {Buffer} from 'buffer';
const { Step } = Steps;
const validateMessages = {
  required: '${label} is required!'
}
export const AddTemplate = ({ setMode, loadTemplates }) => {
  const { orgid } = useParams();
  const [current, setCurrent] = useState(0);
  const [tcl, setTCL] = useState("");
  const editorRef = useRef(null);

  function handleEditorDidMount(editor, monaco) {
    editorRef.current = editor; 
  }
  const handleChange = currentVal => {
    setCurrent(currentVal);
    if(currentVal === 2){
      editorRef.current.defaultValue = tcl;
    }
  };
  const handleClick = () => {
    setCurrent(1);
  };


  const handleComingSoon = e => {
    message.info("Coming Soon!");
  };

  const handleContinue = e => {
    setCurrent(2);
    setTCL(editorRef.current.getValue());
  };

  function handleEditorValidation(markers) {
    markers.forEach(marker => console.log("onValidate:", marker.message));
  }
  

  const onFinish = (values) => {
    const body = {
      data: {
        type: "template",
        attributes: {
          name: values.name,
          description: values.description,
          tcl: Buffer.from(tcl).toString('base64'),
          version:"1.0.0"
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${orgid}/template`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        if (response.status == "201") {
          loadTemplates();
          setMode("list");

        }
      })
  };
  return (
    <div>
      <h1>Create a new Template</h1>
      <div className="App-text">
        Templates allow you to define a custom flow so you can run any tool before or after terraform plan/apply/destroy.
      </div>
      <Steps direction="horizontal" size="small" current={current} onChange={handleChange}>
        <Step title="Choose Type" />
        <Step title="Define Template" />
        <Step title="Configure Settings" />
      </Steps>
      {current == 0 && (
        <Space className="chooseType" direction="vertical">
          <h3>Choose your template</h3>
          <Card hoverable onClick={handleClick}>

            <IconContext.Provider value={{ size: "1.3em" }}>
              <BiFileBlank />
            </IconContext.Provider>
            <span className="workflowType">Blank template</span>
            <div className="workflowDescription App-text">
              Create an empty template. So you can define your template from scratch
            </div>
            <div className="workflowSelect">
            </div>


          </Card>
          <Card hoverable onClick={handleComingSoon}>
            <IconContext.Provider value={{ size: "1.3em" }}>
              <AiOutlineAppstoreAdd />
            </IconContext.Provider>
            <span className="workflowType">Quick start template</span>
            <div className="workflowDescription App-text">
              Start using templates contributed by the Terrakube community
            </div>
          </Card>
        </Space>

      )}
      {current == 1 && (
        <Space className="chooseType" direction="vertical">
          <h3>Set up template</h3>
          <p className="paragraph">
            For additional information about templates and custom flows in Terrakube, please read our <Button className="link" target="_blank" href="" type="link">documentation&nbsp; <HiOutlineExternalLink />.</Button>
          </p>
          <br/>
          <div className="editor">
          <Editor height="40vh" onMount={handleEditorDidMount} onValidate={handleEditorValidation} defaultLanguage="yaml" defaultValue={tcl}/>
          </div>
          <br/>
          <Button style={{float:'right'}} type="primary" onClick={handleContinue}  htmlType="button">Continue</Button>
        </Space>
      )}
      {current == 2 && (
        <Space className="chooseType" direction="vertical">
          <h3>Configure settings</h3>
          <Form  onFinish={onFinish} validateMessages={validateMessages} name="create-vcs" layout="vertical"> 
              <Form.Item name="name" label="Name"  extra=" A name for your Template. This will appear in the workspaces when you execute a new job." rules={[{ required: true }]}>
                <Input />
              </Form.Item>
              <Form.Item name="description" label="Description" >
                <Input.TextArea />
              </Form.Item>
              <Button type="primary"  htmlType="submit">Create Template</Button>
          </Form>
        </Space>
      )}
    </div>
  );
}