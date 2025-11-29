const COMMANDS: &[&str] = &["pick_and_convert_video"];

fn main() {
    tauri_plugin::Builder::new(COMMANDS)
        .android_path("android")
        .ios_path("ios")
        .build();
}
