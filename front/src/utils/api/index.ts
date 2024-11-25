import { UserAccount, User, Transaction, Card } from '../../types';

const baseUrl = 'http://localhost:9091/api';

const myInit = (method: string, token?: string) => ({
  method,
  headers: {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }), // Añade el token solo si está presente
  },
  mode: "cors" as RequestMode,
  cache: "default" as RequestCache,
});

const myRequest = (endpoint: string, method: string, token?: string) =>
  new Request(endpoint, myInit(method, token));

const rejectPromise = (response?: Response): Promise<Response> =>
  Promise.reject({
    status: (response && response.status) || '00',
    statusText: (response && response.statusText) || 'Ocurrió un error',
    err: true,
  });

  const handleResponse = async (response: Response) => {
    if (response.ok) {
      return response.json();
    }
    const error = await response.json().catch(() => ({})); // Intenta obtener el cuerpo de error
    throw { status: response.status, ...error };
  };

  export const login = async (email: string, password: string) => {
    try {
      const response = await fetch(`${baseUrl}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Login failed");
      }
  
      const data = await response.json();
      console.log("Received token:", data.token); 
      return data;
    } catch (error) {
      console.error("Error during login:", error);
      throw error;
    }
  };

export const createAnUser = async (user: User) => {
  try {
    const response = await fetch(
      myRequest(`${baseUrl}/users/register`, "POST"),
      {
        body: JSON.stringify(user),
      }
    );
    return await handleResponse(response);
  } catch (error) {
    console.error("Error during user creation:", error);
    throw error;
  }
};

export const getUser = (user_id: number): Promise<User> => {
  return fetch(myRequest(`${baseUrl}/users/${user_id}`, 'GET'))
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const getUserByKeycloakId = (keycloakId: string): Promise<User | null> => {
  const token = localStorage.getItem("token");
  return fetch(`${baseUrl}/users/keycloak/${keycloakId}`, {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  })
    .then(async (response) => {
      if (!response.ok) {
        return response.json().then((data) => {
          throw new Error(data.message || "Unauthorized");
        });
      }
      console.log("HTTP status:", response.status);
      console.log("Raw response:", await response.text());
      const text = await response.text();
      return text ? JSON.parse(text) : null;
    })
    .catch((err) => {
      console.error("Error fetching user info:", err);
      throw err;
    });
};



export const updateUser = (
  id: string,
  data: any,
  token: string
): Promise<Response> => {
  return fetch(myRequest(`${baseUrl}/users/${id}`, 'PATCH', token), {
    body: JSON.stringify(data),
  })
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

// TODO: remove this functionality once backend is ready
const generateCvu = (): string => {
  let cvu = '';
  for (let i = 0; i < 22; i++) {
    cvu += Math.floor(Math.random() * 10);
  }
  return cvu;
};

// TODO: remove this functionality once backend is ready
const generateAlias = (): string => {
  const words = [
    'Cuenta',
    'Personal',
    'Banco',
    'Argentina',
    'Digital',
    'Money',
    'House',
    'Bank',
    'Account',
    'Cartera',
    'Wallet',
    'Pago',
    'Pay',
    'Rapido',
    'Seguro',
  ];
  const length = 3;
  let alias = '';
  for (let i = 0; i < length; i++) {
    alias += words[Math.floor(Math.random() * words.length)];
    if (i < length - 1) {
      alias += '.';
    }
  }
  return alias;
};

// TODO: remove this functionality once backend is ready
export const createAnAccount = (data: any): Promise<Response> => {
  const { user, accessToken } = data;

  const alias = generateAlias();
  const cvu = generateCvu();
  const account = {
    alias,
    cvu,
    balance: 0,
    name: `${user.fir} ${user.lastname}`,
  };

  return fetch(
    myRequest(`${baseUrl}/users/${user.id}/accounts`, 'POST', accessToken),
    {
      body: JSON.stringify(account),
    }
  ).then((response) =>
    response.ok ? response.json() : rejectPromise(response)
  );
};

export const getAccount = (user_id: number, token: string): Promise<UserAccount> => {
  return fetch(myRequest(`${baseUrl}/users/${user_id}/accounts`, 'GET', token), {})
    .then((response) => {
      if (response.ok) {
        return response.json().then((account) => account[0]);
      }
      return rejectPromise(response);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const getAccounts = (): Promise<UserAccount[]> => {
  return fetch(myRequest(`${baseUrl}/accounts`, 'GET'))
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const updateAccount = (
  user_id: number,
  data: any,
  token: string
): Promise<Response> => {
  return fetch(myRequest(`${baseUrl}/users/${user_id}/accounts/1`, 'PATCH', token), {
    body: JSON.stringify(data),
  })
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const getUserActivities = (
  user_id: number,
  token: string,
  limit?: number
): Promise<Transaction[]> => {
  return fetch(
    myRequest(
      `${baseUrl}/users/${user_id}/activities${limit ? `?_limit=${limit}` : ''}`,
      'GET',
      token
    )
  )
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return rejectPromise(response);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const getUserActivity = (
  user_id: number,
  activityId: string,
  token: string
): Promise<Transaction> => {
  return fetch(
    myRequest(
      `${baseUrl}/users/${user_id}/activities/${activityId}`,
      'GET',
      token
    )
  )
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return rejectPromise(response);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const getUserCards = (
  user_id: number,
  token: string
): Promise<Card[]> => {
  return fetch(myRequest(`${baseUrl}/users/${user_id}/cards`, 'GET', token))
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return rejectPromise(response);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const getUserCard = (user_id: number, cardId: string): Promise<Card> => {
  return fetch(myRequest(`${baseUrl}/users/${user_id}/cards/${cardId}`, 'GET'))
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return rejectPromise(response);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const deleteUserCard = (
  user_id: number,
  cardId: string,
  token: string
): Promise<Response> => {
  return fetch(
    myRequest(`${baseUrl}/users/${user_id}/cards/${cardId}`, 'DELETE', token)
  )
    .then((response) => {
      if (response.ok) {
        return response.json();
      }
      return rejectPromise(response);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

export const createUserCard = (
  user_id: number,
  card: any,
  token: string
): Promise<Response> => {
  return fetch(myRequest(`${baseUrl}/users/${user_id}/cards`, 'POST', token), {
    body: JSON.stringify(card),
  })
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

// TODO: edit when backend is ready
export const createDepositActivity = (
  user_id: number,
  amount: number,
  token: string
) => {
  const maxAmount = 30000;
  if (amount > maxAmount) return rejectPromise();

  const activity = {
    amount,
    type: 'Deposit',
    description: 'Depósito con tarjeta',
    dated: new Date(), // date must be genarated in backend
  };

  return fetch(
    myRequest(`${baseUrl}/users/${user_id}/activities`, 'POST', token),
    {
      body: JSON.stringify(activity),
    }
  )
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .then((data) => {
      depositMoney(data.amount, user_id, token);
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

// TODO: remove when backend is ready
const depositMoney = (amount: number, user_id: number, token: string) => {
  return getAccount(user_id, token)
    .then((account) => {
      const newBalance = account.balance + amount;
      const accountId = account.id;
      return {
        newBalance,
        accountId,
      };
    })
    .then(({ newBalance, accountId }) => {
      fetch(
        myRequest(
          `${baseUrl}/users/${user_id}/accounts/${accountId}`,
          'PATCH',
          token
        ),
        {
          body: JSON.stringify({ balance: newBalance }),
        }
      )
        .then((response) =>
          response.ok ? response.json() : rejectPromise(response)
        )
        .catch((err) => {
          console.log(err);
          return rejectPromise(err);
        });
    });
};

// TODO: edit when backend is ready
export const createTransferActivity = (
  user_id: number,
  token: string,
  origin: string,
  destination: string,
  amount: number,
  name?: string
) => {
  return fetch(
    myRequest(`${baseUrl}/users/${user_id}/activities`, 'POST', token),
    {
      body: JSON.stringify({
        type: 'Transfer',
        amount: amount * -1,
        origin,
        destination,
        name,
        dated: new Date(), // date must be genarated in backend
      }),
    }
  )
    .then((response) =>
      response.ok ? response.json() : rejectPromise(response)
    )
    .then((response) => {
      discountMoney(response.amount, user_id, token);
      return response;
    })
    .catch((err) => {
      console.log(err);
      return rejectPromise(err);
    });
};

// TODO: remove when backend is ready
const discountMoney = (amount: number, user_id: number, token: string) => {
  return getAccount(user_id, token)
    .then((account) => {
      // amount is negavite
      const newBalance = account.balance + amount;
      const accountId = account.id;
      return {
        newBalance,
        accountId,
      };
    })
    .then(({ newBalance, accountId }) => {
      fetch(
        myRequest(
          `${baseUrl}/users/${user_id}/accounts/${accountId}`,
          'PATCH',
          token
        ),
        {
          body: JSON.stringify({ balance: newBalance }),
        }
      )
        .then((response) =>
          response.ok ? response.json() : rejectPromise(response)
        )
        .catch((err) => {
          console.log(err);
          return rejectPromise(err);
        });
    });
};
