# Releasing EmojiSlider

This project ships a Compose Multiplatform library published as Maven coordinates:

```text
com.bernaferrari.emojislider:emojislider:<version>
```

## API notes (CMP 1.0.0)

This is the first Compose Multiplatform release. Maven Central `0.2` is the legacy Android View
widget. `1.0.0` is the new CMP artifact (same coordinates, new major).

Public surface: `EmojiSlider` (`value` / `onValueChange` + `EmojiSliderColors` /
`EmojiSliderBehavior` / `EmojiSliderSizes`), `EmojiSliderParticleSystem`, floating-emoji helpers.

**Removed vs View / early CMP snapshots:**
- `EmojiSliderTooltip`, `EmojiSliderState`, public `TooltipState`
- aliases `progress` / `onProgressChange` / `floatingEmojiDirection`
- per-slider `sliderParticleSystem` lambda — wrap the screen in `EmojiSliderParticleSystem` instead
- flattened color/behavior/size parameters — use the three style types

Do not publish another `1.0.0` if this coordinate is already on Central; bump first.

## Version source of truth

**Library version is defined in** [`emojislider/build.gradle.kts`](../emojislider/build.gradle.kts) inside `mavenPublishing { coordinates(..., version = "…") }`.

Today that value is hardcoded (currently `1.0.0`). Keep it in sync with:

- the git tag you push (`v1.0.0` → version `1.0.0`)
- the version shown in [README.md](../README.md) installation snippet

Do not commit credentials, signing keys, or Sonatype tokens. Use GitHub Actions secrets (or local `~/.gradle/gradle.properties`) only.

## Cut a release (tag → GitHub Release)

1. **Bump the library version** in `emojislider/build.gradle.kts`:

   ```kotlin
   mavenPublishing {
       coordinates(
           groupId = "com.bernaferrari.emojislider",
           artifactId = "emojislider",
           version = "1.0.1", // ← bump this
       )
   }
   ```

2. **Update the README** installation line to the same version.

3. **Commit** on the branch you intend to release (usually `main`):

   ```bash
   git add emojislider/build.gradle.kts README.md
   git commit -m "Release 1.0.1"
   git push origin HEAD
   ```

4. **Tag and push** (annotated tag recommended):

   ```bash
   git tag -a v1.0.1 -m "v1.0.1"
   git push origin v1.0.1
   ```

5. The [Release workflow](../.github/workflows/release.yml) runs on tags matching `v*`:
   - checks out the tag
   - sets up **JDK 17** and the Gradle wrapper cache
   - runs `:emojislider:compileKotlinDesktop`, `:emojislider:desktopJar`, and `:emojislider:desktopTest` (headless; no emulator)
   - optionally tries `:emojislider:compileKotlinWasmJs` (non-blocking)
   - creates a **GitHub Release** with `generate_release_notes: true`

### Manual run

Use **Actions → Release → Run workflow**:

| Input | Purpose |
| --- | --- |
| `tag` | Optional tag (e.g. `v1.0.1`) to check out |
| `dry_run` | Build only; skip GitHub Release and Maven publish |
| `publish` | Attempt Maven Central when secrets are present |

## Maven Central (optional, secrets-gated)

Publishing uses [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin) via:

```bash
./gradlew :emojislider:publishAndReleaseToMavenCentral
```

The workflow job **Publish to Maven Central** is skipped (does not fail the release) when any of these GitHub Actions secrets are missing:

| Secret | Gradle property |
| --- | --- |
| `ORG_GRADLE_PROJECT_mavenCentralUsername` | `mavenCentralUsername` (Sonatype / Central Portal token user) |
| `ORG_GRADLE_PROJECT_mavenCentralPassword` | `mavenCentralPassword` |
| `ORG_GRADLE_PROJECT_signingInMemoryKey` | `signingInMemoryKey` (ASCII-armored GPG private key) |
| `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword` | `signingInMemoryKeyPassword` |

Configure them under **Settings → Secrets and variables → Actions**. Never commit them.

### Local publish (maintainers)

With the same properties in `~/.gradle/gradle.properties` (or env vars with the `ORG_GRADLE_PROJECT_` prefix):

```bash
./gradlew :emojislider:publishToMavenLocal          # local validation, no signing required by this project
./gradlew :emojislider:publishAndReleaseToMavenCentral  # real Central upload + release
```

## Checklist

- [ ] Version bumped in `emojislider/build.gradle.kts` `coordinates { version }`
- [ ] README installation dependency version matches
- [ ] Changes committed and pushed
- [ ] Annotated tag `vX.Y.Z` pushed
- [ ] GitHub Release created by the workflow
- [ ] (Optional) Maven Central secrets set and publish job succeeded
