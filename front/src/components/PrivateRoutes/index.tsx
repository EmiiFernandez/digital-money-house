import React, { useEffect } from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useLocalStorage } from '../../hooks';
import { useAuth } from '../../hooks/useAuth';

export const PrivateRoutes = () => {
  const [token] = useLocalStorage('token');
  const { isAuthenticated, setIsAuthenticated } = useAuth();

  useEffect(() => {
    if (token) {
      setIsAuthenticated(true); // Si hay token, el usuario está autenticado
    } else {
      setIsAuthenticated(false); // Si no hay token, el usuario no está autenticado
    }
  }, [token, setIsAuthenticated]);

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" />;
};
