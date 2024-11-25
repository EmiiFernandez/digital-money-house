import React, { useMemo, useState } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import Button from '@mui/material/Button';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import { useNavigate } from "react-router-dom"; // Importa useNavigate
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import {
  isValueEmpty,
  valuesHaveErrors,
  emailValidationConfig,
  passwordValidationConfig,
  nameValidationConfig,
  phoneValidationConfig,
  dniValidationConfig,
  handleChange,
  createAnUser,
} from '../../utils/';
import { ErrorMessage, Errors } from '../../components/ErrorMessage';
import { SnackBar } from '../../components';
import {
  ERROR_MESSAGES,
  SUCCESS_MESSAGES_KEYS,
  SUCCESS_MESSAGES,
  BAD_REQUEST,
  ROUTES,
} from '../../constants/';

interface RegisterState {
  name: string;
  lastname: string;
  phone: string;
  dni: number | null;
  email: string;
  password: string;
  passwordRepeated: string;
  showPassword: boolean;
}

interface RegisterInputs {
  name: string;
  lastname: string;
  phone: string;
  dni: number;
  email: string;
  password: string;
  passwordRepeated: string;
}

const messageDuration = 2000;

const Register = () => {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isDirty },
  } = useForm<RegisterInputs>({
    criteriaMode: 'all',
  });

  const navigate = useNavigate(); // Inicializa useNavigate

  const [values, setValues] = useState<RegisterState>({
    email: '',
    password: '',
    name: '',
    lastname: '',
    phone: '',
    dni: null,
    passwordRepeated: '',
    showPassword: false,
  });

  const [isSuccess, setIsSuccess] = useState<boolean>(false);
  const [isError, setIsError] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [message, setMessage] = useState<string>('');

  const isEmpty = isValueEmpty(values);
  const hasErrors = useMemo(() => valuesHaveErrors(errors), [errors]);

  const handleClickShowPassword = () => {
    setValues({
      ...values,
      showPassword: !values.showPassword,
    });
  };

  const handleMouseDownPassword = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    event.preventDefault();
  };

  const onChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    maxLength?: number
  ) => handleChange<RegisterState>(event, setValues, maxLength);

  const onSubmit: SubmitHandler<RegisterInputs> = ({
    name,
    lastname,
    password,
    phone,
    dni,
    email,
  }) => {
    setIsSubmitting(true);
    createAnUser({
      firstname: name,
      lastname,
      password,
      phone,
      dni,
      email,
    })
      .then((response) => {
        setIsSuccess(true);
        setMessage(SUCCESS_MESSAGES[SUCCESS_MESSAGES_KEYS.USER_REGISTER]);
        console.log('User registered successfully:', response); // Muestra la respuesta en consola
        setIsSubmitting(false);
        setTimeout(() => {
          navigate(ROUTES.LOGIN); // Redirige al login tras el éxito
        }, messageDuration); // Da tiempo para que el SnackBar sea visible

      })
      .catch((error) => {
        console.error('Error creating user:', error);
        setIsError(true);
        setMessage(ERROR_MESSAGES.INVALID_USER);
        setIsSubmitting(false);
      });
  };


  return (
    <div className="tw-w-full tw-h-full tw-flex tw-flex-col tw-flex-1 tw-items-center tw-justify-center">
      <h2>Crear cuenta</h2>
      <div className="tw-flex tw-max-w-3xl">
        <form
          className="tw-flex tw-flex-wrap tw-gap-x-16 tw-gap-y-12 tw-mt-10 tw-bg-background tw-justify-between"
          onSubmit={handleSubmit(onSubmit)}
        >
          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-password">
                Nombre
              </InputLabel>
              <OutlinedInput
                id="outlined-adornment-name"
                type="text"
                value={values.name}
                {...register('name', nameValidationConfig)}
                onChange={onChange}
                label="nombre"
              />
            </FormControl>
            {errors.name && <ErrorMessage errors={errors.name as Errors} />}
          </div>
          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-password">
                Apellido
              </InputLabel>
              <OutlinedInput
                id="outlined-adornment-last-name"
                type="text"
                value={values.lastname}
                {...register('lastname', nameValidationConfig)}
                onChange={onChange}
                label="lastname"
              />
            </FormControl>
            {errors.lastname && (
              <ErrorMessage errors={errors.lastname as Errors} />
            )}
          </div>
          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-dni">DNI</InputLabel>
              <OutlinedInput
                id="outlined-adornment-dni"
                type="number"
                value={values.dni ?? ''} 
                {...register('dni', dniValidationConfig)}
                onChange={(event) => onChange(event, 8)}
                label="dni"
                autoComplete="off"
              />
            </FormControl>
            {errors.dni && <ErrorMessage errors={errors.dni as Errors} />}
          </div>

          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-password">
                Correo
              </InputLabel>
              <OutlinedInput
                id="outlined-adornment-email"
                type="text"
                value={values.email}
                {...register('email', emailValidationConfig)}
                onChange={onChange}
                label="email"
              />
            </FormControl>
            {errors.email && <ErrorMessage errors={errors.email as Errors} />}
          </div>
          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-password">
                Contraseña
              </InputLabel>
              <OutlinedInput
                id="outlined-adornment-password"
                type={values.showPassword ? 'text' : 'password'}
                value={values.password}
                {...register('password', passwordValidationConfig)}
                onChange={onChange}
                endAdornment={
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      onMouseDown={handleMouseDownPassword}
                      edge="end"
                      className="tw-text-neutral-gray-100"
                    >
                      {values.showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                }
                label="Password"
                autoComplete="off"
              />
            </FormControl>
            {errors.password && (
              <ErrorMessage errors={errors.password as Errors} />
            )}
          </div>

          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-password-repeated">
                Confirmar contraseña
              </InputLabel>
              <OutlinedInput
                id="outlined-adornment-password-repeated"
                type={values.showPassword ? 'text' : 'password'}
                value={values.passwordRepeated}
                {...register('passwordRepeated', {
                  validate: (value: string) => {
                    if (watch('password') !== value) {
                      return ERROR_MESSAGES.PASSWORDS_DO_NOT_MATCH;
                    }
                  },
                })}
                onChange={onChange}
                endAdornment={
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      onMouseDown={handleMouseDownPassword}
                      edge="end"
                      className="tw-text-neutral-gray-100"
                    >
                      {values.showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                }
                label="Password"
                autoComplete="off"
              />
            </FormControl>
            {errors.password && (
              <ErrorMessage errors={errors.passwordRepeated as Errors} />
            )}
          </div>

          <div>
            <FormControl variant="outlined">
              <InputLabel htmlFor="outlined-adornment-dni">Télefono</InputLabel>
              <OutlinedInput
                id="outlined-adornment-phone"
                type="number"
                value={values.phone}
                {...register('phone', phoneValidationConfig)}
                onChange={onChange}
                label="phone"
              />
            </FormControl>
            {errors.phone && <ErrorMessage errors={errors.phone as Errors} />}
          </div>
          <div className="tw-w-full tw-flex tw-justify-center">
            <Button
              className={`tw-h-14 tw-w-80 ${
                hasErrors || !isDirty || isEmpty || isSubmitting
                  ? 'tw-text-neutral-gray-300 tw-border-neutral-gray-300 tw-cursor-not-allowed'
                  : ''
              }`}
              type="submit"
              variant="outlined"
              disabled={hasErrors || !isDirty || isEmpty || isSubmitting}
            >
              Ingresar
            </Button>
          </div>
        </form>
      </div>
      {message.length > 0 && (
        <SnackBar
          duration={messageDuration}
          message={message}
          type={isSuccess ? 'success' : isError ? 'error' : 'primary'}
        />
      )}
    </div>
  );
};

export default Register;
