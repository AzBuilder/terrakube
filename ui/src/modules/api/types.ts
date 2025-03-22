export interface ApiResponse<T> {
  isError: boolean;
  error?: any;
  data?: T;
  responseCode: number;
  originResponseCode?: number;
}
export type QueryParams = { [key: string]: string | boolean | number };
export type RequestOptions = {
  query?: QueryParams;
  requireAuth?: boolean;
  dataWrapped?: boolean;
  contentType?: string;
};
export type ErrorInformation = {
  title: string;
  message?: string;
};
