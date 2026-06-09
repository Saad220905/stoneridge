'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Loader2, Plus, Trash2, CheckCircle2, User, Landmark, Share2, Clipboard, DollarSign, Wallet, Users, ArrowUpRight, Check, Sparkles } from 'lucide-react';
import { getBank } from '@/lib/plaid';
import { createBillSplit, payBillSplit } from '@/lib/split-bills';
import { formatAmount } from '@/lib/utils';
import { BankDropdown } from './BankDropdown';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';

interface SplitBillsFormProps {
    accounts: (Bank | Account)[];
    initialPending: BillSplitParticipant[];
    initialCreated: BillSplit[];
    loggedInUserEmail: string;
}

export default function SplitBillsForm({
    accounts = [],
    initialPending = [],
    initialCreated = [],
    loggedInUserEmail,
}: SplitBillsFormProps) {
    const router = useRouter();
    
    // Tabs state
    const [activeTab, setActiveTab] = useState<'create' | 'pending' | 'created'>('create');
    
    // Lists state
    const [pendingSplits, setPendingSplits] = useState<BillSplitParticipant[]>(initialPending);
    const [createdSplits, setCreatedSplits] = useState<BillSplit[]>(initialCreated);

    // Form state
    const [billName, setBillName] = useState('');
    const [totalAmount, setTotalAmount] = useState('');
    const [creatorBank, setCreatorBank] = useState(accounts[0]?.appwriteItemId || '');
    const [participants, setParticipants] = useState<{ email: string; amount: string }[]>([
        { email: '', amount: '' }
    ]);
    
    // Loading states
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [payingId, setPayingId] = useState<number | null>(null);
    const [payerBanks, setPayerBanks] = useState<{ [key: number]: string }>({});

    // Error / success messages
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    // Handle adding dynamic participant email input
    const addParticipant = () => {
        setParticipants([...participants, { email: '', amount: '' }]);
    };

    // Handle removing dynamic participant email input
    const removeParticipant = (index: number) => {
        if (participants.length > 1) {
            const updated = participants.filter((_, i) => i !== index);
            setParticipants(updated);
        }
    };

    const updateParticipant = (index: number, field: 'email' | 'amount', value: string) => {
        const updated = [...participants];
        updated[index] = { ...updated[index], [field]: value };
        setParticipants(updated);
    };

    // Calculate equal split among all request participants (+ creator)
    const handleSplitEqually = () => {
        const total = parseFloat(totalAmount);
        if (isNaN(total) || total <= 0) {
            setErrorMsg('Please enter a valid total amount before splitting.');
            return;
        }

        setErrorMsg('');
        const totalPeople = participants.length + 1; // Participants + Creator
        const share = (total / totalPeople).toFixed(2);
        
        const updated = participants.map(p => ({
            ...p,
            amount: share
        }));
        setParticipants(updated);
    };

    const handleCreateSplit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg('');
        setSuccessMsg('');

        if (!billName.trim()) {
            setErrorMsg('Please enter a bill name.');
            return;
        }
        const total = parseFloat(totalAmount);
        if (isNaN(total) || total <= 0) {
            setErrorMsg('Please enter a valid total amount.');
            return;
        }
        if (!creatorBank) {
            setErrorMsg('Please select a bank account to receive funds.');
            return;
        }

        // Validate participants
        for (const p of participants) {
            if (!p.email.trim() || !p.email.includes('@')) {
                setErrorMsg('Please enter a valid email address for all participants.');
                return;
            }
            const partAmount = parseFloat(p.amount);
            if (isNaN(partAmount) || partAmount <= 0) {
                setErrorMsg('Please enter a valid share amount for all participants.');
                return;
            }
        }

        setIsSubmitting(true);
        try {
            // Retrieve sender bank details to match appwriteItemId with databaseId if needed,
            // the backend service handles both databaseId and appwriteItemId.
            const splitResponse = await createBillSplit({
                billName,
                totalAmount: total,
                creatorBankId: creatorBank,
                participants: participants.map(p => ({
                    email: p.email,
                    amount: parseFloat(p.amount)
                }))
            });

            if (splitResponse) {
                setSuccessMsg(`Success! Split bill "${billName}" created.`);
                // Reset form
                setBillName('');
                setTotalAmount('');
                setParticipants([{ email: '', amount: '' }]);
                
                // Refresh list
                const updatedCreated = await getCreatedSplits();
                setCreatedSplits(updatedCreated);
                
                // Switch tab
                setTimeout(() => {
                    setActiveTab('created');
                    setSuccessMsg('');
                }, 1500);
            }
        } catch (error: any) {
            setErrorMsg(error.message || 'Failed to create bill split. Verify friend emails are registered.');
        } finally {
            setIsSubmitting(false);
        }
    };

    const handlePaySplit = async (participantId: number) => {
        const sourceBankId = payerBanks[participantId] || accounts[0]?.appwriteItemId;
        if (!sourceBankId) {
            alert('Please select a valid bank account to settle payment.');
            return;
        }

        setPayingId(participantId);
        try {
            await payBillSplit(participantId, sourceBankId);
            
            // Remove paid split from pending list
            setPendingSplits(pendingSplits.filter(p => p.databaseId !== participantId));
            
            alert('Payment authorized and settled successfully via Dwolla!');
            
            // Refresh created splits to update status
            const updatedCreated = await getCreatedSplits();
            setCreatedSplits(updatedCreated);
        } catch (error: any) {
            alert(error.message || 'Dwolla transfer settlement failed. Please try again.');
        } finally {
            setPayingId(null);
        }
    };

    const setDropdownBankValue = (name: string, value: string) => {
        setCreatorBank(value);
    };

    return (
        <Tabs defaultValue="create" value={activeTab} onValueChange={(v) => setActiveTab(v as any)} className="w-full">
            <TabsList className="grid w-full grid-cols-3 max-w-[500px] mb-8 bg-slate-100 p-1.5 rounded-2xl h-14 shadow-inner">
                <TabsTrigger value="create" className="rounded-xl font-bold text-sm transition-all py-2.5">
                    <Sparkles className="size-4 mr-2" />
                    Split a Bill
                </TabsTrigger>
                <TabsTrigger value="pending" className="rounded-xl font-bold text-sm transition-all py-2.5 relative">
                    <Wallet className="size-4 mr-2" />
                    Pending
                    {pendingSplits.length > 0 && (
                        <span className="absolute -top-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-red-500 text-[10px] font-black text-white animate-pulse">
                            {pendingSplits.length}
                        </span>
                    )}
                </TabsTrigger>
                <TabsTrigger value="created" className="rounded-xl font-bold text-sm transition-all py-2.5">
                    <Users className="size-4 mr-2" />
                    Sent Splits
                </TabsTrigger>
            </TabsList>

            <TabsContent value="create" className="focus-visible:outline-none">
                <form onSubmit={handleCreateSplit} className="flex flex-col gap-8 max-w-[800px]">
                    <div className="flex flex-col gap-6">
                        <div className="flex items-center gap-3 border-b border-slate-200 pb-4">
                            <div className="flex size-10 items-center justify-center rounded-xl bg-primary/10 text-primary">
                                <Sparkles className="size-5" />
                            </div>
                            <div className="flex flex-col">
                                <h2 className="text-xl font-bold text-slate-900 leading-tight">Bill Split details</h2>
                                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mt-0.5">Define bill and split recipients</p>
                            </div>
                        </div>

                        {errorMsg && (
                            <div className="p-4 bg-red-50 text-red-700 rounded-xl border border-red-200 text-sm font-bold flex items-center gap-2">
                                <span className="inline-block h-2 w-2 rounded-full bg-red-600 animate-ping"></span>
                                {errorMsg}
                            </div>
                        )}

                        {successMsg && (
                            <div className="p-4 bg-emerald-50 text-emerald-700 rounded-xl border border-emerald-200 text-sm font-bold flex items-center gap-2">
                                <Check className="size-5" />
                                {successMsg}
                            </div>
                        )}

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div className="flex flex-col gap-2">
                                <label className="text-sm font-bold text-slate-700 tracking-tight">Bill Name / Memo</label>
                                <Input
                                    placeholder="Dinner, utilities, cab, etc."
                                    className="h-12 rounded-xl border-slate-200 bg-white px-4 text-sm font-medium shadow-sm focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                                    value={billName}
                                    onChange={(e) => setBillName(e.target.value)}
                                />
                            </div>

                            <div className="flex flex-col gap-2">
                                <label className="text-sm font-bold text-slate-700 tracking-tight">Total Amount ($)</label>
                                <div className="relative">
                                    <DollarSign className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 size-5" />
                                    <Input
                                        placeholder="0.00"
                                        className="h-12 pl-10 rounded-xl border-slate-200 bg-white text-sm font-medium shadow-sm focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                                        value={totalAmount}
                                        onChange={(e) => setTotalAmount(e.target.value)}
                                    />
                                </div>
                            </div>
                        </div>

                        <div className="flex flex-col gap-3">
                            <div className="flex flex-col gap-0.5">
                                <label className="text-sm font-bold text-slate-700 tracking-tight">Receiving Bank Account</label>
                                <span className="text-xs font-medium text-slate-400">Select which bank account will receive the split payments</span>
                            </div>
                            <BankDropdown
                                accounts={accounts}
                                setValue={setDropdownBankValue}
                                otherStyles="!w-full h-12 rounded-xl border-slate-200 bg-white shadow-sm"
                            />
                        </div>

                        <div className="flex flex-col gap-4 pt-4 border-t border-slate-100">
                            <div className="flex items-center justify-between">
                                <div className="flex flex-col gap-0.5">
                                    <h3 className="text-md font-bold text-slate-900">Friends & Shares</h3>
                                    <span className="text-xs font-medium text-slate-400">Add emails of registered friends and specify amounts</span>
                                </div>
                                <Button 
                                    type="button" 
                                    onClick={handleSplitEqually}
                                    className="h-10 px-4 rounded-xl border border-primary/20 bg-primary/5 text-primary font-bold hover:bg-primary/10 transition-colors text-xs"
                                >
                                    Split Equally
                                </Button>
                            </div>

                            <div className="flex flex-col gap-4">
                                {participants.map((participant, index) => (
                                    <div key={index} className="flex gap-4 items-center">
                                        <div className="flex-1">
                                            <Input
                                                placeholder="friend@email.com"
                                                className="h-12 rounded-xl border-slate-200 bg-white px-4 text-sm font-medium shadow-sm focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                                                value={participant.email}
                                                onChange={(e) => updateParticipant(index, 'email', e.target.value)}
                                            />
                                        </div>
                                        <div className="w-32 relative">
                                            <DollarSign className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 size-4" />
                                            <Input
                                                placeholder="0.00"
                                                className="h-12 pl-8 rounded-xl border-slate-200 bg-white text-sm font-medium shadow-sm focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                                                value={participant.amount}
                                                onChange={(e) => updateParticipant(index, 'amount', e.target.value)}
                                            />
                                        </div>
                                        <Button
                                            type="button"
                                            onClick={() => removeParticipant(index)}
                                            className="size-12 p-0 rounded-xl bg-red-50 text-red-500 hover:bg-red-100 transition-colors border border-red-100"
                                            disabled={participants.length <= 1}
                                        >
                                            <Trash2 className="size-5" />
                                        </Button>
                                    </div>
                                ))}

                                <Button
                                    type="button"
                                    onClick={addParticipant}
                                    className="h-12 border border-dashed border-slate-300 hover:border-primary/30 hover:bg-primary/5 rounded-xl text-slate-500 font-bold transition-all text-xs flex items-center justify-center gap-2 bg-slate-50/50"
                                >
                                    <Plus className="size-4" /> Add Another Participant
                                </Button>
                            </div>
                        </div>
                    </div>

                    <div className="pt-6 border-t border-slate-100">
                        <Button type="submit" className="h-14 w-full md:w-fit px-12 bg-primary text-white font-black rounded-xl shadow-xl shadow-primary/20 hover:bg-primary/90 transition-all active:scale-[0.98]">
                            {isSubmitting ? (
                                <Loader2 className="size-5 animate-spin" />
                            ) : (
                                <div className="flex items-center gap-2">
                                    <Share2 className="size-5" />
                                    Initiate Bill Split
                                </div>
                            )}
                        </Button>
                    </div>
                </form>
            </TabsContent>

            <TabsContent value="pending" className="focus-visible:outline-none">
                {pendingSplits.length === 0 ? (
                    <div className="flex flex-col items-center justify-center p-12 text-center bg-slate-50/50 rounded-3xl border border-dashed border-slate-200">
                        <Wallet className="size-12 text-slate-300 mb-4" />
                        <h3 className="text-lg font-bold text-slate-700">No pending splits</h3>
                        <p className="text-sm font-medium text-slate-400 mt-1">You are all squared up! Friends' split requests will show up here.</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-[1200px]">
                        {pendingSplits.map((split) => (
                            <div key={split.databaseId} className="bg-white rounded-3xl p-6 shadow-xl shadow-slate-200/50 border border-slate-100 flex flex-col justify-between gap-6 hover:shadow-2xl hover:shadow-slate-300/30 transition-all">
                                <div className="flex flex-col gap-2">
                                    <div className="flex justify-between items-start">
                                        <span className="px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest bg-amber-50 text-amber-600 border border-amber-100">
                                            Reimbursement Request
                                        </span>
                                        <span className="text-2xl font-black text-slate-900 tracking-tight">
                                            {formatAmount(split.amount)}
                                        </span>
                                    </div>
                                    <h3 className="text-lg font-bold text-slate-800 mt-2">{split.billName}</h3>
                                    <p className="text-sm font-medium text-slate-400 flex items-center gap-1.5 mt-1">
                                        <User className="size-4 text-slate-400" />
                                        Requested by <span className="font-bold text-slate-600">{split.creatorName}</span> ({split.creatorEmail})
                                    </p>
                                </div>

                                <div className="flex flex-col gap-3 pt-4 border-t border-slate-100">
                                    <div className="flex flex-col gap-1.5">
                                        <span className="text-xs font-bold text-slate-500 flex items-center gap-1.5">
                                            <Landmark className="size-4" /> Pay From Account
                                        </span>
                                        <div className="relative">
                                            <select
                                                className="w-full h-11 rounded-xl border border-slate-200 bg-white px-3 text-sm font-bold text-slate-700 shadow-sm focus:border-primary/30 focus:outline-none"
                                                value={payerBanks[split.databaseId] || accounts[0]?.appwriteItemId || ''}
                                                onChange={(e) => setPayerBanks({
                                                    ...payerBanks,
                                                    [split.databaseId]: e.target.value
                                                })}
                                            >
                                                {accounts.map((bank) => (
                                                    <option key={bank.appwriteItemId} value={bank.appwriteItemId}>
                                                        {bank.name} - {formatAmount(bank.currentBalance)}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                    </div>

                                    <Button
                                        onClick={() => handlePaySplit(split.databaseId)}
                                        disabled={payingId === split.databaseId}
                                        className="h-11 w-full bg-primary text-white font-bold rounded-xl shadow-lg shadow-primary/10 hover:bg-primary/95 transition-all mt-2"
                                    >
                                        {payingId === split.databaseId ? (
                                            <Loader2 className="size-5 animate-spin" />
                                        ) : (
                                            <div className="flex items-center justify-center gap-2">
                                                <ArrowUpRight className="size-4" />
                                                Authorize & Pay {formatAmount(split.amount)}
                                            </div>
                                        )}
                                    </Button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </TabsContent>

            <TabsContent value="created" className="focus-visible:outline-none">
                {createdSplits.length === 0 ? (
                    <div className="flex flex-col items-center justify-center p-12 text-center bg-slate-50/50 rounded-3xl border border-dashed border-slate-200">
                        <Users className="size-12 text-slate-300 mb-4" />
                        <h3 className="text-lg font-bold text-slate-700">No split bills initiated yet</h3>
                        <p className="text-sm font-medium text-slate-400 mt-1">Split a bill with friends, and you'll see tracking details here.</p>
                    </div>
                ) : (
                    <div className="flex flex-col gap-6 max-w-[1200px]">
                        {createdSplits.map((split) => {
                            const paidCount = split.participants.filter(p => p.status === 'PAID').length;
                            const totalCount = split.participants.length;
                            const progressPercentage = Math.round((paidCount / totalCount) * 100);

                            return (
                                <div key={split.databaseId} className="bg-white rounded-3xl p-6 md:p-8 shadow-xl shadow-slate-200/50 border border-slate-100 flex flex-col gap-6">
                                    <div className="flex flex-col md:flex-row justify-between md:items-center gap-4">
                                        <div className="flex flex-col gap-1">
                                            <div className="flex items-center gap-3">
                                                <h3 className="text-xl font-bold text-slate-900">{split.billName}</h3>
                                                <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest border ${
                                                    split.status === 'COMPLETED'
                                                        ? 'bg-emerald-50 text-emerald-600 border-emerald-100'
                                                        : 'bg-amber-50 text-amber-600 border-amber-100'
                                                }`}>
                                                    {split.status}
                                                </span>
                                            </div>
                                            <span className="text-xs font-semibold text-slate-400">
                                                Created on {new Date(split.createdAt).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <div className="text-left md:text-right flex flex-col">
                                            <span className="text-sm font-bold text-slate-400 uppercase tracking-wider">Total Bill Amount</span>
                                            <span className="text-2xl font-black text-slate-900 tracking-tight">
                                                {formatAmount(split.totalAmount)}
                                            </span>
                                        </div>
                                    </div>

                                    {/* Progress section */}
                                    <div className="flex flex-col gap-2 pt-2">
                                        <div className="flex justify-between items-center text-xs font-bold text-slate-500">
                                            <span>Collection Progress</span>
                                            <span>{paidCount} / {totalCount} friends paid ({progressPercentage}%)</span>
                                        </div>
                                        <div className="h-3 w-full bg-slate-100 rounded-full overflow-hidden">
                                            <div
                                                className={`h-full rounded-full transition-all duration-500 ${
                                                    split.status === 'COMPLETED' ? 'bg-emerald-500' : 'bg-primary'
                                                }`}
                                                style={{ width: `${progressPercentage}%` }}
                                            />
                                        </div>
                                    </div>

                                    {/* Participants breakdown */}
                                    <div className="pt-4 border-t border-slate-100">
                                        <h4 className="text-xs font-black uppercase tracking-widest text-slate-400 mb-4">Reimbursement Breakdown</h4>
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                            {split.participants.map((p) => (
                                                <div key={p.databaseId} className="flex justify-between items-center bg-slate-50/50 px-4 py-3 rounded-2xl border border-slate-100">
                                                    <div className="flex flex-col gap-0.5">
                                                        <span className="text-sm font-bold text-slate-700 truncate max-w-[200px] md:max-w-[280px]">
                                                            {p.email}
                                                        </span>
                                                        <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wide">
                                                            {p.status === 'PAID' ? 'Settled' : 'Pending payment'}
                                                        </span>
                                                    </div>
                                                    <div className="flex items-center gap-3">
                                                        <span className="text-sm font-black text-slate-800">
                                                            {formatAmount(p.amount)}
                                                        </span>
                                                        {p.status === 'PAID' ? (
                                                            <CheckCircle2 className="size-5 text-emerald-500 fill-emerald-50/50" />
                                                        ) : (
                                                            <div className="size-5 rounded-full border-2 border-slate-200 border-t-amber-500 animate-spin" style={{ animationDuration: '3s' }} />
                                                        )}
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </TabsContent>
        </Tabs>
    );
}
