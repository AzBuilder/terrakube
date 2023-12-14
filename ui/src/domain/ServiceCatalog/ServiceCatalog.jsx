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
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";
import { useParams } from "react-router-dom";
import { Buffer } from "buffer";

const { Title } = Typography;
const { Content } = Layout;
const { TabPane } = Tabs;
const { Option } = Select;
const hcl = require("hcl2-parser");

const validateMessages = {
  required: "${label} is required!",
};

export const ServiceCatalog = () => {
  const { orgid, serviceCatalogId } = useParams();
  const [mode, setMode] = useState("list");
  const [moduleVariables, setModuleVariables] = useState([]);
  const [service, setService] = useState({});
  const [services, setServices] = useState("");
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState({});

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
        readHCL(resp.headers["x-terraform-get"], item);
      });
  };

  async function readHCL(url, item) {
    const { entries } = await unzip(url);
    var hclString = "";
    for (const [name, entry] of Object.entries(entries)) {
      if (name.includes(".tf") && !name.includes("/")) {
        var contentText = await entry.text();
        hclString += "\n" + contentText;
      }
    }

    const hclResult = hcl.parseToObject(hclString);
    const variables = hclResult[0]?.variable;
    var defaultValues = {};
    setModuleVariables(Object.keys(variables));
    Object.keys(variables).forEach((key) => {
      if (item?.variables?.hasOwnProperty(key)) {
        defaultValues[key] = item?.variables[key]?.default;
      } else {
        defaultValues[key] = variables[key][0]?.default;
      }
    });
    setInitialValues(defaultValues);
    setLoading(false);
  }

  const cancelClick = () => {
    setMode("list");
  };

  const loadServiceCatalog = () => {
    setLoading(true);
    axiosInstance
      .get(`organization/${orgid}/servicecatalog/${serviceCatalogId}`)
      .then((response) => {
        let buff = new Buffer(
          response.data.data.attributes.definition,
          "base64"
        );
        var services = YAML.parse(buff.toString("ascii"));
        setServices(services);
        setLoading(false);
      });
  };
  const onFinish = (values) => {
    var module = {
      dynamic: {
        source: service.moduleSource,
        version: service.moduleVersion,
      },
    };

    Object.assign(module.dynamic, values);
    console.log(module);
  };

  useEffect(() => {
    loadServiceCatalog();
  }, []);
  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}></Breadcrumb>
      <div className="site-layout-content">
        {mode === "list" ? (
          <>
            <Title level={4}>Services</Title>
            {loading && !services ? (
              <p>Loading Services...</p>
            ) : (
              <List
                grid={{ gutter: 16, column: 8 }}
                dataSource={services}
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
            )}

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
            <Title level={4}>
              {service?.title || "Create a " + service.name}
            </Title>
            <div className="App-text">
              {" "}
              <ReactMarkdown
                rehypePlugins={[rehypeRaw]}
                remarkPlugins={[remarkGfm]}
              >
                {service?.description}
              </ReactMarkdown>
            </div>
            {loading ? (
              <p>Loading fields...</p>
            ) : (
              <Form
                onFinish={onFinish}
                validateMessages={validateMessages}
                initialValues={initialValues}
                name="execute"
              >
                {moduleVariables.map((variable) => {
                  return (
                    <>
                      {service?.variables?.hasOwnProperty(variable) ? (
                        <Form.Item
                          label={
                            service?.variables[variable]?.label || variable
                          }
                          name={variable}
                          rules={[
                            {
                              required:
                                service?.variables[variable]?.required || false,
                            },
                          ]}
                          hidden={service?.variables[variable]?.hidden || false}
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
