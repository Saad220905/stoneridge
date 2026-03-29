import BankCard from '@/components/BankCard';
import HeaderBox from '@/components/HeaderBox';
import { getBanks } from '@/lib/plaid';
import { getLoggedInUser } from '@/lib/auth';
import { redirect } from 'next/navigation';
import { CreditCard, Plus } from 'lucide-react';
import Link from 'next/link';

const MyBanks = async () => {
  const loggedIn = await getLoggedInUser();

  if (!loggedIn) redirect('/sign-in');

  const accounts = await getBanks();

  return (
    <section className="flex h-screen w-full flex-col bg-slate-50/50 p-8 md:p-12 overflow-y-auto no-scrollbar">
      <div className="flex flex-col gap-8 w-full max-w-[1200px]">
        <div className="flex items-center justify-between border-b border-slate-200 pb-8">
            <HeaderBox
              title="Digital Wallet"
              subtext="Manage and monitor your connected financial institutions."
            />
            <Link 
                href="/" 
                className="flex items-center gap-2 rounded-xl bg-primary px-6 py-3 text-sm font-black text-white shadow-xl shadow-primary/20 transition-all hover:bg-primary/90 active:scale-95"
            >
                <Plus className="size-4" />
                Add New Card
            </Link>
        </div>

        <div className="flex flex-col gap-6">
          <div className="flex items-center gap-3 text-slate-400">
             <CreditCard className="size-5" />
             <h2 className="text-sm font-black uppercase tracking-widest">Active Accounts</h2>
          </div>
          
          <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {accounts && accounts.length > 0 ? (
              accounts.map((a: Bank) => (
                <div key={a.id} className="transform transition-all hover:translate-y-[-4px]">
                    <BankCard
                      account={a}
                      userName={loggedIn?.firstName}
                    />
                </div>
              ))
            ) : (
                <div className="col-span-full flex flex-col items-center justify-center py-20 bg-white border-2 border-dashed border-slate-200 rounded-3xl">
                    <CreditCard className="size-12 text-slate-200 mb-4" />
                    <p className="text-lg font-bold text-slate-400">No bank accounts linked yet</p>
                    <p className="text-sm font-medium text-slate-300 mt-1">Connect your first account to get started</p>
                </div>
            )}
          </div>
        </div>
      </div>
    </section>
  );
};

export default MyBanks;
