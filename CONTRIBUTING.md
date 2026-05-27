# Contributing to NotifyFlow

Thanks for helping improve NotifyFlow. This project is a backend infrastructure project for notification and event processing, so changes should be clear, tested, and easy to operate.

## Ways to Contribute

- Report bugs with enough detail to reproduce the issue.
- Suggest focused improvements through feature requests.
- Improve documentation, examples, tests, or developer experience.
- Submit small, reviewable pull requests.

## Before You Start

1. Check existing issues and pull requests to avoid duplicate work.
2. Open an issue for larger changes before implementing them.
3. Keep pull requests focused on one concern.
4. Avoid unrelated formatting or refactoring in feature and bug-fix PRs.

## Local Development

The backend lives in `notifyflow-api`.

```powershell
cd notifyflow-api
.\mvnw test
.\mvnw spring-boot:run
```

Use Java 21. If Docker Compose is enabled for the application, make sure the local services required by Spring Boot are available before running the app.

## Branch Naming

Use short, descriptive branch names:

- `feature/notification-scheduler`
- `fix/status-filter-404`
- `docs/contributing-guide`
- `chore/update-dependencies`

## Commit Messages

Use Conventional Commits. This keeps project history readable and makes future changelogs easier.

Format:

```text
type(scope): short summary
```

Common types:

- `feat`: user-facing feature
- `fix`: bug fix
- `docs`: documentation only
- `test`: test changes only
- `refactor`: code change without behavior change
- `chore`: maintenance, tooling, dependency updates
- `ci`: CI/CD changes

Examples:

```text
feat(api): add notification creation endpoint
fix(scheduler): retry failed provider calls
docs: add pull request template
test(service): cover notification status transitions
```

Keep the summary in the imperative mood, such as "add", "fix", or "update". Add a body when the reason for the change is not obvious.