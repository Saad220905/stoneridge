import Link from 'next/link';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { BankTabItem } from './BankTabItem';
import BankInfo from './BankInfo';
import TransactionsTable from './TransactionsTable';
import { Pagination } from './Pagination';
import { History, ArrowRight } from 'lucide-react';

const RecentTransactions = ({
  accounts,
  transactions = [],
  appwriteItemId,
  page = 1,
}: RecentTransactionsProps) => {
  const rowsPerPage = 10;
  const totalPages = Math.ceil(transactions.length / rowsPerPage);

  const indexOfLastTransaction = page * rowsPerPage;
  const indexOfFirstTransaction = indexOfLastTransaction - rowsPerPage;

  const currentTransactions = transactions.slice(
    indexOfFirstTransaction,
    indexOfLastTransaction,
  );

  return (
    <section className="flex flex-col gap-8 w-full">
      <header className="flex items-center justify-between border-b border-slate-200/60 pb-4">
        <div className="flex items-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-xl bg-slate-100 text-slate-500">
                <History className="size-5" />
            </div>
            <div className="flex flex-col">
                <h2 className="text-xl font-bold text-slate-900 leading-tight">Recent Activity</h2>
                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mt-0.5">Transaction Ledger</p>
            </div>
        </div>
        <Link
          href={`/transaction-history/?id=${appwriteItemId}`}
          className="group flex items-center gap-2 rounded-xl border border-slate-200 px-4 py-2 text-sm font-bold text-slate-600 transition-all hover:bg-slate-50 active:scale-[0.98]"
        >
          Detailed View
          <ArrowRight className="size-4 transition-transform group-hover:translate-x-0.5" />
        </Link>
      </header>

      <Tabs defaultValue={appwriteItemId} className="w-full">
        <TabsList className="flex w-full items-center gap-1 border-b border-slate-200 overflow-x-auto no-scrollbar">
          {accounts.map((account: Bank | Account) => (
            <TabsTrigger 
              key={account.id} 
              value={account.appwriteItemId}
              className="p-0 border-none bg-transparent data-[state=active]:bg-transparent"
            >
              <BankTabItem
                key={account.id}
                account={account}
                appwriteItemId={appwriteItemId}
              />
            </TabsTrigger>
          ))}
        </TabsList>

        {accounts.map((account: Bank | Account) => (
          <TabsContent
            value={account.appwriteItemId}
            key={account.id}
            className="flex flex-col gap-6 outline-none focus-visible:ring-0"
          >
            <div className="mt-4">
                <BankInfo
                  account={account}
                  appwriteItemId={appwriteItemId}
                  type="full"
                />
            </div>

            <TransactionsTable transactions={currentTransactions} />

            {totalPages > 1 && (
              <div className="my-4 w-full">
                <Pagination totalPages={totalPages} page={page} />
              </div>
            )}
          </TabsContent>
        ))}
      </Tabs>
    </section>
  );
};

export default RecentTransactions;
