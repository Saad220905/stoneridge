'use client';

import { sidebarLinks } from '@/constants';
import { cn } from '@/lib/utils';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import Footer from './Footer';
import PlaidLink from './PlaidLink';
import { LayoutDashboard, ReceiptText, Landmark, ArrowLeftRight, Target } from 'lucide-react';

const iconMap: Record<string, any> = {
  '/': LayoutDashboard,
  '/transaction-history': ReceiptText,
  '/my-banks': Landmark,
  '/payment-transfer': ArrowLeftRight,
  '/goals': Target,
};

const Sidebar = ({ user }: SidebarProps) => {
  const pathname = usePathname();

  return (
    <section className="sidebar bg-slate-50/50">
      <nav className="flex flex-col gap-6">
        <Link href="/" className="mb-10 cursor-pointer flex items-center gap-3 px-4">
          <div className="relative flex size-10 items-center justify-center rounded-xl bg-primary shadow-lg shadow-primary/20">
             <Landmark className="text-white size-6" />
          </div>
          <h1 className="sidebar-logo text-slate-900">StoneRidge</h1>
        </Link>

        <div className="flex flex-col gap-2">
          {sidebarLinks.map((item) => {
            const isActive =
              pathname === item.route || pathname.startsWith(`${item.route}/`);
            
            const Icon = iconMap[item.route] || LayoutDashboard;

            return (
              <Link
                href={item.route}
                key={item.label}
                className={cn('sidebar-link group', { 
                  'active bg-primary text-white': isActive,
                  'text-slate-600 hover:text-primary hover:bg-white': !isActive 
                })}
              >
                <div className="relative size-5">
                   <Icon className={cn("size-5 transition-colors", {
                     "text-white": isActive,
                     "text-slate-400 group-hover:text-primary": !isActive
                   })} />
                </div>
                <p className={cn('sidebar-label transition-all', { 
                  'text-white': isActive,
                  'group-hover:translate-x-1': !isActive
                })}>
                  {item.label}
                </p>
              </Link>
            );
          })}
        </div>

        <div className="px-4 mt-4">
           <PlaidLink user={user} />
        </div>
      </nav>

      <Footer user={user} />
    </section>
  );
};

export default Sidebar;
