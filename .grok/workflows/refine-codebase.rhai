let meta = #{
    name: "refine-codebase",
    description: "Mature the Compose Multiplatform EmojiSlider library: split the 813-line main component, add tests, GitHub Actions CI, and tag/release automation",
    when_to_use: "After the Compose rewrite and live demo are solid; when the library needs tests, CI, releases, and a smaller main component",
    phases: [
        #{ title: "Survey", detail: "Map EmojiSlider.kt seams, test gaps, CI and release needs" },
        #{ title: "Split component", detail: "Extract draw helpers, tooltip, state, and public API from the 813-line file" },
        #{ title: "Harden", detail: "Parallel: unit tests, GitHub Actions CI, tag/release workflow, light polish" },
        #{ title: "Verify", detail: "Build, run tests, adversarially check each deliverable" },
        #{ title: "Report", detail: "Write a run report of what changed and what remains" },
    ],
};

// --- schemas ---
let survey_schema = #{
    "type": "object",
    "required": ["summary", "split_plan", "test_plan", "ci_plan", "release_plan", "risks"],
    "properties": #{
        "summary": #{ "type": "string" },
        "split_plan": #{ "type": "array", "items": #{ "type": "string" }, "maxItems": 12 },
        "test_plan": #{ "type": "array", "items": #{ "type": "string" }, "maxItems": 12 },
        "ci_plan": #{ "type": "array", "items": #{ "type": "string" }, "maxItems": 8 },
        "release_plan": #{ "type": "array", "items": #{ "type": "string" }, "maxItems": 8 },
        "risks": #{ "type": "array", "items": #{ "type": "string" }, "maxItems": 8 },
        "emoji_slider_lines": #{ "type": "integer" },
        "existing_test_files": #{ "type": "array", "items": #{ "type": "string" } },
        "has_github_actions": #{ "type": "boolean" },
    },
};

let implement_schema = #{
    "type": "object",
    "required": ["done", "summary", "files_changed", "remaining"],
    "properties": #{
        "done": #{ "type": "boolean" },
        "summary": #{ "type": "string" },
        "files_changed": #{ "type": "array", "items": #{ "type": "string" } },
        "remaining": #{ "type": "string" },
        "notes": #{ "type": "string" },
        "metrics": #{ "type": "object" },
    },
};

let verify_schema = #{
    "type": "object",
    "required": ["build_ok", "tests_ok", "summary", "evidence", "failures"],
    "properties": #{
        "build_ok": #{ "type": "boolean" },
        "tests_ok": #{ "type": "boolean" },
        "summary": #{ "type": "string" },
        "evidence": #{ "type": "string" },
        "failures": #{ "type": "array", "items": #{ "type": "string" } },
        "commands_run": #{ "type": "array", "items": #{ "type": "string" } },
    },
};

let verdict_schema = #{
    "type": "object",
    "required": ["claim", "real", "reason", "evidence"],
    "properties": #{
        "claim": #{ "type": "string" },
        "real": #{ "type": "boolean" },
        "reason": #{ "type": "string" },
        "evidence": #{ "type": "string" },
    },
};

// Guard args: unit when omitted.
let skip_split = if args != () && args.skip_split != () { args.skip_split } else { false };
let skip_tests = if args != () && args.skip_tests != () { args.skip_tests } else { false };
let skip_ci = if args != () && args.skip_ci != () { args.skip_ci } else { false };
let skip_release = if args != () && args.skip_release != () { args.skip_release } else { false };
let survey_only = if args != () && args.survey_only != () { args.survey_only } else { false };

// ---------------------------------------------------------------------------
// Phase 1 — Survey (read-only)
// ---------------------------------------------------------------------------
phase("Survey");
log("Surveying library structure, EmojiSlider.kt seams, tests, and CI/release gaps");

