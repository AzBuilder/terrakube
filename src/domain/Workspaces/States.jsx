import { React ,useState,useRef} from 'react';
import { List, Space,Card, Row,Col,Avatar,Button} from "antd";
import Editor from "@monaco-editor/react";
import {axiosClient} from "../../config/axiosConfig";

export const States = ({ history, setStateDetailsVisible,stateDetailsVisible  }) => {
  const [currentState,setCurrentState] = useState({});
  const[stateContent,setStateContent] = useState(""); 
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
      }
    ).catch(err => setStateContent(`{"error":"Failed to load state ${err}"}`));
  }

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
        )}/>):(
         <div style={{paddingTop:"10px"}}>
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
                  <Button target="_blank" href={currentState.output}>Download</Button><br/>
                  <span className="stateDetails">{currentState.relativeDate}</span>
              </Col>
            </Row>
            <Row style={{paddingTop:"30px"}}>
            <Editor height="60vh" options={{readOnly:"true"}} onMount={handleEditorDidMount} defaultLanguage="json" defaultValue={stateContent}/>
            </Row>
            <Row style={{paddingTop:"30px"}}>
              <h3>Changes in this version</h3>
            </Row>
         </div>
        )}
    </div>
  )
}