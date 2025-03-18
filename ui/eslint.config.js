import js from "@eslint/js";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import globals from "globals";
import tseslint from "typescript-eslint";
export default tseslint.config(
  { ignores: ["build"] },
  {
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,
      "plugin:react/recommended",
      "plugin:react/jsx-runtime",
      "plugin:react-hooks/recommended",
      "eslint-config-prettier",
    ],
    files: ["**/*.{ts,tsx}"],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      "react/prop-types": "off",
      "react-refresh/only-export-components": ["warn", { allowConstantExport: true }],
    },
  }
);
