## Review Focus

Review only what the diff changes.

- **Correctness** — behaviour that contradicts the intended behavior, logic errors and unhandled edge cases.
- **Resource handling** — streams, files, and native handles released on every path, including the
  exception path.
- **Security** - flag code that exposes the system to attack, including malformed or malicious
  PDF/image/font/XML input, decompression bombs, XXE, SSRF, path traversal, temp file misuse,
  weak cryptography, signature validation flaws, content spoofing (manipulated metadata, hidden
  text, overlapping layers), and sensitive data in logs or error messages.
- **Performance** — complexity on paths that run per page, object, or row; allocations in loops;
  queries issued per item instead of in a batch.
- **Public API surface** — the closest reading in the diff, because it is hardest to take back:
  breaking signature or behaviour changes, naming that fits the surrounding API, docs on public and
  protected members.
- **Project configurations** — review any change to them and describe what it affects.
- **Design** — separation of concerns, error-handling strategy, duplication. Push back on an
  abstraction with one caller, an option nobody asked for, generality for a case that has not
  happened.
- **Comments** — those that say what instead of why, that restate the code, or that talk about the
  change ("fixed", "no longer needed"); and docs the change has made wrong.
- **Thread safety** - Flag unsynchronized shared mutable state, non-thread-safe types reused
  across threads, race conditions, deadlock risk, and async/thread-pool misuse that can cause
  wrong results or data leaking between callers. PdfDocument and other single-document
  processing contexts are not meant to be shared across threads, so only flag static state
  and shared configuration/utility objects passed into individual document processing contexts.

## Non-Negotiable Rules

A violation here blocks the merge.

- No sensitive customer data anywhere: test resources, string literals, logs, commit messages.
- No hardcoded credentials, keys, or tokens — tests included.
- No commented-out code. Work in progress belongs on its own branch.
- A new dependency or embedded third-party code is under a license compatible with the
  repository’s own. Check the repository’s LICENSE before approving one.
- A new third-party resource ships with its full license text plus a NOTICE entry naming the
  resource, its SPDX identifier, the license file, and the exact source.

## Test Conventions

New behaviour arrives with tests; a bug fix arrives with a test that fails without it. If a change
has neither, ask for tests or ask why they are not feasible. Tests must be deterministic — no
dependence on clock, locale, time zone, network, or execution order — and must assert the behaviour,
not just that the code ran. They must follow the repository's existing naming and placement
conventions, since a deviating test may silently never run. Where behaviour is wrong but out of
scope, pin the current behaviour with a TODO rather than ignoring the test. Coverage should not go
down; judge the risk in what is untested, not the number.

## Do Not Comment On

Formatting and anything a formatter, linter, or analyzer enforces. Code the pull request does not touch.

## Reporting

Comment only to ask for a change. Mark each one **🔴 Critical** (must fix before merge) or **🟡 Suggestion**. State the
problem, not the topic, and give the fix with corrected code when you know it. Close with a short
summary: what the change does, what it breaks, whether coverage is adequate, any blocking question,
and either "ready to merge" or the one thing stopping it.