import { React, useEffect, useState } from "react";
import axiosInstance from "../../config/axiosConfig";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import { Table, Button } from "antd";
import { DeleteFilled } from '@ant-design/icons';
import './Workspaces.css';

const include = {
  ENVIRONMENT_VAR: 'environment',
  SECRET_VAR: 'secret',
  TERRAFORM_VAR: 'variable',
  JOB: 'job'
}

const VARIABLES_COLUMS = (organizationId, resourceId) => [
  {
    title: 'Key',
    dataIndex: 'key',
    key: 'key'
  },
  {
    title: 'Type',
    dataIndex: 'type',
    key: 'type'
  },
  {
    title: 'Value',
    dataIndex: 'value',
    key: 'value',
    render: (_, record) => {
      return record.type === 'secret' ? 'Hidden Value' : record.value;
    }
  },
  {
    title: 'Actions',
    key: 'action',
    render: (_, record) => {
      return <Button icon={<DeleteFilled />} onClick={() => deleteVariable(record.id, record.type, organizationId, resourceId)}></Button>
    }
  }
]

const JOBS_COLUMNS = [
  {
    title: 'Id',
    dataIndex: 'id',
    key: 'id'
  },
  {
    title: 'Action',
    dataIndex: 'command',
    key: 'command'
  },
  {
    title: 'Status',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: 'Plan Output',
    dataIndex: 'output',
    key: 'output',
    render: (_, record) => {
      return record.status === 'completed' ? <a href={record.output} target='_blank'>Go to plan</a> : 'Action still in progress'
    }
  }
]

export const WorkspaceDetails = (props) => {
  const resourceId = props.match.params.id;
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [workspace, setWorkspace] = useState({});
  const [variables, setVariables] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(WORKSPACE_ARCHIVE, resourceId);
    axiosInstance.get(`organization/${organizationId}/workspace/${resourceId}?include=environment,job,secret,variable`)
      .then(response => {
        console.log(response);
        setWorkspace(response.data);
        if(response.data.included) {
          setupWorkspaceIncludes(response.data.included, setVariables, setJobs);
        }
        setLoading(false);
      })
  }, [resourceId]);
  
  return(
    <div className="workspaceDisplay">
      {loading || !workspace.data || !variables || !jobs ? (
        <p>Data loading...</p>
      ) : (
        <div className="orgWrapper">
          <h2>Worspace name: {workspace.data.attributes.name}</h2>
          Git repo: <a href={workspace.data.attributes.source} target='_blank'>{workspace.data.attributes.source}</a><br />
          Branch: {workspace.data.attributes.branch}<br />
          Terraform Version: {workspace.data.attributes.terraformVersion}
          <div className='tableWrapper'>
            <div className='variableActions'><h3>Variables</h3><Button type="primary" htmlType="button" shape="round" href={`/workspaces/${workspace.data.attributes.name}/variable/create`}>Add Variable</Button></div>
            <Table dataSource={variables} columns={VARIABLES_COLUMS(organizationId, resourceId)} rowKey='key' />
            <div className='jobActions'><h3>Jobs</h3><Button type="primary" htmlType="button" shape="round" href={`/workspaces/${workspace.data.attributes.name}/jobs/create`}>Trigger Job</Button></div>
            <Table dataSource={jobs} columns={JOBS_COLUMNS} rowKey='id' />
          </div>
        </div>
      )}
    </div>
  )
}

function setupWorkspaceIncludes(includes, setVariables, setJobs) {
  let variables = [];
  let jobs = [];

  includes.forEach(element => {
    switch (element.type) {
      case include.JOB:
        jobs.push(
          {
            id: element.id,
            ...element.attributes
          }
        );
        break;
      default:
        variables.push(
          {
            id: element.id,
            type: element.type,
            ...element.attributes
          }
        );
        break;
    }
  });

  setVariables(variables);
  setJobs(jobs);
}

function deleteVariable(variableId, variableType, organizationId, resourceId) {
  console.log(variableId);

  axiosInstance.delete(`organization/${organizationId}/workspace/${resourceId}/${variableType}/${variableId}`, {
    headers: {
      'Content-Type': 'application/vnd.api+json'
    }
  })
    .then(response => {
      console.log(response);
    })
}