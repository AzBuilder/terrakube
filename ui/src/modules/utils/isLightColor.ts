function parseRgb(hexColor: string): number[] {
  return [
    parseInt(hexColor.substring(1, 3), 16),
    parseInt(hexColor.substring(3, 5), 16),
    parseInt(hexColor.substring(5), 16),
  ];
}

export default function (hexColor: string) {
  const rgb = parseRgb(hexColor);
  const luminance = (0.2126 * rgb[0]) / 255 + (0.7152 * rgb[1]) / 255 + (0.0722 * rgb[2]) / 255;
  return luminance > 0.5;
}
