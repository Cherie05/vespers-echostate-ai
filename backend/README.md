# EchoState AI - FastAPI Backend Gateway

Backend API gateway connecting the **EchoState AI** mobile client to Google DeepMind's hackathon models:
- **Problem Statement 1**: `gemini-3.8-flash` (CameraX frame analysis, obstacle radar, document alignment)
- **Problem Statement 2**: `gemini-3.8-live` (Full-duplex WebSocket audio with barge-in), `gemini-3.8-flash-tts` (Text-to-Speech), `gemini-3.5-transcribe`, and `gemini-3.5-live-translate-preview`
- **Problem Statement 4**: `antigravity-preview-09-2026` via the Interactions API (Managed Agent autonomous orchestration)

---

## Local Development Setup

1. **Navigate to the backend directory**:
   ```bash
   cd backend
   ```

2. **Create and activate a virtual environment**:
   ```bash
   python -m venv venv
   # On Windows:
   .\venv\Scripts\activate
   # On macOS/Linux:
   source venv/bin/activate
   ```

3. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

4. **Configure Environment Variables**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Add your `GEMINI_API_KEY` and optional `ANTIGRAVITY_API_KEY`.

5. **Start the server**:
   ```bash
   uvicorn app.main:app --reload --port 8000
   ```
   * Interactive OpenAPI Docs: [http://localhost:8000/docs](http://localhost:8000/docs)
   * Health Check: [http://localhost:8000/health](http://localhost:8000/health)

---

## Deploying to Railway with Docker

1. Push this repository to GitHub.
2. In [Railway.app](https://railway.app):
   - Click **New Project** -> **Deploy from GitHub repo**.
   - Select your repository.
   - Set the **Root Directory** in Railway settings to `/backend`.
3. Under the **Variables** tab in Railway, set:
   - `GEMINI_API_KEY`: Your Google Gen AI API key
   - `ANTIGRAVITY_API_KEY`: (Optional) Your Antigravity Interactions API key
4. Railway will automatically detect `backend/Dockerfile` and deploy the service.
5. Copy the generated Railway domain (e.g. `https://echostate-backend.up.railway.app`) into your mobile app configuration!
