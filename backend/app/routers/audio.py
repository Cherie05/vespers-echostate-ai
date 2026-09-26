from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from fastapi.responses import Response
from pydantic import BaseModel
import base64
from app.services.gemini_service import GeminiService

router = APIRouter(prefix="/api/v1/audio", tags=["Audio & Voice"])
gemini_service = GeminiService()


class TTSRequest(BaseModel):
    text: str
    voice_name: str = "en-US-Journey-D"


class TranslateRequest(BaseModel):
    audio_base64: str
    target_language: str = "English"
    mime_type: str = "audio/wav"


@router.post("/tts")
async def generate_tts(req: TTSRequest):
    """
    Problem Statement 2: Gemini 3.8 Flash TTS
    Synthesizes natural spoken output for reading Braille, OCR labels, or assistant guidance.
    """
    result = await gemini_service.text_to_speech(req.text, req.voice_name)
    if not result.get("success"):
        raise HTTPException(status_code=502, detail=result.get("error", "TTS synthesis failed"))
    return result


@router.post("/tts/stream")
async def stream_tts(req: TTSRequest):
    """
    Returns raw MP3 audio stream directly for zero-delay playback on Android.
    """
    result = await gemini_service.text_to_speech(req.text, req.voice_name)
    if not result.get("success"):
        raise HTTPException(status_code=502, detail="TTS generation failed")
    raw_bytes = base64.b64decode(result["audio_base64"])
    return Response(content=raw_bytes, media_type="audio/mp3")


@router.post("/transcribe")
async def transcribe_audio(file: UploadFile = File(...)):
    """
    Problem Statement 2: Gemini 3.5 Transcribe
    Turns noisy spoken input from bone-conduction mics into clean text commands.
    """
    audio_bytes = await file.read()
    mime_type = file.content_type or "audio/wav"
    result = await gemini_service.transcribe_audio(audio_bytes, mime_type)
    if not result.get("success"):
        raise HTTPException(status_code=502, detail=result.get("error", "Transcription failed"))
    return result


@router.post("/translate")
async def live_translate(file: UploadFile = File(...), target_language: str = Form("English")):
    """
    Problem Statement 2: Gemini 3.5 Live Translate
    Translates foreign speech (e.g. Japanese barista) while preserving emotional pitch and tone.
    """
    audio_bytes = await file.read()
    mime_type = file.content_type or "audio/wav"
    result = await gemini_service.live_translate(
        audio_bytes=audio_bytes,
        target_language=target_language,
        mime_type=mime_type
    )
    if not result.get("success"):
        raise HTTPException(status_code=502, detail=result.get("error", "Translation failed"))
    return result
