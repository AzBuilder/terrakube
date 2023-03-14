import { React, useState } from "react";
import { Layout, Breadcrumb, Card, List, Typography, Tabs, Avatar } from "antd";
import YAML from "yaml";
import { set } from "yaml/dist/schema/yaml-1.1/set";
const { Title } = Typography;
const { Content } = Layout;
const { TabPane } = Tabs;

export const Portal = () => {
  const [mode, setMode] = useState("list");
  const [service, setService] = useState({});
  let servicesYaml = `
  - name: "Virtual Machine"
    moduleSource: "terrakube/azure-vm/azurerm"
    iconUrl: "https://pbs.twimg.com/media/FPk1KFTXIAYrBC6.png"
  - name: "Grafana"
    moduleSource: "terrakube/azure-vm/azurerm"
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
    },
    {
      name: "size",
      defaultValue: "s3",
    },
  ];
  const handleClick = (item) => {
    setService(item);
    setMode("create");
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
                    onClick={handleClick(item)}
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
          ""
        )}
      </div>
    </Content>
  );
};
