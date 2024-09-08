import React, { useEffect } from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useLocalStorage } from '../../hooks';
import { useAuth } from '../../hooks/useAuth';

export const PrivateRoutes = () => {
  const [token] = useLocalStorage('token');
  const { isAuthenticated, setIsAuthenticated } = useAuth();

  React.useEffect(() => {
    if (token) {
      setIsAuthenticated(true);
    } else {
      setIsAuthenticated(false);
    }
  }, [token, setIsAuthenticated]);

  // Renderiza el contenido o redirige basado en la autenticación
  return isAuthenticated ? <Outlet /> : <Navigate to="/" />;

  
};
