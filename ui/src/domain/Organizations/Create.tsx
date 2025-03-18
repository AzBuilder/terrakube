import { Breadcrumb, Button, Form, Input, Layout, message } from "antd";
import { useNavigate } from "react-router-dom";
import { ORGANIZATION_ARCHIVE, ORGANIZATION_NAME } from "../../config/actionTypes";
import axiosInstance from "../../config/axiosConfig";
import "./Organizations.css";
const { Content } = Layout;

const validateMessages = {
  required: "${label} is required!",
};

type CreateOrganizationForm = {
  name: string;
  description?: string;
};

type Props = {
  setOrganizationName: React.Dispatch<React.SetStateAction<string>>;
};

export const CreateOrganization = ({ setOrganizationName }: Props) => {
  const navigate = useNavigate();

  const onFinish = (values: CreateOrganizationForm) => {
    const body = {
      data: {
        type: "organization",
        attributes: values,
      },
    };

    axiosInstance
      .post("organization", body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        if (response.status === 201) {
          sessionStorage.setItem(ORGANIZATION_ARCHIVE, response.data.data.id);
          sessionStorage.setItem(ORGANIZATION_NAME, response.data.data.attributes.name);
          setOrganizationName(response.data.data.attributes.name);
          navigate(`/organizations/${response.data.data.id}/settings/teams`);
        }
      })
      .catch((error) => {
        if (error.response) {
          if (error.response.status === 403) {
            message.error(
              <span>
                You are not authorized to create Organizations. <br /> Please contact your administrator and request to
                include you in the Terrakube Administrator group. <br /> For more information, visit the{" "}
                <a
                  target="_blank"
                  rel="noopener noreferrer"
                  href="https://docs.terrakube.io/getting-started/security#administrator-group"
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
      <Breadcrumb
        style={{ margin: "16px 0" }}
        items={[
          {
            title: "Organizations",
          },
          {
            title: "New",
          },
        ]}
      />
      <div className="site-layout-content">
        <div className="createOrganization">
          <h1>Create a new organization</h1>
          <div className="App-text">
            Organizations are privately shared spaces for teams to collaborate on infrastructure.
          </div>
          <Form layout="vertical" name="create-org" onFinish={onFinish} validateMessages={validateMessages}>
            <Form.Item
              name="name"
              label="Organization name"
              tooltip="e.g. company-name"
              extra=" Organization names must be unique and will be part of your resource names used in various tools, for example development, production, finance."
              rules={[
                { required: true, message: "This field is required!" },
                { pattern: /^[a-zA-Z0-9]*$/, message: "Only letters and numbers are allowed!" },
              ]}
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
