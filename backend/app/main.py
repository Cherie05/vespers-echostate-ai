from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import get_settings
from app.routers import vision, audio, live, agent

settings = get_settings()

app = FastAPI(
    title=settings.app_name,
    version=settings.version,
    description=(
        "EchoState AI Gateway for Google DeepMind Hackathon. "
        "Bridges blind/visually-impaired users to Gemini 3.8 Flash, Gemini 3.8 Live, "
        "Gemini 3.8 Flash TTS, Gemini 3.5 Transcribe/Translate, and Antigravity Agent."
    ),
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS setup for web, mobile apps, and local testing
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mount API Routers
app.include_router(vision.router)
app.include_router(audio.router)
app.include_router(live.router)
app.include_router(agent.router)


@app.get("/health", tags=["Health"])
async def health_check():
    """Railway healthcheck endpoint."""
    return {
        "status": "healthy",
        "service": settings.app_name,
        "version": settings.version,
        "models": {
            "vision": settings.model_vision_flash,
            "live_audio": settings.model_audio_live,
            "tts": settings.model_audio_tts,
            "transcribe": settings.model_audio_transcribe,
            "translate": settings.model_audio_translate,
            "agent": settings.model_agent_orchestrator
        }
    }


@app.get("/", tags=["Root"])
async def root():
    return {
        "message": "Welcome to EchoState AI Backend Gateway",
        "documentation": "/docs",
        "health": "/health"
    }
