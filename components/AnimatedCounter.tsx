'use client';

import CountUp from 'react-countup';

const AnimatedCounter = ({ amount }: { amount: number }) => {
  return (
    <span className="font-ibm-plex-serif font-black tracking-tighter">
      <CountUp 
        decimals={2} 
        decimal="." 
        prefix="$" 
        end={amount} 
        duration={2}
      />
    </span>
  );
};

export default AnimatedCounter;
