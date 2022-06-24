import { React, useState, useEffect } from "react";
import {
  Button,
  Layout,
  Breadcrumb,
  Input,
  List,
  Space,
  Card,
  Tag,
  Tooltip,
} from "antd";
import {
  GitlabOutlined,
  GithubOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  SyncOutlined,
  ExclamationCircleOutlined,
  InfoCircleOutlined,
  CloseCircleTwoTone,
  CloseCircleOutlined,
  StopOutlined,
} from "@ant-design/icons";

import { SiTerraform, SiBitbucket, SiAzuredevops } from "react-icons/si";
import { IconContext } from "react-icons";
import axiosInstance from "../../config/axiosConfig";
import { useParams, useHistory } from "react-router-dom";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
const { Content } = Layout;
const { DateTime } = require("luxon");
const { Search } = Input;
const include = {
  WORKSPACE: "workspace",
};

export const OrganizationDetails = ({
  setOrganizationName,
  organizationName,
}) => {
  const { id } = useParams();
  const [organization, setOrganization] = useState({});
  const [workspaces, setWorkspaces] = useState([]);
  const [loading, setLoading] = useState(false);
  const history = useHistory();
  const handleCreate = (e) => {
    history.push("/workspaces/create");
  };

  const renderVCSLogo = (hostname) => {
    if (hostname.includes("gitlab"))
      return (
        <Tooltip title="Gitlab">
          <GitlabOutlined style={{ fontSize: "18px" }} />
        </Tooltip>
      );
    if (hostname.includes("bitbucket"))
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <Tooltip title="Bit Bucket">
            <SiBitbucket />
          </Tooltip>
        </IconContext.Provider>
      );
    if (hostname.includes("azure"))
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <Tooltip title="Azure Devops">
            <SiAzuredevops />
          </Tooltip>
        </IconContext.Provider>
      );

    return (
      <Tooltip title="Github">
        <GithubOutlined style={{ fontSize: "18px" }} />
      </Tooltip>
    );
  };

  const handleClick = (id) => {
    console.log(id);
    history.push("/workspaces/" + id);
  };

  useEffect(() => {
    setLoading(true);
    localStorage.setItem(ORGANIZATION_ARCHIVE, id);
    axiosInstance
      .get(`organization/${id}?include=workspace,job`)
      .then((response) => {
        console.log(response);
        setOrganization(response.data);

        if (response.data.included) {
          setupOrganizationIncludes(response.data.included, setWorkspaces);
        }

        setLoading(false);
        localStorage.setItem(
          ORGANIZATION_NAME,
          response.data.data.attributes.name
        );
        setOrganizationName(response.data.data.attributes.name);
      });
  }, [id]);

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>{organizationName}</Breadcrumb.Item>
        <Breadcrumb.Item>Workspaces</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        {loading || !organization.data || !workspaces ? (
          <p>Data loading...</p>
        ) : (
          <div className="workspaceWrapper">
            <div className="variableActions">
              <h2>Workspaces</h2>
              <Button type="primary" htmlType="button" onClick={handleCreate}>
                New workspace
              </Button>
            </div>
            <Search placeholder="Filter workspaces" style={{ width: "100%" }} />
            <List
              split=""
              className="workspaceList"
              dataSource={workspaces}
              renderItem={(item) => (
                <List.Item>
                  <Card
                    onClick={() => handleClick(item.id)}
                    style={{ width: "100%" }}
                    hoverable
                  >
                    <Space
                      style={{ color: "rgb(82, 87, 97)" }}
                      direction="vertical"
                    >
                      <h3>{item.name}</h3>
                      {item.description}
                      <Space size={40} style={{ marginTop: "25px" }}>
                        <Tag
                          icon={
                            item.lastStatus == "completed" ? (
                              <CheckCircleOutlined />
                            ) : item.lastStatus == "running" ? (
                              <SyncOutlined spin />
                            ) : item.lastStatus === "waitingApproval" ? (
                              <ExclamationCircleOutlined />
                            ) : item.lastStatus === "never executed" ? (
                              <InfoCircleOutlined />
                            ) : item.lastStatus === "rejected" ? (
                              <CloseCircleOutlined />
                            ) : item.lastStatus === "cancelled" ? (
                              <StopOutlined />
                            ) : (
                              <ClockCircleOutlined />
                            )
                          }
                          color={item.statusColor}
                        >
                          {item.lastStatus}
                        </Tag>{" "}
                        <br />
                        <span>
                          <ClockCircleOutlined />
                          &nbsp;&nbsp;
                          {DateTime.fromISO(item.lastRun).toRelative() ??
                            "never executed"}
                        </span>
                        <span>
                          <IconContext.Provider value={{ size: "1.3em" }}>
                            <SiTerraform />
                          </IconContext.Provider>
                          &nbsp;&nbsp;{item.terraformVersion}
                        </span>
                        <span>
                          {renderVCSLogo(new URL(item.source).hostname)}&nbsp;{" "}
                          <a href={item.source} target="_blank">
                            {new URL(item.source)?.pathname
                              ?.replace(".git", "")
                              ?.substring(1)}
                          </a>
                        </span>
                      </Space>
                    </Space>
                  </Card>
                </List.Item>
              )}
            />
          </div>
        )}
      </div>
    </Content>
  );
};

function setupOrganizationIncludes(includes, setWorkspaces) {
  let workspaces = [];

  includes.forEach((element) => {
    switch (element.type) {
      case include.WORKSPACE:
        //get latest job for workspace
        var lastJobId = element.relationships?.job?.data?.slice(-1)?.pop()?.id;
        var lastRunDate = includes.find(
          (x) => x.type === "job" && x.id === lastJobId
        )?.attributes?.updatedDate;
        var lastStatus =
          includes.find((x) => x.type === "job" && x.id === lastJobId)
            ?.attributes?.status ?? "never executed";
        console.log("id", lastJobId);
        workspaces.push({
          id: element.id,
          lastRun: lastRunDate,
          lastStatus: lastStatus,
          statusColor:
            lastStatus == "completed"
              ? "#2eb039"
              : lastStatus == "running"
              ? "#108ee9"
              : lastStatus == "waitingApproval"
              ? "#fa8f37"
              : lastStatus === "rejected"
              ? "#FB0136"
              : "",
          ...element.attributes,
        });
        break;
      default:
        break;
    }
  });

  setWorkspaces(workspaces);
}