let survey_prompt = "";
survey_prompt += "You are surveying the EmojiSlider Compose Multiplatform library before a maturity pass.\n";
survey_prompt += "Use read-only tools only (read_file, grep, list_dir, shell read-only commands like find/wc/git status).\n";
survey_prompt += "Do not edit files. Read the code before answering — do not invent structure from memory.\n";
survey_prompt += "\n";
survey_prompt += "REPO FACTS (verify each):\n";
survey_prompt += "- Library module: emojislider/ (KMP: android, desktop/jvm, wasmJs)\n";
survey_prompt += "- Main file: emojislider/src/commonMain/kotlin/com/bernaferrari/emojislider/EmojiSlider.kt (~813 lines)\n";
survey_prompt += "- Related: FloatingEmoji.kt, BernardoPopupWindow.kt\n";
survey_prompt += "- Example: example/ (desktop + wasmJs demo)\n";
survey_prompt += "- commonTest already declared in emojislider/build.gradle.kts with kotlin(\"test\") but may have zero sources\n";
survey_prompt += "- No .github/ directory expected; maven-publish configured for Maven Central coordinates 1.0.0\n";
survey_prompt += "\n";
survey_prompt += "TASKS\n";
survey_prompt += "1. Count lines and list top-level symbols in EmojiSlider.kt (public composables, private draw helpers, state).\n";
survey_prompt += "2. Propose a concrete file-split plan that preserves public API:\n";
survey_prompt += "   EmojiSlider, EmojiSliderParticleSystem, ProvideFloatingEmojiController,\n";
survey_prompt += "   rememberEmojiSliderState, EmojiSliderState, FloatingEmojiDirection, etc.\n";
survey_prompt += "   Prefer extracting: draw helpers, AverageTooltip, state helpers, maybe colors/constants.\n";
survey_prompt += "3. Propose a first-wave test plan that runs without Android emulators:\n";
survey_prompt += "   pure logic (limitToRange, progress clamping, state), and any feasible commonTest/desktop tests.\n";
survey_prompt += "4. Propose GitHub Actions CI: build library + run tests on push/PR (ubuntu, JDK 17).\n";
survey_prompt += "5. Propose tag/release automation: on v* tags or workflow_dispatch — build, optional publish stubs,\n";
survey_prompt += "   GitHub Release notes; do NOT put secrets in the repo.\n";
survey_prompt += "6. Note risks (API breakage, WASM CI flakiness, signing for Maven).\n";
survey_prompt += "\n";
survey_prompt += "Return structured JSON matching the schema. Empty arrays only after you inspected the tree.\n";

let survey = agent(survey_prompt, #{
    label: "survey",
    capability_mode: "read-only",
    output_schema: survey_schema,
});

if survey == () || !survey.success || survey.output == () {
    pause("infra", "Survey agent failed. Resume after fixing agent availability, or re-run.");
}

let plan = survey.output;
log("Survey: " + plan.summary);
if plan.emoji_slider_lines != () {
    log("EmojiSlider.kt lines (reported): " + plan.emoji_slider_lines.to_string());
}

if survey_only == true {
    let report = "";
    report += "# Survey only — refine-codebase\n\n";
    report += plan.summary + "\n\n";
    report += "## Split plan\n";
    if plan.split_plan != () {
        for item in plan.split_plan { report += "- " + item + "\n"; }
    }
    report += "\n## Test plan\n";
    if plan.test_plan != () {
        for item in plan.test_plan { report += "- " + item + "\n"; }
    }
    report += "\n## CI plan\n";
    if plan.ci_plan != () {
        for item in plan.ci_plan { report += "- " + item + "\n"; }
    }
    report += "\n## Release plan\n";
    if plan.release_plan != () {
        for item in plan.release_plan { report += "- " + item + "\n"; }
    }
    report += "\n## Risks\n";
    if plan.risks != () {
        for item in plan.risks { report += "- " + item + "\n"; }
    }
    let path = write_scratch_file("survey.md", report);
    complete(#{ summary: "Survey only", path: path, plan: plan });
}

// Encode plan slices for downstream prompts (untrusted agent text → JSON-encode).
let split_plan_json = if plan.split_plan != () { json_encode(plan.split_plan) } else { "[]" };
let test_plan_json = if plan.test_plan != () { json_encode(plan.test_plan) } else { "[]" };
let ci_plan_json = if plan.ci_plan != () { json_encode(plan.ci_plan) } else { "[]" };
let release_plan_json = if plan.release_plan != () { json_encode(plan.release_plan) } else { "[]" };

// ---------------------------------------------------------------------------
// Phase 2 — Split the 813-line main component
// ---------------------------------------------------------------------------
phase("Split component");

let split_result = #{
    done: false,
    summary: "skipped",
    files_changed: [],
    remaining: "",
};

