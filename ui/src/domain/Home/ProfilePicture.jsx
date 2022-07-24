import { React ,useEffect,useState} from 'react';
import 'antd/dist/antd.css';
import "./Home.css"
import {  Avatar ,Dropdown,Menu,message} from 'antd';
import axios from 'axios';
import { UserOutlined, PoweroffOutlined, QuestionCircleOutlined} from '@ant-design/icons';
import { mgr } from "../../config/authConfig";
import getUserFromStorage from "../../config/authUser";

export const ProfilePicture = (props) => {
  //const [imageUrl, setImageUrl] = useState(null);
  const [username, setUsername] = useState(null);

  const userMenu = (
    <Menu theme="dark">
      <Menu.Item  onClick={() => message.info("Coming Soon")}  key="user-id">
        Signed in as <br/><b>{username}</b>
      </Menu.Item>
      <Menu.Divider/>
      <Menu.Item icon={<UserOutlined/>} onClick={() => message.info("Coming Soon")} key="user-settings">User Settings</Menu.Item>
      <Menu.Item icon={<QuestionCircleOutlined/>} key="help">
        <a target="_blank" rel="noopener noreferrer" href="https://docs.terrakube.org/">
            Help
        </a>
      </Menu.Item>
      <Menu.Item icon={<PoweroffOutlined />} key="sign-out" onClick={() => signOutClickHandler()}>Sign Out</Menu.Item>
    </Menu>
  );
  const signOutClickHandler= () => {
    mgr.signoutRedirect()
  }

  useEffect(() => {
    GetProfileImage();
   }, [])
 
   const GetProfileImage = () => {
 
    const user = getUserFromStorage();
    console.log(user)
    setUsername(user.profile.name);
     
   }

  return (
    <>
     <Dropdown overlay={userMenu} trigger={['click']}>
            <Avatar shape="square" style={{cursor:"pointer"}} size="default" icon={<UserOutlined />} />
    </Dropdown>
    </>
  )
}
