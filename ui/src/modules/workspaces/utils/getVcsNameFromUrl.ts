import formatSshUrl from "./formatSshUrl";

export default function (normalizedUrl: string): string {
  // Let's just be safe in case the wrong url was passed
  const fixedUrl = formatSshUrl(normalizedUrl);
  const urlObj = new URL(fixedUrl);
  const pathname = urlObj.pathname;
  return pathname.substring(1);
}