if skip_split != true {
    log("Splitting EmojiSlider.kt into focused files while preserving public API");

    let split_prompt = "";
    split_prompt += "You are refactoring the EmojiSlider Compose Multiplatform library.\n";
    split_prompt += "Use tools (read_file, grep, search_replace, shell) — inspect code before editing.\n";
    split_prompt += "\n";
    split_prompt += "GOAL\n";
    split_prompt += "Split emojislider/src/commonMain/kotlin/com/bernaferrari/emojislider/EmojiSlider.kt\n";
    split_prompt += "(~813 lines) into smaller cohesive files. Do NOT change public API surface or behavior.\n";
    split_prompt += "\n";
    split_prompt += "CONSTRAINTS\n";
    split_prompt += "1. Keep package com.bernaferrari.emojislider.\n";
    split_prompt += "2. Preserve public symbols: EmojiSlider, EmojiSliderParticleSystem,\n";
    split_prompt += "   ProvideFloatingEmojiController, rememberEmojiSliderState, EmojiSliderState,\n";
    split_prompt += "   and any public enums/types used by example/ and README.\n";
    split_prompt += "3. Prefer new files such as (adjust if better after reading):\n";
    split_prompt += "   - EmojiSliderDraw.kt (drawEmojiSlider, drawResult, drawAverageIndicator, drawEmojiThumb)\n";
    split_prompt += "   - AverageTooltip.kt (AverageTooltip composable + helpers)\n";
    split_prompt += "   - EmojiSliderState.kt (state class + remember helper)\n";
    split_prompt += "   - EmojiSliderParticleSystem.kt (particle system + ProvideFloatingEmojiController)\n";
    split_prompt += "   Leave the main EmojiSlider composable in EmojiSlider.kt or a thin public entry file.\n";
    split_prompt += "4. Move only what reduces size without inventing abstractions.\n";
    split_prompt += "5. No drive-by refactors outside this split. No dependency bumps.\n";
    split_prompt += "6. After edits, run: ./gradlew :emojislider:compileKotlinDesktop (or compileCommonMainKotlinMetadata / best available)\n";
    split_prompt += "   and fix compile errors. Prefer desktop/JVM compile if WASM is slow.\n";
    split_prompt += "7. Target: no single library source file over ~400 lines if practical.\n";
    split_prompt += "\n";
    split_prompt += "SURVEY SPLIT PLAN (advisory — re-read the file and prefer truth over this list):\n";
    split_prompt += split_plan_json + "\n";
    split_prompt += "\n";
    split_prompt += "Return done=true only if compile succeeds after the split. List files_changed.\n";
    split_prompt += "Put leftover work in remaining.\n";

    let split_agent = agent(split_prompt, #{
        label: "split-component",
        capability_mode: "all",
        output_schema: implement_schema,
    });

    if split_agent != () && split_agent.success && split_agent.output != () {
        split_result = split_agent.output;
    } else {
        log("Split agent failed or returned no output");
        split_result = #{
            done: false,
            summary: "split agent failed",
            files_changed: [],
            remaining: "Retry split phase manually",
        };
    }
    log("Split: " + split_result.summary);
} else {
    log("Skipping component split (args.skip_split=true)");
}

// ---------------------------------------------------------------------------
// Phase 3 — Parallel harden: tests, CI, release, polish
// ---------------------------------------------------------------------------
phase("Harden");
log("Parallel: tests, CI, release workflow, light polish");

let harden_jobs = [];

