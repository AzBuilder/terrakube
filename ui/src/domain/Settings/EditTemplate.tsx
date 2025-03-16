import { React, useState, useRef, useEffect } from "react";
import './Settings.css';
import { Space, Button, Form, Input } from "antd";
import axiosInstance from "../../config/axiosConfig";
import { useParams } from "react-router-dom";
import Editor from "@monaco-editor/react";
import { Buffer } from 'buffer';

const validateMessages = {
  required: '${label} is required!'
}
export const EditTemplate = ({ setMode,templateId, loadTemplates}) => {
  const { orgid } = useParams();
  const [tcl, setTCL] = useState("");
  const editorRef = useRef(null);
  const [template, setTemplate] = useState([]);
  const [loading, setLoading] = useState(true);

  function handleEditorDidMount(editor, monaco) {
    editorRef.current = editor;
  }

  useEffect(() => {
    loadTemplate(templateId);
  }, [ templateId]);

  function handleEditorValidation(markers) {
    markers.forEach(marker => console.log("onValidate:", marker.message));
  }

  const loadTemplate= (templateId)=>{
    axiosInstance.get(`organization/${orgid}/template/${templateId}`)
    .then(response => {
      console.log(response);
      setTemplate(response.data.data);
      let buff = new Buffer(response.data.data.attributes.tcl, 'base64');
      setTCL(buff.toString('ascii'));
      setLoading(false);
    });
  }

  const onFinish = (values) => {
    const body = {
      data: {
        type: "template",
        id: templateId,
        attributes: {
          name: values.name,
          description: values.description,
          tcl: Buffer.from(editorRef.current.getValue()).toString('base64'),
          version: "1.0.0"
        }
      }
    }
    console.log(body);

    axiosInstance.patch(`organization/${orgid}/template/${templateId}`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        if (response.status == "204") {
          setMode("list");
          loadTemplates();

        }
      })
  };
  return (
    <div>
      <h1>Edit Template</h1>
      <Space className="chooseType" direction="vertical">
      {loading || !template ? (
            <p>Data loading...</p>
          ) : (
        <Form initialValues={{name:template.attributes.name,description: template.attributes.description}} onFinish={onFinish} validateMessages={validateMessages} name="create-vcs" layout="vertical">
          <Form.Item name="name" label="Name" extra=" A name for your Template. This will appear in the workspaces when you execute a new job." rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Description" >
            <Input.TextArea />
          </Form.Item>
          <Form.Item name="tcl" label="Template">
            <div className="editor">
              <Editor height="40vh" onMount={handleEditorDidMount} onValidate={handleEditorValidation} defaultLanguage="yaml" defaultValue={tcl} />
            </div>
          </Form.Item>
          <Button type="primary" htmlType="submit">Save Template</Button>
        </Form>
      )}
      </Space>






    </div>
  );
}