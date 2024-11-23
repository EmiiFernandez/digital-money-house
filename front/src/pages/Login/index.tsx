import React, { useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputAdornment from "@mui/material/InputAdornment";
import IconButton from "@mui/material/IconButton";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { login } from "../../utils"; // Llama a tu API
import { useAuth, useLocalStorage } from "../../hooks";
import { SnackBar } from "../../components";
import { BAD_REQUEST, ERROR_MESSAGES } from "../../constants";
import { useNavigate } from "react-router-dom";  // Importa el hook

export interface LoginInputs {
  email: string;
  password: string;
}

const messageDuration = 2000;

const Login = () => {
  const navigate = useNavigate();  // Inicializa useNavigate para redirección
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginInputs>({
    criteriaMode: "all",
  });

  const [token, setToken] = useLocalStorage("token");
  const { setIsAuthenticated } = useAuth();
  const [isError, setIsError] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [message, setMessage] = useState<string>("");
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const handleClickShowPassword = () => setShowPassword(!showPassword);
  const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) =>
    event.preventDefault();

  const onSubmit: SubmitHandler<LoginInputs> = ({ email, password }) => {
    login(email, password)
      .then((response) => {
        setToken(response.token); // Asegúrate de que `response.token` sea el token JWT
        setIsAuthenticated(true); // Marca como autenticado
        navigate("/dashboard");  // Redirige al dashboard después de login exitoso
      })
      .catch((error) => {
        console.error("Login failed", error);
        setIsError(true);  // Maneja el error de login
      });
  };

  return (
    <div
      className="tw-w-full tw-flex tw-flex-col tw-flex-1 tw-items-center tw-justify-center"
      style={{
        height: "calc(100vh - 128px)",
      }}
    >
      <h2>Iniciar sesión</h2>
      <form
        className="tw-flex tw-flex-col tw-gap-y-12 tw-mt-10"
        onSubmit={handleSubmit(onSubmit)}
      >
        <FormControl variant="outlined">
          <InputLabel htmlFor="outlined-adornment-email">Correo</InputLabel>
          <OutlinedInput
            id="outlined-adornment-email"
            type="text"
            {...register("email", {
              required: "El correo es obligatorio.",
              pattern: {
                value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
                message: "Correo inválido.",
              },
            })}
            label="Correo"
          />
        </FormControl>
        {errors.email && (
          <p style={{ color: "red" }}>{errors.email.message}</p>
        )}

        <FormControl variant="outlined">
          <InputLabel htmlFor="outlined-adornment-password">Contraseña</InputLabel>
          <OutlinedInput
            id="outlined-adornment-password"
            type={showPassword ? "text" : "password"}
            {...register("password", {
              required: "La contraseña es obligatoria.",
              minLength: {
                value: 6,
                message: "La contraseña debe tener al menos 6 caracteres.",
              },
            })}
            endAdornment={
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={handleClickShowPassword}
                  onMouseDown={handleMouseDownPassword}
                  edge="end"
                  className="tw-text-neutral-gray-100"
                >
                  {showPassword ? <VisibilityOff /> : <Visibility />}
                </IconButton>
              </InputAdornment>
            }
            label="Contraseña"
          />
        </FormControl>
        {errors.password && (
          <p style={{ color: "red" }}>{errors.password.message}</p>
        )}

        <Button
          type="submit"
          variant="outlined"
          disabled={isSubmitting}
          className="tw-h-14"
        >
          {isSubmitting ? "Ingresando..." : "Ingresar"}
        </Button>
      </form>
      {message.length > 0 && (
        <SnackBar
          duration={messageDuration}
          message={message}
          type={isError ? "error" : "primary"}
        />
      )}
    </div>
  );
};

export default Login;
