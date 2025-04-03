export default function (source: string): string {
  if (source.endsWith(".git")) {
    source = source.replace(".git", "");
  }

  if (source.startsWith("git@")) {
    source = source.replace("git@", "https://");
  }

  if (source.includes("dev.azure.com")) {
    source = source.replace(":v3", "").replace("ssh.", "");
    source = stripUserFromHost(source);
    source = source.replace("/_git", "");
  }

  if (source.includes("github.com") || source.includes("ghe.com")) {
    source = source.replace(".com:", ".com/");
  }

  if (source.includes("bitbucket.org")) {
    source = source.replace(".org:", ".org/");
    source = stripUserFromHost(source);
  }

  if (source.includes("gitlab.com")) {
    source = source.replace(".com:", ".com/");
  }

  return source;
}

function stripUserFromHost(source: string): string {
  const atIndex = source.indexOf("@");
  if (atIndex !== -1) {
    source = `https://${source.substring(atIndex + 1, source.length)}`;
  }
  return source;
}
