import { GitlabOutlined, GithubOutlined } from "@ant-design/icons";
import { IconContext } from "react-icons";
import { SiBitbucket, SiGit } from "react-icons/si";
import { VscAzureDevops } from "react-icons/vsc";
import { VcsType } from "../../../domain/types";

type Props = {
  type: VcsType;
};
export default function VcsLogo({ type }: Props) {
  switch (type) {
    case VcsType.GITLAB:
      return <GitlabOutlined style={{ fontSize: "18px" }} />;
    case VcsType.BITBUCKET:
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <SiBitbucket />
          &nbsp;
        </IconContext.Provider>
      );
    case VcsType.AZURE_DEVOPS:
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <VscAzureDevops />
          &nbsp;
        </IconContext.Provider>
      );
    case VcsType.GITHUB:
      return <GithubOutlined style={{ fontSize: "18px" }} />;

    default:
      return <SiGit />;
  }
}
