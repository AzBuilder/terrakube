import { AuditFieldBase } from "@/modules/types";

export type CreateTokenForm = {
  description: string;
  days: number;
};
export type CreatedToken = {
  token: string;
};
export type UserToken = {
  id: string;
  deleted: boolean;
  days: number;
  description: string;
} & AuditFieldBase;