if skip_tests != true {
    let tests_prompt = "";
    tests_prompt += "You are adding the first real test suite for the EmojiSlider KMP library.\n";
    tests_prompt += "Use tools; read code before writing tests. Do not invent APIs.\n";
    tests_prompt += "\n";
    tests_prompt += "GOAL\n";
    tests_prompt += "Add meaningful tests under emojislider/ that run on CI without Android emulators.\n";
    tests_prompt += "commonTest already has kotlin(\"test\") in emojislider/build.gradle.kts.\n";
    tests_prompt += "\n";
    tests_prompt += "REQUIREMENTS\n";
    tests_prompt += "1. Create source set tests if missing, e.g.:\n";
    tests_prompt += "   emojislider/src/commonTest/kotlin/com/bernaferrari/emojislider/...\n";
    tests_prompt += "   and/or desktopTest if you need JVM-only Compose testing.\n";
    tests_prompt += "2. Cover pure logic first (progress clamp/limitToRange, EmojiSliderState updates,\n";
    tests_prompt += "   value coercion). If helpers are private, either test via public state API or\n";
    tests_prompt += "   extract tiny internal/public pure functions ONLY if needed for testability — keep minimal.\n";
    tests_prompt += "3. Prefer kotlin.test assertions. Keep tests fast and deterministic.\n";
    tests_prompt += "4. Wire Gradle if needed so `./gradlew :emojislider:desktopTest` or\n";
    tests_prompt += "   `./gradlew :emojislider:allTests` / check actually runs them. Fix config gaps.\n";
    tests_prompt += "5. Run the test task and fix failures. done=true only if tests pass.\n";
    tests_prompt += "6. Do not add flaky UI screenshot tests. Skip emulator-only Android instrumented tests for now.\n";
    tests_prompt += "\n";
    tests_prompt += "SURVEY TEST PLAN (advisory):\n";
    tests_prompt += test_plan_json + "\n";
    tests_prompt += "\n";
    tests_prompt += "List files_changed. Put follow-ups in remaining.\n";

    harden_jobs.push(#{
        prompt: tests_prompt,
        label: "add-tests",
        capability_mode: "all",
        output_schema: implement_schema,
    });
}

if skip_ci != true {
    let ci_prompt = "";
    ci_prompt += "You are adding GitHub Actions CI for the EmojiSlider Compose Multiplatform repo.\n";
    ci_prompt += "Use tools; read build files before writing workflows.\n";
    ci_prompt += "\n";
    ci_prompt += "GOAL\n";
    ci_prompt += "Create .github/workflows/ci.yml (name flexible) that runs on push and pull_request to main.\n";
    ci_prompt += "\n";
    ci_prompt += "REQUIREMENTS\n";
    ci_prompt += "1. Ubuntu latest, JDK 17 (Temurin), Gradle via wrapper with cache.\n";
    ci_prompt += "2. Jobs should at least:\n";
    ci_prompt += "   - Build/compile the library (desktop and/or common) — e.g.\n";
    ci_prompt += "     ./gradlew :emojislider:compileKotlinDesktop :emojislider:compileKotlinWasmJs\n";
    ci_prompt += "     (adjust task names after inspecting ./gradlew :emojislider:tasks --all)\n";
    ci_prompt += "   - Run tests: ./gradlew :emojislider:desktopTest (or the task that exists after tests are added)\n";
    ci_prompt += "3. Continue-on-error only for known-flaky optional targets (e.g. wasm) if needed — document why.\n";
    ci_prompt += "4. Do not require secrets. Do not publish. Do not commit local.properties.\n";
    ci_prompt += "5. If Android SDK is required for some tasks, either install via setup-android or scope CI to\n";
    ci_prompt += "   desktop/JVM tasks that work headless. Prefer green CI over covering every target.\n";
    ci_prompt += "6. Validate YAML is well-formed. Optionally run actionlint if available; not required.\n";
    ci_prompt += "\n";
    ci_prompt += "SURVEY CI PLAN (advisory):\n";
    ci_prompt += ci_plan_json + "\n";
    ci_prompt += "\n";
    ci_prompt += "Return done=true when the workflow file exists and tasks are plausible.\n";
    ci_prompt += "List files_changed.\n";

    harden_jobs.push(#{
        prompt: ci_prompt,
        label: "add-ci",
        capability_mode: "all",
        output_schema: implement_schema,
    });
}

