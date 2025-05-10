import React, { useState } from 'react';
import { Form, Switch, Typography, Row, Col, Input, Tooltip } from 'antd';
import { InfoCircleOutlined, SearchOutlined } from '@ant-design/icons';

type TeamPermissionsProps = {
  managePermissions: boolean;
};

// Define all permission items for filtering (sorted alphabetically)
const permissionItems = [
  {
    name: "manageCollection",
    label: "Manage Collections",
    tooltip: "Allow members to create and manage all collections within the organization"
  },
  {
    name: "manageJob",
    label: "Manage Jobs",
    tooltip: "Allow members to create jobs inside the organization"
  },
  {
    name: "manageModule",
    label: "Manage Modules",
    tooltip: "Allow members to create and administrate all modules within the organization"
  },
  {
    name: "manageProvider",
    label: "Manage Providers",
    tooltip: "Allow members to create and administrate all providers within the organization"
  },
  {
    name: "manageState",
    label: "Manage State",
    tooltip: "Allow members to manage Terraform/OpenTofu state, include downloading, uploading and view state content of a workspace."
  },
  {
    name: "manageTemplate",
    label: "Manage Templates",
    tooltip: "Allow members to create and administrate all Templates within the organization"
  },
  {
    name: "manageVcs",
    label: "Manage VCS Settings",
    tooltip: "Allow members to create and administrate all VCS Settings within the organization"
  },
  {
    name: "manageWorkspace",
    label: "Manage Workspaces",
    tooltip: "Allow members to create and administrate all workspaces within the organization"
  }
];

export const TeamPermissions: React.FC<TeamPermissionsProps> = ({ managePermissions }) => {
  const [searchText, setSearchText] = useState("");
  
  // Filter permissions based on search text (both label and tooltip)
  const filteredPermissions = permissionItems.filter(item => 
    item.label.toLowerCase().includes(searchText.toLowerCase()) || 
    item.tooltip.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Form.Provider>
      <div className="permissions-section">
        <Typography.Title level={3} style={{ marginBottom: '16px' }}>Permissions</Typography.Title>
        
        <Input
          placeholder="Search permissions..."
          prefix={<SearchOutlined />}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ marginBottom: '16px', maxWidth: '80%' }}
          allowClear
        />
        
        <div className="permissions-table" style={{ maxWidth: '80%', marginLeft: 0 }}>
          <Row className="permissions-header">
            <Col span={12}><Typography.Text strong>Permission</Typography.Text></Col>
            <Col span={12} style={{ textAlign: 'right' }}><Typography.Text strong>Access</Typography.Text></Col>
          </Row>

          <div className="permission-items">
            {filteredPermissions.length > 0 ? (
              filteredPermissions.map(item => (
                <div key={item.name} className="permission-row">
                  <Row style={{ width: '100%' }} align="middle">
                    <Col span={12}>
                      <div style={{ display: 'flex', alignItems: 'center' }}>
                        <span>{item.label}</span>
                        <Tooltip title={item.tooltip}>
                          <Typography.Text type="secondary" style={{ marginLeft: 8, cursor: 'help' }}>
                            <InfoCircleOutlined />
                          </Typography.Text>
                        </Tooltip>
                      </div>
                    </Col>
                    <Col span={12} style={{ textAlign: 'right' }}>
                      <Form.Item
                        name={item.name}
                        valuePropName="checked"
                        noStyle
                      >
                        <Switch disabled={!managePermissions} />
                      </Form.Item>
                    </Col>
                  </Row>
                </div>
              ))
            ) : (
              <div style={{ padding: '20px 0', textAlign: 'center', color: 'rgba(0, 0, 0, 0.45)' }}>
                No permissions found matching "{searchText}"
              </div>
            )}
          </div>
        </div>
      </div>
    </Form.Provider>
  );
}; 