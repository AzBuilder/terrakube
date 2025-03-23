import { IconContext } from "react-icons";
import { SiTerraform } from "react-icons/si";

type Props = {
  type: string;
};
export default function IacTypeLogo({ type }: Props) {
  switch (type) {
    case "terraform":
      return (
        <IconContext.Provider value={{ size: "18px" }}>
          <SiTerraform />
        </IconContext.Provider>
      );
    case "opentofu":
      return <img width="18px" alt="opentofu-logo" src="/providers/opentofu.png" />;

    default:
      return null;
  }
}
