import { Table } from "antd";
import { React, useState, useEffect } from "react";
import { Button } from "antd";
import axiosInstance from "../../config/axiosConfig";
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';

const include = {
  WORKSPACE: "workspace",
  MODULE: "module"
}

const WORKSPACE_COLUMNS = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    render: (_, record) => (
      <a href={"/workspaces/"+record.id}>{record.name}</a>
    )
  },
  {
    title: 'Terraform Version',
    dataIndex: 'terraformVersion',
    key: 'terraformVersion',
  }
]

const MODULE_COLUMNS = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    render: (text, record) => (
      <a href={"/modules/"+record.id}>{record.name}</a>
    )
  },
  {
    title: 'Description',
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: 'Provider',
    dataIndex: 'provider',
    key: 'provider'
  }
]

export const OrganizationDetails = (props) => {
  const resourceId = props.match.params.id;
  const [organization, setOrganization] = useState({});
  const [workspaces, setWorkspaces] = useState([]);
  const [modules, setModules] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, resourceId);
    axiosInstance.get(`organization/${resourceId}?include=workspace,module`)
      .then(response => {
        console.log(response);
        setOrganization(response.data);
        if(response.data.included) {
          setupOrganizationIncludes(response.data.included, setModules, setWorkspaces);
        }
        setLoading(false);
      })
  }, [resourceId]);

  return (
    <div className="orgDisplay">
      {loading || !organization.data || !workspaces ? (
        <p>Data loading...</p>
      ) : (
        <div className="orgWrapper">
          <h2>Organization name: {organization.data.attributes.name}</h2>
          <div>
            <div className='workspaceActions'><h3>Workspaces</h3><Button type="primary" htmlType="button" shape="round" href={`/workspaces/create`}>Create Workspace</Button></div>
            <Table dataSource={workspaces} columns={WORKSPACE_COLUMNS} rowKey='name' />
          </div>
          <div>
          <div className='moduleActions'><h3>Modules</h3><Button type="primary" htmlType="button" shape="round" href={`/modules/create`}>Add Module</Button></div>
            <Table dataSource={modules} columns={MODULE_COLUMNS} rowKey='name' />
          </div>
        </div>
      )}
    </div>
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