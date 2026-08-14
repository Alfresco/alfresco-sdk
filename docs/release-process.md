# Alfresco SDK release process

This document describes how to release the Alfresco SDK using the automated CI pipeline introduced in [ACS-12085](https://hyland.atlassian.net/browse/ACS-12085).

Releases are performed on **`master`** via GitHub Actions. The pipeline uses [`maven-release-slim`](https://github.com/Alfresco/alfresco-build-tools) from Alfresco build-tools and a GitHub App installation token to create **verified** commits and tags.

## Version conventions

### Alpha versions (dot notation)

Alpha releases **must** use a dot between `A` and the alpha number:

| Correct | Incorrect |
|---------|-----------|
| `4.17.0-A.1` | `4.17.0-A1` |
| `4.17.0-A.2` | `4.17.0-A2` |

Use this format in `RELEASE_VERSION`, Git tags, and Maven coordinates for all new alpha releases.

### Next development version (`-SNAPSHOT`)

`DEVELOPMENT_VERSION` **must** include the `-SNAPSHOT` suffix. This is the version written to all POMs after the release completes.

| Correct | Incorrect |
|---------|-----------|
| `4.17.0-A.3-SNAPSHOT` | `4.17.0-A.3` |
| `4.18.0-SNAPSHOT` | `4.18.0` |

This applies to both alpha and GA release cycles.

## Overview

| Step | Trigger | CI job | Result |
|------|---------|--------|--------|
| Alpha / Nexus release | `[release]` in commit message | `release` | Deploy to Alfresco Nexus, Git tag, verified bot commits |
| GA Maven Central publish | `[publish]` in commit message | `publish` | Publish to Maven Central (GA versions only) |

Alpha releases (for example `4.17.0-A.2`) are deployed to **Nexus only**. GA releases (for example `4.17.0`) can additionally be published to **Maven Central** using `[publish]`.

## Prerequisites

- Merge your changes to **`master`** (or use a PR merged to `master`).
- Ensure CI tests pass. Do not use `[skip tests]` on a release commit.
- Confirm the repository has the `GH_APP_ENGINEERING_CONTRIB_CLIENT_ID` variable and `GH_APP_ENGINEERING_CONTRIB_PRIVATE_KEY` secret configured (DevOps).
- Protected branches must allow verified commits from the engineering-contrib GitHub App.

## Configure release versions

Version numbers are **not** stored in `.github/workflows/ci.yml`. They live in [`.github/release-versions.yml`](../.github/release-versions.yml):

```yaml
env:
  global:
    - RELEASE_VERSION=4.17.0-A.2
    - DEVELOPMENT_VERSION=4.17.0-A.3-SNAPSHOT
```

| Variable | Purpose | Example (alpha) | Example (GA) |
|----------|---------|-----------------|--------------|
| `RELEASE_VERSION` | Version to release and tag | `4.17.0-A.2` | `4.17.0` |
| `DEVELOPMENT_VERSION` | Next development version written to POMs after release (must end with `-SNAPSHOT`) | `4.17.0-A.3-SNAPSHOT` | `4.18.0-SNAPSHOT` |

Update both values in the **same commit** that triggers the release. The GitHub App bot does **not** modify this file; you must update it again before the next release.

> **Note:** Release versions are kept in `.github/release-versions.yml` (not in workflow files) so the GitHub App token does not require `workflows: write` permission.

## Alpha release (Nexus)

Example: release `4.17.0-A.3` when `master` is on `4.17.0-A.3-SNAPSHOT`.

1. Update [`.github/release-versions.yml`](../.github/release-versions.yml):

   ```yaml
   - RELEASE_VERSION=4.17.0-A.3
   - DEVELOPMENT_VERSION=4.17.0-A.4-SNAPSHOT
   ```

2. Commit and push to **`master`** with `[release]` in the commit message, for example:

   ```text
   [release] Alfresco SDK 4.17.0-A.3 alpha
   ```

3. Wait for the CI workflow to finish. The `release` job runs only when:
   - Tests succeed
   - The branch is `master`, `fix/**`, or `feature/**`
   - The commit message contains `[release]`
   - The commit message does **not** contain `[no release]`

### What happens automatically

When the `release` job succeeds, CI will:

1. Set all POM versions to `RELEASE_VERSION`
2. Deploy artifacts to Alfresco Nexus
3. Create a **verified** bot commit for the release version
4. Create a Git tag named exactly `RELEASE_VERSION` (for example `4.17.0-A.3`)
5. Set all POM versions to `DEVELOPMENT_VERSION`
6. Create a **verified** bot commit for the next development version

You should **not** manually create Git tags or manually bump POM versions for the release and post-release commits; the pipeline handles that.

### After an alpha release

On `master` you should see:

- Git tag: `4.17.0-A.3` (plain version string from `RELEASE_VERSION`)
- Two new **Verified** commits from the engineering-contrib bot
- Root POM version: `4.17.0-A.4-SNAPSHOT`
- Artifacts on Alfresco Nexus for `4.17.0-A.3`

## GA release and Maven Central

For a GA release (version matching `major.minor.patch` with no suffix):

1. Update [`.github/release-versions.yml`](../.github/release-versions.yml), for example:

   ```yaml
   - RELEASE_VERSION=4.17.0
   - DEVELOPMENT_VERSION=4.18.0-SNAPSHOT
   ```

2. Push to **`master`** with both keywords in the **same** commit message:

   ```text
   [release][publish] Alfresco SDK 4.17.0
   ```

   To publish to Maven Central as part of the GA release, include `[publish]` in the **same** commit message as `[release]` so the workflow can publish the tagged `RELEASE_VERSION`.

   A standalone `[publish]` commit (without `[release]`) will publish only if the POM version on that commit is already GA. After a release run, `master` is typically bumped to `DEVELOPMENT_VERSION` (`-SNAPSHOT`), so a later `[publish]` commit will be skipped by `check_version`.

The `check_version` job verifies the version matches `^\d+\.\d+\.\d+$` before Maven Central publish runs. Alpha, SNAPSHOT, RC, and other suffixed versions are skipped.

## Commit message keywords

| Keyword | Effect |
|---------|--------|
| `[release]` | Run the automated Nexus release |
| `[publish]` | Attempt Maven Central publish (GA only) |
| `[no release]` | Skip the release job even if `[release]` would otherwise match |
| `[skip tests]` | Skip tests (do **not** use on release commits) |

## Troubleshooting

| Symptom | Likely cause |
|---------|----------------|
| Release job skipped | No `[release]` in commit message, wrong branch, or tests failed/skipped |
| App token step fails | GitHub App credentials not configured on the repository |
| Verified commit rejected | Branch protection or App permissions |
| Tag already exists | `RELEASE_VERSION` was released before |
| Maven Central skipped | Non-GA version, or missing `[publish]` |

## Related files

- [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) — CI pipeline
- [`.github/release-versions.yml`](../.github/release-versions.yml) — release version configuration
- [Alfresco build-tools `maven-release-slim`](https://github.com/Alfresco/alfresco-build-tools)
