import { Table } from "antd";
import { React, useState, useEffect } from "react";
import { Button, Layout, Breadcrumb } from "antd";
import axiosInstance from "../../config/axiosConfig";
import {useParams,useHistory} from "react-router-dom";
import { ORGANIZATION_ARCHIVE,ORGANIZATION_NAME } from '../../config/actionTypes';
const { Content } = Layout;
const { DateTime } = require("luxon");
const include = {
  WORKSPACE: "workspace",
  MODULE: "module"
}

const WORKSPACE_COLUMNS = [
  {
    title: 'Workspace Name',
    dataIndex: 'name',
    key: 'name',
    render: (_, record) => (
      <a href={"/workspaces/" + record.id}>{record.name}</a>
    )
  },
  ,
  {
    title: 'Run Status',
    dataIndex: 'runStatus',
    key: 'runStatus',
  },
  ,
  {
    title: 'Repo',
    dataIndex: 'source',
    key: 'source',
  },
  {
    title: 'Latest Change',
    dataIndex: 'latestChange',
    key: 'latestChange',
  }
]

export const OrganizationDetails = ({setOrganizationName,organizationName}) => {
  const { id } = useParams();
  const [organization, setOrganization] = useState({});
  const [workspaces, setWorkspaces] = useState([]);
  const [modules, setModules] = useState([]);
  const [loading, setLoading] = useState(false);
  const history = useHistory();
  const handleCreate = e => {
      history.push("/workspaces/create");
  };
  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, id);
    axiosInstance.get(`organization/${id}?include=workspace,module`)
      .then(response => {
        console.log(response);
        setOrganization(response.data);
       
        if (response.data.included) {
          setupOrganizationIncludes(response.data.included, setModules, setWorkspaces);
        }

        setLoading(false);
        localStorage.setItem(ORGANIZATION_NAME,response.data.data.attributes.name)
        setOrganizationName(response.data.data.attributes.name)
      });
      
  }, [id]);

  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item>Workspaces</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        {loading || !organization.data || !workspaces ? (
          <p>Data loading...</p>
        ) : (
          <div className="orgWrapper">
            <div className='variableActions'><h2>Workspaces</h2><Button type="primary" htmlType="button" onClick={handleCreate}>New workspace</Button></div>
            <Table dataSource={workspaces} columns={WORKSPACE_COLUMNS} rowKey='name' />
          </div>
        )}
      </div>
    </Content>

  );
}

function setupOrganizationIncludes(includes, setModules, setWorkspaces) {
  let modules = [];
  let workspaces = [];

  includes.forEach(element => {
    switch (element.type) {
      case include.WORKSPACE:
        workspaces.push(
          {
            id: element.id,
            latestChange: DateTime.local().minus({ minutes: Math.floor(Math.random() * 5) }).toRelative() ,
            ...element.attributes
          }
        );
        break;
      case include.MODULE:
        modules.push(
          {
            id: element.id,
            ...element.attributes
          }
        );
        break;
      default:
        break;
    }
  });

  setModules(modules);
  setWorkspaces(workspaces);
}