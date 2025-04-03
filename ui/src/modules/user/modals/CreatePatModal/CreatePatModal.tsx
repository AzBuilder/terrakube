import { InfoCircleOutlined } from "@ant-design/icons";
import { Modal, Space, Form, Input, InputNumber, Typography, Alert, ModalProps, Button, Flex } from "antd";
import { useState } from "react";
import userService from "@/modules/user/userService";
import useApiRequest from "@/modules/api/useApiRequest";
import "./CreatePatModal.css";
import { CreateTokenForm } from "@/modules/user/types";

type Props = {
  visible: boolean;
  onCancel: () => void;
  onCreated: () => void;
};

export default function CreatePatModal({ onCancel, onCreated, visible }: Props) {
  const [form] = Form.useForm<CreateTokenForm>();
  const [tokenValue, setTokenValue] = useState<string>();

  const { loading, execute, error } = useApiRequest({
    showErrorAsNotification: false,
    action: (values?: CreateTokenForm) => userService.createPersonalAccessToken(values!),
    onReturn: (data) => {
      setTokenValue(data.token);
      form.resetFields();
    },
    requestErrorInfo: {
      title: "Failed to create token",
      message: "Failed to create token. Please try again",
    },
  });

  async function submitForm() {
    setTokenValue(undefined);
    const formValues = await form.validateFields();
    await execute(formValues);
  }

  const buttonProps: Partial<ModalProps> =
    tokenValue === undefined
      ? {
          okText: "Create",
          okButtonProps: { loading, color: "purple", variant: "solid" },
          onOk: () => submitForm(),
          onCancel: onCancel,
          cancelText: "Cancel",
          cancelButtonProps: { color: "danger", variant: "text" },
        }
      : {
          footer: null,
        };

  return (
    <Modal
      className="create-pat-modal"
      open={visible}
      title="Create Personal Access Token"
      destroyOnClose
      {...buttonProps}
    >
      {tokenValue === undefined && (
        <Space className="content" direction="vertical">
          {error && <Alert type="error" banner message={error?.message} />}
          <Form
            name="tokens"
            form={form}
            layout="vertical"
            disabled={loading}
            validateMessages={{
              required: "${label} is required",
            }}
          >
            <Form.Item
              name="description"
              tooltip={{
                title: "Choose a description to help you identify this token later",
                icon: <InfoCircleOutlined />,
              }}
              label="Description"
              rules={[{ required: true }]}
            >
              <Input placeholder="Please input a description" />
            </Form.Item>
            <Form.Item
              name="days"
              tooltip={{
                title: "Number of days for the token to be valid",
                icon: <InfoCircleOutlined />,
              }}
              label="Days"
              rules={[{ required: true }]}
            >
              <InputNumber min={0} placeholder="10" />
            </Form.Item>
          </Form>
        </Space>
      )}
      {tokenValue !== undefined && (
        <Space className="content" direction="vertical" size="middle">
          <Typography.Text>
            Your new API token is displayed below. Treat this token like a password, as it can be used to access your
            account without a username, password, or two-factor authentication.
          </Typography.Text>

          <Typography.Paragraph className="created-token" copyable>
            {tokenValue}
          </Typography.Paragraph>
          <Alert
            banner
            description={
              <span>
                This token <b>will not be displayed again</b>, so make sure to save it to a safe place.
              </span>
            }
            type="warning"
            showIcon
          />
          <Flex justify="end">
            <Button
              color="red"
              variant="filled"
              onClick={() => {
                onCreated();
                onCancel();
              }}
            >
              Close
            </Button>
          </Flex>
        </Space>
      )}
    </Modal>
  );
}
