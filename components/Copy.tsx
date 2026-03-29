'use client';
import { useState } from 'react';
import { Button } from './ui/button';
import { Copy as CopyIcon, Check } from 'lucide-react';

const Copy = ({ title }: { title: string }) => {
  const [hasCopied, setHasCopied] = useState(false);

  const copyToClipboard = () => {
    navigator.clipboard.writeText(title);
    setHasCopied(true);

    setTimeout(() => {
      setHasCopied(false);
    }, 2000);
  };

  return (
    <Button
      onClick={copyToClipboard}
      variant="outline"
      className="mt-3 flex max-w-[340px] items-center justify-between gap-4 rounded-xl border-slate-200 bg-slate-50/50 px-4 py-2 hover:bg-white hover:border-primary/20 transition-all active:scale-[0.98] group"
    >
      <div className="flex flex-col items-start gap-0.5 overflow-hidden">
        <p className="text-[10px] font-black uppercase tracking-widest text-slate-400 group-hover:text-primary/60 transition-colors">Public Identifier</p>
        <p className="line-clamp-1 w-full text-xs font-bold text-slate-600">
            {title}
        </p>
      </div>

      <div className="flex size-8 items-center justify-center rounded-lg bg-white border border-slate-100 shadow-sm group-hover:border-primary/20">
        {!hasCopied ? (
            <CopyIcon className="size-3.5 text-slate-400 group-hover:text-primary transition-colors" />
        ) : (
            <Check className="size-3.5 text-emerald-500" />
        )}
      </div>
    </Button>
  );
};

export default Copy;
