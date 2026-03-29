'use server';

// Frontend Plaid Client - interacts with backend bank endpoints
import { getAuthToken } from './auth';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8000';

export const createLinkToken = async () => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/bank/create-link-token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create link token.');
    }

    const { linkToken } = await response.json();
    return linkToken;
  } catch (error) {
    console.error('Frontend createLinkToken error:', error);
    throw error;
  }
};

export const exchangePublicToken = async ({ publicToken }: exchangePublicTokenProps) => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/bank/exchange-token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ publicToken }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to exchange public token.');
    }

    return await response.json();
  } catch (error) {
    throw error;
  }
};

export const getBanks = async (): Promise<Bank[]> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/bank/banks`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to fetch banks.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

export const getBank = async ({ documentId }: getBankProps): Promise<Bank> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/bank/${documentId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to fetch bank.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

export const getBankByAccountId = async ({ accountId }: getBankByAccountIdProps): Promise<Bank> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/bank/account/${accountId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to fetch bank by account ID.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}
