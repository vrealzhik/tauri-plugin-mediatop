use serde::de::DeserializeOwned;
use tauri::{plugin::PluginApi, AppHandle, Runtime};

use crate::models::*;

pub fn init<R: Runtime, C: DeserializeOwned>(
    app: &AppHandle<R>,
    _api: PluginApi<R, C>,
) -> crate::Result<Mediatop<R>> {
    Ok(Mediatop(app.clone()))
}

/// Access to the mediatop APIs.
pub struct Mediatop<R: Runtime>(AppHandle<R>);

impl<R: Runtime> Mediatop<R> {
    pub fn pick_and_convert_video(&self, payload: MediaRequest) -> crate::Result<MediaResult> {
        Ok(MediaResult {
            success: Some(bool::default()),
            output_path: Some(String::default()),
        })
    }
}
