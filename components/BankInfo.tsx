'use client';

import { useSearchParams, useRouter } from 'next/navigation';
import {
  cn,
  formUrlQuery,
  formatAmount,
} from '@/lib/utils';
import { Landmark } from 'lucide-react';

const BankInfo = ({ account, appwriteItemId, type }: BankInfoProps) => {
  const router = useRouter();
  const searchParams = useSearchParams();

  const isActive = appwriteItemId === account?.appwriteItemId;

  const handleBankChange = () => {
    const newUrl = formUrlQuery({
      params: searchParams.toString(),
      key: 'id',
      value: account?.appwriteItemId,
    });
    router.push(newUrl, { scroll: false });
  };

  return (
    <div
      onClick={handleBankChange}
      className={cn(
        "flex items-center gap-4 p-4 rounded-2xl border transition-all duration-300 cursor-pointer",
        isActive 
          ? "bg-primary/5 border-primary/20 shadow-sm" 
          : "bg-white border-slate-100 hover:border-slate-200 hover:shadow-md"
      )}
    >
      <figure
        className={cn(
            "flex items-center justify-center size-12 rounded-xl",
            isActive ? "bg-primary text-white" : "bg-slate-100 text-slate-400"
        )}
      >
        <Landmark className="size-6" />
      </figure>
      <div className="flex flex-1 flex-col justify-center gap-0.5">
        <div className="flex items-center justify-between gap-2">
          <h2
            className={cn(
                "text-base font-bold truncate",
                isActive ? "text-primary" : "text-slate-700"
            )}
          >
            {account.name}
          </h2>
          {type === 'full' && (
            <span
              className={cn(
                  "text-[10px] uppercase tracking-widest font-black px-2 py-0.5 rounded-full",
                  isActive ? "bg-primary/10 text-primary" : "bg-slate-100 text-slate-500"
              )}
            >
              {(account as Account).subtype || 'Checking'}
            </span>
          )}
        </div>

        <p className={cn(
            "text-sm font-bold",
            isActive ? "text-primary/70" : "text-slate-400"
        )}>
          {formatAmount(account.currentBalance)}
        </p>
      </div>
    </div>
  );
};

export default BankInfo;
