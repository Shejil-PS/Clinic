export interface Patient {
  id?: string;
  patientId?: string;
  fullName: string;
  phone: string;
  age: number;
  gender: string;
  address?: string;
}

export interface Visit {
  id?: string;
  visitId?: string;
  patientId: string;
  visitDate?: string;
  status?: string;
}

export interface VisitDTO {
  visitId: string;
  patientId: string;
  patientName: string;
  visitDate: string;
  status: string;
  visitStatus?: string;
  doctorName?: string;
}
