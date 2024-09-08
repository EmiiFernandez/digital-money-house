import { log } from 'console';
import { useState, useEffect, useRef } from 'react';

export function useLocalStorage(
  key: string,
  { serialize = JSON.stringify, deserialize = JSON.parse } = {}
  
) {
  const [value, setValue] = useState(() => {
    // Check if the value exists in localStorage
    let valueInLocalStorage = window.localStorage.getItem(key);

    // If not found in localStorage, check the KEYCLOAK_IDENTITY cookie
    if (!valueInLocalStorage && key === 'token') {
      const cookieValue = getCookie('KEYCLOAK_IDENTITY');
      console.log("Cookie Value:", getCookie('KEYCLOAK_IDENTITY'));

      if (cookieValue) {
        valueInLocalStorage = serialize(cookieValue);
        window.localStorage.setItem(key, valueInLocalStorage);
        console.log("Local Storage Value:", localStorage.getItem('token'));

      }
    }

    // Return the deserialized value if it exists
    if (valueInLocalStorage) {
      return deserialize(valueInLocalStorage);
    }
    
    return null;
  });

  const prevKeyRef = useRef(key);

  useEffect(() => {
    const prevKey = prevKeyRef.current;

    if (prevKey !== key) {
      window.localStorage.removeItem(prevKey);
    }
    prevKeyRef.current = key;
    window.localStorage.setItem(key, serialize(value));
  }, [value, serialize, key]);

  return [value, setValue];
}

// Utility function to get the cookie value
function getCookie(name: string) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift();
  return null;
}
