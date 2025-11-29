use tauri::{AppHandle, command, Runtime};

use crate::models::*;
use crate::Result;
use crate::MediatopExt;

#[command]
pub(crate) async fn pick_and_convert_video<R: Runtime>(
    app: AppHandle<R>,
) -> Result<MediaResult> {
    app.mediatop().pick_and_convert_video()
}
