import React, { memo } from 'react';
import { SiTerraform  } from "react-icons/si";
import { Card, Avatar } from 'antd';
import { IconContext } from "react-icons";
import { Handle } from 'react-flow-renderer';
import {getServiceIcon} from './Icons.js';
const { Meta } = Card;

export default memo(({ data, isConnectable }) => {
    return (
        <>
        <Handle
        type="source"
        position="top"
        style={{ background: '#555' }}
        onConnect={(params) => console.log('handle onConnect', params)}
        isConnectable={isConnectable}
      />
         <Card style={{ width: 300 }}>
                <Meta
                    avatar={GetResourceIcon(data)}
                    title={data.name}
                    description={data.type}/>
         </Card>
         <Handle
        type="target"
        position="bottom"
        id="b"
        style={{  background: '#555' }}
        isConnectable={isConnectable}
      />
        </>
    );
});


const GetResourceIcon = (resource) => {
    switch(resource.provider) {
        /* Azure */ 
        case 'registry.terraform.io/hashicorp/azurerm':
            switch(resource.type) {
                /* AAD B2C */ 
                case 'azurerm_aadb2c_directory':
                    return <Avatar shape="square" size="large" src="/providers/azurerm/Identity/10228-icon-service-Azure-AD-B2C.svg" />;
                /* API Management */ 
                case 'azurerm_api_management':
                    return <Avatar shape="square" size="large" src="/providers/azurerm/App Services/10042-icon-service-API-Management-Services.svg" />;
                /* App Service */ 
                case 'azurerm_app_service':
                    return <Avatar shape="square" size="large" src="/providers/azurerm/App Services/10035-icon-service-App-Services.svg" />;
                case 'azurerm_app_service_plan':
                    return <Avatar shape="square" size="large" src="/providers/azurerm/App Services/00046-icon-service-App-Service-Plans.svg" />;
                /* Base */ 
                case 'azurerm_resource_group':
                    return <Avatar shape="square" size="large" src="/providers/azurerm/General/10007-icon-service-Resource-Groups.svg" />;
                default:
                    return <Avatar shape="square" size="large" src="/providers/azurerm/Azure.svg" />;
            }
        /* AWS */
        case 'registry.terraform.io/hashicorp/aws':
            const iconSource = getServiceIcon(resource.provider,resource.type);
            return <Avatar shape="square" size="large" src={iconSource} />;
        /* Default */
        default:
            return <IconContext.Provider value={{ size: "30px" }}><SiTerraform></SiTerraform></IconContext.Provider>;
    }

}