import MobileNav from '@/components/MobileNav';
import Sidebar from '@/components/Sidebar';
import { getLoggedInUser } from '@/lib/auth';
import { redirect } from 'next/navigation';
import { Landmark } from 'lucide-react';

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const loggedIn = await getLoggedInUser();

  if (!loggedIn) redirect('/sign-in');

  return (
    <main className="flex h-screen w-full bg-white font-inter">
      <Sidebar user={loggedIn} />

      <div className="flex size-full flex-col">
        <div className="flex h-16 items-center justify-between p-5 border-b border-slate-200 sm:p-8 md:hidden bg-white/80 backdrop-blur-md sticky top-0 z-50">
          <div className="flex items-center gap-2">
            <div className="flex size-8 items-center justify-center rounded-lg bg-primary shadow-lg shadow-primary/20">
                <Landmark className="text-white size-5" />
            </div>
            <h1 className="text-xl font-bold font-ibm-plex-serif tracking-tight text-slate-900">
                StoneRidge
            </h1>
          </div>
          <div>
            <MobileNav user={loggedIn} />
          </div>
        </div>
        <div className="flex-1 overflow-y-auto no-scrollbar">
          {children}
        </div>
      </div>
    </main>
  );
}
