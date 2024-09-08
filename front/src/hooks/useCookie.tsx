import { useState, useEffect, useRef } from 'react';

export function useCookie(
  key: string,
  { serialize = JSON.stringify, deserialize = JSON.parse } = {}
) {
  const [value, setValue] = useState(() => {
    const valueInCookie = getCookie(key);
    if (valueInCookie) {
      return deserialize(valueInCookie);
    }
    return null;
  });

  const prevKeyRef = useRef(key);

  useEffect(() => {
    const prevKey = prevKeyRef.current;

    if (prevKey !== key) {
      setCookie(prevKey, '', { expires: -1 });
    }
    prevKeyRef.current = key;
    setCookie(key, serialize(value), { expires: 7 }); // Ajusta la expiración a 7 días, por ejemplo
  }, [value, serialize, key]);

  return [value, setValue];
}

function getCookie(name: string) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    const cookieValue = parts.pop();
    if (cookieValue) {
      return cookieValue.split(';').shift();
    }
  }
  return null;
}

function setCookie(name: string, value: string, options: any = {}) {
  let updatedCookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}`;

  if (options.expires) {
    const date = new Date();
    date.setTime(date.getTime() + options.expires * 24 * 60 * 60 * 1000);
    updatedCookie += `; expires=${date.toUTCString()}`;
  }

  updatedCookie += `; path=/`;
  document.cookie = updatedCookie;
}
