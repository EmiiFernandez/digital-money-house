import React, { Suspense, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { PrivateRoutes } from './components';
import { ROUTES } from './constants';
import { Layout } from './components/Layout';
import './tailwind/styles.css';
import CircularProgress from '@mui/material/CircularProgress';
import Dashboard from './pages/Dashboard';
import { useKeycloak } from '@react-keycloak/web';


// pages
const Login = React.lazy(() => import('./pages/Login'));
const Register = React.lazy(() => import('./pages/Register'));
const Activity = React.lazy(() => import('./pages/Activity'));
const ActivityDetails = React.lazy(() => import('./pages/ActivityDetails'));
const Cards = React.lazy(() => import('./pages/Cards'));
const SendMoney = React.lazy(() => import('./pages/SendMoney'));
const LoadMoney = React.lazy(() => import('./pages/LoadMoney'));
const Profile = React.lazy(() => import('./pages/Profile'));
const PageNotFound = React.lazy(() => import('./pages/PageNotFound'));

function App() {

  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    if (initialized && !keycloak.authenticated) {
      keycloak.login();
    }
  }, [initialized, keycloak]);

  if (!initialized) {
    return (
      <div className="tw-w-full tw-h-full tw-flex tw-flex-col tw-items-center tw-justify-center">
        <CircularProgress />
      </div>
    );
  }

  return (
    <Layout isAuthenticated={keycloak.authenticated}>
      <Suspense
        fallback={
          <div className="tw-w-full tw-h-full tw-flex tw-flex-col tw-items-center tw-justify-center">
            <CircularProgress />
          </div>
        }
      >
        <Routes>
          <Route path={ROUTES.HOME} element={<PrivateRoutes />}>
            <Route element={<Dashboard />} path={ROUTES.HOME} />
            <Route element={<Activity />} path={`${ROUTES.ACTIVITY}`} />
            <Route element={<Cards />} path={ROUTES.CARDS} />
            <Route element={<SendMoney />} path={ROUTES.SEND_MONEY} />
            <Route element={<LoadMoney />} path={ROUTES.LOAD_MONEY} />
            <Route element={<Profile />} path={ROUTES.PROFILE} />
            <Route element={<ActivityDetails />} path={ROUTES.ACTIVITY_DETAILS} />
          </Route>
          <Route
            element={
              keycloak.authenticated ? (
                <Navigate replace to={ROUTES.HOME} />
              ) : (
                <Login />
              )
            }
            path={ROUTES.LOGIN}
          />
          <Route
            element={
              keycloak.authenticated ? (
                <Navigate replace to={ROUTES.HOME} />
              ) : (
                <Register />
              )
            }
            path={ROUTES.REGISTER}
          />
          <Route element={<PageNotFound />} path="*" />
        </Routes>
      </Suspense>
    </Layout>
  );
}

export default App;
