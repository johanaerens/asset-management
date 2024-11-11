import dayjs from 'dayjs';
import { Language } from 'app/shared/model/enumerations/language.model';

export interface IEmployee {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  employeeNumber?: string | null;
  phoneNumber?: string | null;
  hireDate?: dayjs.Dayjs | null;
  language?: keyof typeof Language | null;
}

export const defaultValue: Readonly<IEmployee> = {};
