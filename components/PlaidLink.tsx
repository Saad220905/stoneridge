import React, { useCallback, useEffect, useState } from 'react';
import { Button } from './ui/button';
import {
  PlaidLinkOnSuccess,
  PlaidLinkOptions,
  usePlaidLink,
} from 'react-plaid-link';
import { useRouter } from 'next/navigation';
import {
  createLinkToken,
  exchangePublicToken,
} from '@/lib/plaid';
import { Link2, Plus } from 'lucide-react';
import { cn } from '@/lib/utils';

const PlaidLink = ({ user, variant }: PlaidLinkProps) => {
  const router = useRouter();

  const [token, setToken] = useState('');

  useEffect(() => {
    const getLinkToken = async () => {
      const linkToken = await createLinkToken();
      setToken(linkToken);
    };

    getLinkToken();
  }, []);

  const onSuccess = useCallback<PlaidLinkOnSuccess>(
    async (public_token: string) => {
      await exchangePublicToken({
        publicToken: public_token,
      });

      router.push('/');
    },
    [router],
  );

  const config: PlaidLinkOptions = {
    token,
    onSuccess,
  };

  const { open, ready } = usePlaidLink(config);

  return (
    <>
      {variant === 'primary' ? (
        <Button
          onClick={() => open()}
          disabled={!ready}
          className="flex h-11 w-full items-center justify-center gap-2 rounded-xl bg-primary text-sm font-bold text-white shadow-lg shadow-primary/20 transition-all hover:bg-primary/90 active:scale-[0.98] disabled:opacity-50"
        >
          <Plus className="size-4" />
          Connect Bank
        </Button>
      ) : variant === 'ghost' ? (
        <Button
          onClick={() => open()}
          variant="ghost"
          className="flex items-center gap-3 rounded-xl px-4 py-2 font-bold text-slate-600 transition-all hover:bg-slate-50 hover:text-primary active:scale-[0.98]"
        >
          <Link2 className="size-5" />
          <p className="hidden xl:block">Connect Bank</p>
        </Button>
      ) : (
        <Button 
          onClick={() => open()} 
          className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-4 py-2 font-bold text-slate-600 shadow-sm transition-all hover:bg-slate-50 hover:text-primary hover:border-primary/20 active:scale-[0.98]"
        >
          <Link2 className="size-5" />
          <p className="text-sm font-bold">Connect Bank</p>
        </Button>
      )}
    </>
  );
};

export default PlaidLink;
