import AnimatedCounter from './AnimatedCounter';
import DoughnutChart from './DoughnutChart';

const TotalBalanceBox = ({
  accounts = [],
  totalBanks,
  totalCurrentBalance,
}: TotalBalanceBoxProps) => {
  return (
    <section className="total-balance group">
      <div className="flex flex-col gap-2">
        <p className="text-sm font-medium text-slate-500 uppercase tracking-wider">
          Total Combined Balance
        </p>
        <div className="flex items-baseline gap-2">
           <div className="total-balance-amount">
              <AnimatedCounter amount={totalCurrentBalance} />
           </div>
        </div>
        <div className="mt-4 flex items-center gap-2">
            <span className="flex h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
            <p className="text-xs font-semibold text-slate-400">
               {totalBanks} {totalBanks === 1 ? 'Bank Account' : 'Bank Accounts'} Connected
            </p>
        </div>
      </div>

      <div className="total-balance-chart ml-auto relative">
        <DoughnutChart accounts={accounts} />
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
            <span className="text-[10px] font-bold text-slate-300 uppercase">Assets</span>
        </div>
      </div>
    </section>
  );
};

export default TotalBalanceBox;
