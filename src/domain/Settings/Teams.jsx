import { React,useState, useEffect } from "react";
import { Button, List, Popconfirm ,Form,Modal,Space,Input,Switch,Avatar} from "antd";
import './Settings.css';
import axiosInstance from "../../config/axiosConfig";
import {useParams} from "react-router-dom";
import { InfoCircleOutlined,TeamOutlined,EditOutlined,DeleteOutlined} from '@ant-design/icons';
export const TeamSettings = () => {

  const { orgid } = useParams();
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(false);
  const [visible, setVisible] = useState(false);
  const [teamName, setTeamName] = useState(false);
  const [mode, setMode] = useState("create");
  const [teamId, setTeamId] = useState([]);
  const [form] = Form.useForm();
  const onCancel = () => {
    setVisible(false);
  };
  const onEdit = (id) => {
    setMode("edit");
    setTeamId(id);
    setVisible(true);
    axiosInstance.get(`organization/${orgid}/team/${id}`)
    .then(response => {
      console.log(response);
      setTeamName(response.data.data.attributes.name);
      form.setFieldsValue({manageProvider: response.data.data.attributes.manageProvider,manageModule:response.data.data.attributes.manageModule, manageWorkspace:response.data.data.attributes.manageWorkspace});
    });
  };

  const onNew = () => {
    form.resetFields();
    setVisible(true);
    setTeamName("");
    setMode("create");
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance.delete(`organization/${orgid}/team/${id}`)
    .then(response => {
      console.log(response);
      loadTeams();
    });
  };


  const onCreate = (values) => {
    const body = {
      data: {
        type: "team",
        attributes: {
          name: values.name,
          manageWorkspace: values.manageWorkspace,
          manageModule: values.manageModule,
          manageProvider:values.manageProvider
        }
      }
    }
    console.log(body);

    axiosInstance.post(`organization/${orgid}/team`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        loadTeams();
        setVisible(false);
        form.resetFields();
      });
  };

  const onUpdate = (values) => {
    const body = {
      data: {
        type: "team",
        id:teamId,
        attributes: {
          manageWorkspace: values.manageWorkspace,
          manageModule: values.manageModule,
          manageProvider:values.manageProvider
        }
      }
    }
    console.log(body);

    axiosInstance.patch(`organization/${orgid}/team/${teamId}`, body, {
      headers: {
        'Content-Type': 'application/vnd.api+json'
      }
    })
      .then(response => {
        console.log(response);
        loadTeams();
        setVisible(false);
        form.resetFields();
      });
  };



  const loadTeams= ()=>{
    axiosInstance.get(`organization/${orgid}/team`)
    .then(response => {
      console.log(response);
      setTeams(response.data);
      setLoading(false);
    });
  }
  useEffect(() => {
    setLoading(true);
    loadTeams();

  }, [orgid]);


  return (
    <div className="setting">
      <h1>Team Management</h1>
      <div className="App-text">
        Teams let you group users into specific categories to enable finer grained access control policies. For example, your developers could be on a dev team that only has access to run jobs.
      </div>
      <Button type="primary" onClick={onNew} htmlType="button">Create team</Button><br></br>
     
      <h3 style={{marginTop:"30px"}}>Teams</h3>
      {loading || !teams.data ? (
          <p>Data loading...</p>
        ) : (
      <List
        itemLayout="horizontal" 
        dataSource={teams.data}
        renderItem={item => (
          <List.Item  actions={[<Button onClick={() => {
            onEdit(item.id);
          }} icon={<EditOutlined />} type="link">Edit</Button>, <Popconfirm onConfirm={() => {
            onDelete(item.id);
          }} style={{width:"20px"}} title={<p>This will permanently delete this team <br/>and any permissions associated with it. <br/>
          Are you sure?</p>} okText="Yes" cancelText="No"> <Button icon={<DeleteOutlined />} type="link" danger>Delete</Button></Popconfirm>]}> 
          <List.Item.Meta
               avatar={<Avatar style={{ backgroundColor: '#1890ff' }} icon={<TeamOutlined />}></Avatar>}
                title={item.attributes.name}
                description={<Space split={<span>&nbsp;</span>}e>
                 Access to Manage:  
                 {item.attributes.manageWorkspace ? ("Workspaces"):("")}
                  {item.attributes.manageModule ? ("Modules"):("")}
                  {item.attributes.manageProvider ? ("Providers"):("")}
                </Space>}
              />
          </List.Item>
        )}
      />)}

   <Modal width="600px" visible={visible} title={mode == "edit" ? "Edit team " + teamName : "Create new team"} okText="Save team" onCancel={onCancel} cancelText="Cancel" 
        onOk={() => {
          form.validateFields().then((values) => {
            if(mode == "create")
              onCreate(values);
            else
              onUpdate(values);
          }).catch((info) => {
            console.log('Validate Failed:', info);
          });
        }}>
        <Space style={{width:"100%"}} direction="vertical">
          <Form name="team" form={form} layout="vertical" >
            {mode == "create"?(
            <Form.Item name="name"  tooltip={{ title: 'Must be a valid AD Group name', icon: <InfoCircleOutlined /> }} label="Name" rules={[{ required: true }]}>
              <Input />
            </Form.Item>):""}
            <Form.Item name="manageWorkspace" valuePropName="checked" label="Manage Workspaces" tooltip={{ title: 'Allow members to create and administrate all workspaces within the organization', icon: <InfoCircleOutlined /> }} >
              <Switch  />
           </Form.Item>
           <Form.Item name="manageModule" valuePropName="checked" label="Manage Modules" tooltip={{ title: 'Allow members to create and administrate all modules within the organization', icon: <InfoCircleOutlined /> }} >
              <Switch />
           </Form.Item>
           <Form.Item name="manageProvider" valuePropName="checked" label="Manage Providers" tooltip={{ title: 'Allow members to create and administrate all providers within the organization', icon: <InfoCircleOutlined /> }} >
              <Switch />
           </Form.Item>
          </Form>
        </Space>
      </Modal>
    </div>
  );
}