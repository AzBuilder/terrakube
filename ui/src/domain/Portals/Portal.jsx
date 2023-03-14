import { React, useState } from "react";
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

const { Title } = Typography;
const { Content } = Layout;
const { TabPane } = Tabs;

export const Portal = () => {
  const [mode, setMode] = useState("list");
  const [service, setService] = useState({});
  let servicesYaml = `
  - name: "Virtual Machine"
    moduleSource: "terrakube/azure-vm/azurerm"
    description: "Create a virtual machine that runs Linux or Windows. Select an image from Azure marketplace or use your own customized image."
    iconUrl: "https://pbs.twimg.com/media/FPk1KFTXIAYrBC6.png"
  - name: "Grafana"
    moduleSource: "terrakube/azure-vm/azurerm"
    description: "Create a virtual machine that runs Linux or Windows. Select an image from Azure marketplace or use your own customized image."
    iconUrl: "https://cdn.worldvectorlogo.com/logos/grafana.svg"
  - name: "Service 3"
    moduleSource: "terrakube/azure-vm/azurerm"
    iconUrl: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfOAminWvMkr1_XtolJpSX-uRnvnvdcNwh-w&usqp=CAU"
  - name: "Cloud Storage"
    moduleSource: "terrakube/azure-vm/azurerm"
    iconUrl: "https://download.logo.wine/logo/Google_Storage/Google_Storage-Logo.wine.png"
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

  const inputs = [
    {
      name: "name",
      defaultValue: "test",
      type: "input",
    },
    {
      name: "size",
      defaultValue: "s3",
      type: "select",
      options: ["size 1", "size 2"],
    },
  ];
  const handleClick = (item) => {
    setService(item);
    setMode("create");
  };

  const cancelClick = () => {
    setMode("list");
  };
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
            <Form name="execute">
              {inputs.map((input, index) => {
                return (
                  <Form.Item label={input.name} name={input.name}>
                    {(() => {
                      switch (input.type) {
                        case "input":
                          return <Input />;
                        case "inputNumber":
                          return <InputNumber />;
                        case "select":
                          return (
                            <Select>
                              {input.options.map((option, index) => {
                                return <Option value={option}>{option}</Option>;
                              })}
                            </Select>
                          );
                        default:
                          return <Input />;
                      }
                    })()}
                  </Form.Item>
                );
              })}
              <Form.Item>
                <Button onClick={cancelClick} type="default">
                  Cancel
                </Button>{" "}
                &nbsp;
                <Button type="primary" htmlType="submit">
                  Create
                </Button>
              </Form.Item>
            </Form>
          </>
        )}
      </div>
    </Content>
  );
};
