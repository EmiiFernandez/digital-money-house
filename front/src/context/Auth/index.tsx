/* eslint-disable @typescript-eslint/no-empty-function */
import React, { createContext,  SetStateAction, ReactNode } from 'react';
import Keycloak from 'keycloak-js';
import { ReactKeycloakProvider} from '@react-keycloak/web';

export const AuthContext = createContext<{
  isAuthenticated: boolean;
  setIsAuthenticated: React.Dispatch<SetStateAction<boolean>>;
  logout: () => void;
}>({
  isAuthenticated: false,
  setIsAuthenticated: () => {},
  logout: () => {},
});

const AuthProvider = ({ children }: { children: ReactNode }) => {

  const client = new Keycloak({
    realm: 'dmh_realm_dev',
    url: 'http://localhost:3000',
    clientId: 'front-dmh-client',
  });

  return (
    <ReactKeycloakProvider authClient={client}>
      {children}
    </ReactKeycloakProvider>
  );
};

export default AuthProvider;
