import HeaderBox from '@/components/HeaderBox';
import { getLoggedInUser } from '@/lib/auth';
import { getGoals } from '@/lib/goals';
import { redirect } from 'next/navigation';
import GoalProgress from '@/components/GoalProgress';
import { Target, Plus } from 'lucide-react';

const Goals = async () => {
  const loggedIn = await getLoggedInUser();
  if (!loggedIn) redirect('/sign-in');

  const goals: Goal[] = await getGoals();

  return (
    <section className="flex flex-col gap-8 w-full p-8">
      <header className="flex items-center justify-between border-b border-slate-200/60 pb-6">
        <HeaderBox
          type="title"
          title="Savings Goals"
          subtext="Set and track your financial milestones."
        />
        <button className="flex items-center gap-2 rounded-xl bg-primary px-6 py-3 text-sm font-bold text-white shadow-lg shadow-primary/25 transition-all hover:bg-primary/90 active:scale-95">
          <Plus className="size-4" />
          Create New Goal
        </button>
      </header>

      <div className="grid gap-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="flex flex-col gap-2 p-6 rounded-2xl bg-slate-50 border border-slate-200">
                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Total Savings</p>
                <h3 className="text-3xl font-black text-slate-900">
                    ${goals.reduce((acc: number, g: Goal) => acc + g.currentAmount, 0).toLocaleString()}
                </h3>
            </div>
            <div className="flex flex-col gap-2 p-6 rounded-2xl bg-emerald-50/50 border border-emerald-100">
                <p className="text-xs font-bold text-emerald-600 uppercase tracking-widest">Target Remaining</p>
                <h3 className="text-3xl font-black text-emerald-700">
                    ${(goals.reduce((acc: number, g: Goal) => acc + g.targetAmount, 0) - goals.reduce((acc: number, g: Goal) => acc + g.currentAmount, 0)).toLocaleString()}
                </h3>
            </div>
            <div className="flex flex-col gap-2 p-6 rounded-2xl bg-primary/5 border border-primary/10">
                <p className="text-xs font-bold text-primary uppercase tracking-widest">Active Goals</p>
                <h3 className="text-3xl font-black text-primary">
                    {goals.filter((g: Goal) => g.status === 'active').length}
                </h3>
            </div>
        </div>

        <GoalProgress goals={goals} />
        
        {goals.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 bg-slate-50/50 rounded-3xl border-2 border-dashed border-slate-200">
            <div className="p-4 rounded-full bg-slate-100 mb-4">
                <Target className="size-10 text-slate-300" />
            </div>
            <h3 className="text-lg font-bold text-slate-900">No goals found</h3>
            <p className="text-slate-500 max-w-[300px] text-center mt-2">
                Start your journey towards financial freedom by creating your first savings goal.
            </p>
          </div>
        )}
      </div>
    </section>
  );
};

export default Goals;
