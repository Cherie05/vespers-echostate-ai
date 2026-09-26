from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional, Dict, Any, List
from app.services.antigravity_service import AntigravityAgentService

router = APIRouter(prefix="/api/v1/agent", tags=["Antigravity Orchestration"])
agent_service = AntigravityAgentService()


class AgentInteractionRequest(BaseModel):
    session_id: str
    user_intent: str
    local_state: Dict[str, Any] = {}
    history: Optional[List[Dict[str, str]]] = None


@router.post("/orchestrate")
async def orchestrate_action(req: AgentInteractionRequest):
    """
    Problem Statement 4: Antigravity Agent (antigravity-preview-09-2026) via Interactions API
    Builds stateful, multi-agent navigation plans and handles error recovery.
    """
    result = await agent_service.execute_interaction(
        session_id=req.session_id,
        user_intent=req.user_intent,
        local_context=req.local_state,
        history=req.history
    )
    if not result.get("success"):
        raise HTTPException(
            status_code=502,
            detail=result.get("error", "Agent orchestration failed")
        )
    return result
