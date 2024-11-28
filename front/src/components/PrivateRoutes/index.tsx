import React, { useEffect } from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useLocalStorage } from '../../hooks';
import { useAuth } from '../../hooks/useAuth';

export const PrivateRoutes = () => {
  const [token] = useLocalStorage('token');
  const { isAuthenticated, setIsAuthenticated } = useAuth();
  
  useEffect(() => {
    console.log('Token in PrivateRoutes:', token);
    
    if (token) {
      setIsAuthenticated(true);
    } else {
      setIsAuthenticated(false);
    }
  }, [setIsAuthenticated, token]);

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" />;
};