import HeaderBox from '@/components/HeaderBox';
import SplitBillsForm from '@/components/SplitBillsForm';
import { getBanks } from '@/lib/plaid';
import { getLoggedInUser } from '@/lib/auth';
import { getPendingSplits, getCreatedSplits } from '@/lib/split-bills';
import { redirect } from 'next/navigation';
import { Users } from 'lucide-react';

const SplitBillsPage = async () => {
  const loggedIn = await getLoggedInUser();

  if (!loggedIn) redirect('/sign-in');

  const accounts = await getBanks();
  let pendingSplits: BillSplitParticipant[] = [];
  let createdSplits: BillSplit[] = [];

  try {
    if (accounts && accounts.length > 0) {
      pendingSplits = await getPendingSplits();
      createdSplits = await getCreatedSplits();
    }
  } catch (error) {
    console.error('Error loading split bills data:', error);
  }

  return (
    <section className="flex h-screen w-full flex-col bg-slate-50/50 p-8 md:p-12 overflow-y-auto no-scrollbar">
      <div className="flex flex-col gap-10 w-full max-w-[1200px]">
        <header className="flex flex-col gap-6 border-b border-slate-200 pb-10">
            <div className="flex items-center gap-4 text-primary">
                <div className="flex size-12 items-center justify-center rounded-2xl bg-primary shadow-xl shadow-primary/20">
                    <Users className="text-white size-6" />
                </div>
                <div className="flex flex-col">
                    <h2 className="text-sm font-black uppercase tracking-widest opacity-60">Social Payments</h2>
                    <h1 className="text-3xl font-black text-slate-900 tracking-tight">Split Bills</h1>
                </div>
            </div>
            <HeaderBox
                title="Settle Up Instantly"
                subtext="Request money from friends for shared dinners, travel, or utilities, and pay incoming requests securely."
            />
        </header>

        <section className="bg-white rounded-3xl p-8 md:p-12 shadow-xl shadow-slate-200/50 border border-slate-100">
            {accounts && accounts.length > 0 ? (
                <SplitBillsForm
                    accounts={accounts}
                    initialPending={pendingSplits}
                    initialCreated={createdSplits}
                    loggedInUserEmail={loggedIn.email}
                />
            ) : (
                <div className="flex flex-col items-center justify-center p-12 text-center bg-slate-50/50 rounded-3xl border border-dashed border-slate-200">
                    <Users className="size-12 text-slate-300 mb-4" />
                    <h3 className="text-lg font-bold text-slate-700">Link a Bank Account first</h3>
                    <p className="text-sm font-medium text-slate-400 mt-1">To split bills or receive money, you must connect at least one funding source via Plaid.</p>
                </div>
            )}
        </section>
      </div>
    </section>
  );
};

export default SplitBillsPage;
