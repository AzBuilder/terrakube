import { React,useState,useEffect } from 'react';
import {  Tag, Space, Collapse, Avatar } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE,ORGANIZATION_NAME } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import {axiosClient} from "../../config/axiosConfig";
import { CheckCircleOutlined, CheckCircleTwoTone ,SyncOutlined,ClockCircleOutlined,ClockCircleTwoTone, CopyOutlined} from '@ant-design/icons';
const layout = {
  labelCol: { span: 14 },
  wrapperCol: { span: 100 }
}



const validateMessages = {
  required: '${label} is required!'
}
const { Panel } = Collapse;

export const DetailsJob = ({jobId}) => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [loading, setLoading] = useState(false);
  const [organizationName, setOrganizationName] = useState([]);
  const [job, setJob] = useState([]);
  const [log, setLog] = useState("Waiting logs...");
  useEffect(() => {
    setLoading(true);
    console.log(jobId)
    axiosInstance.get(`organization/${organizationId}/job/${jobId}`)
      .then(response => {
        console.log(response);
        setJob(response.data);
        setLoading(false);
        setOrganizationName(localStorage.getItem(ORGANIZATION_NAME));
        console.log(response.data.data.attributes.output);
        if (response.data.data.attributes.output!= null) {
        axiosClient.get(response.data.data.attributes.output).then(
          resp => {
            console.log(resp);
            setLog(resp.data);
          }
        )
        .catch(err => { console.log(err);setLog("No logs available"); });;
      }
      else
      {
        setLog("Waiting logs...")
      }
      });
  }, [jobId]);

  return (

    <div style={{marginTop:"14px"}}>
      {loading || !job.data ? (
            <p>Data loading...</p>
          ) : (
      <Space direction="vertical" style={{ width: "100%" }}>
        <div>
          <Tag icon={job.data.attributes.status =="completed" ?<CheckCircleOutlined /> :(job.data.attributes.status =="running"?<SyncOutlined spin />:<ClockCircleOutlined /> )} color={job.data.attributes.status =="completed" ?"#2eb039" :(job.data.attributes.status =="running"?"#108ee9":"" )}>{job.data.attributes.status}</Tag> <h2 style={{ display: "inline" }}>Triggered via UI</h2>
        </div>
        <Space direction="vertical" style={{ width: "100%" }}>
          <Collapse defaultActiveKey={['2']}>
            <Panel header={<span><Avatar size="small" shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" /> <b>jcanizalez</b> triggered a run from UI</span>} key="1">
              <p></p>
            </Panel>
          </Collapse>
          <Collapse >
            <Panel header={<span>{job.data.attributes.status =="completed" ?<CheckCircleTwoTone twoToneColor="#52c41a" style={{ fontSize: "20px" }} /> : (job.data.attributes.status =="running" ?<SyncOutlined spin  style={{color:"#108ee9", fontSize: "20px" }} />:<ClockCircleOutlined  style={{ fontSize: "20px" }} />)}<h3 style={{ display: "inline" }}> Job {job.data.attributes.status}</h3></span>} key="2">
              <div id="code-container">
                <div id="code-content">
                  {log}
                </div>
              </div>
            </Panel>
          </Collapse>
        </Space>
      </Space>)}
    </div>
  )
}


 