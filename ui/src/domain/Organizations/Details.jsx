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
  Radio,
  Row,
  Col,
  Select,
} from "antd";
import {
  GitlabOutlined,
  GithubOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  SyncOutlined,
  ExclamationCircleOutlined,
  InfoCircleOutlined,
  CloseCircleOutlined,
  StopOutlined,
  PlusOutlined,
  ImportOutlined,
} from "@ant-design/icons";
import { BiTerminal } from "react-icons/bi";
import { SiTerraform, SiBitbucket, SiAzuredevops } from "react-icons/si";
import { IconContext } from "react-icons";
import axiosInstance, { axiosGraphQL } from "../../config/axiosConfig";
import { useParams, useNavigate } from "react-router-dom";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
const { Content } = Layout;
const { DateTime } = require("luxon");
const { Search } = Input;

export const OrganizationDetails = ({
  setOrganizationName,
  organizationName,
}) => {
  const { id } = useParams();
  const [organization, setOrganization] = useState({});
  const [workspaces, setWorkspaces] = useState([]);
  const [filteredWorkspaces, setFilteredWorkspaces] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filterValue, setFilterValue] = useState("");
  const [searchValue, setSearchValue] = useState("");
  const [filterTags, setFilterTags] = useState([]);
  const [tags, setTags] = useState([]);
  const navigate = useNavigate();
  
  const handleCreate = (e) => {
    navigate("/workspaces/create");
  };

  const handleImport = (e) => {
    navigate("/workspaces/import");
  };

  const iacTypes = [
    {
      id: "terraform",
      name: "Terraform",
      description: "Create an empty template. So you can define your template from scratch.",
      icon: (
        <IconContext.Provider value={{ size: "1.3em" }}>
          <SiTerraform />
        </IconContext.Provider>
      ),
    },
    {
      id: "tofu",
      name: "OpenTofu",
      icon: <img width="18px" src="/providers/opentofu.png" />,
    },
  ];

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

  const handleClick = (workspaceId) => {
    console.log(id);
    navigate("/organizations/" + id + "/workspaces/" + workspaceId);
  };

  const onFilterChange = (e) => {
    setFilterValue(e.target.value);
    applyFilters(searchValue, e.target.value, filterTags);
  };

  const onRadioClick = (e) => {
    const tag = e.target;
    if (tag.type === "radio" && filterValue === tag.value.toString()) {
      setFilterValue("");
      applyFilters(searchValue, "", filterTags);
    }
  };

  const handleChange = (value) => {
    console.log(`selected ${value}`);
    setFilterTags(value);
    applyFilters(searchValue, filterValue, value);
  };

  const onSearch = (value) => {
    setSearchValue(value);
    applyFilters(value, filterValue, filterTags);
  };

  const getTagName = (tagId) => {
    return tags.data.find((tag) => tag.id === tagId)?.attributes?.name;
  };

  const filterOption = (input, option) =>
    (option?.label ?? "").toLowerCase().includes(input.toLowerCase());

  const getIaCIconById = (id) => {
    const item = iacTypes.find((iacType) => iacType.id === id);
    return item ? item.icon : null;
  };

  const applyFilters = (searchValue, filterValue, selectedTags) => {
    // update values
    sessionStorage.setItem("searchValue", searchValue);
    sessionStorage.setItem("filterValue", filterValue);
    sessionStorage.setItem("selectedTags", selectedTags);
    console.log(searchValue || "serach empty");
    console.log(filterValue || "filter empty");
    console.log(selectedTags || "tags empty");

    var filteredWorkspaces = filterWorkspaces(
      workspaces,
      searchValue,
      filterValue,
      selectedTags
    );
    setFilteredWorkspaces(filteredWorkspaces);
    return;
  };

  const filterWorkspaces = (
    workspaces,
    searchValue,
    filterValue,
    selectedTags
  ) => {
    // filter by description and name
    var filteredWorkspaces = workspaces.filter((workspace) => {
      if (workspace.description) {
        return (
          workspace.name.includes(searchValue || workspace.name) ||
          workspace.description?.includes(searchValue || workspace?.description)
        );
      } else {
        return workspace.name.includes(searchValue || workspace.name);
      }
    });

    // filter by status
    filteredWorkspaces = filteredWorkspaces.filter(
      (workspace) =>
        workspace.lastStatus === (filterValue || workspace.lastStatus)
    );

    // filter by tag
    filteredWorkspaces = filteredWorkspaces.filter((workspace) => {
      if (selectedTags && selectedTags.length > 0) {
        return workspace.workspaceTag.edges.some((tag) =>
          selectedTags.includes(tag.node.tagId)
        );
      } else {
        return true;
      }
    });

    return filteredWorkspaces;
  };

  useEffect(() => {
    setLoading(true);
    sessionStorage.setItem(ORGANIZATION_ARCHIVE, id);
    // reload values
    const _searchValue = sessionStorage.getItem("searchValue") || "";
    const _filterValue = sessionStorage.getItem("filterValue") || "";
    const _selectedTags = sessionStorage.getItem("selectedTags") || [];

    setSearchValue(_searchValue);
    setFilterValue(_filterValue);
    setFilterTags(_selectedTags);

    const body = {
      query: `{
        organization(ids: ["${id}"]) {
          edges {
            node {
              id
              name
              workspace(sort: "name") {
                edges {
                  node {
                    id
                    name
                    description
                    source
                    branch
                    terraformVersion
                    iacType
                    workspaceTag {
                      edges { 
                        node {
                          id
                          tagId
                        }
                      } 
                    }
                    job(sort: "id") {
                      edges {
                        node {
                          id
                          status
                          updatedDate
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }`,
    };
    axiosGraphQL
      .post("", body, {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        console.log(response);
        var organizationName =
          response.data.data.organization.edges[0].node.name;
        setOrganization(response.data.data.organization.edges[0].node);

        setupOrganizationIncludes(
          response.data.data.organization.edges[0].node.workspace.edges,
          setWorkspaces,
          setFilteredWorkspaces,
          filterWorkspaces
        );

        axiosInstance.get(`organization/${id}/tag`).then((response) => {
          setTags(response.data);
          setLoading(false);
        });

        sessionStorage.setItem(ORGANIZATION_NAME, organizationName);
        setOrganizationName(organizationName);
      });
  }, [id]);

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: organizationName,
          },
          {
            title: "Workspaces",
          },
        ]}
      />

      <div className="site-layout-content">
        <div className="workspaceWrapper">
          <div className="variableActions">
            <h2>Workspaces</h2>
            <Space>
              <Button
                icon={<ImportOutlined />}
                htmlType="button"
                onClick={handleImport}
              >
                Import workspaces
              </Button>
              <Button
                icon={<PlusOutlined />}
                type="primary"
                htmlType="button"
                onClick={handleCreate}
              >
                New workspace
              </Button>
            </Space>
          </div>
          <Row style={{ marginTop: "10px" }}>
            <Col span={12}>
              <div onClick={onRadioClick}>
                <Radio.Group onChange={onFilterChange} value={filterValue}>
                  <Tooltip
                    placement="bottom"
                    title="Show only workspaces needing attention"
                  >
                    <Radio.Button value="waitingApproval">
                      <ExclamationCircleOutlined
                        style={{ color: "#fa8f37" }}
                      />
                    </Radio.Button>
                  </Tooltip>
                  <Tooltip
                    placement="bottom"
                    title="Show only workspaces with error"
                  >
                    <Radio.Button value="failed">
                      <StopOutlined style={{ color: "#FB0136" }} />
                    </Radio.Button>
                  </Tooltip>
                  <Tooltip
                    placement="bottom"
                    title="Show only running workspaces"
                  >
                    <Radio.Button value="running">
                      <SyncOutlined style={{ color: "#108ee9" }} />
                    </Radio.Button>
                  </Tooltip>
                  <Tooltip
                    placement="bottom"
                    title="Show only successfully completed workspaces"
                  >
                    <Radio.Button value="completed">
                      <CheckCircleOutlined style={{ color: "#2eb039" }} />
                    </Radio.Button>
                  </Tooltip>
                  <Tooltip
                    placement="bottom"
                    title="Show only never executed workspaces"
                  >
                    <Radio.Button value="never executed">
                      <InfoCircleOutlined />
                    </Radio.Button>
                  </Tooltip>
                </Radio.Group>
              </div>
            </Col>
            <Col span={12}>
              <Row justify="end">
                <Col span={11}>
                  <Select
                    mode="multiple"
                    showSearch
                    optionFilterProp="children"
                    allowClear
                    filterOption={filterOption}
                    style={{ width: "100%" }}
                    placeholder="Search by tag"
                    onChange={handleChange}
                    filterSort={(optionA, optionB) =>
                      (optionA?.label ?? "")
                        .toLowerCase()
                        .localeCompare((optionB?.label ?? "").toLowerCase())
                    }
                    loading={!tags.data}
                    options={tags?.data?.map(function (tag) {
                      return { label: tag.attributes.name, value: tag.id };
                    })}
                    defaultValue={filterTags}
                  />
                </Col>
                <Col span={1}></Col>
                <Col span={12}>
                  <Search
                    placeholder="Search by name, description"
                    onSearch={onSearch}
                    defaultValue={searchValue}
                    allowClear
                  />
                </Col>
              </Row>
            </Col>
          </Row>
          <div style={{ clear: "both", paddingTop: "10px" }}>
            <List
              split=""
              loading={{ spinning: loading, tip: "Loading Workspaces..." }}
              className="workspaceList"
              dataSource={filteredWorkspaces}
              pagination={{ showSizeChanger: true, defaultPageSize: 10 }}
              renderItem={(item) => (
                <List.Item>
                  <Card
                    onClick={() => handleClick(item.id)}
                    style={{ width: "100%" }}
                    hoverable
                  >
                    <Space
                      style={{ color: "rgb(82, 87, 97)", width: "100%" }}
                      direction="vertical"
                    >
                      <Row>
                        <Col span={12}>
                          <h3>{item.name}</h3>
                          {item.description}
                        </Col>
                        <Col span={10}>
                          <Row justify="start">
                            <Col span={24}>
                              <Tooltip title="Workspace Tags">
                                {loading || !tags ? (
                                  <p></p>
                                ) : (
                                  item.workspaceTag.edges.map((tag) => (
                                    <Tag color="geekblue">
                                      {getTagName(tag.node.tagId)}
                                    </Tag>
                                  ))
                                )}
                              </Tooltip>
                            </Col>
                          </Row>
                        </Col>
                      </Row>
                      <Space size={40} style={{ marginTop: "25px" }}>
                        <Tag
                          icon={
                            item.lastStatus == "completed" ? (
                              <CheckCircleOutlined />
                            ) : item.lastStatus == "noChanges" ? (
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
                            ) : item.lastStatus === "failed" ? (
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
                          {getIaCIconById(item.iacType)}
                          &nbsp;&nbsp;{item.terraformVersion}
                        </span>
                        {item.branch !== "remote-content" ? (
                          <span>
                            {renderVCSLogo(
                              new URL(fixSshURL(item.source)).hostname
                            )}
                            &nbsp;{" "}
                            <a href={fixSshURL(item.source)} target="_blank">
                              {new URL(fixSshURL(item.source))?.pathname
                                ?.replace(".git", "")
                                ?.substring(1)}
                            </a>
                          </span>
                        ) : (
                          <span
                            style={{
                              verticalAlign: "middle",
                              display: "inline-block",
                            }}
                          >
                            <IconContext.Provider value={{ size: "1.4em" }}>
                              <BiTerminal />
                            </IconContext.Provider>
                            &nbsp;&nbsp;cli/api driven workflow
                          </span>
                        )}
                      </Space>
                    </Space>
                  </Card>
                </List.Item>
              )}
            />
          </div>
        </div>
      </div>
    </Content>
  );
};

