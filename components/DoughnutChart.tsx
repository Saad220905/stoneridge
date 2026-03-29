'use client';

import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

const DoughnutChart = ({ accounts }: DoughnutChartProps) => {
  const balances = accounts.map((a) => a.currentBalance);

  const data = {
    datasets: [
      {
        label: 'Banks',
        data: balances,
        backgroundColor: ['#0179FE', '#4893FF', '#85B7FF'],
        borderWidth: 0,
        hoverOffset: 4,
      },
    ],
    labels: accounts.map((a) => a.name),
  };

  return (
    <Doughnut
      data={data}
      options={{
        cutout: '75%',
        plugins: {
          legend: {
            display: false,
          },
          tooltip: {
            backgroundColor: '#1e293b',
            padding: 12,
            titleFont: { size: 14, weight: 'bold' },
            bodyFont: { size: 13 },
            displayColors: false,
          }
        },
      }}
    />
  );
};

export default DoughnutChart;
