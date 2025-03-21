export default function (constantString: string): string {
  let hash = 0;
  constantString.split("").forEach((char) => {
    hash = char.charCodeAt(0) + ((hash << 4) - hash);
  });
  let colorPart = "#";
  for (let i = 0; i < 3; i++) {
    const value = (hash >> (i * 8)) & 0xff;
    colorPart += value.toString(16).padStart(2, "0");
  }
  return colorPart;
}
