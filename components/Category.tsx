import { topCategoryStyles } from '@/constants';
import { cn } from '@/lib/utils';
import { Progress } from './ui/progress';
import { Utensils, Plane, ArrowRightLeft, ShoppingBag, Landmark, HelpCircle } from 'lucide-react';

const iconMap: Record<string, any> = {
  'Food and Drink': Utensils,
  'Travel': Plane,
  'Transfer': ArrowRightLeft,
  'Shopping': ShoppingBag,
  'Bank': Landmark,
  'default': HelpCircle
};

const Category = ({ category }: CategoryProps) => {
  const {
    bg,
    circleBg,
    text: { main, count },
    progress: { bg: progressBg, indicator },
  } = topCategoryStyles[category.name as keyof typeof topCategoryStyles] ||
  topCategoryStyles.default;

  const Icon = iconMap[category.name] || iconMap.default;

  return (
    <div className={cn('flex items-center gap-4 p-4 rounded-2xl border border-transparent hover:border-slate-100 hover:bg-white transition-all duration-300 group', bg)}>
      <figure className={cn('flex items-center justify-center size-10 rounded-xl shadow-sm group-hover:shadow-md transition-shadow', circleBg)}>
        <Icon className="size-5 text-inherit opacity-80" />
      </figure>
      <div className="flex flex-1 flex-col gap-2.5">
        <div className="flex justify-between items-end">
          <h2 className={cn('text-sm font-bold', main)}>{category.name}</h2>
          <span className={cn('text-xs font-black tracking-tighter opacity-70', count)}>
            {Math.round((category.count / category.totalCount) * 100)}%
          </span>
        </div>
        <Progress
          value={(category.count / category.totalCount) * 100}
          className={cn('h-1.5 w-full bg-slate-100', progressBg)}
          indicatorClassName={cn('h-1.5 rounded-full', indicator)}
        />
      </div>
    </div>
  );
};

export default Category;