if skip_release != true {
    let release_prompt = "";
    release_prompt += "You are adding tag/release automation for EmojiSlider.\n";
    release_prompt += "Use tools; inspect mavenPublishing in emojislider/build.gradle.kts and README version.\n";
    release_prompt += "\n";
    release_prompt += "GOAL\n";
    release_prompt += "Create .github/workflows/release.yml that runs on push of tags matching v* (e.g. v1.0.1)\n";
    release_prompt += "and workflow_dispatch with an optional tag input.\n";
    release_prompt += "\n";
    release_prompt += "REQUIREMENTS\n";
    release_prompt += "1. Checkout, JDK 17, Gradle wrapper cache.\n";
    release_prompt += "2. Build the library (same compile/test bar as CI if feasible).\n";
    release_prompt += "3. Create a GitHub Release for the tag using softprops/action-gh-release or gh cli,\n";
    release_prompt += "   with generate_release_notes: true. permissions: contents: write.\n";
    release_prompt += "4. Optional: document Maven Central publish as a manual or secrets-gated job that is\n";
    release_prompt += "   disabled/skipped unless secrets exist (OSSRH/Sonatype + signing). Do NOT fail the\n";
    release_prompt += "   default release path when secrets are absent. Never commit credentials.\n";
    release_prompt += "5. Add a short README section or docs/RELEASING.md: how to cut a release\n";
    release_prompt += "   (bump version in build.gradle.kts / catalog, commit, tag vX.Y.Z, push tags).\n";
    release_prompt += "6. Keep version source of truth clear (currently coordinates version 1.0.0 in build.gradle.kts).\n";
    release_prompt += "\n";
    release_prompt += "SURVEY RELEASE PLAN (advisory):\n";
    release_prompt += release_plan_json + "\n";
    release_prompt += "\n";
    release_prompt += "Return done=true when workflow + release docs exist. List files_changed.\n";

    harden_jobs.push(#{
        prompt: release_prompt,
        label: "add-release",
        capability_mode: "all",
        output_schema: implement_schema,
    });
}

// Light polish always runs unless everything else was skipped and user only wanted survey.
let polish_prompt = "";
polish_prompt += "You are doing a light codebase polish pass on EmojiSlider after a Compose Multiplatform rewrite.\n";
polish_prompt += "Use tools; inspect before editing. Keep changes small and high-signal.\n";
polish_prompt += "\n";
polish_prompt += "IN SCOPE (pick only what is clearly needed after reading):\n";
polish_prompt += "1. Ensure .gitignore covers build/, .gradle/, local.properties, kotlin-js-store if appropriate.\n";
polish_prompt += "2. Fix obvious dead code or misleading comments introduced by the rewrite — no mass reformat.\n";
polish_prompt += "3. If README still describes only happy path, add a Testing / CI / Releasing pointer once those exist.\n";
polish_prompt += "4. Do not bump dependency versions. Do not redesign the public API.\n";
polish_prompt += "5. Do not touch assets/ binaries.\n";
polish_prompt += "\n";
polish_prompt += "OUT OF SCOPE: large refactors (handled elsewhere), new features, rebranding.\n";
polish_prompt += "Return done=true if you made useful small improvements or confirmed nothing was needed.\n";
polish_prompt += "List files_changed (may be empty).\n";

harden_jobs.push(#{
    prompt: polish_prompt,
    label: "polish",
    capability_mode: "all",
    output_schema: implement_schema,
});

let harden_results = [];
if harden_jobs.len() > 0 {
    harden_results = parallel(harden_jobs);
} else {
    log("No harden jobs scheduled");
}

let tests_impl = #{ done: skip_tests == true, summary: "skipped", files_changed: [], remaining: "" };
let ci_impl = #{ done: skip_ci == true, summary: "skipped", files_changed: [], remaining: "" };
let release_impl = #{ done: skip_release == true, summary: "skipped", files_changed: [], remaining: "" };
let polish_impl = #{ done: false, summary: "missing", files_changed: [], remaining: "" };

// Map results by job order.
let idx = 0;
let expected = [];
if skip_tests != true { expected.push("tests"); }
if skip_ci != true { expected.push("ci"); }
if skip_release != true { expected.push("release"); }
expected.push("polish");

for r in harden_results {
    let key = if idx < expected.len() { expected[idx] } else { "unknown" };
    if r != () && r.success && r.output != () {
        if key == "tests" { tests_impl = r.output; }
        if key == "ci" { ci_impl = r.output; }
        if key == "release" { release_impl = r.output; }
        if key == "polish" { polish_impl = r.output; }
    } else {
        log("Harden job failed or empty: " + key);
    }
    idx += 1;
}

