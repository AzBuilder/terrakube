import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import dynamicImportVars from "@rollup/plugin-dynamic-import-vars";
import commonjs from "vite-plugin-commonjs";
import viteTsconfigPaths from "vite-tsconfig-paths";

export default defineConfig(() => {
  return {
    server: {
      port: 3000,
    },
    build: {
      outDir: "build",
    },
    plugins: [react(), commonjs(), viteTsconfigPaths()],
    rollup: {
      plugins: [
        dynamicImportVars({
          // options
        }),
      ],
    },
  };
});
