import type { OnMount } from '@monaco-editor/react';
import { ThemeMode } from './themeConfig';

type IStandaloneCodeEditor = Parameters<OnMount>[0];

export const getMonacoTheme = (themeMode: ThemeMode): string => {
  return themeMode === 'dark' ? 'vs-dark' : 'vs-light';
};

export const monacoOptions: Parameters<IStandaloneCodeEditor['updateOptions']>[0] = {
  minimap: {
    enabled: false
  },
  scrollBeyondLastLine: false,
  fontSize: 14,
  lineNumbers: 'on',
  roundedSelection: false,
  scrollbar: {
    vertical: 'visible',
    horizontal: 'visible',
    useShadows: false,
    verticalScrollbarSize: 10,
    horizontalScrollbarSize: 10
  }
}; 