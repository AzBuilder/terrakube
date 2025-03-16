import { React, useState, useEffect } from "react";
import {
  Tag,
  Space,
  Collapse,
  Avatar,
  Card,
  Button,
  message,
  Radio,
  Spin,
} from "antd";
import { ORGANIZATION_ARCHIVE } from "../../config/actionTypes";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import {
  CheckCircleOutlined,
  CheckCircleTwoTone,
  SyncOutlined,
  ClockCircleOutlined,
  ExclamationCircleOutlined,
  CheckOutlined,
  CloseOutlined,
  CommentOutlined,
  CloseCircleTwoTone,
  StopOutlined,
  UserOutlined,
} from "@ant-design/icons";
import Ansi from "ansi-to-react";
import parse from "html-react-parser";

const { DateTime } = require("luxon");

export const DetailsJob = ({ jobId }) => {
  const organizationId = sessionStorage.getItem(ORGANIZATION_ARCHIVE);
  const [loading, setLoading] = useState(false);
  const [job, setJob] = useState([]);
  const [steps, setSteps] = useState([]);
  const [uiType, setUIType] = useState("structured");
  const [uiTemplates, setUITemplates] = useState({});
  const outputLog = async (output, status) => {
    if (output != null) {
      const apiDomain = new URL(window._env_.REACT_APP_TERRAKUBE_API_URL)
        .hostname;
      if (output.includes(apiDomain))
        return axiosInstance
          .get(output)
          .then((resp) => resp.data)
          .catch((err) => "No logs available");
      else
        return axiosClient
          .get(output)
          .then((resp) => resp.data)
          .catch((err) => "No logs available");
    } else {
      if (status === "running") return "Initializing the backend...";
      else return "Waiting logs...";
    }
  };

  const handleComingSoon = (e) => {
    message.info("Coming Soon!");
  };

  const onChange = (e) => {
    setUIType(e.target.value);
  };

  const handleCancel = (e) => {
    const body = {
      data: {
        type: "job",
        id: jobId,
        attributes: {
          status: "cancelled",
        },
      },
    };

    axiosInstance
      .patch(`organization/${organizationId}/job/${jobId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        message.success("Job Cancelled Succesfully");
        loadJob();
      });
  };

  const getIconStatus = (item) => {
    switch (item.status) {
      case "completed":
        return (
          <CheckCircleTwoTone
            twoToneColor="#52c41a"
            style={{ fontSize: "20px" }}
          />
        );
      case "noChanges":
        return (
            <CheckCircleTwoTone
                twoToneColor="#52c41a"
                style={{ fontSize: "20px" }}
            />
        );
      case "notExecuted":
        return (
            <CheckCircleTwoTone
                twoToneColor="#fa8f37"
                style={{ fontSize: "20px" }}
            />
        );
      case "running":
        return (
          <SyncOutlined spin style={{ color: "#108ee9", fontSize: "20px" }} />
        );
      case "failed":
        return (
          <CloseCircleTwoTone
            twoToneColor="#FB0136"
            style={{ fontSize: "20px" }}
          />
        );
      case "cancelled":
        return (
          <CloseCircleTwoTone
            twoToneColor="#FB0136"
            style={{ fontSize: "20px" }}
          />
        );
      default:
        return <ClockCircleOutlined style={{ fontSize: "20px" }} />;
    }
  };

  const handleApprove = (e) => {
    const body = {
      data: {
        type: "job",
        id: jobId,
        attributes: {
          status: "approved",
        },
      },
    };

    axiosInstance
      .patch(`organization/${organizationId}/job/${jobId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
      });
  };

  const handleRejected = (e) => {
    const body = {
      data: {
        type: "job",
        id: jobId,
        attributes: {
          status: "rejected",
        },
      },
    };

    axiosInstance
      .patch(`organization/${organizationId}/job/${jobId}`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
      });
  };

  const sortbyName = (a, b) => {
    if (a.stepNumber < b.stepNumber) return -1;
    if (a.stepNumber > b.stepNumber) return 1;
    return 0;
  };

  useEffect(() => {
    setLoading(true);
    loadJob();
    loadContext();
    setLoading(false);
    const interval = setInterval(() => {
      loadJob();
      loadContext();
    }, 5000);
    return () => clearInterval(interval);
  }, [jobId]);

  const loadJob = () => {
    let jobSteps = [];
    axiosInstance
      .get(`organization/${organizationId}/job/${jobId}?include=step`)
      .then((response) => {
        (async () => {
          setJob(response.data);

          if (response.data.included != null) {
            for (const element of response.data.included) {
              let log = await outputLog(
                element.attributes.output,
                element.attributes.status
              );
              jobSteps.push({
                id: element.id,
                stepNumber: element.attributes.stepNumber,
                status: element.attributes.status,
                output: element.attributes.output,
                name: element.attributes.name,
                outputLog: log,
              });
            }
          }
          setSteps(jobSteps.sort(sortbyName));
        })();
      });
  };

  const loadContext = () => {
    const api = new URL(window._env_.REACT_APP_TERRAKUBE_API_URL);

    axiosInstance
      .get(`${api.protocol}//${api.host}/context/v1/${jobId}`)
      .then((response) => {
        console.log("terrakube");
        console.log(response?.data?.terrakubeUI);
        if (response?.data?.terrakubeUI)
          setUITemplates(response?.data?.terrakubeUI);
      });
  };
  return (
    <div style={{ marginTop: "14px" }}>
      {loading || !job.data || !steps ? (
        <Spin spinning={true} tip="Loading Job...">
          <p style={{ marginTop: "50px" }}></p>
        </Spin>
      ) : (
        <Space direction="vertical" style={{ width: "100%" }}>
          <div>
            <Tag
              icon={
                job.data.attributes.status === "completed" ? (
                  <CheckCircleOutlined />
                ) : job.data.attributes.status === "running" ? (
                  <SyncOutlined spin />
                ) : job.data.attributes.status === "waitingApproval" ? (
                  <ExclamationCircleOutlined />
                ) : job.data.attributes.status === "cancelled" ? (
                  <StopOutlined />
                ) : job.data.attributes.status === "failed" ? (
                  <StopOutlined />
                ) : (
                  <ClockCircleOutlined />
                )
              }
              color={
                job.data.attributes.status === "completed"
                  ? "#2eb039":
                  job.data.attributes.status === "noChanges"
                  ? "#2eb039" :
                  job.data.attributes.status === "notExecuted"
                  ? "#fa8f37"
                  : job.data.attributes.status === "running"
                  ? "#108ee9"
                  : job.data.attributes.status == "waitingApproval"
                  ? "#fa8f37"
                  : job.data.attributes.status == "rejected"
                  ? "#FB0136"
                  : job.data.attributes.status == "failed"
                  ? "#FB0136"
                  : ""
              }
            >
              {job.data.attributes.status}
            </Tag>{" "}
            <h2 style={{ display: "inline" }}>Triggered via UI</h2>
          </div>

          <Collapse
            items={[
              {
                key: "1",
                label: (
                  <span>
                    <Avatar
                      size="small"
                      shape="square"
                      icon={<UserOutlined />}
                    />{" "}
                    <b>{job.data.attributes.createdBy}</b> triggered a run from{" "}
                    {job.data.attributes.via || "UI"}{" "}
                    {DateTime.fromISO(
                      job.data.attributes.createdDate
                    ).toRelative()}
                  </span>
                ),
                children: <p></p>,
              },
            ]}
          />
          {steps.length > 0 ? (
            steps.map((item) => (
              <>
                <style>
                  {`
                .ant-collapse .ant-collapse-content > .ant-collapse-content-box {
                  padding: 0 !important;
                }
              `}
                </style>
                <Collapse
                  style={{ width: "100%" }}
                  defaultActiveKey={item.status === "running" ? ["2"] : []}
                  items={[
                    {
                      key: "2",
                      label: (
                        <span>
                          {getIconStatus(item)}
                          <h3 style={{ display: "inline" }}>
                            &nbsp; {item.name} {item.status}
                          </h3>
                        </span>
                      ),
                      children: (
                        <>
                          {console.log(uiTemplates)}
                          {uiTemplates.hasOwnProperty(item.stepNumber) ? (
                            <>
                              <div
                                style={{
                                  textAlign: "right",
                                  padding: "5px",
                                }}
                              >
                                <Radio.Group
                                  onChange={onChange}
                                  value={uiType}
                                  size="small"
                                >
                                  <Radio.Button value="structured">
                                    Structured
                                  </Radio.Button>
                                  <Radio.Button value="console">
                                    Console
                                  </Radio.Button>
                                </Radio.Group>
                              </div>
                              {uiType === "structured" ? (
                                <div>{parse(uiTemplates[item.stepNumber])}</div>
                              ) : (
                                <div id="code-container">
                                  <div id="code-content">
                                    <Ansi>{item.outputLog}</Ansi>
                                  </div>
                                </div>
                              )}
                            </>
                          ) : (
                            <div id="code-container">
                              <div id="code-content">
                                <Ansi>{item.outputLog}</Ansi>
                              </div>
                            </div>
                          )}
                        </>
                      ),
                    },
                  ]}
                />
              </>
            ))
          ) : (
            <span />
          )}

          {job.data.attributes.status === "waitingApproval" ? (
            <div style={{ margin: "auto", width: "50%", marginTop: "20px" }}>
              <Card
                title={
                  <span style={{ fontSize: "14px" }}>
                    <b>Needs Confirmation:</b> Someone from{" "}
                    <b>{job.data.attributes.approvalTeam}</b> must confirm to
                    continue.
                  </span>
                }
              >
                <Space size={20}>
                  <Button
                    icon={<CheckOutlined />}
                    onClick={handleApprove}
                    type="primary"
                  >
                    Approve
                  </Button>
                  <Button
                    icon={<CloseOutlined />}
                    onClick={handleRejected}
                    type="primary"
                    danger
                  >
                    Discard
                  </Button>
                  <Button icon={<CommentOutlined />} onClick={handleComingSoon}>
                    Add Comment
                  </Button>
                </Space>
              </Card>
            </div>
          ) : (
            <span />
          )}

          {job.data.attributes.status === "running" ||
          job.data.attributes.status === "pending" ? (
            <div style={{ margin: "auto", width: "50%", marginTop: "20px" }}>
              <Card
                title={
                  <span style={{ fontSize: "14px" }}>
                    <b>Cancelable:</b> You can cancel this job to stop it from
                    executing.
                  </span>
                }
              >
                <Space size={20}>
                  <Button
                    icon={<StopOutlined />}
                    onClick={handleCancel}
                    type="default"
                    danger
                  >
                    Cancel Job
                  </Button>
                  <Button icon={<CommentOutlined />} onClick={handleComingSoon}>
                    Add Comment
                  </Button>
                </Space>
              </Card>
            </div>
          ) : (
            <span />
          )}
        </Space>
      )}
    </div>
  );
};
