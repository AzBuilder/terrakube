import { React } from 'react';
import {  Tag, Space, Collapse, Avatar } from "antd";
import { ORGANIZATION_ARCHIVE, WORKSPACE_ARCHIVE } from '../../config/actionTypes';
import axiosInstance from "../../config/axiosConfig";
import { CheckCircleOutlined, CheckCircleTwoTone } from '@ant-design/icons';
const layout = {
  labelCol: { span: 14 },
  wrapperCol: { span: 100 }
}



const validateMessages = {
  required: '${label} is required!'
}
const { Panel } = Collapse;

export const DetailsJob = () => {
  const workspaceId = localStorage.getItem(WORKSPACE_ARCHIVE);
  const organizationId = localStorage.getItem(ORGANIZATION_ARCHIVE);


  return (

    <div>
      <Space direction="vertical" style={{ width: "100%" }}>
        <div>
          <Tag icon={<CheckCircleOutlined />} color="#2eb039">Completed</Tag> <h2 style={{ display: "inline" }}>Triggered via UI</h2>
        </div>
        <Space direction="vertical" style={{ width: "100%" }}>
          <Collapse defaultActiveKey={['2']}>
            <Panel header={<span><Avatar size="small" shape="square" src="https://avatarfiles.alphacoders.com/128/thumb-128984.png" /> <b>Rick</b> triggered a run from UI</span>} key="1">
              <p></p>
            </Panel>
          </Collapse>
          <Collapse >
            <Panel header={<span><CheckCircleTwoTone twoToneColor="#52c41a" style={{ fontSize: "20px" }} />  <h3 style={{ display: "inline" }}> Job Completed</h3></span>} key="2">
              <div id="code-container">
                <div id="code-content">Initializing the backend...<br/>

                Successfully configured the backend "azurerm"! Terraform will automatically<br/>
                use this backend unless the backend configuration changes.<br/><br/>

                  Initializing provider plugins...<br/>
                  - Finding latest version of hashicorp/time...<br/>
                  - Installing hashicorp/time v0.7.2...<br/>
                  - Installed hashicorp/time v0.7.2 (self-signed, key ID 34365D9472D7468F)<br/><br/>

                  Partner and community providers are signed by their developers.<br/>
                  If you'd like to know more about provider signing, you can read about it here:<br/>
                  https://www.terraform.io/docs/cli/plugins/signing.html<br/><br/>

                  Terraform has created a lock file .terraform.lock.hcl to record the provider<br/>
                  selections it made above. Include this file in your version control repository<br/>
                  so that Terraform can guarantee to make the same selections by default when<br/>
                  you run "terraform init" in the future.<br/><br/>

                  Terraform has been successfully initialized!<br/><br/>

                  You may now begin working with Terraform. Try running "terraform plan" to see<br/>
                  any changes that are required for your infrastructure. All Terraform commands<br/>
                  should now work.<br/><br/>

                  If you ever set or change modules or backend configuration for Terraform,<br/>
                  rerun this command to reinitialize your working directory. If you forget, other<br/>
                  commands will detect it and remind you to do so if necessary.<br/>
                  time_sleep.wait: Refreshing state... [id=2021-08-26T23:47:54Z]<br/><br/>

                  No changes. Infrastructure is up-to-date.<br/><br/>

                  This means that Terraform did not detect any differences between your<br/>
                  configuration and the remote system(s). As a result, there are no actions to<br/>
                  take.<br/><br/>
                </div>
              </div>
            </Panel>
          </Collapse>
        </Space>
      </Space>
    </div>
  )
}