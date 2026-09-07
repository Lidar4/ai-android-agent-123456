export default function Page() {
  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto flex min-h-screen max-w-4xl flex-col items-center justify-center px-6 text-center">
        <div className="mb-6 rounded-2xl border bg-card p-5 shadow-sm">
          <svg aria-hidden="true" width="56" height="56" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 3v4M12 17v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M3 12h4M17 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" />
            <circle cx="12" cy="12" r="4" stroke="currentColor" strokeWidth="1.7" />
          </svg>
        </div>
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl">AI Android Agent</h1>
        <p className="mt-4 max-w-2xl text-balance text-muted-foreground sm:text-lg">
          Your Android AI agent workspace is ready. Connect the agent runtime and tools here.
        </p>
        <div className="mt-8 flex flex-wrap justify-center gap-3 text-sm">
          <span className="rounded-full border px-4 py-2">Agent Core</span>
          <span className="rounded-full border px-4 py-2">Android Tools</span>
          <span className="rounded-full border px-4 py-2">Task Automation</span>
        </div>
      </section>
    </main>
  )
}
