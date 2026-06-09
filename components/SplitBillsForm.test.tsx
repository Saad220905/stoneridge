import { render, screen, fireEvent } from '@testing-library/react';
import SplitBillsForm from './SplitBillsForm';

// Mock useRouter and useSearchParams from next/navigation
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
  }),
  useSearchParams: () => ({
    toString: () => '',
  }),
}));

// Mock API library methods
jest.mock('@/lib/split-bills', () => ({
  createBillSplit: jest.fn(),
  payBillSplit: jest.fn(),
  getCreatedSplits: jest.fn(),
}));

// Mock utils helper to avoid query-string ES module import issues in Jest
jest.mock('@/lib/utils', () => ({
  formatAmount: (amount: number) => `$${amount.toFixed(2)}`,
  cn: (...inputs: any[]) => inputs.filter(Boolean).join(' '),
  formUrlQuery: jest.fn(),
}));

describe('SplitBillsForm', () => {
  const mockAccounts = [
    {
      id: 'bank-1',
      databaseId: 1,
      accountId: 'acc-1',
      bankId: 'bank-1',
      accessToken: 'token-1',
      fundingSourceUrl: 'url-1',
      userId: 'user-1',
      shareableId: 'share-1',
      appwriteItemId: 'item-1',
      mask: '0001',
      name: 'Test Bank 1',
      currentBalance: 5000,
    },
  ];

  it('renders the form elements in the split bill tab', () => {
    render(
      <SplitBillsForm
        accounts={mockAccounts}
        initialPending={[]}
        initialCreated={[]}
        loggedInUserEmail="user@example.com"
      />,
    );

    expect(screen.getByText('Bill Split details')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Dinner, utilities, cab, etc.')).toBeInTheDocument();
    expect(screen.getAllByPlaceholderText('0.00')[0]).toBeInTheDocument();
    expect(screen.getByText('Split Equally')).toBeInTheDocument();
    expect(screen.getByText('Initiate Bill Split')).toBeInTheDocument();
  });

  it('allows adding and removing dynamic participant inputs', () => {
    render(
      <SplitBillsForm
        accounts={mockAccounts}
        initialPending={[]}
        initialCreated={[]}
        loggedInUserEmail="user@example.com"
      />,
    );

    // Initial has 1 participant email input
    const inputsBefore = screen.getAllByPlaceholderText('friend@email.com');
    expect(inputsBefore.length).toBe(1);

    // Click Add Another Participant
    const addButton = screen.getByText('Add Another Participant');
    fireEvent.click(addButton);

    // Should now have 2 participant email inputs
    const inputsAfter = screen.getAllByPlaceholderText('friend@email.com');
    expect(inputsAfter.length).toBe(2);
  });

  it('calculates equal splits correctly when Split Equally is clicked', () => {
    render(
      <SplitBillsForm
        accounts={mockAccounts}
        initialPending={[]}
        initialCreated={[]}
        loggedInUserEmail="user@example.com"
      />,
    );

    // Enter total amount: 90
    const totalAmountInput = screen.getAllByPlaceholderText('0.00')[0];
    fireEvent.change(totalAmountInput, { target: { value: '90.00' } });

    // Add another participant (making it 2 participants + 1 creator = 3 people total)
    const addButton = screen.getByText('Add Another Participant');
    fireEvent.click(addButton);

    // Click Split Equally
    const splitButton = screen.getByText('Split Equally');
    fireEvent.click(splitButton);

    // 90 / 3 = 30 for each participant
    const shareInputs = screen.getAllByPlaceholderText('0.00');
    // First input is totalAmount (90.00)
    expect((shareInputs[0] as HTMLInputElement).value).toBe('90.00');
    // Second and third inputs are participant shares (should be 30.00 each)
    expect((shareInputs[1] as HTMLInputElement).value).toBe('30.00');
    expect((shareInputs[2] as HTMLInputElement).value).toBe('30.00');
  });
});
