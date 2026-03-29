import HeaderBox from '@/components/HeaderBox';
import PaymentTransferForm from '@/components/PaymentTransferForm';
import { getBanks } from '@/lib/plaid';
import { getLoggedInUser } from '@/lib/auth';
import { redirect } from 'next/navigation';
import { SendHorizontal } from 'lucide-react';

const Transfer = async () => {
  const loggedIn = await getLoggedInUser();

  if (!loggedIn) redirect('/sign-in');

  const accounts = await getBanks();

  if (!accounts) return;

  return (
    <section className="flex h-screen w-full flex-col bg-slate-50/50 p-8 md:p-12 overflow-y-auto no-scrollbar">
      <div className="flex flex-col gap-10 w-full max-w-[1200px]">
        <header className="flex flex-col gap-6 border-b border-slate-200 pb-10">
            <div className="flex items-center gap-4 text-primary">
                <div className="flex size-12 items-center justify-center rounded-2xl bg-primary shadow-xl shadow-primary/20">
                    <SendHorizontal className="text-white size-6" />
                </div>
                <div className="flex flex-col">
                    <h2 className="text-sm font-black uppercase tracking-widest opacity-60">Global Reach</h2>
                    <h1 className="text-3xl font-black text-slate-900 tracking-tight">StoneRidge Transfers</h1>
                </div>
            </div>
            <HeaderBox
                title="Seamless Payments"
                subtext="Securely move funds across your connected accounts and recipients worldwide."
            />
        </header>

        <section className="bg-white rounded-3xl p-8 md:p-12 shadow-xl shadow-slate-200/50 border border-slate-100">
            <PaymentTransferForm accounts={accounts} />
        </section>
      </div>
    </section>
  );
};

export default Transfer;
