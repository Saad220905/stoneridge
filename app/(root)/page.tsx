import HeaderBox from '@/components/HeaderBox';
import RecentTransactions from '@/components/RecentTransactions';
import RightSidebar from '@/components/RightSidebar';
import TotalBalanceBox from '@/components/TotalBalanceBox';
import GoalProgress from '@/components/GoalProgress';
import { getBanks } from '@/lib/plaid';
import { getLoggedInUser } from '@/lib/auth';
import { getTransactions } from '@/lib/transactions';
import { getGoals } from '@/lib/goals';
import { redirect } from 'next/navigation';

const Home = async ({ searchParams }: SearchParamProps) => {
  const { id, page } = await searchParams;
  const currentPage = Number(page as string) || 1;
  const loggedIn = await getLoggedInUser();

  if (!loggedIn) redirect('/sign-in');

  const accounts = await getBanks();

  if (!accounts) return;

  const accountsData = accounts;
  const appwriteItemId = (id as string) || accountsData[0]?.appwriteItemId;

  const account = accountsData.find((acc: Bank) => acc.appwriteItemId === appwriteItemId);

  // Fetch transactions using the new client
  const transactions = await getTransactions(currentPage, 10, account?.appwriteItemId); 

  // Fetch goals
  const goals = await getGoals();

  return (
    <section className="home">
      <div className="home-content">
        <header className="home-header">
          <HeaderBox
            type="greeting"
            title="Welcome"
            user={loggedIn?.firstName || 'Guest'}
            subtext="Access and manage your account and transactions efficiently."
          />

          <TotalBalanceBox
            accounts={accountsData}
            totalBanks={accountsData.length}
            totalCurrentBalance={accountsData.reduce((acc: number, cur: Bank) => acc + cur.currentBalance, 0)}
          />
        </header>

        <div className="mt-8">
            <GoalProgress goals={goals} />
        </div>

        <RecentTransactions
          accounts={accountsData}
          transactions={transactions}
          appwriteItemId={appwriteItemId}
          page={currentPage}
        />
      </div>

      <RightSidebar
        user={loggedIn}
        transactions={transactions}
        banks={accountsData?.slice(0, 2)}
      />
    </section>
  );
};

export default Home;
