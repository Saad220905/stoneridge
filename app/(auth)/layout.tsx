import { Landmark } from 'lucide-react';
import Link from 'next/link';

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <main
      className="relative flex min-h-screen w-full items-center justify-center bg-slate-950 bg-fixed bg-cover bg-center bg-gradient-to-br from-slate-900 via-slate-950 to-primary/10"
    >
      <div className="absolute inset-0 bg-slate-950/40 backdrop-brightness-50" />

      {/* Static Logo and Name */}
      <div className="absolute top-10 left-10 z-20 flex items-center space-x-4">
        <Link href="/" className="cursor-pointer flex items-center gap-3">
          <div className="flex size-10 items-center justify-center rounded-xl bg-primary shadow-2xl shadow-primary/40">
            <Landmark className="text-white size-6" />
          </div>
          <h1 className="text-white text-2xl font-black tracking-tighter">StoneRidge</h1>
        </Link>
      </div>

      {/* Translucent Form Container */}
      <div className="relative z-10 bg-white/80 backdrop-blur-2xl rounded-3xl p-10 w-full max-w-[520px] shadow-2xl shadow-black/20 border border-white/20">
        {children}
      </div>
    </main>
  );
}
