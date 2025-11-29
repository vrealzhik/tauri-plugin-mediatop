use serde::de::DeserializeOwned;
use tauri::{
    plugin::{PluginApi, PluginHandle},
    AppHandle, Runtime,
};

use crate::models::*;

#[cfg(target_os = "ios")]
tauri::ios_plugin_binding!(init_plugin_mediatop);

// initializes the Kotlin or Swift plugin classes
pub fn init<R: Runtime, C: DeserializeOwned>(
    _app: &AppHandle<R>,
    api: PluginApi<R, C>,
) -> crate::Result<Mediatop<R>> {
    #[cfg(target_os = "android")]
    let handle = api.register_android_plugin("com.plugin.mediatop", "MediatopPlugin")?;
    #[cfg(target_os = "ios")]
    let handle = api.register_ios_plugin(init_plugin_mediatop)?;
    Ok(Mediatop(handle))
}

/// Access to the mediatop APIs.
pub struct Mediatop<R: Runtime>(PluginHandle<R>);

impl<R: Runtime> Mediatop<R> {
    pub fn pick_and_convert_video(&self, payload: MediaRequest) -> crate::Result<MediaResult> {
        self.0
            .run_mobile_plugin("pickAndConvertVideo", ())
            .map_err(Into::into)
    }
}
