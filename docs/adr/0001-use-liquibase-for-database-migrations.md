# ADR-0001: Use Liquibase for Database Migrations

## Status

Accepted

## Date

2026-02-22

## Context

The project requires a database migration tool to manage schema changes for the PostgreSQL
database across all environments (local, CI, production). Two migrations tools were evaluated:
**Flyway** and **Liquibase**.

The initial project scaffolding added Flyway (`spring-boot-starter-flyway`) as a transitive
choice, but the first migration file (`V1__create_users_table.sql`) was written as bare SQL
with no migration-tool-specific headers. From `V2` onward, all migration files were authored
with `--liquibase formatted sql` headers and `--changeset` / `--rollback` blocks — meaning
the intent was always Liquibase, but the build wiring was never updated to match.

The `docs/architecture-overview.md` already documents Liquibase as the chosen migration tool.
This ADR formalises that decision and records the rationale.

### Forces

- The codebase is early-stage; only three migration files exist (V1–V3). Migration cost is low.
- Liquibase's SQL-format changelog is human-readable and diff-friendly, same as Flyway's plain SQL.
- Liquibase provides explicit `--rollback` support per changeset, which Flyway only supports in
  its paid tier.
- The architecture documentation and all post-V1 migration files were already written for
  Liquibase — the tooling must be updated to match the intent.
- Spring Boot's Liquibase auto-configuration (`spring-boot-starter-liquibase`) is first-class
  and well-supported under the Spring Boot BOM.

## Decision

Use **Liquibase** (SQL format changelogs) as the sole database migration tool. Flyway is
removed from the project.

Migration files use the `.sql` extension with `--liquibase formatted sql` as the first line,
changesets identified by `--changeset author:slug`, and rollback documented via `--rollback`.

The following four operational rules apply to all changesets:

1. **Keep changesets atomic** — each changeset contains one type of change (e.g. a single
   `CREATE TABLE`) to prevent partial updates and simplify error recovery.
2. **Use consistent naming** — file and changeset ID naming follows the team convention
   defined below; deviations will cause merge conflicts and confuse migration order.
3. **Never modify an applied changeset** — once applied to any shared database, the SQL, `id`,
   and file path are immutable. Modifications cause checksum errors or double-execution.
   Always write a new incremental changeset instead.
4. **Include rollback statements** — every changeset must define its inverse operation via a
   `--rollback` block to enable seamless downgrades without manual SQL.

File naming convention (unchanged from Flyway):
```
V{version}__{description}.sql
```

Liquibase will be configured via `application.properties`:
```properties
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
```

The master changelog (`db.changelog-master.xml`) includes each versioned SQL file in order.

## Alternatives Considered

### Keep Flyway

- ✅ Already wired in `build.gradle` and `application.properties`
- ❌ All post-V1 migration files are already written in Liquibase SQL format — they would need
  to be rewritten or Flyway would silently ignore `--liquibase` comment headers
- ❌ Rollback support requires a paid Flyway Teams licence
- ❌ Conflicts with the existing architecture documentation

### Use a Different Tool (Flyway Community, jOOQ Migrations, etc.)

Not considered — the team has already standardised on Liquibase SQL format in practice.

## Consequences

### Positive

- Build tooling now matches the existing migration files and architecture documentation
- Explicit `--rollback` blocks per changeset are supported without a paid licence, enabling
  seamless downgrades for all change types including complex schema transformations
- `DATABASECHANGELOG` table managed by Liquibase provides checksum verification — applied
  changesets are immutable by design; any modification triggers a startup failure, enforcing
  the "never modify an applied changeset" rule automatically
- Atomic changesets (one change type per changeset) prevent partial updates and make targeted
  rollbacks reliable
- Consistent file and changeset ID naming conventions simplify merges and make migration
  history navigable for new developers
- Master changelog file provides a single source of truth for migration order

### Negative

- `V1__create_users_table.sql` must be wrapped with a Liquibase `--changeset` header since
  it was applied by Flyway without one. The Flyway `flyway_schema_history` table must be
  dropped (or migrated) from any environment where Flyway already ran.
- Integration test `AppIntegrationTest` has two tests named `flyway*` that must be renamed
  to reflect Liquibase.

### Neutral

- No change to the SQL content of migration files — only the tooling wrapper changes
- Spring Boot BOM manages the Liquibase version; no manual version pinning required

## Implementation

Tracked in the following GitHub issues (complete in order):

1. **#50** — Swap Flyway for Liquibase in `build.gradle` and `application.properties`
2. **#51** — Add Liquibase master changelog and convert V1 migration to Liquibase SQL format
3. **#52** — Rename `flyway*` tests in `AppIntegrationTest` to reflect Liquibase
4. **#53** — Update `architecture-overview.md` and `README.md` to remove Flyway references

## References

- [Liquibase SQL Format Changelogs](https://docs.liquibase.com/concepts/changelogs/sql-format.html)
- [Spring Boot Liquibase Auto-configuration](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.liquibase)
- `docs/architecture-overview.md` — Database Strategy section
