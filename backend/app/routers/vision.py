from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from pydantic import BaseModel
from typing import Optional
import base64
from app.services.gemini_service import GeminiService

router = APIRouter(prefix="/api/v1/vision", tags=["Vision & Multimodal"])
gemini_service = GeminiService()


class Base64VisionRequest(BaseModel):
    image_base64: Optional[str] = None
    video_base64: Optional[str] = None
    mime_type: str = "image/jpeg"
    prompt: Optional[str] = None


class CameraGuidanceResponse(BaseModel):
    is_centered: bool
    direction_hint: str  # "tilt_left", "tilt_right", "move_higher", "move_lower", "centered"
    haptic_pattern: str  # "vibrate_left", "vibrate_right", "double_pulse", "none"
    detected_text_preview: Optional[str] = None
    description: str


@router.post("/analyze")
async def analyze_scene(
    file: Optional[UploadFile] = File(None),
    prompt: Optional[str] = Form(None)
):
    """
    Problem Statement 1: Gemini 3.8 Flash
    Processes camera frames or video sequences for high-speed obstacle and environmental description.
    """
    if file:
        content = await file.read()
        mime_type = file.content_type or "image/jpeg"
        result = await gemini_service.analyze_multimodal(
            image_bytes=content if "video" not in mime_type else None,
            video_bytes=content if "video" in mime_type else None,
            mime_type=mime_type,
            prompt=prompt
        )
    else:
        raise HTTPException(status_code=400, detail="No media file provided")

    if not result.get("success"):
        raise HTTPException(status_code=502, detail=result.get("error", "Vision analysis failed"))

    return result


@router.post("/analyze-base64")
async def analyze_scene_base64(req: Base64VisionRequest):
    """
    Accepts raw base64 frame stream directly from Android CameraX buffer.
    """
    image_bytes = base64.b64decode(req.image_base64) if req.image_base64 else None
    video_bytes = base64.b64decode(req.video_base64) if req.video_base64 else None

    if not image_bytes and not video_bytes and not req.prompt:
        raise HTTPException(status_code=400, detail="No media data or prompt provided")

    result = await gemini_service.analyze_multimodal(
        image_bytes=image_bytes,
        video_bytes=video_bytes,
        mime_type=req.mime_type,
        prompt=req.prompt
    )

    if not result.get("success"):
        raise HTTPException(status_code=502, detail=result.get("error", "Vision analysis failed"))

    return result


class ChatPromptRequest(BaseModel):
    prompt: str


@router.post("/chat")
async def text_chat(req: ChatPromptRequest):
    """
    Standard text conversational endpoint with Gemini 3.8 Flash.
    """
    result = await gemini_service.analyze_multimodal(prompt=req.prompt)
    if not result.get("success"):
        raise HTTPException(status_code=502, detail=result.get("error", "Chat failed"))
    return result


@router.post("/camera-guidance", response_model=CameraGuidanceResponse)
async def get_camera_guidance(file: UploadFile = File(...)):
    """
    Spatial Camera Guidance & Haptic Trigger:
    Analyzes document/object alignment and returns haptic vibration patterns for the mobile app:
    - Left vibration: Tilt left
    - Right vibration: Tilt right
    - Double pulse: Centered and ready for Gemma 4 offline reading
    """
    content = await file.read()
    guidance_prompt = (
        "You are assisting a blind user aiming their phone camera at a document, label, or doorway. "
        "Strictly answer with: "
        "STATUS: [CENTERED | TILT_LEFT | TILT_RIGHT | MOVE_HIGHER | MOVE_LOWER] "
        "LABEL: [brief readable title or text] "
        "DESCRIPTION: [one sentence navigational advice]"
    )

    res = await gemini_service.analyze_multimodal(
        image_bytes=content,
        mime_type=file.content_type or "image/jpeg",
        prompt=guidance_prompt
    )

    if not res.get("success"):
        return CameraGuidanceResponse(
            is_centered=False,
            direction_hint="move_closer",
            haptic_pattern="none",
            detected_text_preview=None,
            description="Unable to detect camera orientation"
        )

    desc = res.get("description", "")
    upper_desc = desc.upper()

    if "TILT_LEFT" in upper_desc:
        hint = "tilt_left"
        haptic = "vibrate_left"
        centered = False
    elif "TILT_RIGHT" in upper_desc:
        hint = "tilt_right"
        haptic = "vibrate_right"
        centered = False
    elif "MOVE_HIGHER" in upper_desc:
        hint = "move_higher"
        haptic = "vibrate_pulse_high"
        centered = False
    elif "MOVE_LOWER" in upper_desc:
        hint = "move_lower"
        haptic = "vibrate_pulse_low"
        centered = False
    else:
        hint = "centered"
        haptic = "double_pulse"
        centered = True

    return CameraGuidanceResponse(
        is_centered=centered,
        direction_hint=hint,
        haptic_pattern=haptic,
        detected_text_preview=desc[:120],
        description=desc
    )
