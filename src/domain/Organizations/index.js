import { React, useEffect } from "react";
import axiosInstance from "../../config/axiosConfig";

export const Organizations = () => {
  useEffect(() => {
    axiosInstance.get("organization")
      .then(response => {
        console.log(response)
      })
  });

  return(
    <p>Test</p>
  );
};