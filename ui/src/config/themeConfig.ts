import { ThemeConfig, theme } from 'antd';

export type ColorSchemeOption = 'default' | 'terrakube';
export type ThemeMode = 'light' | 'dark';

export const getThemeConfig = (colorScheme: ColorSchemeOption, themeMode: ThemeMode): ThemeConfig => {
  const colorPrimary = colorScheme === 'default' ? '#1890ff' : '#722ED1';
  
  return {
    algorithm: themeMode === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
    token: {
      colorPrimary,
    },
    components: {
      Menu: {
        darkItemBg: '#000000',
        darkPopupBg: '#000000',
        darkSubMenuItemBg: '#000000',
      },
      Layout: {
        headerBg: '#000000',
      },
    },
  };
};

export const defaultColorScheme: ColorSchemeOption = 'default';
export const defaultThemeMode: ThemeMode = 'light';

// Export a default theme configuration using the default color scheme and theme mode
export const themeConfig = getThemeConfig(defaultColorScheme, defaultThemeMode); 