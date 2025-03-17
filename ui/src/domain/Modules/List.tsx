import { ClockCircleOutlined, CloudOutlined, CloudUploadOutlined, DownloadOutlined } from "@ant-design/icons";
import { Breadcrumb, Button, Card, Input, Layout, List, Space, Tag } from "antd";
import { DateTime } from "luxon";
import { useEffect, useState } from "react";
import { IconContext } from "react-icons";
import { FaAws } from "react-icons/fa";
import { MdBusiness } from "react-icons/md";
import { RiFolderHistoryLine } from "react-icons/ri";
import { VscAzure } from "react-icons/vsc";
import { Link, useNavigate, useParams } from "react-router-dom";
import { ORGANIZATION_ARCHIVE, ORGANIZATION_NAME } from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import { FlatModule, ModuleModel } from "../types";
import { compareVersions } from "../Workspaces/Workspaces";
import "./Module.css";
const { Content } = Layout;
const include = { MODULE: "module" };
const { Search } = Input;

type Params = {
  orgid: string;
};

type Props = {
  organizationName: string;
  setOrganizationName: React.Dispatch<React.SetStateAction<string>>;
};

export const ModuleList = ({ setOrganizationName, organizationName }: Props) => {
  const { orgid } = useParams<Params>();
  const [modules, setModules] = useState<FlatModule[]>([]);
  const [filteredModules, setFilteredModules] = useState<FlatModule[]>([]);
  const [loading, setLoading] = useState(false);

  const onSearch = (value: string) => {
    applyFilters(value);
  };

  const applyFilters = (searchValue: string) => {
    if (searchValue !== "") {
      var filteredModules = modules.filter((modules) => modules.name.includes(searchValue));
      setFilteredModules(filteredModules);
      return;
    }

    setFilteredModules(modules);
  };

  useEffect(() => {
    setLoading(true);
    sessionStorage.setItem(ORGANIZATION_ARCHIVE, orgid!);
    axiosInstance.get(`organization/${orgid}?include=module`).then((response) => {
      console.log(response);
      if (response.data.included) {
        setupOrganizationIncludes(response.data.included, setModules, setFilteredModules);
      }

      setLoading(false);
      sessionStorage.setItem(ORGANIZATION_NAME, response.data.data.attributes.name);
      setOrganizationName(response.data.data.attributes.name);
    });
  }, [orgid]);
  const navigate = useNavigate();
  const handleClick = (id: string) => {
    console.log(id);
    navigate("/organizations/" + orgid + "/registry/" + id);
  };

  const renderLogo = (provider: string) => {
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
            <Button type="primary" htmlType="button" icon={<CloudUploadOutlined />} onClick={handlePublish}>
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
            split
            loading={{ spinning: loading, tip: "Loading Modules..." }}
            className="moduleList"
            dataSource={filteredModules}
            pagination={{ showSizeChanger: true, defaultPageSize: 10 }}
            renderItem={(item) => (
              <List.Item>
                <Card onClick={() => handleClick(item.id)} style={{ width: "100%" }} hoverable>
                  <Space style={{ color: "rgb(82, 87, 97)" }} direction="vertical">
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

function setupOrganizationIncludes(
  includes: ModuleModel[],
  setModules: React.Dispatch<React.SetStateAction<FlatModule[]>>,
  setFilteredModules: React.Dispatch<React.SetStateAction<FlatModule[]>>
) {
  let modules: FlatModule[] = [];

  includes.forEach((element: ModuleModel) => {
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
