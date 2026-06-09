'use server';

import { getAuthToken } from './auth';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8000';

export const createBillSplit = async (data: {
    billName: string;
    totalAmount: number;
    creatorBankId: string;
    participants: { email: string; amount: number }[];
}): Promise<BillSplit> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/billsplits/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to create bill split.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
};

export const getPendingSplits = async (): Promise<BillSplitParticipant[]> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/billsplits/pending`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to fetch pending splits.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
};

export const getCreatedSplits = async (): Promise<BillSplit[]> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/billsplits/created`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to fetch created splits.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
};

export const payBillSplit = async (participantId: number, sourceBankId: string): Promise<BillSplitParticipant> => {
    try {
        const token = await getAuthToken();
        const response = await fetch(`${BACKEND_URL}/api/billsplits/pay/${participantId}?sourceBankId=${sourceBankId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to settle split share.');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
};
