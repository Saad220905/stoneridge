import { FormControl, FormField, FormLabel, FormMessage } from './ui/form';
import { Input } from './ui/input';
import { Control, FieldPath } from 'react-hook-form';
import { z } from 'zod';
import { authFormSchema } from '@/lib/utils';
import { cn } from '@/lib/utils';

const formSchema = authFormSchema('sign-up');

interface CustomInputProps {
  control: Control<z.infer<typeof formSchema>>;
  name: FieldPath<z.infer<typeof formSchema>>;
  label: string;
  placeholder: string;
}

const CustomInput = ({ control, name, label, placeholder }: CustomInputProps) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({ field }) => (
        <div className="flex flex-col gap-2">
          <FormLabel className="text-sm font-bold text-slate-700 ml-1 italic tracking-tight">{label}</FormLabel>
          <div className="flex w-full flex-col">
            <FormControl>
              <Input
                placeholder={placeholder}
                className="h-11 rounded-xl border-slate-200 bg-slate-50/50 px-4 text-sm font-medium transition-all focus:border-primary/30 focus:bg-white focus:ring-4 focus:ring-primary/5"
                type={name === 'password' ? 'password' : 'text'}
                {...field}
              />
            </FormControl>
            <FormMessage className="text-[10px] font-bold text-red-500 mt-1 ml-1" />
          </div>
        </div>
      )}
    />
  );
};

export default CustomInput;
