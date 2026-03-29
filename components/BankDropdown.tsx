'use client';

import { useSearchParams, useRouter } from 'next/navigation';
import { useState } from 'react';
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
} from '@/components/ui/select';
import { formUrlQuery, formatAmount, cn } from '@/lib/utils';
import { CreditCard, Landmark } from 'lucide-react';

export const BankDropdown = ({
  accounts = [],
  setValue,
  otherStyles,
}: BankDropdownProps) => {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [selected, setSelected] = useState(accounts[0]);

  const handleBankChange = (id: string) => {
    const account = accounts.find((account) => account.appwriteItemId === id)!;

    setSelected(account);
    const newUrl = formUrlQuery({
      params: searchParams.toString(),
      key: 'id',
      value: id,
    });
    router.push(newUrl, { scroll: false });

    if (setValue) {
      setValue('senderBank', id);
    }
  };

  return (
    <Select
      defaultValue={selected?.appwriteItemId}
      onValueChange={(value) => handleBankChange(value)}
    >
      <SelectTrigger
        className={cn(
            "flex w-full items-center gap-3 rounded-xl border-slate-200 bg-white px-4 h-12 transition-all focus:ring-4 focus:ring-primary/5 shadow-sm",
            otherStyles
        )}
      >
        <Landmark className="size-5 text-slate-400" />
        <p className="line-clamp-1 w-full text-left text-sm font-bold text-slate-700">
            {selected?.name}
        </p>
      </SelectTrigger>
      <SelectContent
        className="w-full bg-white border-slate-200 rounded-xl shadow-xl p-1"
        align="end"
      >
        <SelectGroup>
          <SelectLabel className="px-3 py-2 text-[10px] font-black uppercase tracking-widest text-slate-400">
            Select Linked Account
          </SelectLabel>
          {accounts.map((account: Bank | Account) => (
            <SelectItem
              key={account.appwriteItemId}
              value={account.appwriteItemId}
              className="cursor-pointer rounded-lg px-3 py-2.5 focus:bg-slate-50 transition-colors"
            >
              <div className="flex flex-col gap-0.5">
                <p className="text-sm font-bold text-slate-700">{account.name}</p>
                <p className="text-xs font-bold text-primary opacity-80">
                  {formatAmount(account.currentBalance)}
                </p>
              </div>
            </SelectItem>
          ))}
        </SelectGroup>
      </SelectContent>
    </Select>
  );
};
