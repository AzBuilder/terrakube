import { React, useState } from "react";
import { SiTerraform } from "react-icons/si";
import { Layout, Breadcrumb, Card, List, Typography, Tabs, Avatar } from "antd";
const { Title } = Typography;
const { Content } = Layout;
const { TabPane } = Tabs;

export const Portal = () => {
  const services = [
    {
      name: "Virtual Machine",
      iconUrl: "https://pbs.twimg.com/media/FPk1KFTXIAYrBC6.png",
    },
    {
      name: "Grafana",
      iconUrl: "https://cdn.worldvectorlogo.com/logos/grafana.svg",
    },
    {
      name: "Service 3",
      iconUrl:
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfOAminWvMkr1_XtolJpSX-uRnvnvdcNwh-w&usqp=CAU",
    },
    {
      name: "Cloud Storage",
      iconUrl:
        "https://download.logo.wine/logo/Google_Storage/Google_Storage-Logo.wine.png",
    },
  ];

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
  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}></Breadcrumb>
      <div className="site-layout-content">
        {" "}
        <Title level={4}>Services</Title>
        <List
          grid={{ gutter: 16, column: 8 }}
          dataSource={services}
          renderItem={(item) => (
            <List.Item>
              <Card style={{ height: "150px" }} hoverable>
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
      </div>
    </Content>
  );
};
