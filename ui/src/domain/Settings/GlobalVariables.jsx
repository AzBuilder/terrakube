import { React,useState, useEffect } from "react";
import { Button, Table, Popconfirm ,Form,Modal,Space,Input,Switch,Select,Tag} from "antd";
import './Settings.css';
import axiosInstance from "../../config/axiosConfig";
import {useParams} from "react-router-dom";
import { InfoCircleOutlined,EditOutlined,DeleteOutlined} from '@ant-design/icons';
export const GlobalVariablesSettings = () => {

  const { orgid } = useParams();
  const [globalVariables, setGlobalVariables] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [variableKey, setVariableKey] = useState(false);
  const [mode, setMode] = useState("create");
  const [variableId, setVariableId] = useState([]);
  const [form] = Form.useForm();

  const VARIABLES_COLUMS = (organizationId,onEdit) => [
    {
      title: 'Key',
      dataIndex: 'key',
      width: "40%",
      key: 'key',
      render: (_, record) => {
        return  <div>{record.attributes.key} &nbsp;&nbsp;&nbsp;&nbsp; <Tag visible={record.attributes.hcl}>HCL</Tag> <Tag visible={record.attributes.sensitive}>Sensitive</Tag></div> ;
      }
    },
    {
      title: 'Value',
      dataIndex: 'value',
      key: 'value',
      width: "40%",
      render: (_, record) => {
        return record.attributes.sensitive ? <i>Sensitive - write only</i> : <div>{record.attributes.value}</div> ;
      }
    },
    {
      title: 'Actions',
      key: 'action',
      render: (_, record) => {
        return <div>
          <Button  type="link" icon={<EditOutlined />} onClick={() => onEdit(record.id)}>Edit</Button>
          <Popconfirm  onConfirm={() => {onDelete(record.id)}} title={<p>This will permanently delete this global variable <br/>and it will no longer be used in future runs. <br/>
            Are you sure?</p>} okText="Yes" cancelText="No"> <Button danger type="link" icon={<DeleteOutlined />}>Delete</Button></Popconfirm>
        </div>
      }
    }
  ]
  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (id) => {
    setMode("edit");
    setVariableId(id);
    setVisible(true);
    axiosInstance.get(`organization/${orgid}/globalvar/${id}`)
    .then(response => {
      console.log(response);
      setVariableKey(response.data.data.attributes.key);
      form.setFieldsValue({key: response.data.data.attributes.key,value:response.data.data.attributes.value, hcl:response.data.data.attributes.hcl, sensitive:response.data.data.attributes.sensitive,category:response.data.data.attributes.category,description:response.data.data.attributes.description});
    });
  };

  const onNew = () => {
    form.resetFields();
    setVisible(true);
    setVariableKey("");
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance.delete(`organization/${orgid}/globalvar/${id}`)
    .then(response => {
      console.log(response);
      loadGlobalVariables();
    });
  };


  const onCreate = (values) => {
    const body = {
      data: {
        type: "globalvar",
        attributes: {
          key: values.key,
          value: values.value,
          sensitive: values.sensitive,
          description: values.description,
          hcl: values.hcl,
          category: values.category
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${orgid}/globalvar`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        loadGlobalVariables();
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "globalvar",
        id:variableId,
        attributes: {
          key: values.key,
          value: values.value,
          description: values.description,
          hcl: values.hcl,
          category: values.category
        }
      }
    }
    console.log(body);

    axiosInstance.patch(`organization/${orgid}/globalvar/${variableId}`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        loadGlobalVariables();
        setVisible(false);
        form.resetFields();
      });
  };



  const loadGlobalVariables= ()=>{
    axiosInstance.get(`organization/${orgid}/globalvar`)
    .then(response => {
      console.log(response);
      setGlobalVariables(response.data);
      setLoading(false);
    });
  }
  useEffect(() => {
    setLoading(true);
    loadGlobalVariables();

  }, [orgid]);


  return (
    <div className="setting">
      <h1>Global Variables</h1>
      <div className="App-text">
      Global Variables allow you to define and apply variables one time across multiple workspaces within an organization.
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">Create global variable</Button><br></br>
     
      <h3 style={{marginTop:"30px"}}>Global Variables</h3>
      {loading || !globalVariables.data ? (
          <p>Data loading...</p>
        ) : (
          <Table dataSource={globalVariables.data} columns={VARIABLES_COLUMS(orgid,onEdit)} rowKey='key' />)}

   <Modal width="600px" open={visible} title={mode === "edit" ? "Edit global variable " + variableKey : "Create new global variable"} okText="Save global variable" onCancel={onCancel} cancelText="Cancel" 
        onOk={() => {
          form.validateFields().then((values) => {
            if(mode === "create")
              onCreate(values);
            else
              onUpdate(values);
          }).catch((info) => {
            console.log('Validate Failed:', info);
          });
        }}>
        <Space style={{width:"100%"}} direction="vertical">
          <Form name="globalVariable" form={form} layout="vertical" >
          <Form.Item name="key" label="Key" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="value" label="Value" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="category" label="Category" rules={[{ required: true }]}>
              <Select placeholder="Please select a category">
                <Select.Option value="TERRAFORM">Terraform Variable</Select.Option>
                <Select.Option value="ENV">Environment Variable</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="description" rules={[{ required: true }]} label="Description">
              <Input.TextArea width="800px" />
            </Form.Item>    
            <Form.Item name="hcl" valuePropName="checked" label="HCL" tooltip={{ title: 'Parse this field as HashiCorp Configuration Language (HCL). This allows you to interpolate values at runtime.', icon: <InfoCircleOutlined /> }} >
              <Switch />
            </Form.Item>
            {mode === "create"?(
            <Form.Item name="sensitive" valuePropName="checked" label="Sensitive" tooltip={{ title: 'Sensitive variables are never shown in the UI or API. They may appear in Terraform logs if your configuration is designed to output them.', icon: <InfoCircleOutlined /> }} >
              <Switch />
            </Form.Item> ):""}
          </Form>
        </Space>
      </Modal>
    </div>
  );
}