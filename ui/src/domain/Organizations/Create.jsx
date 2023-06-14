import { React } from "react";
import axiosInstance from "../../config/axiosConfig";
import { Button, Layout, Breadcrumb, Form, Input, message } from "antd";
import {
  ORGANIZATION_ARCHIVE,
  ORGANIZATION_NAME,
} from "../../config/actionTypes";
import "./Organizations.css";
import { useHistory } from "react-router-dom";
const { Content } = Layout;

const validateMessages = {
  required: "${label} is required!",
};

export const CreateOrganization = ({ setOrganizationName }) => {
  const history = useHistory();
  const onFinish = (values) => {
    const body = {
      data: {
        type: "organization",
        attributes: values,
      },
    };
    console.log(body);

    axiosInstance
      .post("organization", body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status == "201") {
          localStorage.setItem(ORGANIZATION_ARCHIVE, response.data.data.id);
          localStorage.setItem(
            ORGANIZATION_NAME,
            response.data.data.attributes.name
          );
          setOrganizationName(response.data.data.attributes.name);
          history.push(
            `/organizations/${response.data.data.id}/settings/teams`
          );
        }
      })
      .catch((error) => {
        if (error.response) {
          if (error.response.status === 403) {
            message.error(
              <span>
                You are not authorized to create Organizations. <br /> Please
                contact your administrator and request to include you in the
                Terrakube Administrator group. <br /> For more information,
                visit the{" "}
                <a
                  target="_blank"
                  href="https://docs.terrakube.org/getting-started/security#administrator-group"
                >
                  Terrakube documentation
                </a>
                .
              </span>
            );
          }
        }
      });
  };

  return (
    <Content style={{ padding: "0 50px" }}>
      <Breadcrumb style={{ margin: "16px 0" }}>
        <Breadcrumb.Item>Organizations</Breadcrumb.Item>
        <Breadcrumb.Item>New</Breadcrumb.Item>
      </Breadcrumb>
      <div className="site-layout-content">
        <div className="createOrganization">
          <h1>Create a new organization</h1>
          <div className="App-text">
            Organizations are privately shared spaces for teams to collaborate
            on infrastructure.
          </div>
          <Form
            layout="vertical"
            name="create-org"
            onFinish={onFinish}
            validateMessages={validateMessages}
          >
            <Form.Item
              name="name"
              label="Organization name"
              tooltip="e.g. company-name"
              extra=" Organization names must be unique and will be part of your resource names used in various tools, for example terrakube/www-prod."
              required
            >
              <Input />
            </Form.Item>

            <Form.Item name="description" label="Description">
              <Input.TextArea />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Create organization
              </Button>
            </Form.Item>
          </Form>
        </div>
      </div>
    </Content>
  );
};
