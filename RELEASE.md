# Alfresco SDK Release Process

This document describes the process for creating a new release of the Alfresco SDK.

## Overview

The Alfresco SDK uses a semi-automated release process that combines:
- Manual version changes in the POM files
- Automated publishing to Maven Central via GitHub Actions
- Manual creation of GitHub release tags
- Manual version bump to the next SNAPSHOT

## Prerequisites

Before starting a release, ensure you have:

1. **Permissions**: Write access to the Alfresco/alfresco-sdk repository
2. **All tests passing**: The CI pipeline should be green on the master branch
3. **Credentials configured** (for maintainers):
   - Maven Central credentials (OSS Sonatype)
   - GPG signing key for artifact signing
   - These are configured as GitHub secrets:
     - `OSS_SONATYPE_USERNAME`
     - `OSS_SONATYPE_PASSWORD`
     - `GPG_SIGNING_PRIVATE_KEY`
     - `GPG_SIGNING_PASSPHRASE`

## Release Steps

### Step 1: Update Version from SNAPSHOT to Release

1. **Update the version** in all `pom.xml` files from SNAPSHOT to the final release version.
   
   Example: When releasing SDK 4.12.0, replace all occurrences of `4.12.0-SNAPSHOT` with `4.12.0`

   Files to update:
   - `/pom.xml` (root aggregator)
   - `/modules/alfresco-rad/pom.xml`
   - `/plugins/alfresco-maven-plugin/pom.xml`
   - `/archetypes/alfresco-platform-jar-archetype/pom.xml`
   - `/archetypes/alfresco-share-jar-archetype/pom.xml`
   - `/archetypes/alfresco-allinone-archetype/pom.xml`
   - `/archetypes/archetypes-it/pom.xml`

   You can use this command to find all pom.xml files that need updating:
   ```bash
   find . -name "pom.xml" -not -path "*/target/*" -not -path "*/.git/*"
   ```

2. **Commit the changes** with the commit message `[publish]`
   
   The `[publish]` keyword is important as it triggers the GitHub Actions release workflow.

   ```bash
   git add .
   git commit -m "[publish] Release version 4.12.0"
   git push origin master
   ```

### Step 2: Automated Publishing

Once you push the commit with `[publish]` in the message:

1. The **GitHub Actions CI workflow** (`.github/workflows/ci.yml`) will automatically:
   - Run all tests to ensure everything passes
   - Check if the version is a GA (General Availability) release (format: `X.Y.Z`)
   - If it's a GA version:
     - Build the artifacts
     - Sign them with GPG
     - Publish to Maven Central using the `central-publishing-maven-plugin`

2. **Monitor the workflow**:
   - Go to: https://github.com/Alfresco/alfresco-sdk/actions
   - Check the "Alfresco SDK CI" workflow
   - Ensure all jobs complete successfully, especially the "Publish artifacts" job

3. **Verify publication**:
   - Check Maven Central: https://repo1.maven.org/maven2/org/alfresco/maven/
   - Note: It may take a few minutes for artifacts to appear on Maven Central after publication

### Step 3: Create GitHub Release Tag

After successful publication to Maven Central:

1. **Navigate to GitHub Releases**:
   - Go to: https://github.com/Alfresco/alfresco-sdk/releases

2. **Create a new release**:
   - Click "Draft a new release"
   - Tag version: Use the format `sdk-X.Y.Z` (e.g., `sdk-4.12.0`)
   - Target: `master` branch
   - Release title: `Alfresco SDK X.Y.Z` (e.g., `Alfresco SDK 4.12.0`)
   
3. **Write release notes**:
   - Include a summary of new features, improvements, and bug fixes
   - Reference important pull requests and issues
   - Mention supported Alfresco versions
   - Include any breaking changes or upgrade notes

4. **Publish the release**

### Step 4: Bump Version to Next SNAPSHOT

After creating the GitHub release:

1. **Update version** to the next SNAPSHOT version in all `pom.xml` files
   
   Example: After releasing 4.12.0, update to `4.13.0-SNAPSHOT`
   
   Note: Only update `pom.xml` files, not README documentation (unless the documentation needs updating for other reasons)

2. **Commit and push**:
   ```bash
   git add .
   git commit -m "Bump version to 4.13.0-SNAPSHOT"
   git push origin master
   ```

### Step 5: Update Documentation (if needed)

If the release requires documentation updates:

1. Update the **News** section in `README.md` with the new release
2. Update the **Documentation about Previous Versions** table if necessary
3. Update any other relevant documentation

## Release Checklist

Use this checklist when performing a release:

- [ ] All tests are passing on master branch
- [ ] Version updated from SNAPSHOT to release version in all pom.xml files
- [ ] Committed and pushed with `[publish]` message
- [ ] GitHub Actions workflow completed successfully
- [ ] Artifacts verified on Maven Central
- [ ] GitHub release tag created (`sdk-X.Y.Z`)
- [ ] Release notes published on GitHub
- [ ] Version bumped to next SNAPSHOT in all pom.xml files
- [ ] Documentation updated (if necessary)

## Understanding the CI/CD Pipeline

The release automation is defined in `.github/workflows/ci.yml`:

### Key Workflow Jobs:

1. **pre_commit**: Runs pre-commit checks
2. **build**: Builds the application and runs verification
3. **tests**: Runs integration tests against multiple Alfresco versions
4. **check_version**: Triggered when commit message contains `[publish]`
   - Validates if version is a GA release (X.Y.Z format)
   - Non-GA versions (like SNAPSHOT, RC, etc.) are not published
5. **publish**: Triggered after successful tests when version is GA
   - Builds artifacts with source and javadoc
   - Signs artifacts with GPG
   - Publishes to Maven Central using Central Publishing Maven Plugin

### Maven Profile: `sdk-release`

The release process uses the `sdk-release` Maven profile defined in the root `pom.xml`, which configures:

- **central-publishing-maven-plugin**: For publishing to Maven Central
- **maven-source-plugin**: Attaches source JARs
- **maven-javadoc-plugin**: Attaches Javadoc JARs
- **maven-gpg-plugin**: Signs artifacts
- **maven-release-plugin**: Release management

## Troubleshooting

### Release workflow not triggering

- Ensure commit message contains `[publish]`
- Check that version in pom.xml is in GA format (X.Y.Z, no SNAPSHOT, RC, etc.)
- Verify all tests pass

### GPG signing failures

- Check that `GPG_SIGNING_PRIVATE_KEY` and `GPG_SIGNING_PASSPHRASE` secrets are correctly configured
- Ensure GPG key is valid and not expired

### Maven Central publication failures

- Verify `OSS_SONATYPE_USERNAME` and `OSS_SONATYPE_PASSWORD` credentials are valid
- Check that artifacts meet Maven Central requirements (sources, javadocs, POM metadata)
- Review the Central Publishing Maven Plugin documentation for requirements

### Version conflicts

- Ensure all module pom.xml files have consistent version numbers
- Verify parent version references match

## Additional Resources

- [Maven Central Publishing](https://central.sonatype.org/publish/)
- [Central Publishing Maven Plugin](https://central.sonatype.org/publish/publish-portal-maven/)
- [GPG Signing](https://central.sonatype.org/publish/requirements/gpg/)
- [Alfresco SDK Developer Wiki](https://github.com/Alfresco/alfresco-sdk/wiki/Developer-Wiki)

## Contact

For questions about the release process:
- Open an issue on [GitHub](https://github.com/Alfresco/alfresco-sdk/issues)
- Contact the Alfresco SDK maintainers
