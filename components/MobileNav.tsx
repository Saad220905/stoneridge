'use client';

import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetTrigger,
} from '@/components/ui/sheet';
import { sidebarLinks } from '@/constants';
import { cn } from '@/lib/utils';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import Footer from './Footer';
import { LayoutDashboard, ReceiptText, Landmark, ArrowLeftRight, Menu } from 'lucide-react';

const iconMap: Record<string, any> = {
  '/': LayoutDashboard,
  '/transaction-history': ReceiptText,
  '/my-banks': Landmark,
  '/payment-transfer': ArrowLeftRight,
};

const MobileNav = ({ user }: MobileNavProps) => {
  const pathname = usePathname();

  return (
    <section className="w-full max-w-[264px]">
      <Sheet>
        <SheetTrigger asChild>
          <button className="flex items-center justify-center p-2 rounded-lg bg-slate-50 border border-slate-200 text-slate-600 hover:bg-white hover:shadow-sm transition-all">
            <Menu className="size-6" />
          </button>
        </SheetTrigger>
        <SheetContent side="left" className="border-none bg-white p-0">
          <div className="flex flex-col h-full py-8 px-6">
            <Link
              href="/"
              className="cursor-pointer flex items-center gap-3 mb-10"
            >
              <div className="flex size-9 items-center justify-center rounded-xl bg-primary shadow-lg shadow-primary/20">
                 <Landmark className="text-white size-5" />
              </div>
              <h1 className="text-xl font-bold font-ibm-plex-serif tracking-tight text-slate-900">
                StoneRidge
              </h1>
            </Link>

            <div className="flex-1 flex flex-col gap-2 overflow-y-auto no-scrollbar">
              <SheetClose asChild>
                <nav className="flex flex-col gap-2">
                  {sidebarLinks.map((item) => {
                    const isActive =
                      pathname === item.route ||
                      pathname.startsWith(`${item.route}/`);
                    
                    const Icon = iconMap[item.route] || LayoutDashboard;

                    return (
                      <SheetClose asChild key={item.route}>
                        <Link
                          href={item.route}
                          className={cn('flex items-center gap-4 py-3 px-4 rounded-xl transition-all', {
                            'bg-primary text-white shadow-lg shadow-primary/20': isActive,
                            'text-slate-600 hover:bg-slate-50': !isActive,
                          })}
                        >
                          <Icon className={cn("size-5", isActive ? "text-white" : "text-slate-400")} />
                          <p className={cn('text-sm font-bold', {
                            'text-white': isActive,
                            'text-slate-500': !isActive,
                          })}>
                            {item.label}
                          </p>
                        </Link>
                      </SheetClose>
                    );
                  })}
                </nav>
              </SheetClose>
            </div>

            <div className="mt-auto">
               <Footer user={user} type="mobile" />
            </div>
          </div>
        </SheetContent>
      </Sheet>
    </section>
  );
};

export default MobileNav;
