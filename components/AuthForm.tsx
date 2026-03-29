'use client';

import Image from 'next/image';
import Link from 'next/link';
import React, { useState } from 'react';

import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Button } from '@/components/ui/button';
import { Form } from '@/components/ui/form';
import CustomInput from './CustomInput';
import { authFormSchema } from '@/lib/utils';
import { Loader2, Landmark, ArrowRight } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { signIn, signUp } from '@/lib/auth';
import PlaidLink from './PlaidLink';

const AuthForm = ({ type }: { type: string }) => {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const formSchema = authFormSchema(type);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (data: z.infer<typeof formSchema>) => {
    setIsLoading(true);
    try {
      if (type === 'sign-up') {
        const userData = {
          firstName: data.firstName!,
          lastName: data.lastName!,
          address1: data.address1!,
          city: data.city!,
          state: data.state!,
          postalCode: data.postalCode!,
          dateOfBirth: data.dateOfBirth!,
          ssn: data.ssn!,
          email: data.email,
          password: data.password,
        };
        const newUser = await signUp(userData);
        setUser(newUser);
      }

      if (type === 'sign-in') {
        const response = await signIn({
          email: data.email,
          password: data.password,
        });
        if (response) router.push('/');
      }
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <section className="flex flex-col gap-6">
      <header className="flex flex-col gap-3 text-center">
        <div className="flex justify-center mb-2">
            <div className="flex size-12 items-center justify-center rounded-2xl bg-primary shadow-xl shadow-primary/20">
                <Landmark className="text-white size-7" />
            </div>
        </div>
        <div className="flex flex-col gap-1">
          <h1 className="text-3xl font-bold tracking-tight text-slate-900">
            {user ? 'Link Account' : type === 'sign-in' ? 'Welcome Back' : 'Create Account'}
          </h1>
          <p className="text-slate-500 font-medium">
            {user
              ? 'Connect your bank to begin'
              : type === 'sign-in' ? 'Enter your credentials to continue' : 'Join StoneRidge today'}
          </p>
        </div>
      </header>

      {user ? (
        <div className="flex flex-col gap-4 py-4">
          <PlaidLink user={user} variant="primary" />
        </div>
      ) : (
        <>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
              {type === 'sign-up' && (
                <div className="grid grid-cols-2 gap-4">
                    <CustomInput
                      control={form.control}
                      name="firstName"
                      label="First Name"
                      placeholder="Jane"
                    />
                    <CustomInput
                      control={form.control}
                      name="lastName"
                      label="Last Name"
                      placeholder="Doe"
                    />
                    <div className="col-span-2">
                        <CustomInput
                          control={form.control}
                          name="address1"
                          label="Address"
                          placeholder="123 Main St"
                        />
                    </div>
                    <CustomInput
                      control={form.control}
                      name="city"
                      label="City"
                      placeholder="New York"
                    />
                    <CustomInput
                      control={form.control}
                      name="state"
                      label="State"
                      placeholder="NY"
                    />
                    <CustomInput
                      control={form.control}
                      name="postalCode"
                      label="Postal Code"
                      placeholder="10001"
                    />
                    <CustomInput
                      control={form.control}
                      name="dateOfBirth"
                      label="DOB"
                      placeholder="YYYY-MM-DD"
                    />
                    <div className="col-span-2">
                        <CustomInput
                          control={form.control}
                          name="ssn"
                          label="SSN"
                          placeholder="Last 4 digits"
                        />
                    </div>
                </div>
              )}

              <CustomInput
                control={form.control}
                name="email"
                label="Email"
                placeholder="jane@stoneridge.com"
              />

              <CustomInput
                control={form.control}
                name="password"
                label="Password"
                placeholder="••••••••"
              />

              <Button type="submit" disabled={isLoading} className="form-btn mt-4">
                {isLoading ? (
                  <Loader2 size={20} className="animate-spin" />
                ) : (
                  <div className="flex items-center gap-2">
                    {type === 'sign-in' ? 'Sign In' : 'Sign Up'}
                    <ArrowRight size={18} />
                  </div>
                )}
              </Button>
            </form>
          </Form>

          <footer className="flex justify-center gap-1.5 pt-2 text-sm">
            <p className="text-slate-500">
              {type === 'sign-in'
                ? "Don't have an account?"
                : 'Already have an account?'}
            </p>
            <Link
              href={type === 'sign-in' ? '/sign-up' : '/sign-in'}
              className="font-bold text-primary hover:underline"
            >
              {type === 'sign-in' ? 'Get started' : 'Sign in'}
            </Link>
          </footer>
        </>
      )}
    </section>
  );
};

export default AuthForm;
