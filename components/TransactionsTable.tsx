import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { transactionCategoryStyles } from '@/constants';
import {
  cn,
  formatAmount,
  formatDateTime,
  getTransactionStatus,
  removeSpecialCharacters,
} from '@/lib/utils';
import { ArrowUpRight, ArrowDownLeft, Circle } from 'lucide-react';

const CategoryBadge = ({ category }: CategoryBadgeProps) => {
  const { borderColor, backgroundColor, textColor, chipBackgroundColor } =
    transactionCategoryStyles[
      category as keyof typeof transactionCategoryStyles
    ] || transactionCategoryStyles.default;

  return (
    <div className={cn('flex items-center gap-1.5 rounded-full px-2.5 py-0.5 border text-xs font-semibold', borderColor, chipBackgroundColor, textColor)}>
      <div className={cn('size-1.5 rounded-full', backgroundColor)} />
      {category}
    </div>
  );
};

const TransactionsTable = ({ transactions }: TransactionTableProps) => {
  return (
    <div className="rounded-xl border bg-white shadow-sm overflow-hidden">
        <Table>
          <TableHeader className="bg-slate-50/50">
            <TableRow className="hover:bg-transparent border-b">
              <TableHead className="px-4 py-4 text-xs font-bold uppercase tracking-wider text-slate-500">Transaction</TableHead>
              <TableHead className="px-4 py-4 text-xs font-bold uppercase tracking-wider text-slate-500">Amount</TableHead>
              <TableHead className="px-4 py-4 text-xs font-bold uppercase tracking-wider text-slate-500">Status</TableHead>
              <TableHead className="px-4 py-4 text-xs font-bold uppercase tracking-wider text-slate-500">Date</TableHead>
              <TableHead className="px-4 py-4 text-xs font-bold uppercase tracking-wider text-slate-500 max-md:hidden text-right">Channel</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {transactions.map((t: Transaction) => {
              const status = getTransactionStatus(new Date(t.date));
              const amount = formatAmount(t.amount);

              const isDebit = t.type === 'debit' || t.amount < 0;
              const isCredit = t.type === 'credit' || t.amount > 0;

              return (
                <TableRow
                  key={t.id}
                  className="hover:bg-slate-50/50 transition-colors border-b last:border-0"
                >
                  <TableCell className="px-4 py-4">
                    <div className="flex items-center gap-3">
                        <div className={cn(
                            "flex size-9 items-center justify-center rounded-lg border",
                            isDebit ? "bg-red-50/50 border-red-100" : "bg-emerald-50/50 border-emerald-100"
                        )}>
                            {isDebit ? 
                                <ArrowUpRight className="size-4 text-red-600" /> : 
                                <ArrowDownLeft className="size-4 text-emerald-600" />
                            }
                        </div>
                        <span className="text-sm font-bold text-slate-700 truncate max-w-[200px]">
                            {removeSpecialCharacters(t.name)}
                        </span>
                    </div>
                  </TableCell>

                  <TableCell className={cn(
                      "px-4 py-4 text-sm font-bold",
                      isDebit ? "text-red-600" : "text-emerald-600"
                  )}>
                    {isDebit ? `-${amount}` : `+${amount}`}
                  </TableCell>

                  <TableCell className="px-4 py-4">
                    <CategoryBadge category={status} />
                  </TableCell>

                  <TableCell className="px-4 py-4 text-sm text-slate-500 font-medium">
                    {formatDateTime(new Date(t.date)).dateTime}
                  </TableCell>

                  <TableCell className="px-4 py-4 text-right max-md:hidden">
                    <span className="inline-flex items-center rounded-md bg-slate-100 px-2 py-1 text-xs font-bold text-slate-600 capitalize">
                        {t.paymentChannel}
                    </span>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
    </div>
  );
};

export default TransactionsTable;
