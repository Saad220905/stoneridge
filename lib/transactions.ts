'use server';

// Frontend Transactions Client - interacts with backend transaction endpoints
import { getAuthToken } from './auth';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8000';

export const getTransactions = async (page: number = 1, pageSize: number = 10, bankId?: string): Promise<Transaction[]> => {
    try {
        const token = await getAuthToken();
        const queryParams = new URLSearchParams({
            page: page.toString(),
            pageSize: pageSize.toString(),
        });
        if (bankId) {
            queryParams.append('bankId', bankId);
        }

        const response = await fetch(`${BACKEND_URL}/api/transactions/transactions?${queryParams.toString()}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to fetch transactions.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
};

export const createTransaction = async (transactionData: CreateTransactionProps): Promise<Transaction> => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/transactions/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(transactionData)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create transaction.');
    }

    return await response.json();
  } catch (error) {
    throw error;
  }
};
