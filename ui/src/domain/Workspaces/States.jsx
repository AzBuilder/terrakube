import { React, useState, useRef, useCallback, useMemo } from "react";
import { List, Space, Card, Row, Col, Avatar } from "antd";
import Editor from "@monaco-editor/react";
import axiosInstance, { axiosClient } from "../../config/axiosConfig";
import ReactFlow, {
  Controls,
  Background,
  applyNodeChanges,
  applyEdgeChanges,
} from "reactflow";
import NodeResource from "./NodeResource";
import { DownloadState } from "./DownloadState";
import { UserOutlined } from "@ant-design/icons";
import 'reactflow/dist/style.css';
import {ResourceDrawer} from "../Workspaces/ResourceDrawer";


export const States = ({
  history,
  setStateDetailsVisible,
  stateDetailsVisible,
}) => {
  const [currentState, setCurrentState] = useState({});
  const [stateContent, setStateContent] = useState("");
  const [rawStateContent, setRawStateContent] = useState("");
  const [activeTab, setactivetab] = useState("diagram");
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  const [open, setOpen] = useState(false);
  const [resource, setResource] = useState({});
  const onNodesChange = useCallback(
    (changes) => setNodes((ns) => applyNodeChanges(changes, ns)),
    []
  );
  const onEdgesChange = useCallback(
    (changes) => setEdges((es) => applyEdgeChanges(changes, es)),
    []
  );
  const editorRef = useRef(null);
  const handleClick = (state) => {
    changeState(state);
  };

  function handleEditorDidMount(editor, monaco) {
    editorRef.current = editor;
  }
  const showDrawer = (record) => {
    setOpen(true);
    setResource(record);
  };


  function pushNode(nodes, dependencies, element, xmap, y) {
    nodes.push({
      id: element.address,
      type: "resourceNode",
      data: {
        name: element.name,
        provider: element.provider_name,
        type: element.type,
        values: element.values,
        depends_on: element.depends_on,
        showDrawer: showDrawer,
      },
      position: { x: xmap.get(dependencies), y: y + dependencies * 130 },
    });

    return nodes;
  }

  function pushEdge(edges, dependencies, element, elementDependsOn) {
    if (dependencies > 0)
      elementDependsOn.forEach((dep) => {
        edges.push({
          id: element.address + "-" + dep,
          source: element.address,
          target: dep,
          className: "normal-edge",
          animated: true,
          arrowHeadType: "arrow",
          style: { stroke: "#1890ff" },
        });
      });

    return edges;
  }

  function loadData(resp) {
    console.log(resp.data);

    let nodes = [];
    let edges = [];
    let x = new Map();
    let y = 100;

    if (
      resp.data != null &&
      resp.data.values != null &&
      resp.data.values.root_module != null
    ) {
      try {
        if (resp.data.values.root_module.resources != null) {
          resp.data.values.root_module.resources.forEach((element) => {
            let dependencies = 0;
            if (element.depends_on != null)
              dependencies = element.depends_on.length;
            x.set(
              dependencies,
              (x.get(dependencies) ? x.get(dependencies) : 0) + 350
            );

            nodes = pushNode(nodes, dependencies, element, x, y);
            edges = pushEdge(edges, dependencies, element, element.depends_on);
          });
        }

        if (resp.data.values.root_module.child_modules != null) {
          resp.data.values.root_module.child_modules.forEach((child) => {
            if (child.resources != null)
              child.resources.forEach((element) => {
                let dependencies = 0;
                if (element.depends_on != null)
                  dependencies = element.depends_on.length;
                x.set(
                  dependencies,
                  (x.get(dependencies) ? x.get(dependencies) : 0) + 350
                );

                nodes = pushNode(nodes, dependencies, element, x, y);
                edges = pushEdge(
                  edges,
                  dependencies,
                  element,
                  element.depends_on
                );
              });
          });
        }
      } catch (error) {
        console.error(`Failed to build diagram: ${error}`);
        nodes = [];
        nodes.push({
          id: "1",
          data: {
            name: "Error Building Diagram",
            provider: "azurerm",
            type: "unknown",
          },
          position: { x: 0, y: 130 },
        });
      }
    }

    setNodes(nodes);
    setEdges(edges);
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
          setStateContent(JSON.stringify(resp.data, null, "\t"));
          loadData(resp);

          //GET RAW STATE BASICALLY JUST ADDING .raw.json
          axiosInstance.get(state.output.replace(".json",".raw.json"))
              .then((response) => {
                setRawStateContent(JSON.stringify(response.data, null, "\t"))
              }).catch((err) =>{
            setStateContent(`{"error":"Failed to load raw state${err}"}`)
          })
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
    {
      key: "rawState",
      tab: "rawState",
    },
  ];

  const onTabChange = (key) => {
    setactivetab(key);
  };

  const nodeTypes = useMemo(
    () => ({
      resourceNode: NodeResource,
    }),
    []
  );

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
              <DownloadState stateUrl={currentState.output} />
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
                    <ResourceDrawer resource={resource} setOpen={setOpen} open={open}/>
                    <ReactFlow
                      zoomOnScroll={false}
                      nodeTypes={nodeTypes}
                      nodes={nodes}
                      edges={edges}
                      onNodesChange={onNodesChange}
                      onEdgesChange={onEdgesChange}
                      proOptions={{hideAttribution: true}}
                    >
                      <Controls />
                      <Background />
                    </ReactFlow>
                  </div>
                ) : activeTab === "rawState" ? (
                    <Editor
                        height="60vh"
                        options={{ readOnly: "true" }}
                        onMount={handleEditorDidMount}
                        defaultLanguage="json"
                        defaultValue={rawStateContent}
                    />
                ) : (
                  <Editor
                    height="60vh"
                    options={{ readOnly: "true" }}
                    onMount={handleEditorDidMount}
                    defaultLanguage="json"
                    defaultValue={stateContent}
                  />
                )

                }
              </Card>
            </Col>
          </Row>
        </div>
      )}
    </div>
  );
};