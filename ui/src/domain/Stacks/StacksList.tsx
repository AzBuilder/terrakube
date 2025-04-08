import { Button, List, Tag, Space, Empty, Flex } from "antd";
import { useNavigate, useParams } from "react-router-dom";
import { useCallback, useEffect, useState } from "react";
import { listStacks } from "@/modules/stacks/stackService";
import { StackListItem, ListStacksResponse } from "@/modules/stacks/types";
import StackCard from "@/modules/stacks/components/StackCard";
import { PlusOutlined } from "@ant-design/icons";
import PageWrapper from "@/modules/layout/PageWrapper/PageWrapper";
import { ORGANIZATION_NAME } from "../../config/actionTypes";
import useApiRequest from "@/modules/api/useApiRequest";

export function StacksList() {
  const { id: organizationId } = useParams();
  const navigate = useNavigate();
  const [stacks, setStacks] = useState<StackListItem[]>([]);
  const [organizationName, setOrganizationName] = useState<string>(
    sessionStorage.getItem(ORGANIZATION_NAME) || ""
  );

  const handleReturn = useCallback((response: ListStacksResponse) => {
    setStacks(response.stacks || []);
    if (response.organizationName) {
      setOrganizationName(response.organizationName);
      sessionStorage.setItem(ORGANIZATION_NAME, response.organizationName);
    }
  }, []);

  const { loading, execute, error } = useApiRequest<ListStacksResponse, void>({
    action: () => listStacks(organizationId!),
    onReturn: handleReturn,
    showErrorAsNotification: true,
  });

  useEffect(() => {
    if (organizationId) {
      execute();
    }
  }, [organizationId]);

  return (
    <PageWrapper
      title={
        <Space>
          Stacks
          <Tag color="blue" style={{ fontSize: '12px', padding: '0 6px', marginLeft: '8px' }}>BETA</Tag>
        </Space>
      }
      subTitle={`Stacks in the ${organizationName} organization`}
      loadingText="Loading stacks..."
      loading={loading}
      error={error}
      breadcrumbs={[
        { label: organizationName, path: "/" },
        { label: "Stacks", path: `/organizations/${organizationId}/stacks` },
      ]}
      fluid
      actions={
        <Space>
          <Button 
            icon={<PlusOutlined />} 
            type="primary"
            onClick={() => navigate(`/organizations/${organizationId}/stacks/create`)}
          >
            Create Stack
          </Button>
        </Space>
      }
    >
      <div style={{ width: '100%', marginTop: '24px' }}>
        {!loading && stacks.length === 0 ? (
          <Flex justify="center">
            <Empty
              style={{ textAlign: "center" }}
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description="No stacks have been created yet. Create a new stack to get started with your infrastructure as code."
            />
          </Flex>
        ) : (
          <List
            split={false}
            dataSource={stacks}
            pagination={{ showSizeChanger: true, defaultPageSize: 10 }}
            style={{ width: '100%' }}
            renderItem={(item) => (
              <List.Item style={{ width: '100%' }}>
                <StackCard item={item} />
              </List.Item>
            )}
          />
        )}
      </div>
    </PageWrapper>
  );
} 