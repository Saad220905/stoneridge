'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { Loader2, Send, Info } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import * as z from 'zod';

import { createTransfer } from '@/lib/dwolla';
import { createTransaction } from '@/lib/transactions';
import { getBank, getBankByAccountId } from '@/lib/plaid';
import { decryptId } from '@/lib/utils';

import { BankDropdown } from './BankDropdown';
import { Button } from './ui/button';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from './ui/form';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';

const formSchema = z.object({
  email: z.string().email('Invalid email address'),
  name: z.string().min(4, 'Transfer note is too short'),
  amount: z.string().min(4, 'Amount is too short'),
  senderBank: z.string().min(4, 'Please select a valid bank account'),
  shareableId: z.string().min(8, 'Please select a valid sharable Id'),
});

const PaymentTransferForm = ({ accounts }: PaymentTransferFormProps) => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: '',
      email: '',
      amount: '',
      senderBank: '',
      shareableId: '',
    },
  });

  const submit = async (data: z.infer<typeof formSchema>) => {
    setIsLoading(true);

    try {
      const receiverAccountId = decryptId(data.shareableId);
      const receiverBank = await getBankByAccountId({
        accountId: receiverAccountId,
      });
      const senderBank = await getBank({ documentId: data.senderBank });

      const transferParams = {
        sourceFundingSourceUrl: senderBank.fundingSourceUrl,
        destinationFundingSourceUrl: receiverBank.fundingSourceUrl,
        amount: data.amount,
      };
      // create transfer
      const transfer = await createTransfer(transferParams);

      // create transfer transaction
      if (transfer) {
        const transaction = {
          name: data.name,
          amount: data.amount,
          senderId: senderBank.userId,
          senderBankId: senderBank.id,
          receiverId: receiverBank.userId,
          receiverBankId: receiverBank.id,
          email: data.email,
        };

        const newTransaction = await createTransaction(transaction);

        if (newTransaction) {
          form.reset();
          router.push('/');
        }
      }
    } catch (error) {
      console.error('Submitting create transfer request failed: ', error);
    }

    setIsLoading(false);
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(submit)} className="flex flex-col gap-8 max-w-[800px]">
        <div className="flex flex-col gap-6">
            <div className="flex items-center gap-3 border-b border-slate-200 pb-4">
                <div className="flex size-10 items-center justify-center rounded-xl bg-slate-100 text-slate-500">
                    <Info className="size-5" />
                </div>
                <div className="flex flex-col">
                    <h2 className="text-xl font-bold text-slate-900 leading-tight">Transfer Details</h2>
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mt-0.5">Origin and Recipient Information</p>
                </div>
            </div>

            <FormField
              control={form.control}
              name="senderBank"
              render={() => (
                <FormItem className="flex flex-col gap-3">
                  <div className="flex flex-col gap-1">
                      <FormLabel className="text-sm font-bold text-slate-700 tracking-tight">Source Account</FormLabel>
                      <FormDescription className="text-xs font-medium text-slate-400">Select the bank account you want to transfer funds from</FormDescription>
                  </div>
                  <FormControl>
                    <BankDropdown
                      accounts={accounts}
                      setValue={form.setValue as any}
                      otherStyles="!w-full h-12 rounded-xl border-slate-200 bg-white shadow-sm"
                    />
                  </FormControl>
                  <FormMessage className="text-[10px] font-bold text-red-500" />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem className="flex flex-col gap-3">
                  <div className="flex flex-col gap-1">
                      <FormLabel className="text-sm font-bold text-slate-700 tracking-tight">Memo (Optional)</FormLabel>
                      <FormDescription className="text-xs font-medium text-slate-400">Additional information related to this transfer</FormDescription>
                  </div>
                  <FormControl>
                    <Textarea
                      placeholder="Enter a brief description"
                      className="min-h-[100px] rounded-xl border-slate-200 bg-white px-4 py-3 text-sm font-medium transition-all focus:border-primary/30 focus:ring-4 focus:ring-primary/5 shadow-sm"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage className="text-[10px] font-bold text-red-500" />
                </FormItem>
              )}
            />
        </div>

        <div className="flex flex-col gap-6 pt-4">
            <div className="flex flex-col gap-1 border-b border-slate-200 pb-4">
              <h2 className="text-lg font-bold text-slate-900">Destination Account</h2>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Recipient Metadata</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <FormField
                  control={form.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem className="flex flex-col gap-2">
                      <FormLabel className="text-sm font-bold text-slate-700 tracking-tight">Recipient Email</FormLabel>
                      <FormControl>
                        <Input
                          placeholder="johndoe@gmail.com"
                          className="h-12 rounded-xl border-slate-200 bg-white px-4 text-sm font-medium shadow-sm transition-all focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage className="text-[10px] font-bold text-red-500" />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="shareableId"
                  render={({ field }) => (
                    <FormItem className="flex flex-col gap-2">
                      <FormLabel className="text-sm font-bold text-slate-700 tracking-tight">Sharable ID</FormLabel>
                      <FormControl>
                        <Input
                          placeholder="Public account identifier"
                          className="h-12 rounded-xl border-slate-200 bg-white px-4 text-sm font-medium shadow-sm transition-all focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage className="text-[10px] font-bold text-red-500" />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="amount"
                  render={({ field }) => (
                    <FormItem className="flex flex-col gap-2 md:col-span-2">
                      <FormLabel className="text-sm font-bold text-slate-700 tracking-tight">Transfer Amount</FormLabel>
                      <div className="relative">
                        <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-bold">$</span>
                        <FormControl>
                            <Input
                              placeholder="0.00"
                              className="h-14 pl-8 rounded-xl border-slate-200 bg-white text-lg font-black text-slate-900 shadow-sm transition-all focus:border-primary/30 focus:ring-4 focus:ring-primary/5"
                              {...field}
                            />
                        </FormControl>
                      </div>
                      <FormMessage className="text-[10px] font-bold text-red-500" />
                    </FormItem>
                  )}
                />
            </div>
        </div>

        <div className="pt-6">
          <Button type="submit" className="h-14 w-full md:w-fit px-12 bg-primary text-white font-black rounded-xl shadow-xl shadow-primary/20 hover:bg-primary/90 transition-all active:scale-[0.98]">
            {isLoading ? (
              <Loader2 className="size-5 animate-spin" />
            ) : (
              <div className="flex items-center gap-2">
                <Send className="size-5" />
                Authorize Transfer
              </div>
            )}
          </Button>
        </div>
      </form>
    </Form>
  );
};

export default PaymentTransferForm;
