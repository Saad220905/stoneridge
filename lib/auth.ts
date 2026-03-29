'use server';

import { cookies } from "next/headers";
import { redirect } from "next/navigation";

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8000';

export const signIn = async ({ email, password }: signInProps) => {
  try {
    const response = await fetch(`${BACKEND_URL}/api/auth/signin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
    }

    const data = await response.json();
    const token = data.token;

    (await cookies()).set("stoneridge-session", token, {
      path: "/",
      httpOnly: true,
      sameSite: "strict",
      secure: true,
    });

    return data;
  } catch (error) {
    console.error("SignIn Error", error);
    throw error;
  }
};

export const signUp = async (userData: SignUpParams) => {
  try {
    const response = await fetch(`${BACKEND_URL}/api/auth/signup`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Signup failed');
    }

    const data = await response.json();
    const token = data.token;

    (await cookies()).set("stoneridge-session", token, {
      path: "/",
      httpOnly: true,
      sameSite: "strict",
      secure: true,
    });

    return data;
  } catch (error) {
    console.error("SignUp Error", error);
    throw error;
  }
};

export async function getLoggedInUser() {
  try {
    const session = (await cookies()).get("stoneridge-session");
    if (!session || !session.value) return null;

    const response = await fetch(`${BACKEND_URL}/api/auth/user`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${session.value}`
        }
      });

    if (!response.ok) return null;
    return await response.json();
  } catch (error) {
    return null;
  }
}

export async function logoutAccount() {
  try {
    (await cookies()).delete("stoneridge-session");
    redirect("/sign-in");
  } catch (error) {
    return null;
  }
}

// Helper to get JWT for other API calls
export const getAuthToken = async () => {
    const session = (await cookies()).get("stoneridge-session");
    return session ? session.value : null;
}
