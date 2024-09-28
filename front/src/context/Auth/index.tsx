/* eslint-disable @typescript-eslint/no-empty-function */
import {createContext, ReactNode, SetStateAction } from 'react';
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
    url: 'http://localhost:9092',
    realm: 'dmh-realm-dev',
    clientId: 'auth-client-dev',
  });

  return (
    <ReactKeycloakProvider
      authClient={client}
      initOptions={{ onLoad: 'check-sso' }} // Este parámetro forza el login en la carga de la app
    >
      {children}
    </ReactKeycloakProvider>
  );
};

export default AuthProvider;  
