import HeaderBox from '@/components/HeaderBox';
import { Pagination } from '@/components/Pagination';
import TransactionsTable from '@/components/TransactionsTable';
import { getBanks } from '@/lib/plaid';
import { getLoggedInUser } from '@/lib/auth';
import { getTransactions } from '@/lib/transactions';
import { formatAmount } from '@/lib/utils';
import { redirect } from 'next/navigation';
import { History, Landmark, Wallet } from 'lucide-react';

const TransactionHistory = async ({
  searchParams,
}: SearchParamProps) => {
  const { id, page } = await searchParams;
  const currentPage = Number(page as string) || 1;
  const loggedIn = await getLoggedInUser();

  if (!loggedIn) redirect('/sign-in');

  const accounts = await getBanks();

  if (!accounts) return;

  const appwriteItemId = (id as string) || accounts[0]?.appwriteItemId;
  const account = accounts.find((acc: Bank) => acc.appwriteItemId === appwriteItemId);

  const rowsPerPage = 10;
  const transactions = await getTransactions(currentPage, rowsPerPage, account?.appwriteItemId);
  const totalPages = Math.ceil(transactions.length / rowsPerPage);

  return (
    <div className="flex h-screen w-full flex-col bg-slate-50/50 p-8 md:p-12 overflow-y-auto no-scrollbar">
      <div className="flex flex-col gap-10 w-full max-w-[1200px]">
        <header className="flex flex-col gap-6 border-b border-slate-200 pb-10">
            <div className="flex items-center gap-4 text-primary">
                <div className="flex size-12 items-center justify-center rounded-2xl bg-primary shadow-xl shadow-primary/20">
                    <History className="text-white size-6" />
                </div>
                <div className="flex flex-col">
                    <h2 className="text-sm font-black uppercase tracking-widest opacity-60">Financial Ledger</h2>
                    <h1 className="text-3xl font-black text-slate-900 tracking-tight">StoneRidge History</h1>
                </div>
            </div>
            <HeaderBox
                title="Transaction Archive"
                subtext="Explore a detailed record of your financial movements and account performance."
            />
        </header>

        <section className="flex flex-col gap-8">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-2 flex flex-col justify-between p-8 rounded-3xl bg-slate-900 text-white shadow-2xl shadow-slate-900/20 relative overflow-hidden group">
                    <div className="absolute top-[-20%] right-[-10%] size-64 bg-primary/20 rounded-full blur-3xl group-hover:bg-primary/30 transition-all duration-700" />
                    
                    <div className="relative z-10 flex flex-col gap-6">
                        <div className="flex items-center gap-3">
                            <div className="flex size-10 items-center justify-center rounded-xl bg-white/10 backdrop-blur-md">
                                <Landmark className="size-5 text-primary" />
                            </div>
                            <div className="flex flex-col">
                                <h3 className="text-xl font-black tracking-tight">{account?.name}</h3>
                                <p className="text-xs font-bold text-white/40 uppercase tracking-[0.2em]">Primary Institution</p>
                            </div>
                        </div>

                        <div className="flex flex-col gap-1">
                            <p className="text-xs font-bold text-white/40 uppercase tracking-widest">Available Liquidity</p>
                            <h2 className="text-4xl font-black tracking-tighter">
                                {formatAmount(account?.currentBalance || 0)}
                            </h2>
                        </div>
                    </div>

                    <div className="relative z-10 mt-8 flex items-center justify-between border-t border-white/10 pt-6">
                        <p className="text-sm font-black tracking-[4px] text-white/60">
                            ●●●● ●●●● ●●●● <span className="text-white">{account?.mask}</span>
                        </p>
                        <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md">
                            <div className="size-1.5 rounded-full bg-emerald-400 animate-pulse" />
                            <span className="text-[10px] font-black uppercase tracking-widest text-white/80">Active Vault</span>
                        </div>
                    </div>
                </div>

                <div className="flex flex-col items-center justify-center p-8 rounded-3xl bg-white border border-slate-100 shadow-xl shadow-slate-200/50 gap-4 text-center">
                    <div className="flex size-14 items-center justify-center rounded-2xl bg-emerald-50 text-emerald-600">
                        <Wallet className="size-7" />
                    </div>
                    <div className="flex flex-col">
                        <h4 className="text-sm font-black text-slate-400 uppercase tracking-widest">Account Health</h4>
                        <p className="text-2xl font-black text-slate-900 mt-1">Excellent</p>
                    </div>
                    <p className="text-xs font-medium text-slate-400 max-w-[160px]">Your account is currently in good standing with no pending flags.</p>
                </div>
            </div>

            <div className="flex flex-col gap-6">
                <div className="flex items-center gap-3 text-slate-400">
                   <History className="size-5" />
                   <h2 className="text-sm font-black uppercase tracking-widest">Complete Ledger</h2>
                </div>
                
                <TransactionsTable transactions={transactions} />
                
                {totalPages > 1 && (
                  <div className="mt-4 flex justify-center">
                    <Pagination totalPages={totalPages} page={currentPage} />
                  </div>
                )}
            </div>
        </section>
      </div>
    </div>
  );
};

export default TransactionHistory;
