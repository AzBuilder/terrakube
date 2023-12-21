import { React, useEffect, useState } from "react";
import { Select } from "antd";
import axiosInstance from "../../config/axiosConfig";

export const Tags = ({ organizationId, workspaceId }) => {
  const [tags, setTags] = useState([]);
  const [newTags, setNewTags] = useState([]);
  const [currentTags, setCurrentTags] = useState([]);
  const [loading, setLoading] = useState(false);
  const handleSelect = (tagId) => {
    // create a new Tag
    if (!isGuid(tagId)) createNewTag(tagId);
    else addTagToWorkspace(tagId);
  };
  const createNewTag = (tagName) => {
    const body = {
      data: {
        type: "tag",
        attributes: {
          name: tagName,
        },
      },
    };

    //search for existing tag if you type the name and hit enter
    let existingTagId
    console.log("Searching existing value")
    axiosInstance
    .get(`organization/${organizationId}/tag?filter[tag]=name==${tagName}`)
    .then((oldTag) => {
      existingTagId = oldTag.data?.data[0]?.id
      console.log(`Existing TagId ${existingTagId} for ${tagName}`)

      if(existingTagId === undefined){
        console.log('Tag does not exists, creating a new one....')
        axiosInstance
        .post(`organization/${organizationId}/tag`, body, {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        })
        .then((response) => {
          newTags.push(response.data?.data);
          setNewTags(newTags);
          console.log(newTags);
          addTagToWorkspace(response.data?.data?.id);
        });
      } else {
        console.log('Adding existing tag to workspace ....')
        addTagToWorkspace(existingTagId)
      }
    });

  };
  const addTagToWorkspace = (tagId) => {
    const body = {
      data: {
        type: "workspacetag",
        attributes: {
          tagId: tagId,
        },
      },
    };

    axiosInstance
      .post(
        `organization/${organizationId}/workspace/${workspaceId}/workspaceTag`,
        body,
        {
          headers: {
            "Content-Type": "application/vnd.api+json",
          },
        }
      )
      .then((response) => {
        currentTags.data.push(response.data?.data);
        console.log(currentTags);
        setCurrentTags(currentTags);
      });
  };
  const handleDeselect = (tagId) => {
    if (!isGuid(tagId)) {
      tagId = newTags.find((x) => x.attributes.name === tagId)?.id;
    }
    console.log(tagId);
    var id = currentTags.data.find((x) => x.attributes.tagId === tagId)?.id;
    console.log(id);
    axiosInstance
      .delete(
        `organization/${organizationId}/workspace/${workspaceId}/workspaceTag/${id}`
      )
      .then((response) => {
        var currentTagsFilter = currentTags.data.filter(function (x) {
          return x.id !== id;
        });
        currentTags.data = currentTagsFilter;
        setCurrentTags(currentTags);
        console.log(currentTags);
      });
  };

  function isGuid(value) {
    var regex = /[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}/i;
    var match = regex.exec(value);
    return match != null;
  }
  const loadTags = () => {
    axiosInstance
      .get(
        `organization/${organizationId}/workspace/${workspaceId}/workspaceTag`
      )
      .then((response) => {
        axiosInstance
          .get(`organization/${organizationId}/tag`)
          .then((response) => {
            setTags(response.data);
            setLoading(false);
          });
        setCurrentTags(response.data);
        console.log(response.data);
      });
  };
  useEffect(() => {
    loadTags();
  }, [workspaceId]);

  return loading || !tags.data ? (
    <p>loading...</p>
  ) : (
    <Select
      mode="tags"
      style={{ width: "100%" }}
      onSelect={handleSelect}
      onDeselect={handleDeselect}
      defaultValue={currentTags.data.map(function (tag) {
        return tag.attributes?.tagId;
      })}
      options={tags.data.map(function (tag) {
        return { label: tag.attributes.name, value: tag.id };
      })}
      placeholder="Add a tag"
      showArrow={true}
    />
  );
};
