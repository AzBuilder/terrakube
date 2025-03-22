export default function (url: string): string {
  const urlObj = new URL(url);
  const pathname = urlObj.pathname;
  const hostname = urlObj.hostname;

  if (hostname === "dev.azure.com") {
    return pathname.replace("/v3/", "");
  }
  if (pathname.endsWith(".git")) return pathname.replace(".git", "").substring(1);
  return pathname;
}
