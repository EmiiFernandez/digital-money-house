import React, { createContext, useEffect, useReducer } from "react";
import userReducer from "./userReducer";
import { User } from "../../types";
import { useAuth, useLocalStorage } from "../../hooks";
import { getUserByKeycloakId, parseJwt } from "../../utils";
import { userActionTypes } from "./types";
import { UNAUTHORIZED } from "../../constants/status";

export interface UserInfoState {
  user: User | null;
  loading: boolean;
}

const initialState: UserInfoState = {
  user: null,
  loading: true,
};

export const userInfoContext = createContext<{
  user: User | null;
  loading: boolean;
  dispatch: React.Dispatch<any>;
}>({
  ...initialState,
  dispatch: () => null,
});

const UserInfoProvider = ({ children }: { children: React.ReactNode }) => {
  const [state, dispatch] = useReducer(userReducer, initialState);
  const [token, setToken] = useLocalStorage("token");
  const { isAuthenticated, setIsAuthenticated } = useAuth();

  useEffect(() => {
    const fetchUserInfo = async () => {
      if (isAuthenticated) {
        try {
          if (token) {
            const info = parseJwt(token);
            const keycloakId = info?.sub; // Usa 'sub' como Keycloak ID

            if (keycloakId) {
              const user = await getUserByKeycloakId(keycloakId);
              dispatch({ type: userActionTypes.SET_USER, payload: user });
              dispatch({
                type: userActionTypes.SET_USER_LOADING,
                payload: false,
              });
            }
          } else {
            setIsAuthenticated(false);
          }
        } catch (error: any) {
          if (error.status === UNAUTHORIZED) {
            setToken(null);
            setIsAuthenticated(false);
          }
          console.error("Error fetching user info:", error);
          dispatch({
            type: userActionTypes.SET_USER_LOADING,
            payload: false,
          });
        }
      }
    };

    fetchUserInfo();
  }, [isAuthenticated, setIsAuthenticated, setToken, token]);

  return (
    <userInfoContext.Provider
      value={{ user: state.user, loading: state.loading, dispatch }}
    >
      {children}
    </userInfoContext.Provider>
  );
};

export default UserInfoProvider;
