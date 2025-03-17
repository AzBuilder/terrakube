import * as Icons from "@ant-design/icons";
import { transform } from "@babel/standalone";
import { Collapse, DatePicker, Typography } from "antd";
import React, { Component, ErrorInfo, ReactNode, useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import { Crosshair, Hint, HorizontalGridLines, LineSeries, VerticalGridLines, XAxis, XYPlot, YAxis } from "react-vis";
import "react-vis/dist/style.css";
import axiosInstance from "./config/axiosConfig";

// List of antd components to consider for dynamic importing
const antdComponents = [
  "Affix",
  "Anchor",
  "AutoComplete",
  "Alert",
  "Avatar",
  "BackTop",
  "Badge",
  "Breadcrumb",
  "Button",
  "Calendar",
  "Card",
  "Collapse",
  "Carousel",
  "Cascader",
  "Checkbox",
  "Col",
  "ConfigProvider",
  "DatePicker",
  "Descriptions",
  "Divider",
  "Dropdown",
  "Drawer",
  "Empty",
  "Form",
  "Input",
  "InputNumber",
  "Layout",
  "List",
  "message",
  "Menu",
  "Mentions",
  "Modal",
  "Statistic",
  "notification",
  "PageHeader",
  "Pagination",
  "Popconfirm",
  "Popover",
  "Progress",
  "Radio",
  "Rate",
  "Result",
  "Row",
  "Select",
  "Skeleton",
  "Slider",
  "Space",
  "Spin",
  "Steps",
  "Switch",
  "Table",
  "Transfer",
  "Tree",
  "TreeSelect",
  "Tabs",
  "Tag",
  "TimePicker",
  "Timeline",
  "Tooltip",
  "Typography",
  "Upload",
];

const { Panel } = Collapse;
const { Paragraph, Text } = Typography;
const { RangePicker } = DatePicker;
const { DateTime } = require("luxon");

// List of antd icons to consider for dynamic importing
const antdIcons = Object.keys(Icons).filter((name) => name.endsWith("Outlined"));

// Function to identify required antd components
const getRequiredAntdComponents = (componentString: string) => {
  return antdComponents.filter((component) => componentString.includes(component));
};

// Function to identify required antd icons
const getRequiredAntdIcons = (componentString: string) => {
  return antdIcons.filter((icon) => componentString.includes(icon));
};

// Function to dynamically import antd components
const importAntdComponents = async (components: string[]) => {
  const imports = await Promise.all(
    components.map((component: string) => import(`antd/es/${component.toLowerCase()}/index.js`))
  );
  const importedComponents: Record<string, any> = {};
  components.forEach((component: string, index: number) => {
    importedComponents[component] = imports[index].default;
  });
  return importedComponents;
};

// Function to dynamically import antd icons
const importAntdIcons = async (icons: any) => {
  const imports = await Promise.all(icons.map((icon: string) => import(`@ant-design/icons/es/icons/${icon}`)));
  const importedIcons: Record<string, any> = {};
  icons.forEach((icon: string, index: number) => {
    importedIcons[icon] = imports[index].default;
  });
  return importedIcons;
};

// List of react-icons to consider for dynamic importing
const reactIcons = [
  "VscAzure",
  "SiAmazonaws",
  "SiGithub",
  "SiGrafana",
  "SiKubernetes",
  "SiTerraform",
  "SiDocker",
  "SiPrometheus",
  "SiGooglecloud",
  "SiOpenai",
];

// Function to identify required react-icons components
const getRequiredReactIcons = (componentString: string) => {
  return reactIcons.filter((icon) => componentString.includes(icon));
};

// Function to dynamically import react-icons components
const importReactIcons = async (icons: string[]) => {
  const imports: any = await Promise.all(icons.map(() => import(`react-icons/si`)));
  const importedIcons: Record<any, any> = {};
  icons.forEach((icon: any, index: any) => {
    importedIcons[icon] = imports[index][icon];
  });
  return importedIcons;
};

type Props = {
  children: ReactNode;
};

type State = {
  hasError: boolean;
};

class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(_: Error) {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("ErrorBoundary caught an error", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return <div>An error occurred while rendering this action.</div>;
    }

    return this.props.children;
  }
}

const ActionLoader = ({ action, context }: { action: any; context: any }) => {
  const [Component, setComponent] = useState<any>(null);

  useEffect(() => {
    const loadComponent = async () => {
      try {
        const componentString = decodeURIComponent(escape(window.atob(action)));
        console.debug("Decoded Component String:", componentString);

        const requiredAntdComponents = getRequiredAntdComponents(componentString);
        const requiredAntdIcons = getRequiredAntdIcons(componentString);
        const requiredReactIcons = getRequiredReactIcons(componentString);

        const [importedComponents, importedIcons, importedReactIcons] = await Promise.all([
          importAntdComponents(requiredAntdComponents),
          importAntdIcons(requiredAntdIcons),
          importReactIcons(requiredReactIcons),
        ]);

        // Transform JSX to JavaScript
        let transpiledCode = transform(componentString, {
          presets: ["react"],
        }).code;
        console.debug("Transpiled Code:", transpiledCode);

        // Remove the last semicolon
        const lastSemicolonIndex = transpiledCode!.lastIndexOf(";");
        if (lastSemicolonIndex !== -1) {
          transpiledCode = transpiledCode!.slice(0, lastSemicolonIndex);
        }

        // Create the component function
        const createComponent = new Function(
          "React",
          "useEffect",
          "useState",
          "Panel",
          "Paragraph",
          "Text",
          "XYPlot",
          "LineSeries",
          "XAxis",
          "YAxis",
          "Hint",
          "Crosshair",
          "HorizontalGridLines",
          "VerticalGridLines",
          "axiosInstance",
          "RangePicker",
          "DateTime",
          "ReactMarkdown",
          ...requiredAntdComponents,
          ...requiredAntdIcons,
          ...requiredReactIcons,
          `return (${transpiledCode})`
        );
        const component = createComponent(
          React,
          useEffect,
          useState,
          Panel,
          Paragraph,
          Text,
          XYPlot,
          LineSeries,
          XAxis,
          YAxis,
          Hint,
          Crosshair,
          HorizontalGridLines,
          VerticalGridLines,
          axiosInstance,
          RangePicker,
          DateTime,
          ReactMarkdown,
          ...requiredAntdComponents.map((component) => importedComponents[component]),
          ...requiredAntdIcons.map((icon) => importedIcons[icon]),
          ...requiredReactIcons.map((icon) => importedReactIcons[icon])
        );
        console.debug("Component Function:", component);

        setComponent(() => component);
      } catch (error) {
        console.error("Error creating component:", error);
        setComponent(() => () => <div>Error loading component</div>);
      }
    };

    loadComponent();
  }, [action]);

  if (!Component) {
    return <div>Loading...</div>;
  }

  return (
    <ErrorBoundary>
      <Component context={context} />
    </ErrorBoundary>
  );
};

export default ActionLoader;
