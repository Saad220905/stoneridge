'use server';

// Frontend Dwolla Client - interacts with backend dwolla endpoints
import { getAuthToken } from './auth';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8000';

export const createTransfer = async ({
  sourceFundingSourceUrl,
  destinationFundingSourceUrl,
  amount,
}: TransferParams) => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/dwolla/create-transfer`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        sourceFundingSourceUrl,
        destinationFundingSourceUrl,
        amount,
      })
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to create transfer.');
    }

    return await response.json();
  } catch (error) {
    throw error;
  }
};
