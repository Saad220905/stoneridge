'use client';

import { useSearchParams, useRouter } from 'next/navigation';
import { cn, formUrlQuery } from '@/lib/utils';
import { Landmark } from 'lucide-react';

export const BankTabItem = ({ account, appwriteItemId }: BankTabItemProps) => {
  const searchParams = useSearchParams();
  const router = useRouter();
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
        "flex items-center gap-2 px-4 py-2 cursor-pointer transition-all border-b-2",
        isActive 
          ? "border-primary bg-primary/5" 
          : "border-transparent hover:bg-slate-50"
      )}
    >
      <Landmark className={cn("size-4", isActive ? "text-primary" : "text-slate-400")} />
      <p
        className={cn("text-sm font-bold truncate transition-colors", {
          "text-primary": isActive,
          "text-slate-500": !isActive,
        })}
      >
        {account.name}
      </p>
    </div>
  );
};
