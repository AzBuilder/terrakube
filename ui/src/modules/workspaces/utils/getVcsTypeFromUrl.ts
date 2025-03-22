import fixSshUrl from "@/modules/workspaces/utils/fixSshUrl";
import { VcsType } from "../../../domain/types";

export default function (url: string): VcsType {
  const githubEndpoints = ["github.com", "ghe.com"];
  const devOpsEndpints = ["visualstudio.com", "dev.azure.com"];
  const gitlabEndpoints = ["gitlab.com"];
  const bitbucketEndpoints = ["bitbucket.com"];
  const fixedUrl = fixSshUrl(url);
  const hostname = new URL(fixedUrl).hostname;
  if (githubEndpoints.includes(hostname)) return VcsType.GITHUB;
  if (devOpsEndpints.includes(hostname)) return VcsType.AZURE_DEVOPS;
  if (gitlabEndpoints.includes(hostname)) return VcsType.GITLAB;
  if (bitbucketEndpoints.includes(hostname)) return VcsType.BITBUCKET;
  return VcsType.UNKNOWN;
}
