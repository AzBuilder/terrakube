import { React, useState, useRef } from "react";
import { List, Space, Card, Row, Col, Avatar, Button } from "antd";
import Editor from "@monaco-editor/react";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import ReactFlow, { Controls, Background } from "react-flow-renderer";
import NodeResource from "./NodeResource";
import { UserOutlined } from "@ant-design/icons";

export const States = ({
  history,
  setStateDetailsVisible,
  stateDetailsVisible,
}) => {
  const [currentState, setCurrentState] = useState({});
  const [stateContent, setStateContent] = useState("");
  const [activeTab, setactivetab] = useState("diagram");
  const [resources, setResources] = useState([]);

  const editorRef = useRef(null);
  const handleClick = (state) => {
    changeState(state);
  };

  function handleEditorDidMount(editor, monaco) {
    editorRef.current = editor;
  }

  function pushNode(resources, dependencies, element, xmap, y) {
    resources.push({
      id: element.address,
      type: "resourceNode",
      data: {
        name: element.name,
        provider: element.provider_name,
        type: element.type,
      },
      position: { x: xmap.get(dependencies), y: y + dependencies * 130 },
    });

    return resources;
  }

  function pushNodeDependency(
    resources,
    dependencies,
    element,
    elementDependsOn
  ) {
    if (dependencies > 0)
      elementDependsOn.forEach((dep) => {
        resources.push({
          id: element.address + "-" + dep,
          source: element.address,
          target: dep,
          className: "normal-edge",
          animated: true,
          arrowHeadType: "arrow",
          style: { stroke: "#1890ff" },
        });
      });

    return resources;
  }

  function loadData(resp) {
    console.log(resp.data);
    setStateContent(JSON.stringify(resp.data, null, "\t"));

    let resources = [];
    let x = new Map();
    let y = 100;

    if (
      resp.data != null &&
      resp.data.values != null &&
      resp.data.values.root_module != null
    ) {
      if (resp.data.values.root_module.resources != null) {
        resp.data.values.root_module.resources.forEach((element) => {
          let dependencies = 0;
          if (element.depends_on != null)
            dependencies = element.depends_on.length;
          x.set(
            dependencies,
            (x.get(dependencies) ? x.get(dependencies) : 0) + 350
          );

          resources = pushNode(resources, dependencies, element, x, y);
          resources = pushNodeDependency(
            resources,
            dependencies,
            element,
            element.depends_on
          );
        });
      }

      if (resp.data.values.root_module.child_modules != null) {
        resp.data.values.root_module.child_modules.forEach((child) => {
          child.resources.forEach((element) => {
            let dependencies = 0;
            if (element.depends_on != null)
              dependencies = element.depends_on.length;
            x.set(
              dependencies,
              (x.get(dependencies) ? x.get(dependencies) : 0) + 350
            );

            resources = pushNode(resources, dependencies, element, x, y);
            resources = pushNodeDependency(
              resources,
              dependencies,
              element,
              element.depends_on
            );
          });
        });
      }
    }

    setResources(resources);
  }

  const changeState = (state) => {
    setCurrentState(state);
    setStateDetailsVisible(true);

    const apiDomain = new URL(window._env_.REACT_APP_TERRAKUBE_API_URL)
      .hostname;
    if (state.output.includes(apiDomain))
      axiosInstance
        .get(state.output)
        .then((resp) => {
          loadData(resp);
        })
        .catch((err) =>
          setStateContent(`{"error":"Failed to load state ${err}"}`)
        );
    else
      axiosClient
        .get(state.output)
        .then((resp) => {
          loadData(resp);
        })
        .catch((err) =>
          setStateContent(`{"error":"Failed to load state ${err}"}`)
        );
  };

  const tabs = [
    {
      key: "diagram",
      tab: "diagram",
    },
    {
      key: "code",
      tab: "code",
    },
  ];

  const onTabChange = (key) => {
    setactivetab(key);
  };

  const nodeTypes = {
    resourceNode: NodeResource,
  };

  return (
    <div>
      {!stateDetailsVisible ? (
        <List
          split=""
          className="moduleList"
          dataSource={history
            .sort((a, b) => a.jobReference - b.jobReference)
            .reverse()}
          renderItem={(item) => (
            <List.Item>
              <Card
                onClick={() => handleClick(item)}
                style={{ width: "100%" }}
                hoverable
              >
                <Space className="states" size={40} split="|">
                  <span>#{item.id}</span>
                  <span>
                    <b>{item.createdBy}</b> triggered from Terraform{" "}
                    {item.relativeDate}
                  </span>
                  <span>
                    <a>job #{item.jobReference}</a>
                  </span>
                </Space>
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <div style={{ paddingTop: "10px" }}>
          <Row>
            <Col span={1}>
              <Avatar shape="square" icon={<UserOutlined />} />
            </Col>
            <Col span={21}>
              <h3>{currentState.title}</h3>
              <Space className="stateDetails" size={40} split="|">
                <span>#{currentState.id}</span>
                <span>
                  <b>{currentState.createdBy}</b> triggered from Terraform
                </span>
                <span>
                  <a>job #{currentState.jobReference}</a>
                </span>
              </Space>
            </Col>
            <Col span={2}>
              <Button target="_blank" href={currentState.output}>
                Download
              </Button>
              <br />
              <span className="stateDetails">{currentState.relativeDate}</span>
            </Col>
          </Row>
          <Row style={{ paddingTop: "30px" }}>
            <Col span={24}>
              <Card
                style={{ width: "100%" }}
                tabList={tabs}
                activeTabKey={activeTab}
                onTabChange={(key) => {
                  onTabChange(key);
                }}
              >
                {activeTab === "diagram" ? (
                  <div style={{ height: 500 }}>
                    <ReactFlow
                      zoomOnScroll={false}
                      nodeTypes={nodeTypes}
                      elements={resources}
                    >
                      <Controls />
                      <Background />
                    </ReactFlow>
                  </div>
                ) : (
                  <Editor
                    height="60vh"
                    options={{ readOnly: "true" }}
                    onMount={handleEditorDidMount}
                    defaultLanguage="json"
                    defaultValue={stateContent}
                  />
                )}
              </Card>
            </Col>
          </Row>
        </div>
      )}
    </div>
  );
};
