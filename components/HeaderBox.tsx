const HeaderBox = ({
  type = 'title',
  title,
  subtext,
  user,
}: HeaderBoxProps) => {
  return (
    <div className="flex flex-col gap-1 pb-6 border-b border-slate-200/60 mb-8">
      <h1 className="text-3xl lg:text-4xl font-bold tracking-tight text-slate-900">
        {title}
        {type === 'greeting' && (
          <span className="text-primary ml-2">&nbsp;{user}</span>
        )}
      </h1>
      <p className="text-base lg:text-lg font-medium text-slate-500 max-w-[600px]">
        {subtext}
      </p>
    </div>
  );
};

export default HeaderBox;
