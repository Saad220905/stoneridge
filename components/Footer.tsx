import { logoutAccount } from '@/lib/auth';
import { LogOut } from 'lucide-react';
import { cn } from '@/lib/utils';

const Footer = ({ user, type = 'desktop' }: FooterProps) => {
  const handleLogOut = async () => {
    await logoutAccount();
  };

  return (
    <footer className={cn("flex items-center justify-between border-t border-slate-200/60 pt-6", {
        "flex-col gap-4": type === 'mobile'
    })}>
      <div className="flex items-center gap-3">
        <div className="flex size-10 items-center justify-center rounded-xl bg-slate-100 ring-1 ring-slate-200 shadow-sm">
          <p className="text-sm font-black text-primary uppercase">
            {user?.firstName?.[0]}
          </p>
        </div>

        <div className={cn("flex flex-col", { "hidden xl:flex": type === 'desktop' })}>
          <h1 className="text-sm font-bold text-slate-900 leading-none">
            {user?.firstName} {user?.lastName}
          </h1>
          <p className="text-xs font-medium text-slate-500 mt-1 truncate max-w-[140px]">
            {user?.email}
          </p>
        </div>
      </div>

      <button 
        onClick={handleLogOut}
        className={cn(
            "flex size-9 items-center justify-center rounded-lg text-slate-400 transition-all hover:bg-red-50 hover:text-red-500 hover:shadow-md hover:shadow-red-500/10 active:scale-95",
            { "hidden xl:flex": type === 'desktop' }
        )}
      >
        <LogOut className="size-5" />
      </button>
    </footer>
  );
};

export default Footer;