function fixSshURL(source) {
  if (source.startsWith("git@")) {
    return source.replace(":", "/").replace("git@", "https://");
  } else {
    return source;
  }
}

function setupOrganizationIncludes(
  includes,
  setWorkspaces,
  setFilteredWorkspaces,
  filter
) {
  let workspaces = [];

  includes.forEach((element) => {
    //get latest job for workspace
    var lastJob = element.node.job.edges?.slice(-1)?.pop()?.node;
    var lastStatus = lastJob?.status ?? "never executed";
    workspaces.push({
      id: element.node.id,
      lastRun: lastJob?.updatedDate,
      lastStatus: lastJob?.status ?? "never executed",
      statusColor:
        lastStatus == "completed"
          ? "#2eb039"
          : lastStatus == "running"
          ? "#108ee9"
          : lastStatus == "waitingApproval"
          ? "#fa8f37"
          : lastStatus === "rejected"
          ? "#FB0136"
          : lastStatus === "failed"
          ? "#FB0136"
          : lastStatus == "noChanges"
          ? "#e037fa"
          : "",
      ...element.node,
    });
  });

  setWorkspaces(workspaces);
  const _searchValue = sessionStorage.getItem("searchValue") || "";
  const _filterValue = sessionStorage.getItem("filterValue") || "";
  const _selectedTags = sessionStorage.getItem("selectedTags") || [];

  var filteredWorkspaces = filter(
    workspaces,
    _searchValue,
    _filterValue,
    _selectedTags
  );

  setFilteredWorkspaces(filteredWorkspaces);
  console.log(workspaces);
}