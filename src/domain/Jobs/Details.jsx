import { React, useState, useEffect } from 'react';
import { Tag, Space, Collapse, Avatar, Skeleton } from "antd";
import { ORGANIZATION_ARCHIVE } from '../../config/actionTypes';
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import { CheckCircleOutlined, CheckCircleTwoTone, SyncOutlined, ClockCircleOutlined } from '@ant-design/icons';
import Ansi from "ansi-to-react";
const { Panel } = Collapse;

export const DetailsJob = ({ jobId }) => {
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);
  const [loading, setLoading] = useState(false);
  const [job, setJob] = useState([]);
  const [steps, setSteps] = useState([]);
  const outputLog = async (output, status) => {
    if (output != null) {
      return axiosClient.get(output).then(
        resp => resp.data
      ).catch(err => "No logs available");
    }
    else {
      if (status === "running")
        return "Initializing the backend...";
      else
        return "Waiting logs...";
    }
  }

  const sortbyName = (a, b) => {
    if (a.stepNumber < b.stepNumber)
      return -1;
    if (a.stepNumber > b.stepNumber)
      return 1;
    return 0;
  }

  useEffect(() => {
    setLoading(true);
    console.log(jobId)
    loadJob();
    setLoading(false);
    const interval = setInterval(() => {
      loadJob();
    }, 1000);
    return () => clearInterval(interval);
  }, [jobId]);

  const loadJob = () => {
    let jobSteps = [];
    axiosInstance.get(`organization/${organizationId}/job/${jobId}?include=step`)
      .then(response => {
        (async () => {
          setJob(response.data);

          if (response.data.included != null) {
            for (const element of response.data.included) {
              let log = await outputLog(element.attributes.output, element.attributes.status);
              jobSteps.push({
                id: element.id,
                stepNumber: element.attributes.stepNumber,
                status: element.attributes.status,
                output: element.attributes.output,
                outputLog: log
              });
            }
          }
          setSteps(jobSteps.sort(sortbyName));

        })();
      });
  }
  return (

    <div style={{ marginTop: "14px" }}>
      {loading || !job.data || !steps ? (
        <p>Data loading...</p>
      ) : (
        <Space direction="vertical" style={{ width: "100%" }}>
          <div>
            <Tag icon={job.data.attributes.status === "completed" ? <CheckCircleOutlined /> : (job.data.attributes.status === "running" ? <SyncOutlined spin /> : <ClockCircleOutlined />)} color={job.data.attributes.status === "completed" ? "#2eb039" : (job.data.attributes.status === "running" ? "#108ee9" : "")}>{job.data.attributes.status}</Tag> <h2 style={{ display: "inline" }}>Triggered via UI</h2>
          </div>

          <Collapse >
            <Panel header={<span><Avatar size="small" shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" /> <b>jcanizalez</b> triggered a run from UI</span>} key="1">
              <p></p>
            </Panel>
          </Collapse>
          {steps.length > 0 ? (steps.map(item => (
            <Collapse style={{ width: "100%" }} defaultActiveKey={item.status === "running" ? ['2'] : []} >
              <Panel header={<span>{item.status === "completed" ? <CheckCircleTwoTone twoToneColor="#52c41a" style={{ fontSize: "20px" }} /> : (item.status === "running" ? <SyncOutlined spin style={{ color: "#108ee9", fontSize: "20px" }} /> : <ClockCircleOutlined style={{ fontSize: "20px" }} />)}<h3 style={{ display: "inline" }}> Step {item.stepNumber} {item.status}</h3></span>} key="2">
                <div id="code-container">
                  <div id="code-content">
                    <Ansi>
                      {item.outputLog}
                    </Ansi>
                  </div>
                </div>
              </Panel>
            </Collapse>
          ))) : (
            <span/>
          )}


        </Space>)}
    </div>
  )
}


