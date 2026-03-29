import { type ClassValue, clsx } from 'clsx';
import qs from 'query-string';
import { twMerge } from 'tailwind-merge';
import { z } from 'zod';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

// FORMAT DATE TIME
export const formatDateTime = (dateString: Date) => {
  const dateTimeOptions: Intl.DateTimeFormatOptions = {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
  };

  const dateDayOptions: Intl.DateTimeFormatOptions = {
    weekday: 'short',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  };

  const dateOptions: Intl.DateTimeFormatOptions = {
    month: 'short',
    year: 'numeric',
    day: 'numeric',
  };

  const timeOptions: Intl.DateTimeFormatOptions = {
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
  };

  const formattedDateTime: string = new Date(dateString).toLocaleString(
    'en-US',
    dateTimeOptions,
  );

  const formattedDateDay: string = new Date(dateString).toLocaleString(
    'en-US',
    dateDayOptions,
  );

  const formattedDate: string = new Date(dateString).toLocaleString(
    'en-US',
    dateOptions,
  );

  const formattedTime: string = new Date(dateString).toLocaleString(
    'en-US',
    timeOptions,
  );

  return {
    dateTime: formattedDateTime,
    dateDay: formattedDateDay,
    dateOnly: formattedDate,
    timeOnly: formattedTime,
  };
};

export function formatAmount(amount: number): string {
  const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
  });

  return formatter.format(amount);
}

export const parseStringify = (value: unknown) => JSON.parse(JSON.stringify(value));

export const encryptId = (id: string) => btoa(id);
export const decryptId = (id: string) => atob(id);

export const removeSpecialCharacters = (value: string) => {
  return value.replace(/[^\w\s]/gi, '');
};

interface UrlQueryParams {
  params: string;
  key: string;
  value: string;
}

export function formUrlQuery({ params, key, value }: UrlQueryParams) {
  const currentUrl = qs.parse(params);

  currentUrl[key] = value;

  return qs.stringifyUrl(
    {
      url: window.location.pathname,
      query: currentUrl,
    },
    { skipNull: true },
  );
}

export function countTransactionCategories(
  transactions: Transaction[],
): CategoryCount[] {
  const categoryCounts: { [category: string]: number } = {};
  let totalCount = 0;

  if (transactions) {
    transactions.forEach((transaction) => {
      const category = transaction.category;

      if (Object.prototype.hasOwnProperty.call(categoryCounts, category)) {
        categoryCounts[category]++;
      } else {
        categoryCounts[category] = 1;
      }
      totalCount++;
    });
  }

  const aggregatedCategories: CategoryCount[] = Object.keys(categoryCounts).map(
    (category) => ({
      name: category,
      count: categoryCounts[category],
      totalCount,
    }),
  );

  aggregatedCategories.sort((a, b) => b.count - a.count);

  return aggregatedCategories;
}

export const getTransactionStatus = (date: Date) => {
  const today = new Date();
  const twoDaysAgo = new Date(today);
  twoDaysAgo.setDate(today.getDate() - 2);

  return date > twoDaysAgo ? 'Processing' : 'Success';
};

export const authFormSchema = (type: string) =>
  z.object({
    // sign up
    firstName: type === 'sign-in' ? z.string().optional() : z.string().min(3),
    lastName: type === 'sign-in' ? z.string().optional() : z.string().min(3),
    address1: type === 'sign-in' ? z.string().optional() : z.string().max(50),
    city: type === 'sign-in' ? z.string().optional() : z.string().max(50),
    state:
      type === 'sign-in' ? z.string().optional() : z.string().min(2).max(2),
    postalCode:
      type === 'sign-in' ? z.string().optional() : z.string().min(3).max(6),
    dateOfBirth: type === 'sign-in' ? z.string().optional() : z.string().min(3),
    ssn: type === 'sign-in' ? z.string().optional() : z.string().min(3),
    // both
    email: z.string().email(),
    password: z.string().min(8),
  });
