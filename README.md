# EchoState AI - GDG DeepMind Hackathon Monorepo

> **An Autonomous, Multi-Modal Accessibility Platform for Blind, Visually Impaired, and Deaf-Blind Users.**  
> Powered by on-device **Gemma 4** local agent loops and cloud-escalated **Gemini 3.8 Live & Flash** models.

---

## Repository Structure

```
gdg-deepmind/
├── backend/                       # Python FastAPI Gateway (Deployable to Railway via Docker)
│   ├── Dockerfile                 # Optimized container build for Railway
│   ├── railway.json               # Railway deployment configuration
│   ├── requirements.txt           # FastAPI, WebSockets, Google Gen AI SDK
│   ├── .env.example               # Template for backend secrets
│   └── app/
│       ├── main.py                # App entrypoint, CORS, routes & healthcheck
│       ├── config.py              # Pydantic environment configuration
│       ├── routers/
│       │   ├── vision.py          # Gemini 3.8 Flash (Image & Video frame analysis)
│       │   ├── audio.py           # Gemini 3.8 Flash TTS, Transcribe, Live Translate
│       │   ├── live.py            # Gemini 3.8 Live (Full-duplex WebSocket proxy)
│       │   └── agent.py           # Antigravity Agent (Interactions API orchestration)
│       └── services/
│           ├── gemini_service.py  # Google Gen AI model calls
│           └── antigravity_service.py # Managed agent coordination
│
├── app/                           # Android Native Client (Jetpack Compose + Kotlin)
│   ├── app/src/main/              # Android app source code
│   │   ├── java/com/echostate/ai/
│   │   │   ├── accessibility/     # 6-finger Braille Screen Input & Haptic feedback
│   │   │   ├── audio/             # WebSocket audio client for Gemini 3.8 Live
│   │   │   ├── camera/            # CameraX 10 FPS frame stream
│   │   │   ├── cloud/             # HTTP gateway client to FastAPI backend
│   │   │   ├── llm/               # Gemma 4 offline Sense-Decide-Act-Check loop
│   │   │   ├── ui/                # High-contrast WCAG AAA screens & Two-Way display
│   │   │   └── viewmodel/         # Reactive state management
│   ├── build.gradle.kts           # Configured with BuildConfig backend URLs
│   └── local.properties.example   # Template for local Android development
│
├── .gitignore                     # Zero-leakage git ignore for backend & mobile
└── README.md                      # Project documentation
```

---

## Hackathon Problem Statements Addressed

| Track | Model Used | Architecture Role |
| :--- | :--- | :--- |
| **Track 5: Local-First Agents** | **Gemma 4 (E2B / E4B)** | Runs 100% on-device via MediaPipe. Senses private documents (prescriptions/cards), parses Braille commands at zero latency, and decides when to escalate to cloud. |
| **Track 2: Next-Gen Voice** | **Gemini 3.8 Live** | Full-duplex WebSocket audio streaming (`/ws/live`) with sub-150ms mid-sentence barge-in. |
| **Track 2: Speech & Translation** | **Gemini 3.8 Flash TTS** & **Gemini 3.5 Live Translate** | Natural speech generation and tone-preserved real-time cross-language translation. |
| **Track 1: Frontier Multimodal** | **Gemini 3.8 Flash** | High-velocity video sequence and frame understanding for obstacle avoidance and door/sign reading. |
| **Track 4: Autonomous Agents** | **Antigravity Agent (`antigravity-preview-09-2026`)** | Long-horizon path planning and recovery coordination via the Interactions API. |

---

## 🚀 Quickstart Guide

### 1. Deploying the Backend to Railway
1. Push this repository to GitHub (secrets in `.env` and `local.properties` are ignored by `.gitignore`).
2. Log into [Railway.app](https://railway.app), click **New Project** -> **Deploy from GitHub repo**.
3. In service settings, configure **Root Directory** to `/backend`.
4. Add the following environment variables in Railway:
   * `GEMINI_API_KEY`: Your Google Gen AI API key
   * `ANTIGRAVITY_API_KEY`: (Optional) Your Antigravity key
5. Once deployed, Railway provides a public URL (e.g. `https://your-service.up.railway.app`).

### 2. Running the Mobile App
1. Open the `/app` folder in **Android Studio**.
2. Copy `local.properties.example` to `local.properties`:
   ```properties
   BACKEND_BASE_URL=https://your-service.up.railway.app
   BACKEND_WS_URL=wss://your-service.up.railway.app
   ```
3. Sync Gradle and build the app to an emulator or physical Android phone.
