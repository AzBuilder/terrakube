import { React, useState, useEffect } from "react";
import {
  Layout,
  Breadcrumb,
  Card,
  List,
  Typography,
  Tabs,
  Avatar,
  Form,
  Input,
  Button,
  InputNumber,
  Select,
} from "antd";
import YAML from "yaml";
import axiosInstance from "../../config/axiosConfig";
import { unzip } from "unzipit";

const { Title } = Typography;
const { Content } = Layout;
const { TabPane } = Tabs;
const hcl = require("hcl2-parser");

export const Portal = () => {
  const [mode, setMode] = useState("list");
  const [moduleVariables, setModuleVariables] = useState([]);
  const [service, setService] = useState({});
  const [loading, setLoading] = useState(false);

  let servicesYaml = `
  - name: "Virtual Machine"
    moduleSource: "azure/repository/sample"
    moduleVersion: "1.1.0"
    description: "Create a virtual machine that runs Linux or Windows. Select an image from Azure marketplace or use your own customized image."
    iconUrl: "https://pbs.twimg.com/media/FPk1KFTXIAYrBC6.png"
  - name: "Grafana"
    moduleSource: "azure/repository/sample"
    moduleVersion: "1.1.0"
    description: "Create a virtual machine that runs Linux or Windows. Select an image from Azure marketplace or use your own customized image."
    iconUrl: "https://cdn.worldvectorlogo.com/logos/grafana.svg"
    variables:
      time:
         type: "inputNumber"
  - name: "Service 3"
    moduleSource: "azure/repository/sample"
    moduleVersion: "1.1.0"
    iconUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfOAminWvMkr1_XtolJpSX-uRnvnvdcNwh-w&usqp=CAU"
    variables:
      time:
         label: "Time label"
         type: "inputNumber"
      name:
         type: "select"
         options:
            - "Size 1"
            - "Size 2"
  - name: "Cloud Storage"
    moduleSource: "azure/repository/sample"
    moduleVersion: "1.0.0"
    iconUrl: "https://download.logo.wine/logo/Google_Storage/Google_Storage-Logo.wine.png"
    variables:
      time:
         label: "My label"
  `;

  const resources = [
    {
      title: "My VM",
      resourceUrl: "https://pbs.twimg.com/media/FPk1KFTXIAYrBC6.png",
    },
    {
      title: "My Bucket",
      resourceUrl:
        "https://download.logo.wine/logo/Google_Storage/Google_Storage-Logo.wine.png",
    },
  ];

  const handleClick = (item) => {
    setLoading(true);
    setService(item);
    setMode("create");
    loadModuleVariables(item);
  };

  const loadModuleVariables = (item) => {
    axiosInstance
      .get(
        `${window._env_.REACT_APP_REGISTRY_URI}/terraform/modules/v1/${item.moduleSource}/${item.moduleVersion}/download`
      )
      .then((resp) => {
        readHCL(resp.headers["x-terraform-get"]);
      });
  };

  async function readHCL(url) {
    const { entries } = await unzip(url);
    var hclString = "";
    for (const [name, entry] of Object.entries(entries)) {
      if (name.includes(".tf") && !name.includes("/")) {
        var contentText = await entry.text();
        hclString += "\n" + contentText;
      }
    }

    const hclResult = hcl.parseToObject(hclString);
    setModuleVariables(Object.keys(hclResult[0]?.variable));
    setLoading(false);
  }

  const cancelClick = () => {
    setMode("list");
  };

  useEffect(() => {}, []);
  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}></Breadcrumb>
      <div className="site-layout-content">
        {mode === "list" ? (
          <>
            <Title level={4}>Services</Title>
            <List
              grid={{ gutter: 16, column: 8 }}
              dataSource={YAML.parse(servicesYaml)}
              renderItem={(item) => (
                <List.Item>
                  <Card
                    onClick={() => handleClick(item)}
                    style={{ height: "150px" }}
                    hoverable
                  >
                    <div
                      style={{
                        marginLeft: "auto",
                        marginRight: "auto",
                        width: "50%",
                      }}
                    >
                      <Avatar shape="square" src={item.iconUrl} />
                      <br /> <br />
                    </div>
                    <div
                      style={{
                        marginLeft: "auto",
                        marginRight: "auto",
                        width: "70%",
                      }}
                    >
                      {item.name}
                    </div>
                  </Card>
                </List.Item>
              )}
            />
            <br />
            <br />
            <Title level={4}>Resources</Title>
            <Tabs>
              <TabPane tab="Recent" key="1">
                <List
                  itemLayout="horizontal"
                  dataSource={resources}
                  renderItem={(item, index) => (
                    <List.Item>
                      <List.Item.Meta
                        avatar={
                          <Avatar
                            size="small"
                            shape="square"
                            src={item.resourceUrl}
                          />
                        }
                        title={<a href="https://ant.design">{item.title}</a>}
                      />
                    </List.Item>
                  )}
                />
              </TabPane>
              <TabPane tab="Favorite" key="2"></TabPane>
            </Tabs>
          </>
        ) : (
          <>
            {" "}
            <Title level={4}>Create a {service.name}</Title>
            <div className="App-text">{service?.description}</div>
            {loading ? (
              <p>Loading fields...</p>
            ) : (
              <Form name="execute">
                {moduleVariables.map((variable) => {
                  return (
                    <>
                      {service?.variables?.hasOwnProperty(variable) ? (
                        <Form.Item
                          label={
                            service?.variables[variable]?.label || variable
                          }
                          name={variable}
                        >
                          {(() => {
                            switch (service?.variables[variable]?.type) {
                              case "input":
                                return <Input />;
                              case "inputNumber":
                                return <InputNumber />;
                              case "select":
                                return (
                                  <Select>
                                    {service?.variables[variable]?.options.map(
                                      (option) => {
                                        return (
                                          <Option value={option}>
                                            {option}
                                          </Option>
                                        );
                                      }
                                    )}
                                  </Select>
                                );
                              default:
                                return <Input />;
                            }
                          })()}
                        </Form.Item>
                      ) : (
                        <Form.Item label={variable} name={variable}>
                          <Input />
                        </Form.Item>
                      )}
                    </>
                  );
                })}
                <Form.Item>
                  <Button type="primary" htmlType="submit">
                    Create
                  </Button>{" "}
                  &nbsp;
                  <Button onClick={cancelClick} type="default">
                    Cancel
                  </Button>{" "}
                </Form.Item>
              </Form>
            )}
          </>
        )}
      </div>
    </Content>
  );
};
