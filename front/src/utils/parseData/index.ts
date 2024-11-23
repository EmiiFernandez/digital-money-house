import { IRecord } from '../../components';

export const parseRecordContent = (record: any, variant: any): IRecord => {
  return {
    content: { ...record },
    variant,
  };
};

export function parseJwt(token: string | null | undefined) {
  // Validar que el token esté presente
  if (!token || token.split('.').length !== 3) {
    console.error("Invalid or missing token");
    return null;
  }

  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => `%${('00' + c.charCodeAt(0).toString(16)).slice(-2)}`)
        .join('')
    );

    return JSON.parse(jsonPayload); // Decodifica y retorna el payload del token
  } catch (error) {
    console.error("Failed to parse JWT:", error);
    return null;
  }
}