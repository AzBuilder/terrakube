import isLightColor from "./isLightColor";
import stringToDeterministicColor from "./stringToDeterministicColor";

export default function (baseHex: string) {
  const isLight = isLightColor(baseHex);
  const color = stringToDeterministicColor(baseHex);

  return {
    color: isLight ? "#ffffff" : "#000000",
    background: color,
  };
}
