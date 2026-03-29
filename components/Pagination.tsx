'use client';

import { useRouter, useSearchParams } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { formUrlQuery } from '@/lib/utils';
import { ChevronLeft, ChevronRight } from 'lucide-react';

export const Pagination = ({ page, totalPages }: PaginationProps) => {
  const router = useRouter();
  const searchParams = useSearchParams()!;

  const handleNavigation = (type: 'prev' | 'next') => {
    const pageNumber = type === 'prev' ? page - 1 : page + 1;

    const newUrl = formUrlQuery({
      params: searchParams.toString(),
      key: 'page',
      value: pageNumber.toString(),
    });

    router.push(newUrl, { scroll: false });
  };

  return (
    <div className="flex items-center justify-center gap-4 py-4">
      <Button
        variant="outline"
        size="sm"
        className="flex items-center gap-1.5 rounded-xl border-slate-200 font-bold text-slate-600 transition-all hover:bg-slate-50 active:scale-95 disabled:opacity-40"
        onClick={() => handleNavigation('prev')}
        disabled={Number(page) <= 1}
      >
        <ChevronLeft className="size-4" />
        Previous
      </Button>
      
      <div className="flex items-center gap-2">
          <span className="flex size-8 items-center justify-center rounded-lg bg-primary text-xs font-black text-white shadow-lg shadow-primary/20">
            {page}
          </span>
          <span className="text-xs font-bold text-slate-400">
            of {totalPages}
          </span>
      </div>

      <Button
        variant="outline"
        size="sm"
        className="flex items-center gap-1.5 rounded-xl border-slate-200 font-bold text-slate-600 transition-all hover:bg-slate-50 active:scale-95 disabled:opacity-40"
        onClick={() => handleNavigation('next')}
        disabled={Number(page) >= totalPages}
      >
        Next
        <ChevronRight className="size-4" />
      </Button>
    </div>
  );
};
