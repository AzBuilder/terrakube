import getDeterministicColors from "@/modules/utils/getDeterministicColors";
import { Tag } from "antd";
import { WorkspaceListItem } from "@/modules/workspaces/types";
import { TagModel } from "@/modules/organizations/types";

type Props = {
  item: WorkspaceListItem;
  tags: TagModel[];
};
export default function WorkspaceCardTags({ item, tags }: Props) {
  if (item.tags === undefined) return null;

  return item.tags.map((tag) => {
    const c = getDeterministicColors(tag);
    return (
      <Tag color={c.background} style={{ color: c.color }} key={tag}>
        {tags.find((tg) => tg.id === tag)?.name}
      </Tag>
    );
  });
}
