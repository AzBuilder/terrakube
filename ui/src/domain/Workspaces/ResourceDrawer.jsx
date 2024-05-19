import React, { useEffect, useState } from "react";
import { Drawer, Avatar, Space, Tabs, Col, Row, Spin, Button } from "antd";
import { getServiceIcon } from "./Icons.js";
import ActionLoader from "../../ActionLoader.jsx";
import axiosInstance from "../../config/axiosConfig";
export const ResourceDrawer = ({ open, resource, setOpen }) => {
  const [drawerOpen, setDrawerOpen] = useState(open);
  const [actions, setActions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchActions = async () => {
      try {
        setLoading(true);
        const response = await axiosInstance.get(`action?filter[action]=active==true;type=in=('Workspace/ResourceDrawer/Action','Workspace/ResourceDrawer/Tab')`);
        console.log("Actions:", response.data);
        const fetchedActions = response.data.data || [];

        // Filter actions and attach settings to each action
        const filteredActions = fetchedActions.reduce((acc, action) => {
          if (!action.attributes.displayCriteria) {
            acc.push(action);
            return acc;
          }

          let displayCriteria;
          try {
            displayCriteria = JSON.parse(action.attributes.displayCriteria);
          } catch (error) {
            console.error('Error parsing displayCriteria JSON:', error);
            return acc;
          }

          for (const criteria of displayCriteria) {
            const settings = evaluateCriteria(criteria, { state: resource, apiUrl: new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin });
            if (settings) {
              action.settings = settings; // Attach settings to the action
              console.log("settings");
              console.log(action);
              acc.push(action);
              break;
            }
          }

          return acc;
        }, []);

        setActions(filteredActions);
      } catch (error) {
        console.error("Error fetching actions:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchActions();
    if (!open) {
      setDrawerOpen(false);
    } else {
      setDrawerOpen(true);
    }
  }, [open, resource]);

  const onClose = () => {
    setOpen(false);
    setDrawerOpen(false);
  };

  const context = {
    state: resource,
    apiUrl: new URL(window._env_.REACT_APP_TERRAKUBE_API_URL).origin,
  };

  // Function to evaluate display criteria and return settings
  const evaluateCriteria = (criteria, context) => {
    try {
      console.log('Evaluating criteria:', criteria);
      const result = eval(criteria.filter);
      console.log('Result:', result);
      if (result) {
        if (!criteria.settings) {
          return {};
        }
        return criteria.settings.reduce((acc, setting) => {
          acc[setting.key] = setting.value;
          return acc;
        }, {});
      }
    } catch (error) {
      console.error('Error evaluating criteria:', error);
    }
    return null;
  };  

  return (
    <Drawer
      width={640}
      title={
        <>
          <Avatar
            shape="square"
            size="small"
            src={getServiceIcon(resource.provider, resource.type)}
          />{" "}
          {resource.name}
        </>
      }
      placement="right"
      onClose={onClose}
      open={drawerOpen}
    >
      {loading ? (
        <Spin tip="Loading...">
          <Space size={10} style={{ width: "100%" }} direction="vertical" />
        </Spin>
      ) : (
        <Space size={10} style={{ width: "100%" }} direction="vertical">
          <Row>
            <Col span={24}>
              {actions &&
                actions
                  .filter(
                    (action) =>
                      action?.attributes.type ===
                      "Workspace/ResourceDrawer/Action"
                  )
                  .map((action, index) => (
                    <ActionLoader
                      key={index}
                      action={action?.attributes.action}
                      context={{ ...context, settings: action.settings }}
                    />
                  ))}
            </Col>
          </Row>

          <Tabs
            defaultActiveKey="1"
            items={[
              ...actions
                .filter(
                  (action) => action?.attributes.type === "Workspace/ResourceDrawer/Tab"
                )
                .map((action, index) => ({
                  key: `tab-${index + 1}`,
                  label: action.attributes.label,
                  children: (
                    <ActionLoader
                      key={index}
                      action={action?.attributes.action}
                      context={{ ...context, settings: action.settings }}
                    />
                  ),
                })),
            ]}
          />
        </Space>
      )}
    </Drawer>
  );
};