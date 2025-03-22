export default function fixSshUrl(source: string): string {
  if (source.indexOf("ssh.dev.azure.com") !== -1) {
    if (source.startsWith("git@")) {
      return source.replace(":", "/").replace("git@", "https://").replace("ssh.", "").replace(":v3", "");
    }
    const atIndex = source.indexOf("@");
    if (atIndex !== -1) {
      source = `https://${source.substring(atIndex + 1, source.length)}`;
    }
    return source.replace("/_git", "");
  }

  if (source.startsWith("git@")) {
    return source.replace(":", "/").replace("git@", "https://");
  }

  return source;
}
