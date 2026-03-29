import { cn, formatAmount } from '@/lib/utils';
import Image from 'next/image';
import Link from 'next/link';
import React from 'react';
import Copy from './Copy';
import { Landmark, CreditCard, Wifi } from 'lucide-react';

const BankCard = ({
  account,
  userName,
  showBalance = true,
}: CreditCardProps) => {
  return (
    <div className="flex flex-col">
      <Link
        href={`/transaction-history/?id=${account.appwriteItemId}`}
        className="bank-card group"
      >
        <div className="bank-card_content">
          <div>
            <div className="flex justify-between items-start">
                <div className="flex flex-col gap-1">
                    <h1 className="text-16 font-bold text-white tracking-wide">
                        {account.name || userName}
                    </h1>
                    <p className="font-ibm-plex-serif font-black text-white/70 text-sm">
                        {formatAmount(account.currentBalance)}
                    </p>
                </div>
                <Landmark className="text-white/40 size-6 group-hover:text-white/60 transition-colors" />
            </div>
          </div>

          <div className="flex flex-col gap-2">
            <div className="flex justify-between items-end">
              <h2 className="text-12 font-bold text-white tracking-[2px]">
                ●●●● ●●●● ●●●● <span className="text-16">{account?.mask || '1234'}</span>
              </h2>
              <div className="flex flex-col items-end">
                  <div className="flex gap-2 items-center">
                     <Wifi className="text-white/40 size-4 rotate-90" />
                     <div className="flex -space-x-3">
                        <div className="size-6 rounded-full bg-white/20 backdrop-blur-sm border border-white/10" />
                        <div className="size-6 rounded-full bg-white/10 backdrop-blur-sm border border-white/10" />
                     </div>
                  </div>
              </div>
            </div>
          </div>
        </div>

        <div className="absolute right-0 top-0 h-full w-24 bg-gradient-to-l from-white/5 to-transparent pointer-events-none" />
      </Link>

      {showBalance && <Copy title={account?.shareableId} />}
    </div>
  );
};

export default BankCard;
