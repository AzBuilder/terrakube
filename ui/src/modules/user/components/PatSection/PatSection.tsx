import { Alert, Button, Empty, Flex, Spin, Typography } from "antd";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { UserToken } from "@/modules/user/types";
import "./PatSection.css";
import CreatePatModal from "@/modules/user/modals/CreatePatModal/CreatePatModal";
import userService from "@/modules/user/userService";
import useApiRequest from "@/modules/api/useApiRequest";
import TokenGrid from "@/modules/user/components/TokenList/TokenGrid";

type Params = {
  orgid: string;
};

export const Tokens = () => {
  const { orgid } = useParams<Params>();
  const [tokens, setTokens] = useState<UserToken[]>([]);
  const [visible, setVisible] = useState(false);
  const {
    loading,
    execute: loadTokens,
    error,
  } = useApiRequest({
    action: () => userService.listPersonalAccessTokens(),
    onReturn: (data) => {
      setTokens(data);
    },
    requestErrorInfo: {
      title: "Failed to cancel job",
      message: "Failed to cancel job. Please try again",
    },
  });

  useEffect(() => {
    loadTokens();
  }, [orgid]);

  return (
    <div className="pat-section">
      <Flex gap="middle" justify="space-between" align="center">
        <Flex vertical>
          <Typography.Title className="title">Personal Access Tokens</Typography.Title>
          <Typography.Text type="secondary">
            Personal Access Tokens (PAT), also known as API tokens can be used to access the Terrakube API and perform
            all the actions your user account is entitled to. For more information, see the Terrakube documentation.
          </Typography.Text>
        </Flex>
        <Button color="purple" variant="solid" onClick={() => setVisible(true)}>
          New token
        </Button>
      </Flex>

      {error && (
        <Alert className="alert" message="Failed to load tokens. Please try again later" type="error" showIcon banner />
      )}

      {loading && (
        <Flex align="center" className="loader" vertical gap="middle">
          <Spin tip="Loading" size="large" />
          <Typography.Text>Loading tokens...</Typography.Text>
        </Flex>
      )}

      {!loading && tokens.length === 0 && (
        <Flex justify="center">
          <Empty
            className="no-content"
            styles={{
              description: {
                textAlign: "center",
              },
            }}
            description="You have not created any Personal Access Tokens. Create one now to start integrating with the Terrakube API"
          >
            <Button color="purple" variant="filled" onClick={() => setVisible(true)}>
              Create a new token
            </Button>
          </Empty>
        </Flex>
      )}
      {!loading && tokens.length > 0 && <TokenGrid tokens={tokens} onDeleted={() => loadTokens()} />}

      {visible && (
        <CreatePatModal visible={visible} onCancel={() => setVisible(false)} onCreated={() => loadTokens()} />
      )}
    </div>
  );
};
