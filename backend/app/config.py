from pydantic_settings import BaseSettings, SettingsConfigDict
from functools import lru_cache
from typing import List


class Settings(BaseSettings):
    app_name: str = "EchoState AI Gateway"
    version: str = "1.0.0"
    env: str = "production"
    debug: bool = False
    port: int = 8000
    allowed_origins: str = "*"

    # Gemini & Google Gen AI
    gemini_api_key: str = ""
    
    # Antigravity Managed Agent Interactions API
    antigravity_api_key: str = ""
    antigravity_endpoint: str = "https://api.antigravity.google/v1alpha"

    # Specific Hackathon Models
    model_vision_flash: str = "gemini-3.8-flash"
    model_audio_live: str = "gemini-3.8-live"
    model_audio_tts: str = "gemini-3.8-flash-tts"
    model_audio_transcribe: str = "gemini-3.5-transcribe"
    model_audio_translate: str = "gemini-3.5-live-translate-preview"
    model_agent_orchestrator: str = "antigravity-preview-09-2026"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    @property
    def cors_origins(self) -> List[str]:
        if self.allowed_origins == "*":
            return ["*"]
        return [origin.strip() for origin in self.allowed_origins.split(",") if origin.strip()]


@lru_cache
def get_settings() -> Settings:
    return Settings()
