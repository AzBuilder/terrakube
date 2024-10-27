import { GithubOutlined, GitlabOutlined } from "@ant-design/icons";
import { message } from "antd";
import { IconContext } from "react-icons";
import { SiAzuredevops, SiBitbucket, SiTerraform } from "react-icons/si";
import axiosInstance from "../../config/axiosConfig";

export const genericHeader = {
  headers: {
    "Content-Type": "application/vnd.api+json",
  },
};

export function compareVersions(a, b) {
  if (a === b) {
    return 0;
  }
  let splitA = a.replace("v", "").split('.');
  let splitB = b.replace("v", "").split('.');
  const length = Math.max(splitA.length, splitB.length);
  for (let i = 0; i < length; i++) {
    if (parseInt(splitA[i]) > parseInt(splitB[i]) ||
      ((splitA[i] === splitB[i]) && isNaN(splitB[i + 1]))) {
      return 1;
    }
    if (parseInt(splitA[i]) < parseInt(splitB[i]) ||
      ((splitA[i] === splitB[i]) && isNaN(splitA[i + 1]))) {
      return -1;
    }
  }
}

export const renderVCSLogo = (vcs) => {
  switch (vcs) {
    case "GITLAB":
      return <GitlabOutlined style={{ fontSize: "18px" }} />;
    case "BITBUCKET":
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <SiBitbucket />
          &nbsp;
        </IconContext.Provider>
      );
    case "AZURE_DEVOPS":
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <SiAzuredevops />
          &nbsp;
        </IconContext.Provider>
      );
    default:
      return <GithubOutlined style={{ fontSize: "18px" }} />;
  }
}

export const atomicHeader = {
  headers: {
    "content-type": "application/vnd.api+json;ext=\"https://jsonapi.org/ext/atomic\"",
    "accept": "application/vnd.api+json;ext=\"https://jsonapi.org/ext/atomic\"",
  },
};

// This should be re-written to handle only one webhook
export const deleteWebhook = (organizationId, workspaceId, webhook) => {
  const webhooks = Object.entries(webhook);
  if (webhooks.length == 0) return;
  var body = {
    "atomic:operations": []
  };
  webhooks.map(([_, hook]) => {
    body["atomic:operations"].push({
      op: "remove",
      href: `/organization/${organizationId}/workspace/${workspaceId}/webhook/${hook.id}`,
    });
  });
  try {
    axiosInstance.post(
      "/operations",
      body, atomicHeader
    )
      .then((response) => {
        if (response.status == 200) {
          message.success("Webhook deleted successfully");
        } else {
          message.error("Webhook deletion failed");
        }
      });
  } catch (error) {
    console.error("Error deleting webhook:", error);
    message.error("Webhook deletion failed");
    if (error.response) {
      if (error.response.status === 424) {
        message.error("Failed to delete webhook, please check if the set VCS connection has the correct permissions on the linked repository.");
      }
    }
  }
}

export const getIaCIconById = (id) => {
  const item = iacTypes.find((iacType) => iacType.id === id);
  return item ? item.icon : null;
}
export const getIaCNameById = (id) => {
  const item = iacTypes.find((iacType) => iacType.id === id);
  return item ? item.name : null;
}

export const iacTypes = [
  {
    id: "terraform",
    name: "Terraform",
    description:
      "Create an empty template. So you can define your template from scratch.",
    icon: (
      <IconContext.Provider value={{ size: "1.4em" }}>
        <SiTerraform />
      </IconContext.Provider>
    ),
  },
  {
    id: "tofu",
    name: "OpenTofu",
    icon: <img width="18px" src="/providers/opentofu.png" />,
  },
];


