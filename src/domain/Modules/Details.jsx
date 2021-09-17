import { Table } from "antd";
import { React, useState, useEffect } from "react";
import { Button, Layout, Breadcrumb } from "antd";
import axiosInstance from "../../config/axiosConfig";
import {useParams} from "react-router-dom";
import { CloudUploadOutlined } from '@ant-design/icons';

import { ORGANIZATION_ARCHIVE,ORGANIZATION_NAME } from '../../config/actionTypes';
const { Content } = Layout;
const { DateTime } = require("luxon");
const include = {
  WORKSPACE: "workspace",
  MODULE: "module"
}

const MODULE_COLUMNS = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    render: (text, record) => (
      <a href={"/modules/" + record.id}>{record.name}</a>
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

export const ModuleDetails = ({setOrganizationName,organizationName}) => {
  const { orgid } = useParams();
  const [organization, setOrganization] = useState({});
  const [modules, setModules] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, orgid);
    axiosInstance.get(`organization/${orgid}?include=workspace,module`)
      .then(response => {
        console.log(response);
        setOrganization(response.data);
       
        if (response.data.included) {
          setupOrganizationIncludes(response.data.included, setModules);
        }

        setLoading(false);
        localStorage.setItem(ORGANIZATION_NAME,response.data.data.attributes.name)
        setOrganizationName(response.data.data.attributes.name)
      });
      
  }, [orgid]);

  return (
    <Content style={{ padding: '0 50px' }}>
      <Breadcrumb style={{ margin: '16px 0' }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item>Registry</Breadcrumb.Item>
        <Breadcrumb.Item>Modules</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        {loading || !organization.data || !modules ? (
          <p>Data loading...</p>
        ) : (
          <div className="orgWrapper">
            <div className='workspaceActions'><h2>Modules</h2><Button type="primary" htmlType="button" icon={<CloudUploadOutlined />}  href={`/organization/${orgid}/registry/create`}>Publish module</Button></div>
            <Table dataSource={modules} columns={MODULE_COLUMNS} rowKey='name' />
          </div>
        )}
      </div>
    </Content>

  );
}

function setupOrganizationIncludes(includes, setModules, setWorkspaces) {
  let modules = [];

  includes.forEach(element => {
    switch (element.type) {
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
}