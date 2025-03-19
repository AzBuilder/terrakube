import { DeleteOutlined } from "@ant-design/icons";
import { Button, Popconfirm, Space, message } from "antd";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../../../config/axiosConfig";
import { Workspace } from "../../types";
import { genericHeader } from "../Workspaces";

type Props = {
  workspace: Workspace;
  manageWorkspace: boolean;
};

export const WorkspaceAdvanced = ({ workspace, manageWorkspace }: Props) => {
  const organizationId = workspace.relationships.organization.data.id;
  const navigate = useNavigate();
  const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  function generateRandomString(length: number) {
    let result = "";
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }

    return result;
  }
  const onDelete = (workspace: Workspace) => {
    const id = workspace.id;
    let randomLetters = generateRandomString(4);
    let deletedName = `${workspace.attributes.name.substring(0, 21)}_DEL_${randomLetters}`;

    const body = {
      data: {
        type: "workspace",
        id: id,
        attributes: {
          name: deletedName,
          deleted: "true",
        },
      },
    };
    axiosInstance
      .patch(
        `/organization/${organizationId}/workspace/${id}/relationships/vcs`,
        {
          data: null,
        },
        {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        }
      )
      .then(() => {
        axiosInstance.patch(`organization/${organizationId}/workspace/${id}`, body, genericHeader).then((response) => {
          if (response.status === 204) {
            message.success("Workspace deleted successfully");
            navigate(`/organizations/${organizationId}/workspaces`);
          } else {
            message.error("Workspace deletion failed");
          }
        });
      });
  };

  return (
    <div>
      <h1>Advanced Settings</h1>
      <p>
        Deleting thill permanently delete the information. Please be certain that you understand this. This action
        cannot be undone.
      </p>
      <Popconfirm
        onConfirm={() => {
          onDelete(workspace);
        }}
        style={{ width: "100%" }}
        title={
          <p>
            Workspace will be permanently deleted <br /> from this organization.
            <br />
            Are you sure?
          </p>
        }
        okText="Yes"
        cancelText="No"
        placement="bottom"
      >
        <Button type="default" danger style={{ width: "100%" }} disabled={!manageWorkspace}>
          <Space>
            <DeleteOutlined />
            Delete from Terrakube
          </Space>
        </Button>
      </Popconfirm>
    </div>
  );
};