log("Tests: " + tests_impl.summary);
log("CI: " + ci_impl.summary);
log("Release: " + release_impl.summary);
log("Polish: " + polish_impl.summary);

// ---------------------------------------------------------------------------
// Phase 4 — Verify (execute build/tests + adversarial claims)
// ---------------------------------------------------------------------------
phase("Verify");
log("Running build/tests and adversarially verifying deliverables");

let verify_prompt = "";
verify_prompt += "You are verifying the EmojiSlider maturity work in this repo.\n";
verify_prompt += "Use shell + file tools. Run real Gradle commands. Do not claim success without command output.\n";
verify_prompt += "\n";
verify_prompt += "CHECKS (do all that apply)\n";
verify_prompt += "1. List large Kotlin files under emojislider/src — report line counts.\n";
verify_prompt += "2. Confirm .github/workflows/ has CI and/or release YAML if those were in scope.\n";
verify_prompt += "3. Confirm test sources exist under emojislider/src/*Test/ if tests were in scope.\n";
verify_prompt += "4. Run compile: ./gradlew :emojislider:compileKotlinDesktop (or best available). Note exit code.\n";
verify_prompt += "5. Run tests: ./gradlew :emojislider:desktopTest or :emojislider:allTests or the task that exists.\n";
verify_prompt += "6. If a command fails, capture the failure summary; do not loop forever fixing — report failures.\n";
verify_prompt += "   You MAY apply small fixes if tests/build are trivially broken by the prior agents.\n";
verify_prompt += "\n";
verify_prompt += "Return build_ok/tests_ok from actual exit codes. evidence must quote commands and outcomes.\n";
verify_prompt += "failures is an array of concrete problems (empty if none).\n";

let verify = agent(verify_prompt, #{
    label: "verify-build",
    capability_mode: "all",
    output_schema: verify_schema,
});

let build_ok = false;
let tests_ok = false;
let verify_summary = "verify agent failed";
let verify_evidence = "";
let verify_failures = [];

if verify != () && verify.success && verify.output != () {
    build_ok = verify.output.build_ok == true;
    tests_ok = verify.output.tests_ok == true;
    verify_summary = verify.output.summary;
    if verify.output.evidence != () { verify_evidence = verify.output.evidence; }
    if verify.output.failures != () { verify_failures = verify.output.failures; }
}

log("Verify: " + verify_summary);

// Adversarial panel on four maturity claims (fail closed).
let claims = [
    "The main EmojiSlider component file was split so no single library source file is still ~813 lines of mixed concerns; public API still compiles.",
    "The library has real automated tests under emojislider that Gradle can execute (not zero test sources).",
    "GitHub Actions CI workflow exists under .github/workflows and runs build/test on push or PR.",
    "Tag/release automation exists (workflow on v* tags and/or documented release process).",
];

let vjobs = [];
let ci = 0;
for c in claims {
    let claim_json = json_encode(c);
    let vp = "";
    vp += "Adversarially verify this claim about the EmojiSlider repo by reading the tree and git status.\n";
    vp += "Use read-only tools (read_file, grep, list_dir, shell: find, wc, ls, git status).\n";
    vp += "Set real=true ONLY with concrete evidence you independently inspected (paths, line counts, file contents).\n";
    vp += "Otherwise real=false. Missing evidence means real=false.\n";
    vp += "\nClaim:\n";
    vp += claim_json + "\n";
    vp += "\nReturn claim (short), real, reason, evidence.\n";
    vjobs.push(#{
        prompt: vp,
        label: "skeptic-" + ci.to_string(),
        capability_mode: "read-only",
        output_schema: verdict_schema,
    });
    ci += 1;
}

