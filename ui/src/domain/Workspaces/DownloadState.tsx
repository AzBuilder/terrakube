import { FileJpgOutlined } from "@ant-design/icons";
import { Dropdown } from "antd";
import { toJpeg, toPng, toSvg } from "html-to-image";
import { BsFiletypeJson, BsFiletypePng, BsFiletypeSvg } from "react-icons/bs";
import axiosInstance from "../../config/axiosConfig";

type Props = {
  stateUrl: string;
  manageState: boolean;
};

export const DownloadState = ({ stateUrl, manageState }: Props) => {
  function downloadImage(dataUrl: string, fileName: string) {
    const a = document.createElement("a");
    a.setAttribute("download", fileName);
    a.setAttribute("href", dataUrl);
    a.click();
  }

  const downloadPNG = () => {
    const imageWidth = 1024;
    const imageHeight = 768;

    toPng(document.querySelector(".react-flow__renderer") as HTMLElement, {
      width: imageWidth,
      height: imageHeight,
      style: {
        width: imageWidth.toString(),
        height: imageHeight.toString(),
      },
    }).then((dataUrl) => {
      downloadImage(dataUrl, "terrakube-state-diagram.png");
    });
  };

  const downloadJPG = () => {
    const imageWidth = 1024;
    const imageHeight = 768;

    toJpeg(document.querySelector(".react-flow__renderer") as HTMLElement, {
      backgroundColor: "white",
      width: imageWidth,
      height: imageHeight,
      style: {
        width: imageWidth.toString(),
        height: imageHeight.toString(),
      },
    }).then((dataUrl) => {
      downloadImage(dataUrl, "terrakube-state-diagram.jpg");
    });
  };

  const downloadSVG = () => {
    const imageWidth = 1024;
    const imageHeight = 768;

    toSvg(document.querySelector(".react-flow__renderer") as HTMLElement, {
      width: imageWidth,
      height: imageHeight,
      style: {
        width: imageWidth.toString(),
        height: imageHeight.toString(),
      },
    }).then((dataUrl) => {
      downloadImage(dataUrl, "terrakube-state-diagram.svg");
    });
  };

  const downloadJSON = () => {
    axiosInstance.request({ url: stateUrl, method: "GET", responseType: "blob" }).then((response) => {
      const href = window.URL.createObjectURL(response.data);

      const anchorElement = document.createElement("a");

      anchorElement.href = href;
      anchorElement.download = "terrakube-state.json";

      document.body.appendChild(anchorElement);
      anchorElement.click();

      document.body.removeChild(anchorElement);
      window.URL.revokeObjectURL(href);
    });
  };

  const handleButtonClick = () => {
    downloadJSON();
  };

  const handleMenuClick = (e: { key: string }) => {
    switch (e.key) {
      case "1":
        downloadJSON();
        break;
      case "2":
        downloadPNG();
        break;
      case "3":
        downloadJPG();
        break;
      case "4":
        downloadSVG();
        break;
      default:
        downloadJSON();
        break;
    }
  };

  const items = [
    {
      label: "Download JSON",
      key: "1",
      icon: <BsFiletypeJson />,
    },
    {
      label: "Download JPEG",
      key: "3",
      icon: <FileJpgOutlined />,
    },
    {
      label: "Donwload PNG",
      key: "2",
      icon: <BsFiletypePng />,
    },
    {
      label: "Download SVG",
      key: "4",
      icon: <BsFiletypeSvg />,
    },
  ];

  const menuProps = {
    items,
    onClick: handleMenuClick,
  };

  return (
    <Dropdown.Button menu={menuProps} onClick={handleButtonClick} disabled={!manageState}>
      Download
    </Dropdown.Button>
  );
};
