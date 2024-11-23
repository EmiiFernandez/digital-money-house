/* eslint-disable @typescript-eslint/no-empty-function */
import React, { createContext, useState, SetStateAction, useEffect } from "react";
import { useLocalStorage } from '../../hooks';
import { parseJwt } from "../../utils";

export const AuthContext = createContext<{
  isAuthenticated: boolean;
  setIsAuthenticated: React.Dispatch<SetStateAction<boolean>>;
  logout: () => void;
}>({
  isAuthenticated: false,
  setIsAuthenticated: () => {},
  logout: () => {},
});

const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [token, setToken] = useLocalStorage("token");
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!token);

  useEffect(() => {
    if (token) {
      try {
        const decodedToken = parseJwt(token);
        const isExpired = decodedToken.exp * 1000 < Date.now(); // Valida la expiración
        if (isExpired) {
          logout();
        } else {
          setIsAuthenticated(true);
        }
      } catch (error) {
        console.error("Invalid token:", error);
        logout();
      }
    } else {
      setIsAuthenticated(false);
    }
  }, [token]); // Ejecuta este efecto si el token cambia

  const logout = () => {
    setIsAuthenticated(false);
    setToken(null);
  };

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, setIsAuthenticated, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;

