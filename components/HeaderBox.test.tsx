import { render, screen } from '@testing-library/react';
import HeaderBox from './HeaderBox';

describe('HeaderBox', () => {
  it('renders the title and subtext', () => {
    render(
      <HeaderBox type="title" title="Test Title" subtext="Test Subtext" />,
    );

    expect(screen.getByText('Test Title')).toBeInTheDocument();
    expect(screen.getByText('Test Subtext')).toBeInTheDocument();
  });

  it('renders the greeting with the user name', () => {
    render(
      <HeaderBox
        type="greeting"
        title="Hello"
        subtext="Welcome"
        user="Guest"
      />,
    );

    expect(screen.getByText(/Hello/)).toBeInTheDocument();
    expect(screen.getByText(/Guest/)).toBeInTheDocument();
  });
});
