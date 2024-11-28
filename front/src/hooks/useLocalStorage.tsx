import { useState, useEffect, useRef } from 'react';

export function useLocalStorage(
  key: string,
  { serialize = JSON.stringify, deserialize = JSON.parse } = {}
) {
  const [value, setValue] = useState<any>(() => {
    try {
      const valueInLocalStorage = window.localStorage.getItem(key);
      if (valueInLocalStorage) {
        return deserialize(valueInLocalStorage);
      }
      return null;
    } catch (error) {
      console.warn(`Error parsing localStorage key "${key}":`, error);
      // Remove the malformed item from localStorage
      window.localStorage.removeItem(key);
      return null;
    }
  });

  const prevKeyRef = useRef(key);

  useEffect(() => {
    try {
      const prevKey = prevKeyRef.current;

      if (prevKey !== key) {
        window.localStorage.removeItem(prevKey);
      }
      prevKeyRef.current = key;
      
      // Only set item if value is not null or undefined
      if (value !== null && value !== undefined) {
        window.localStorage.setItem(key, serialize(value));
      }
    } catch (error) {
      console.warn(`Error setting localStorage key "${key}":`, error);
    }
  }, [value, serialize, key]);

  return [value, setValue];
}