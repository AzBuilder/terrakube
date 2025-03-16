import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import dynamicImportVars from "@rollup/plugin-dynamic-import-vars";
import commonjs from "vite-plugin-commonjs";

export default defineConfig(() => {
  return {
    server: {
      port: 3000,
    },
    build: {
      outDir: "build",
    },
    plugins: [react(), commonjs()],
    rollup: {
      plugins: [
        dynamicImportVars({
          // options
        }),
      ],
    },
  };
});
