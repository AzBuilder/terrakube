import { React, useState, useRef, useEffect } from 'react';
import { List, Space, Card, Row, Col, Avatar, Button } from "antd";
import Editor from "@monaco-editor/react";
import { axiosClient } from "../../config/axiosConfig";
import ReactFlow, { Controls, Background } from 'react-flow-renderer';
import NodeResource from './NodeResource';


export const States = ({ history, setStateDetailsVisible, stateDetailsVisible }) => {
  const [currentState, setCurrentState] = useState({});
  const [stateContent, setStateContent] = useState("");
  const [activeTab, setactivetab] = useState('diagram');
  const [resources, setResources] = useState([]);

  const editorRef = useRef(null);
  const handleClick = state => {
    changeState(state);
  };

  function handleEditorDidMount(editor, monaco) {
    editorRef.current = editor;
  }

  const changeState = state => {
    setCurrentState(state);
    setStateDetailsVisible(true);

    axiosClient.get(state.output).then(
      resp => {
        console.log(JSON.stringify(resp.data, null, '\t'));
        setStateContent(JSON.stringify(resp.data, null, '\t'));

        let resources = [];
        let x = new Map();
        let y = 100;

        if (resp.data != null &&  resp.data.values!= null &&resp.data.values.root_module != null) { 

        resp.data.values.root_module.resources.forEach(element => {
          let dependencies = 0;
          if (element.depends_on != null)
            dependencies = element.depends_on.length;
          x.set(dependencies, (x.get(dependencies) ? x.get(dependencies) : 0) + 350);

          resources.push(
            {
              id: element.address,
              type: 'resourceNode',
              data: { name: element.name, provider: element.provider_name, type: element.type },
              position: { x: x.get(dependencies), y: (y + (dependencies * 130)) },
            });

          if (dependencies > 0) {
            element.depends_on.forEach(dep => {

              resources.push(
                {
                  id: element.address + "-" + dep,
                  source: element.address,
                  target: dep,
                  className: 'normal-edge',
                  animated: true,
                  arrowHeadType: 'arrow',
                  style: { stroke: '#1890ff' },
                });
            });
          }
        });

        setResources(resources);
       
      }
      }
    ).catch(err => setStateContent(`{"error":"Failed to load state ${err}"}`));
  }

  const tabs = [
    {
      key: 'diagram',
      tab: 'diagram',
    },
    {
      key: 'code',
      tab: 'code',
    },
  ];

  const onTabChange = key => {
    setactivetab(key);
  };

  

  const nodeTypes = {
    resourceNode: NodeResource,
  };


  return (
    <div>
      {!stateDetailsVisible ?
        (<List split="" className="moduleList" dataSource={history}
          renderItem={item => (
            <List.Item>
              <Card onClick={() => handleClick(item)} style={{ width: "100%" }} hoverable>
                <Space className="states" size={40} split="|">
                  <span>#{item.id}</span>
                  <span><b>username</b> triggered from Terraform</span>
                  <span><a>job #32</a></span>
                  <span>{item.relativeDate}</span>
                </Space>
              </Card>
            </List.Item>
          )} />) : (
          <div style={{ paddingTop: "10px" }}>
            <Row>
              <Col span={1}><Avatar shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" /></Col>
              <Col span={21}><h3>{currentState.title}</h3>
                <Space className="stateDetails" size={40} split="|">
                  <span>#{currentState.id}</span>
                  <span><b>username</b> triggered from Terraform</span>
                  <span><a>job #32</a></span>
                </Space>
              </Col>
              <Col span={2}>
                <Button target="_blank" href={currentState.output}>Download</Button><br />
                <span className="stateDetails">{currentState.relativeDate}</span>
              </Col>
            </Row>
            <Row style={{ paddingTop: "30px" }}>
              <Col span={24}>

                <Card
                  style={{ width: '100%' }}
                  tabList={tabs}
                  activeTabKey={activeTab}
                  onTabChange={key => {
                    onTabChange(key);
                  }}
                >
                  {activeTab === "diagram" ? (
                    <div style={{ height: 500 }}>
                      <ReactFlow zoomOnScroll={false} nodeTypes={nodeTypes}  elements={resources}>
                        <Controls />
                        <Background />
                      </ReactFlow>
                    </div>

                  ) : (
                    <Editor height="60vh" options={{ readOnly: "true" }} onMount={handleEditorDidMount} defaultLanguage="json" defaultValue={stateContent} />
                  )}


                </Card>

              </Col>
            </Row>

          </div>
        )}
    </div>
  )
}