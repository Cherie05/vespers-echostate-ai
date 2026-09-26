# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## Reporting a Vulnerability

The EchoState AI team takes security and privacy seriously, particularly because this platform processes real-time camera frames, audio feeds, and potentially sensitive personal documents for individuals with disabilities.

If you believe you have found a security vulnerability in EchoState AI, please do **NOT** report it through a public GitHub issue.

Instead, please send an email to **arunvpp24@gmail.com** with:
- A description of the vulnerability and its potential impact.
- Step-by-step reproduction instructions or proof-of-concept code.
- Your name or pseudonym if you would like attribution in our release notes.

We will acknowledge receipt within 48 hours and work with you to coordinate a responsible disclosure and patch.

---

## Sensitive Configuration & Zero Secret Leakage

- **API Keys**:
  - `GEMINI_API_KEY` and `ANTIGRAVITY_API_KEY` must **never** be hardcoded or checked into source control.
  - The repository's root `.gitignore` explicitly prevents `.env` and `local.properties` files from being committed.
  - In production deployments (e.g. Railway), configure secrets using platform environment variables.
- **On-Device Data Isolation**:
  - Sensitive offline inferences performed via **Gemma 4** run entirely in memory on the device via the MediaPipe runtime and are never uploaded to the cloud without explicit user intent.
