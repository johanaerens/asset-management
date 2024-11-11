import dayjs from 'dayjs';
import { IEmployee } from 'app/shared/model/employee.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IAsset {
  id?: number;
  number?: string | null;
  brand?: string | null;
  model?: string | null;
  serialNumber?: string | null;
  purchaseDate?: dayjs.Dayjs | null;
  warantDate?: dayjs.Dayjs | null;
  comments?: string | null;
  status?: keyof typeof Status | null;
  employee?: IEmployee | null;
}

export const defaultValue: Readonly<IAsset> = {};
