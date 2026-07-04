export interface Medicine {
  id?: string;
  medicineId?: string;
  medicineName: string;
  dosage: string;
  active?: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
