# Publishing

EmojiSlider publishes the `:emojislider` Kotlin Multiplatform library to Maven Central through the Central Portal. The app and example modules are not published.

## Coordinates

```text
com.bernaferrari.emojislider:emojislider:1.0.0
```

Target-specific KMP artifacts are created by Gradle from the same base coordinates.

## Required setup

Create a Sonatype Central Portal account, register the `com.bernaferrari.emojislider` namespace, and generate a Central Portal user token. Maven Central also requires signed release artifacts, so export an ASCII-armored GPG private key.

Keep credentials out of the repository. Put them in `~/.gradle/gradle.properties` or expose them as environment variables:

```properties
mavenCentralUsername=...
mavenCentralPassword=...
signingInMemoryKey=...
signingInMemoryKeyId=...
signingInMemoryKeyPassword=...
```

For CI, use the matching Gradle environment variable names:

```sh
ORG_GRADLE_PROJECT_mavenCentralUsername=...
ORG_GRADLE_PROJECT_mavenCentralPassword=...
ORG_GRADLE_PROJECT_signingInMemoryKey=...
ORG_GRADLE_PROJECT_signingInMemoryKeyId=...
ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=...
```

## Commands

Validate the local publications:

```sh
./gradlew :emojislider:publishToMavenLocal
```

Upload a release deployment to the Central Portal:

```sh
./gradlew :emojislider:publishToMavenCentral
```

After validation succeeds, publish the deployment manually in the Central Portal. To upload and release in one Gradle run, use:

```sh
./gradlew :emojislider:publishAndReleaseToMavenCentral
```
