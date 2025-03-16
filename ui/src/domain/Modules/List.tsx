import { React, useState, useEffect } from "react";
import {
  Button,
  Layout,
  Breadcrumb,
  Card,
  List,
  Space,
  Input,
  Tag,
} from "antd";
import axiosInstance from "../../config/axiosConfig";
import { useParams, useNavigate, Link } from "react-router-dom";
import {
  CloudUploadOutlined,
  CloudOutlined,
  ClockCircleOutlined,
  DownloadOutlined,
} from "@ant-design/icons";
import { VscAzure } from "react-icons/vsc";
import { FaAws } from "react-icons/fa";
import { RiFolderHistoryLine } from "react-icons/ri";
import { IconContext } from "react-icons";
import { MdBusiness } from "react-icons/md";
import { compareVersions } from "../Workspaces/Workspaces";
import "./Module.css";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
const { DateTime } = require("luxon");
const { Content } = Layout;
const include = { MODULE: "module" };
const { Search } = Input;

export const ModuleList = ({ setOrganizationName, organizationName }) => {
  const { orgid } = useParams();
  const [organization, setOrganization] = useState({});
  const [modules, setModules] = useState([]);
  const [filteredModules, setFilteredModules] = useState([]);
  const [filterValue, setFilterValue] = useState("");
  const [searchValue, setSearchValue] = useState("");
  const [loading, setLoading] = useState(false);

  const onSearch = (value) => {
    setSearchValue(value);
    applyFilters(value, filterValue);
  };

  const applyFilters = (searchValue, filterValue) => {
    if (searchValue !== "" && filterValue !== "") {
      var filteredModules = modules.filter(
        (modules) =>
          modules.name.includes(searchValue) &&
          modules.lastStatus === filterValue
      );
      setFilteredModules(filteredModules);
      return;
    }

    if (searchValue !== "") {
      var filteredModules = modules.filter((modules) =>
        modules.name.includes(searchValue)
      );
      setFilteredModules(filteredModules);
      return;
    }

    if (filterValue !== "") {
      console.log("filter by status " + filterValue);
      var filteredModules = modules.filter(
        (modules) => modules.lastStatus === filterValue
      );
      setFilteredModules(filteredModules);
      return;
    }
    console.log("no filter");
    setFilteredModules(modules);
  };

  useEffect(() => {
    setLoading(true);
    sessionStorage.setItem(ORGANIZATION_ARCHIVE, orgid);
    axiosInstance
      .get(`organization/${orgid}?include=module`)
      .then((response) => {
        console.log(response);
        setOrganization(response.data);

        if (response.data.included) {
          setupOrganizationIncludes(
            response.data.included,
            setModules,
            setFilteredModules
          );
        }

        setLoading(false);
        sessionStorage.setItem(
          ORGANIZATION_NAME,
          response.data.data.attributes.name
        );
        setOrganizationName(response.data.data.attributes.name);
      });
  }, [orgid]);
  const navigate = useNavigate();
  const handleClick = (id) => {
    console.log(id);
    navigate("/organizations/" + orgid + "/registry/" + id);
  };

  const renderLogo = (provider) => {
    switch (provider) {
      case "azurerm":
        return (
          <IconContext.Provider value={{ color: "#008AD7", size: "1.5em" }}>
            <VscAzure />
          </IconContext.Provider>
        );
      case "aws":
        return (
          <IconContext.Provider value={{ color: "#232F3E", size: "1.5em" }}>
            <FaAws />
          </IconContext.Provider>
        );
      default:
        return <CloudOutlined />;
    }
  };

  const handlePublish = () => {
    navigate("/organizations/" + orgid + "/registry/create");
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: organizationName,
          },
          {
            title: <Link to={`/organizations/${orgid}/registry`}>Modules</Link>,
          },
        ]}
      />

      <div className="site-layout-content">
          <div className="modulesWrapper">
            <div className="variableActions">
              <h2>Modules</h2>
              <Button
                type="primary"
                htmlType="button"
                icon={<CloudUploadOutlined />}
                onClick={handlePublish}
              >
                Publish module
              </Button>
            </div>
            <Search
              placeholder="Filter modules"
              onSearch={onSearch}
              allowClear
              style={{ width: "100%", marginTop: "10px" }}
            />
            <List
              split=""
              loading={{ spinning: loading, tip: "Loading Modules..."}}
              className="moduleList"
              dataSource={filteredModules}
              pagination={{ showSizeChanger: true, defaultPageSize: 10 }}
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
                        <Tag color="blue">
                          <span>
                            <MdBusiness /> Private
                          </span>
                        </Tag>
                        <span>
                          {renderLogo(item.provider)}&nbsp;&nbsp;{item.provider}
                        </span>
                        <span>
                          <IconContext.Provider value={{ size: "1.3em" }}>
                            <RiFolderHistoryLine />
                          </IconContext.Provider>
                          &nbsp;&nbsp;
                          {item.versions.sort(compareVersions).reverse()[0]}
                        </span>
                        <span>
                          <ClockCircleOutlined />
                          &nbsp;&nbsp;
                          {DateTime.fromISO(item.createdDate).toRelative()}
                        </span>
                        <span>
                          <DownloadOutlined />
                          &nbsp;&nbsp; {item.downloadQuantity}
                        </span>
                      </Space>
                    </Space>
                  </Card>
                </List.Item>
              )}
            />
          </div>
      </div>
    </Content>
  );
};

function setupOrganizationIncludes(includes, setModules, setFilteredModules) {
  let modules = [];

  includes.forEach((element) => {
    switch (element.type) {
      case include.MODULE:
        modules.push({
          id: element.id,
          ...element.attributes,
        });
        break;
      default:
        break;
    }
  });

  setModules(modules);
  setFilteredModules(modules);
}