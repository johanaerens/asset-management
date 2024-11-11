import dayjs from 'dayjs';
import { IAsset } from 'app/shared/model/asset.model';
import { IEmployee } from 'app/shared/model/employee.model';

export interface IAssetHistory {
  id?: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  asset?: IAsset | null;
  employee?: IEmployee | null;
}

export const defaultValue: Readonly<IAssetHistory> = {};
