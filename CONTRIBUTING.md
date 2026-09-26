# Contributing to EchoState AI

Thank you for your interest in contributing to **EchoState AI**! We welcome contributions to make digital and physical accessibility seamless, fast, and empowering for blind, visually impaired, and deaf-blind individuals worldwide.

---

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please treat all contributors with kindness, empathy, and respect.

---

## How Can I Contribute?

### 1. Reporting Bugs
- Check the [GitHub Issues](https://github.com/Cherie05/vespers-echostate-ai/issues) to ensure the issue hasn't already been reported.
- Use our [Bug Report Template](.github/ISSUE_TEMPLATE/bug_report.md).
- Include device specifications, Android versions (note if 16KB page size), and steps to reproduce.

### 2. Suggesting Features & Enhancements
- Open a feature request using our [Feature Request Template](.github/ISSUE_TEMPLATE/feature_request.md).
- Describe the accessibility impact clearly: how does your proposal empower the end-user?

### 3. Submitting Pull Requests
1. Fork the repository and create your branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Keep your changes focused. Avoid mixing unrelated refactors into a single PR.
3. Test your changes locally before submitting:
   - **Backend**:
     ```bash
     cd backend
     python -m unittest discover -s tests -p "test_*.py"
     ```
   - **Android Mobile App**:
     ```bash
     cd app
     ./gradlew assembleDebug
     ```
4. Adhere to our [Security Policy](SECURITY.md)—**never commit API keys or sensitive credentials**.
5. Commit using descriptive commit messages (Conventional Commits encouraged: `feat:`, `fix:`, `docs:`, `perf:`).
6. Push to your fork and submit a Pull Request to `main`. Fill out the [Pull Request Template](.github/PULL_REQUEST_TEMPLATE.md).

---

## Development Standards

### Android Client (Kotlin & Jetpack Compose)
- **Design for Accessibility First**:
  - Minimum touch targets of 48dp x 48dp.
  - WCAG AAA contrast ratio (7:1 minimum for regular text).
  - Proper `contentDescription` on all visual elements for TalkBack.
  - Distinct haptic feedback patterns for spatial hints and tactile alerts.
- **Modern Kotlin**: Follow official Kotlin coding conventions and Jetpack Compose best practices.
- **Android 15 Compatibility**: Be mindful of 16KB page size requirements when linking native JNI libraries.

### Backend Gateway (FastAPI & Python 3.11+)
- **Async First**: Use asynchronous route handlers and non-blocking IO for streaming WebSocket connections.
- **Type Annotations**: Use Pydantic models for request/response validation and strict Python type hints.
- **Error Handling**: Gracefully handle Google Gen AI API timeouts or network blips so the client can fallback to on-device Gemma 4.

---

## Questions and Support

Feel free to open an issue or start a discussion on GitHub. We are thrilled to have you build the future of multimodal accessibility with us!
