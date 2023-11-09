import { React } from "react";
import { Dropdown } from "antd";
import {
  FileJpgOutlined,
} from "@ant-design/icons";
import { toPng, toSvg, toJpeg } from "html-to-image";
import axiosInstance from "../../config/axiosConfig";
import{BsFiletypeSvg,BsFiletypeJson,BsFiletypePng} from "react-icons/bs";

export const DownloadState = ({stateUrl}) => {
  function downloadImage(dataUrl, fileName) {
    const a = document.createElement("a");
    a.setAttribute("download", fileName);
    a.setAttribute("href", dataUrl);
    a.click();
  }

  const downloadPNG = () => {
    const imageWidth = 1024;
    const imageHeight = 768;

    toPng(document.querySelector(".react-flow__renderer"), {
      width: imageWidth,
      height: imageHeight,
      style: {
        width: imageWidth,
        height: imageHeight,
      },
    }).then((dataUrl) => {
      downloadImage(dataUrl, "terrakube-state-diagram.png");
    });
  };

  const downloadJPG = () => {
    const imageWidth = 1024;
    const imageHeight = 768;

    toJpeg(document.querySelector(".react-flow__renderer"), {
      backgroundColor: "white",
      width: imageWidth,
      height: imageHeight,
      style: {
        width: imageWidth,
        height: imageHeight,
      },
    }).then((dataUrl) => {
      downloadImage(dataUrl, "terrakube-state-diagram.jpg");
    });
  };

  const downloadSVG = () => {
    const imageWidth = 1024;
    const imageHeight = 768;

    toSvg(document.querySelector(".react-flow__renderer"), {
      width: imageWidth,
      height: imageHeight,
      style: {
        width: imageWidth,
        height: imageHeight,
      },
    }).then((dataUrl) => {
      downloadImage(dataUrl, "terrakube-state-diagram.svg");
    });
  };

  const downloadJSON = () => {
    axiosInstance.request({ url: stateUrl, method: "GET", responseType:"blob" }).then((response) => {  
      const href = window.URL.createObjectURL(response.data);

      const anchorElement = document.createElement('a');

      anchorElement.href = href;
      anchorElement.download = "terrakube-state.json";

      document.body.appendChild(anchorElement);
      anchorElement.click();

      document.body.removeChild(anchorElement);
      window.URL.revokeObjectURL(href);
    });
  };

  const handleButtonClick = (e) => {
    downloadJSON();
  };

  const handleMenuClick = (e) => {
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
    <Dropdown.Button menu={menuProps} onClick={handleButtonClick}>
      Download
    </Dropdown.Button>
  );
};
