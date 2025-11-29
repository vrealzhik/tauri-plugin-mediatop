use tauri::{command, AppHandle, Runtime};

use crate::models::*;
use crate::MediatopExt;
use crate::Result;

#[command]
pub(crate) async fn pick_and_convert_video<R: Runtime>(
    app: AppHandle<R>,
    payload: MediaRequest,
) -> Result<MediaResult> {
    app.mediatop().pick_and_convert_video(payload)
}
