import ReactDOM from 'react-dom/client';
import App from './App';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { BrowserRouter as Router } from 'react-router-dom';
import UserInfoProvider from './context/User';
import AuthProvider from './context/Auth';
const darkTheme = createTheme({
  palette: {
    mode: 'dark',
  },
});

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
 
    <AuthProvider>
      <UserInfoProvider>
        <ThemeProvider theme={darkTheme}>
          <Router>
            <App />
          </Router>
        </ThemeProvider>
      </UserInfoProvider>
    </AuthProvider>
);
