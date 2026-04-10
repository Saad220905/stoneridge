'use server';

import { getAuthToken } from './auth';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8000';

export const getGoals = async () => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/goals`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) throw new Error('Failed to fetch goals');
    return await response.json();
  } catch (error) {
    console.error(error);
    return [];
  }
};

export const createGoal = async (goalData: any) => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/goals`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(goalData)
    });

    if (!response.ok) throw new Error('Failed to create goal');
    return await response.json();
  } catch (error) {
    console.error(error);
  }
};

export const updateGoalProgress = async (goalId: number, amount: number) => {
  try {
    const token = await getAuthToken();
    const response = await fetch(`${BACKEND_URL}/api/goals/${goalId}/progress?amount=${amount}`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) throw new Error('Failed to update progress');
    return await response.json();
  } catch (error) {
    console.error(error);
  }
};
