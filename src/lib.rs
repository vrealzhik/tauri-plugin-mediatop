use tauri::{
    plugin::{Builder, TauriPlugin},
    Manager, Runtime,
};

pub use models::*;

#[cfg(desktop)]
mod desktop;
#[cfg(mobile)]
mod mobile;

mod commands;
mod error;
mod models;

pub use error::{Error, Result};

#[cfg(desktop)]
use desktop::Mediatop;
#[cfg(mobile)]
use mobile::Mediatop;

/// Extensions to [`tauri::App`], [`tauri::AppHandle`] and [`tauri::Window`] to access the mediatop APIs.
pub trait MediatopExt<R: Runtime> {
    fn mediatop(&self) -> &Mediatop<R>;
}

impl<R: Runtime, T: Manager<R>> crate::MediatopExt<R> for T {
    fn mediatop(&self) -> &Mediatop<R> {
        self.state::<Mediatop<R>>().inner()
    }
}

/// Initializes the plugin.
pub fn init<R: Runtime>() -> TauriPlugin<R> {
    Builder::new("mediatop")
        .invoke_handler(tauri::generate_handler![commands::pick_and_convert_video])
        .setup(|app, api| {
            #[cfg(mobile)]
            let mediatop = mobile::init(app, api)?;
            #[cfg(desktop)]
            let mediatop = desktop::init(app, api)?;
            app.manage(mediatop);
            Ok(())
        })
        .build()
}
