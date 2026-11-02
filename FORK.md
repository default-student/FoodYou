# Food You Fork

This is an unofficial fork of [Food You](https://github.com/maksimowiczm/FoodYou), licensed under GPL-3.0-or-later. It retains the upstream source and attribution and adds a launcher widget for opening the existing quick meal-entry form.

The widget opens the app's existing quick-add form and chooses the meal whose configured time range matches the current local time. If none matches, it falls back to the most recently started meal or the first meal. Saving uses Food You's existing diary repository and does not create placeholder entries.

## Upstream updates and releases

`.github/workflows/fork-release.yml` checks hourly for a newer stable upstream tag. It merges that tag into `main`, updates `.github/upstream-release-tag` only after the merge succeeds, and pushes only to this fork. Merge conflicts stop the run before a push. A successful merge is built and published as a fork-only release such as `v3.4.9-fork.123`.

The same workflow publishes a signed APK for pushes to `main` and manual runs. Fork release version codes combine the upstream Android version code with the workflow run number, so new releases remain above that upstream version. The package ID is `com.defaultstudent.foodyou`, allowing the fork to install beside upstream Food You. The fork has its own signing identity; APKs from the upstream project cannot update this fork and vice versa.

Before enabling Actions or pushing the initial `main` branch, add these repository Actions secrets for the fork's stable release key:

- `FOODYOU_KEYSTORE_BASE64`: base64-encoded fork keystore
- `FOODYOU_KEYSTORE_PASSWORD`: keystore password
- `FOODYOU_KEY_PASSWORD`: key password

The key alias is fixed in the workflow as `foodyou-fork-release`. The local `foodyou-fork-release.jks` file uses PKCS12 format, whose private key uses the store password. Set both password secrets to that same value. Validate signing with `apksigner`; `keytool -list` only verifies that the store can be opened and does not prove that the configured private-key password works.

Keep the keystore and passwords private and backed up. Never replace the key after publishing, or Android will reject updates to the installed fork. Keep GitHub Actions enabled with workflow permission to write contents and releases.

To upload the ignored local signing files with GitHub CLI, run these commands from the repository root in a shell with `gh` authenticated to the fork owner. They send values through standard input and do not print them:

```bash
set -a
. ./keystore.properties
set +a
base64 -w 0 foodyou-fork-release.jks | gh secret set FOODYOU_KEYSTORE_BASE64 --repo default-student/FoodYou
printf '%s' "$FOODYOU_KEYSTORE_PASSWORD" | gh secret set FOODYOU_KEYSTORE_PASSWORD --repo default-student/FoodYou
printf '%s' "$FOODYOU_KEY_PASSWORD" | gh secret set FOODYOU_KEY_PASSWORD --repo default-student/FoodYou
unset FOODYOU_KEYSTORE_PASSWORD FOODYOU_KEY_PASSWORD
```

The release workflow grants `contents: write` only to its release job and never pushes to the upstream repository. Keep the repository's default Actions token permission read-only; the workflow requests the write scope it needs for upstream pushes and Releases. An organization policy that blocks that job-level permission will prevent publishing. Its scheduled job tracks stable `x.y.z` tags only; prereleases and the `develop` branch are not merged automatically.
