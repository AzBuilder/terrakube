import { React, useState, useEffect } from "react";
import "./Settings.css";
import {
  Button,
  List,
  Card,
  Divider,
  Row,
  Col,
  Popconfirm,
  Typography,
} from "antd";
import { GithubOutlined, GitlabOutlined } from "@ant-design/icons";
import { AddVCS } from "./AddVCS";
import { useParams } from "react-router-dom";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import { SiBitbucket, SiAzuredevops } from "react-icons/si";
import axiosInstance from "../../config/axiosConfig";
import { IconContext } from "react-icons";
import { ORGANIZATION_NAME } from "../../config/actionTypes";
const { Paragraph } = Typography;
export const VCSSettings = ({ vcsMode }) => {
  const { orgid } = useParams();
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState(vcsMode != null ? vcsMode : "list");
  const [vcs, setVCS] = useState([]);

  const onAddVCS = () => {
    setMode("new");
  };

  const renderVCSLogo = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return <GitlabOutlined style={{ fontSize: "20px" }} />;
      case "BITBUCKET":
        return (
          <IconContext.Provider value={{ size: "20px" }}>
            <SiBitbucket />
          </IconContext.Provider>
        );
      case "AZURE_DEVOPS":
        return (
          <IconContext.Provider value={{ size: "20px" }}>
            <SiAzuredevops />
          </IconContext.Provider>
        );
      default:
        return <GithubOutlined style={{ fontSize: "20px" }} />;
    }
  };

  const renderVCSType = (vcs) => {
    switch (vcs) {
      case "GITLAB":
        return "GitLab";
      case "BITBUCKET":
        return "BitBucket";
      case "AZURE_DEVOPS":
        return "Azure Devops";
      default:
        return "GitHub";
    }
  };

  const getConnectUrl = (vcs, clientId, callbackUrl, endpoint) => {
    switch (vcs) {
      case "GITLAB":
        if(endpoint != null)
          return `${endpoint}/oauth/authorize?client_id=${clientId}&response_type=code&scope=api&&redirect_uri=${callbackUrl}`;
        else
          return `https://gitlab.com/oauth/authorize?client_id=${clientId}&response_type=code&scope=api&&redirect_uri=${callbackUrl}`;
      case "BITBUCKET":
        if(endpoint != null)
          return `${endpoint}/site/oauth2/authorize?client_id=${clientId}&response_type=code&response_type=code&scope=repository`;
        else
          return `https://bitbucket.org/site/oauth2/authorize?client_id=${clientId}&response_type=code&response_type=code&scope=repository`;
      case "AZURE_DEVOPS":
        if(endpoint != null)
          return `${endpoint}/oauth2/authorize?client_id=${clientId}&redirect_uri=${callbackUrl}&response_type=Assertion&scope=vso.code+vso.code_status`;
        else
          return `https://app.vssps.visualstudio.com/oauth2/authorize?client_id=${clientId}&redirect_uri=${callbackUrl}&response_type=Assertion&scope=vso.code+vso.code_status`;
      default:
        if(endpoint != null)
          return `${endpoint}/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=repo`;
        else
          return `https://github.com/login/oauth/authorize?client_id=${clientId}&allow_signup=false&scope=repo`;
    }
  };

  const onDelete = (id) => {
    console.log("deleted " + id);
    axiosInstance.delete(`organization/${orgid}/vcs/${id}`).then((response) => {
      console.log(response);
      loadVCS();
    });
  };

  const getCallBackUrl = (id) => {
    return `${
      new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin
    }/callback/v1/vcs/${id}`;
  };

  useEffect(() => {
    setLoading(true);
    loadVCS();
  }, [orgid]);

  const loadVCS = () => {
    axiosInstance.get(`organization/${orgid}/vcs`).then((response) => {
      console.log(response);
      setVCS(response.data);
      setLoading(false);
    });
  };

  return (
    <div className="setting">
      {mode != "new" ? (
        <div>
          {" "}
          <h1 style={{ paddingBottom: "10px" }}>
            VCS Providers
            <Button
              type="primary"
              onClick={onAddVCS}
              className="addVCS"
              htmlType="button"
            >
              Add a VCS Provider
            </Button>{" "}
          </h1>
          <br />
          {loading || !vcs.data ? (
            <p>Data loading...</p>
          ) : (
            <List
              className="vcsList"
              itemLayout="horizontal"
              dataSource={vcs.data}
              split=""
              renderItem={(item) => (
                <List.Item>
                  <Card
                    style={{ width: "100%" }}
                    title={
                      <span>
                        {renderVCSLogo(item.attributes.vcsType)}&nbsp;&nbsp;
                        {item.attributes.name}
                      </span>
                    }
                    actions={[
                      <div style={{ float: "right" }}>
                        <Button type="default" icon={<EditOutlined />}>
                          Edit Client
                        </Button>
                        &nbsp;&nbsp;&nbsp;
                        <Popconfirm
                          onConfirm={() => {
                            onDelete(item.id);
                          }}
                          style={{ width: "100px" }}
                          title={
                            <p>
                              Deleting this{" "}
                              {renderVCSType(item.attributes.vcsType)} client
                              will disconnect <br /> any workspaces currently
                              using it. <br /> This means that VCS changes will
                              not trigger <br /> jobs on those workspaces.{" "}
                              <br />
                              Are you sure?
                            </p>
                          }
                          okText="Yes"
                          cancelText="Cancel"
                        >
                          {" "}
                          <Button
                            type="primary"
                            icon={<DeleteOutlined />}
                            danger
                          >
                            Delete Client
                          </Button>
                        </Popconfirm>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                      </div>,
                    ]}
                  >
                    <p className="paragraph">
                      <Row>
                        <Col span={6}>Callback URL</Col>
                        <Col span={18}>
                          <Paragraph copyable>
                            {" "}
                            {getCallBackUrl(
                              item.attributes?.callback ?? item.id
                            )}{" "}
                          </Paragraph>
                        </Col>
                      </Row>
                    </p>
                    <Divider />
                    <p className="paragraph">
                      <Row>
                        <Col span={6}>API URL</Col>
                        <Col span={18}>
                          {item.attributes?.apiUrl}
                        </Col>
                      </Row>
                    </p>
                    <Divider />
                    <p className="paragraph">
                      <Row>
                        <Col span={6}>Created</Col>
                        <Col span={18}>{item.attributes.createdDate}</Col>
                      </Row>
                    </p>
                    <Divider />
                    <p className="paragraph">
                      <Row>
                        <Col span={6}>
                          {item.attributes.status !== "COMPLETED" ? (
                            <span>
                              Connect to{" "}
                              {renderVCSType(item.attributes.vcsType)}
                            </span>
                          ) : (
                            <span>Connection</span>
                          )}
                        </Col>
                        <Col span={12}>
                          {item.attributes.status !== "COMPLETED" ? (
                            <span>
                              Connecting to{" "}
                              {renderVCSType(item.attributes.vcsType)} will take
                              your {renderVCSType(item.attributes.vcsType)} user
                              through the OAuth flow to create an authorization
                              token for access to all repositories for this
                              organization. This means that your currently
                              logged in {renderVCSType(item.attributes.vcsType)}{" "}
                              user token will be used for all{" "}
                              {renderVCSType(item.attributes.vcsType)} API
                              interactions by any Terrakube user anywhere within
                              the scope of{" "}
                              <b>{sessionStorage.getItem(ORGANIZATION_NAME)}</b>.
                            </span>
                          ) : (
                            <span>
                              A connection was made on{" "}
                              {item.attributes.createdDate} by authenticating
                              via OAuth as{" "}
                              {renderVCSType(item.attributes.vcsType)} user{" "}
                              <b>{item.attributes.createdBy}</b>, which assigned
                              an OAuth token for use by all Terrakube users in
                              the{" "}
                              <b>{sessionStorage.getItem(ORGANIZATION_NAME)}</b>{" "}
                              organization.
                            </span>
                          )}
                        </Col>
                        <Col span={6}>
                          {item.attributes.status !== "COMPLETED" ? (
                            <Button
                              type="primary"
                              target="_blank"
                              href={getConnectUrl(
                                item.attributes.vcsType,
                                item.attributes.clientId,
                                getCallBackUrl(
                                  item.attributes?.callback ?? item.id
                                ),
                                  item.attributes.endpoint
                              )}
                              size="small"
                            >
                              Connect to{" "}
                              {renderVCSType(item.attributes.vcsType)}
                            </Button>
                          ) : (
                            <span />
                          )}
                        </Col>
                      </Row>
                    </p>
                  </Card>
                </List.Item>
              )}
            />
          )}
        </div>
      ) : (
        <AddVCS setMode={setMode} loadVCS={loadVCS} />
      )}
    </div>
  );
};
