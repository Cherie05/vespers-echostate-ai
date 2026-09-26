import base64
import httpx
import logging
from typing import Optional, List, Dict, Any
from app.config import get_settings

logger = logging.getLogger(__name__)


class GeminiService:
    def __init__(self):
        self.settings = get_settings()
        self.api_key = self.settings.gemini_api_key
        self.base_url = "https://generativelanguage.googleapis.com/v1beta"

    async def analyze_multimodal(
        self,
        image_bytes: Optional[bytes] = None,
        video_bytes: Optional[bytes] = None,
        mime_type: str = "image/jpeg",
        prompt: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Problem Statement 1: Frontier Intelligence at Flash Speed
        Calls gemini-3.8-flash for instant multimodal scene and obstacle understanding.
        """
        if not prompt:
            prompt = (
                "You are an AI assistant for a blind person using their smartphone camera. "
                "Analyze the visual frame. Describe key obstacles, doorways, readable labels, "
                "and whether text or documents are fully centered or cut off. "
                "Keep the response concise, clear, and focused on physical navigation."
            )

        parts: List[Dict[str, Any]] = [{"text": prompt}]

        media_data = image_bytes or video_bytes
        if media_data:
            b64_data = base64.b64encode(media_data).decode("utf-8")
            parts.append({
                "inline_data": {
                    "mime_type": mime_type,
                    "data": b64_data
                }
            })

        payload = {
            "contents": [{"parts": parts}],
            "generationConfig": {
                "temperature": 0.2,
                "maxOutputTokens": 300
            }
        }

        url = f"{self.base_url}/models/{self.settings.model_vision_flash}:generateContent?key={self.api_key}"

        async with httpx.AsyncClient(timeout=30.0) as client:
            response = await client.post(url, json=payload)
            if response.status_code != 200:
                logger.error(f"Gemini Flash API error: {response.status_code} - {response.text}")
                return {
                    "success": False,
                    "error": f"API returned status {response.status_code}",
                    "details": response.text
                }

            data = response.json()
            try:
                candidate = data["candidates"][0]["content"]["parts"][0]["text"]
                return {
                    "success": True,
                    "description": candidate,
                    "model": self.settings.model_vision_flash
                }
            except (KeyError, IndexError) as exc:
                logger.error(f"Failed to parse Gemini response: {exc}")
                return {
                    "success": False,
                    "error": "Failed to parse model output"
                }

    async def text_to_speech(self, text: str, voice_name: str = "en-US-Journey-D") -> Dict[str, Any]:
        """
        Problem Statement 2: Gemini 3.8 Flash TTS
        Synthesizes spoken audio for reading Braille, OCR, and AI responses.
        """
        url = f"{self.base_url}/models/{self.settings.model_audio_tts}:synthesize?key={self.api_key}"
        payload = {
            "text": text,
            "voice": voice_name,
            "audioConfig": {
                "audioEncoding": "MP3"
            }
        }

        async with httpx.AsyncClient(timeout=20.0) as client:
            response = await client.post(url, json=payload)
            if response.status_code != 200:
                return {
                    "success": False,
                    "error": f"TTS error {response.status_code}: {response.text}"
                }
            return {
                "success": True,
                "audio_base64": base64.b64encode(response.content).decode("utf-8"),
                "mime_type": "audio/mp3",
                "model": self.settings.model_audio_tts
            }

    async def transcribe_audio(self, audio_bytes: bytes, mime_type: str = "audio/wav") -> Dict[str, Any]:
        """
        Problem Statement 2: Gemini 3.5 Transcribe
        Turns noisy, rapid multi-speaker audio into structured text.
        """
        url = f"{self.base_url}/models/{self.settings.model_audio_transcribe}:generateContent?key={self.api_key}"
        b64_audio = base64.b64encode(audio_bytes).decode("utf-8")
        payload = {
            "contents": [{
                "parts": [
                    {"text": "Transcribe this audio precisely. Extract spoken commands and questions."},
                    {"inline_data": {"mime_type": mime_type, "data": b64_audio}}
                ]
            }]
        }

        async with httpx.AsyncClient(timeout=20.0) as client:
            response = await client.post(url, json=payload)
            if response.status_code != 200:
                return {"success": False, "error": response.text}
            data = response.json()
            try:
                text = data["candidates"][0]["content"]["parts"][0]["text"]
                return {"success": True, "transcription": text, "model": self.settings.model_audio_transcribe}
            except Exception as e:
                return {"success": False, "error": str(e)}

    async def live_translate(
        self,
        audio_bytes: bytes,
        target_language: str = "English",
        mime_type: str = "audio/wav"
    ) -> Dict[str, Any]:
        """
        Problem Statement 2: Gemini 3.5 Live Translate
        Real-time cross-language translation matching vocal tone and emotional pitch.
        """
        url = f"{self.base_url}/models/{self.settings.model_audio_translate}:generateContent?key={self.api_key}"
        b64_audio = base64.b64encode(audio_bytes).decode("utf-8")
        prompt = (
            f"Translate this spoken speech into {target_language}. "
            "Preserve emotional pitch and tone (e.g. urgent, polite, friendly). "
            "Return both the translated text and emotional tone annotation."
        )
        payload = {
            "contents": [{
                "parts": [
                    {"text": prompt},
                    {"inline_data": {"mime_type": mime_type, "data": b64_audio}}
                ]
            }]
        }

        async with httpx.AsyncClient(timeout=25.0) as client:
            response = await client.post(url, json=payload)
            if response.status_code != 200:
                return {"success": False, "error": response.text}
            data = response.json()
            try:
                translation = data["candidates"][0]["content"]["parts"][0]["text"]
                return {
                    "success": True,
                    "translation": translation,
                    "target_language": target_language,
                    "model": self.settings.model_audio_translate
                }
            except Exception as e:
                return {"success": False, "error": str(e)}
