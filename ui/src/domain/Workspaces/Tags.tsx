import { Select } from "antd";
import { useEffect, useState } from "react";
import axiosInstance from "../../config/axiosConfig";
import { Tag, WorkspaceTag } from "../types";
type Props = {
  organizationId: string;
  workspaceId: string;
  manageWorkspace: boolean;
};
export const Tags = ({ organizationId, workspaceId, manageWorkspace }: Props) => {
  const [tags, setTags] = useState<Tag[]>([]);
  const [newTags, setNewTags] = useState<Tag[]>([]);
  const [currentTags, setCurrentTags] = useState<WorkspaceTag[]>([]);
  const [loading, setLoading] = useState(false);
  const handleSelect = (tagId: string) => {
    // create a new Tag
    if (!isGuid(tagId)) createNewTag(tagId);
    else addTagToWorkspace(tagId);
  };
  const createNewTag = (tagName: string) => {
    const body = {
      data: {
        type: "tag",
        attributes: {
          name: tagName,
        },
      },
    };

    //search for existing tag if you type the name and hit enter
    let existingTagId;
    axiosInstance.get(`organization/${organizationId}/tag?filter[tag]=name==${tagName}`).then((oldTag) => {
      existingTagId = oldTag.data?.data[0]?.id;

      if (existingTagId === undefined) {
        axiosInstance
          .post(`organization/${organizationId}/tag`, body, {
            headers: {
              "Content-Type": "application/vnd.api+json",
            },
          })
          .then((response) => {
            setNewTags((prev) => [...prev, response.data?.data]);

            addTagToWorkspace(response.data?.data?.id);
          });
      } else {
        addTagToWorkspace(existingTagId);
      }
    });
  };
  const addTagToWorkspace = (tagId: string) => {
    const body = {
      data: {
        type: "workspacetag",
        attributes: {
          tagId: tagId,
        },
      },
    };

    axiosInstance
      .post(`organization/${organizationId}/workspace/${workspaceId}/workspaceTag`, body, {
        headers: {
          "Content-Type": "application/vnd.api+json",
        },
      })
      .then((response) => {
        currentTags.push(response.data?.data);
        setCurrentTags(currentTags);
      });
  };
  const handleDeselect = (tagId: string) => {
    let currentTag: string | undefined = tagId;
    if (!isGuid(tagId)) {
      currentTag = newTags.find((x) => x.attributes.name === tagId)?.id;
    }

    var id = currentTags.find((x) => x.attributes.tagId === currentTag)?.id;

    axiosInstance.delete(`organization/${organizationId}/workspace/${workspaceId}/workspaceTag/${id}`).then(() => {
      var currentTagsFilter = currentTags.filter(function (x) {
        return x.id !== id;
      });
      setCurrentTags(currentTagsFilter);
    });
  };

  function isGuid(value: string) {
    var regex = /[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}/i;
    var match = regex.exec(value);
    return match != null;
  }
  const loadTags = () => {
    axiosInstance.get(`organization/${organizationId}/workspace/${workspaceId}/workspaceTag`).then((response) => {
      axiosInstance.get(`organization/${organizationId}/tag`).then((res) => {
        setTags(res.data.data);
        setLoading(false);
      });
      setCurrentTags(response.data.data);
      console.log(response.data);
    });
  };
  useEffect(() => {
    loadTags();
  }, [workspaceId]);

  console.log(
    "mapped",
    currentTags.map(function (tag) {
      return tag.attributes?.tagId;
    })
  );

  return loading ? (
    <p>loading...</p>
  ) : (
    <Select
      mode="tags"
      style={{ width: "100%" }}
      onSelect={handleSelect}
      onDeselect={handleDeselect}
      defaultValue={currentTags.map(function (tag) {
        return tag.attributes?.tagId;
      })}
      options={tags.map(function (tag) {
        return { label: tag.attributes.name, value: tag.id };
      })}
      placeholder="Add a tag"
      disabled={!manageWorkspace}
    />
  );
};
