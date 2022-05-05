import { React ,useEffect,useState} from 'react';
import 'antd/dist/antd.css';
import "./Home.css"
import {  Avatar ,Dropdown,Menu,message} from 'antd';
import axios from 'axios';
import {UserOutlined,PoweroffOutlined,QuestionCircleOutlined} from '@ant-design/icons';
import {msalInstance ,userRequest} from "../../config/authConfig";
import { useMsal } from "@azure/msal-react";

export const ProfilePicture = (props) => {
  const [imageUrl, setImageUrl] = useState(null);
  const [username, setUsername] = useState(null);
  const { instance } = useMsal();
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
      <Menu.Item icon={<PoweroffOutlined />} key="sign-out" onClick={() => signOutClickHandler(instance)}>Sign Out</Menu.Item>
    </Menu>
  );
  const signOutClickHandler= (instance) => {
    instance.logoutRedirect({
      onRedirectNavigate: (url) => {
          return false;
      }
  });
  }

  useEffect(() => {
    GetProfileImage();
   }, [])
 
   const GetProfileImage = () => {
 
     const activeAccount = msalInstance.getActiveAccount();
     const accounts = msalInstance.getAllAccounts();
     
     msalInstance.acquireTokenSilent({...userRequest,account: activeAccount || accounts[0]}).then((accessTokenResponse) => {
       let accessToken = accessTokenResponse.accessToken;
       axios.get('https://graph.microsoft.com/v1.0/me/photo/$value', {
       responseType: 'blob',headers: {"Authorization" : `Bearer ${accessToken}`} 
     }).then(o => {
       const url = window.URL || window.webkitURL;
       const blobUrl = url.createObjectURL(o.data);
       setImageUrl(blobUrl);
     }).catch(e => {
       setImageUrl("");
       console.log("Error getting photo", e);
     });

     axios.get('https://graph.microsoft.com/v1.0/me/', {
      headers: {"Authorization" : `Bearer ${accessToken}`} 
    }).then(o => {
      setUsername(o.data.displayName);
    }).catch(e => {
      setUsername("username");
      console.log("Error getting user info", e);
    });
     });
     
   }

  return (
    <>
     <Dropdown overlay={userMenu} trigger={['click']}>
       {imageUrl ? <Avatar shape="square" style={{cursor:"pointer"}} size="default" src={imageUrl} /> : 
            <Avatar shape="square" style={{cursor:"pointer"}} size="default" icon={<UserOutlined />} />}
    </Dropdown>
    </>
  )
}