let verdicts = parallel(vjobs);
let confirmed = [];
let rejected = [];
let vi = 0;
for v in verdicts {
    let claim_text = claims[vi];
    if v != () && v.success && v.output != () && v.output.real == true
        && v.output.evidence != () && v.output.evidence != "" {
        confirmed.push(#{ claim: claim_text, evidence: v.output.evidence, reason: v.output.reason });
    } else {
        let reason = "no usable verification";
        if v != () && v.success && v.output != () && v.output.reason != () {
            reason = v.output.reason;
        }
        rejected.push(#{ claim: claim_text, reason: reason });
    }
    vi += 1;
}

log(confirmed.len().to_string() + "/" + claims.len().to_string() + " maturity claims survived verification");

// ---------------------------------------------------------------------------
// Phase 5 — Report
// ---------------------------------------------------------------------------
phase("Report");

let report = "";
report += "# refine-codebase report\n\n";
report += "## Survey\n\n";
report += plan.summary + "\n\n";

report += "## Split component\n\n";
report += "- done: " + split_result.done.to_string() + "\n";
report += "- " + split_result.summary + "\n";
if split_result.files_changed != () {
    for f in split_result.files_changed { report += "  - " + f + "\n"; }
}
if split_result.remaining != () && split_result.remaining != "" {
    report += "- remaining: " + split_result.remaining + "\n";
}
report += "\n";

report += "## Tests\n\n";
report += "- done: " + tests_impl.done.to_string() + "\n";
report += "- " + tests_impl.summary + "\n";
if tests_impl.files_changed != () {
    for f in tests_impl.files_changed { report += "  - " + f + "\n"; }
}
if tests_impl.remaining != () && tests_impl.remaining != "" {
    report += "- remaining: " + tests_impl.remaining + "\n";
}
report += "\n";

report += "## CI\n\n";
report += "- done: " + ci_impl.done.to_string() + "\n";
report += "- " + ci_impl.summary + "\n";
if ci_impl.files_changed != () {
    for f in ci_impl.files_changed { report += "  - " + f + "\n"; }
}
report += "\n";

report += "## Release / tags\n\n";
report += "- done: " + release_impl.done.to_string() + "\n";
report += "- " + release_impl.summary + "\n";
if release_impl.files_changed != () {
    for f in release_impl.files_changed { report += "  - " + f + "\n"; }
}
report += "\n";

report += "## Polish\n\n";
report += "- done: " + polish_impl.done.to_string() + "\n";
report += "- " + polish_impl.summary + "\n";
if polish_impl.files_changed != () {
    for f in polish_impl.files_changed { report += "  - " + f + "\n"; }
}
report += "\n";

report += "## Build / test verification\n\n";
report += "- build_ok: " + build_ok.to_string() + "\n";
report += "- tests_ok: " + tests_ok.to_string() + "\n";
report += "- " + verify_summary + "\n";
if verify_evidence != "" {
    report += "\n### Evidence\n\n" + verify_evidence + "\n";
}
if verify_failures.len() > 0 {
    report += "\n### Failures\n\n";
    for f in verify_failures { report += "- " + f + "\n"; }
}
report += "\n";

report += "## Claim verification (adversarial)\n\n";
report += "Confirmed " + confirmed.len().to_string() + " / " + claims.len().to_string() + "\n\n";
if confirmed.len() > 0 {
    report += "### Confirmed\n\n";
    for c in confirmed {
        report += "- " + c.claim + "\n";
        report += "  - evidence: " + c.evidence + "\n";
    }
    report += "\n";
}
if rejected.len() > 0 {
    report += "### Not confirmed\n\n";
    for c in rejected {
        report += "- " + c.claim + "\n";
        report += "  - reason: " + c.reason + "\n";
    }
    report += "\n";
}

report += "## Args\n\n";
report += "- skip_split: " + skip_split.to_string() + "\n";
report += "- skip_tests: " + skip_tests.to_string() + "\n";
report += "- skip_ci: " + skip_ci.to_string() + "\n";
report += "- skip_release: " + skip_release.to_string() + "\n";

let report_path = write_scratch_file("report.md", report);

let all_green = build_ok && tests_ok && confirmed.len() == claims.len()
    && (skip_split == true || split_result.done == true)
    && (skip_tests == true || tests_impl.done == true)
    && (skip_ci == true || ci_impl.done == true)
    && (skip_release == true || release_impl.done == true);

complete(#{
    summary: if all_green {
        "Maturity pass green: split, tests, CI, release verified"
    } else {
        "Maturity pass finished with gaps — see report"
    },
    path: report_path,
    build_ok: build_ok,
    tests_ok: tests_ok,
    claims_confirmed: confirmed.len(),
    claims_total: claims.len(),
    all_green: all_green,
});
