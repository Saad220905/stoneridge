'use client';

import React from 'react';
import { Progress } from './ui/progress';
import { cn, formatAmount } from '@/lib/utils';
import { Target, TrendingUp, Calendar } from 'lucide-react';

const GoalProgress = ({ goals = [] }: GoalProgressProps) => {
  if (goals.length === 0) return null;

  return (
    <div className="flex flex-col gap-6 p-6 rounded-2xl border border-slate-200 bg-white shadow-sm">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-lg bg-primary/10 text-primary">
            <Target className="size-5" />
          </div>
          <h2 className="text-lg font-bold text-slate-900">Savings Goals</h2>
        </div>
        <span className="text-xs font-bold text-slate-400 uppercase tracking-widest">
            {goals.filter(g => g.status === 'active').length} Active
        </span>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {goals.map((goal) => {
          const percentage = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
          
          return (
            <div key={goal.id} className="flex flex-col gap-3 p-4 rounded-xl bg-slate-50/50 border border-slate-100 transition-all hover:border-primary/20 hover:shadow-md">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-slate-800">{goal.name}</h3>
                <span className="text-xs font-black text-primary px-2 py-1 rounded-md bg-white shadow-sm ring-1 ring-slate-200">
                  {Math.round(percentage)}%
                </span>
              </div>

              <Progress value={percentage} className="h-2 bg-slate-200" />

              <div className="flex items-center justify-between mt-1">
                <div className="flex flex-col">
                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-tighter">Current</p>
                    <p className="text-sm font-black text-slate-900">{formatAmount(goal.currentAmount)}</p>
                </div>
                <div className="flex flex-col items-end">
                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-tighter">Target</p>
                    <p className="text-sm font-bold text-slate-500">{formatAmount(goal.targetAmount)}</p>
                </div>
              </div>

              <div className="flex items-center gap-4 pt-2 border-t border-slate-200/50 mt-1">
                 <div className="flex items-center gap-1.5">
                    <Calendar className="size-3 text-slate-400" />
                    <span className="text-[10px] font-bold text-slate-500">
                        {new Date(goal.targetDate).toLocaleDateString('en-US', { month: 'short', year: 'numeric' })}
                    </span>
                 </div>
                 <div className="flex items-center gap-1.5">
                    <TrendingUp className="size-3 text-emerald-500" />
                    <span className="text-[10px] font-bold text-emerald-600">
                        On Track
                    </span>
                 </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default GoalProgress;
