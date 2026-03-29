import Link from 'next/link';
import BankCard from './BankCard';
import { countTransactionCategories } from '@/lib/utils';
import Category from './Category';
import { Plus, User as UserIcon } from 'lucide-react';

const RightSidebar = ({ user, transactions, banks }: RightSidebarProps) => {
  const categories: CategoryCount[] = countTransactionCategories(transactions);

  return (
    <aside className="right-sidebar bg-slate-50/30">
      <section className="flex flex-col border-b border-slate-200/60 pb-8">
        <div className="profile-banner relative overflow-hidden">
            <div className="absolute inset-0 bg-gradient-to-tr from-primary/20 to-transparent mix-blend-overlay" />
        </div>
        <div className="profile">
          <div className="profile-img ring-4 ring-white shadow-xl shadow-slate-200/50 flex items-center justify-center bg-slate-100">
             {user.firstName ? (
                 <span className="text-3xl font-black text-primary">
                    {user.firstName[0]}
                 </span>
             ) : (
                 <UserIcon className="size-8 text-slate-400" />
             )}
          </div>

          <div className="profile-details">
            <h1 className="text-xl font-bold text-slate-900 leading-tight">
              {user.firstName} {user.lastName}
            </h1>
            <p className="text-sm font-medium text-slate-500 mt-0.5">{user.email}</p>
          </div>
        </div>
      </section>

      <section className="px-6 py-10 flex flex-col gap-10">
        <div className="flex flex-col gap-6">
            <div className="flex w-full items-center justify-between">
              <h2 className="text-sm font-bold text-slate-400 uppercase tracking-widest">My Portfolio</h2>
              <Link href="/" className="group flex items-center gap-1.5 transition-colors hover:text-primary">
                <div className="flex size-6 items-center justify-center rounded-full bg-slate-100 group-hover:bg-primary/10">
                    <Plus className="size-3.5 text-slate-600 group-hover:text-primary" />
                </div>
                <span className="text-xs font-bold text-slate-600 group-hover:text-primary">Connect Bank</span>
              </Link>
            </div>

            {banks?.length > 0 ? (
              <div className="relative flex w-full flex-col items-center justify-center pt-4">
                <div className="relative z-10 w-full transform transition-transform hover:translate-y-[-4px]">
                  <BankCard
                    key={banks[0].appwriteItemId}
                    account={banks[0]}
                    userName={`${user.firstName} ${user.lastName}`}
                    showBalance={false}
                  />
                </div>
                {banks[1] && (
                  <div className="absolute right-[-10px] top-12 z-0 w-full scale-[0.9] opacity-40 blur-[1px] transform transition-transform hover:translate-x-2">
                    <BankCard
                      key={banks[1].appwriteItemId}
                      account={banks[1]}
                      userName={`${user.firstName} ${user.lastName}`}
                      showBalance={false}
                    />
                  </div>
                )}
              </div>
            ) : (
                <div className="flex flex-col items-center justify-center py-10 border-2 border-dashed border-slate-200 rounded-2xl bg-slate-50/50">
                    <Plus className="size-8 text-slate-300 mb-2" />
                    <p className="text-xs font-bold text-slate-400">No Banks Connected</p>
                </div>
            )}
        </div>

        <div className="flex flex-col gap-6">
          <h2 className="text-sm font-bold text-slate-400 uppercase tracking-widest">Spending Insight</h2>

          <div className="space-y-6">
            {categories.length > 0 ? categories.map((category) => (
              <Category key={category.name} category={category} />
            )) : (
                <p className="text-xs font-bold text-slate-400 text-center py-4">No spending data yet</p>
            )}
          </div>
        </div>
      </section>
    </aside>
  );
};

export default RightSidebar;
