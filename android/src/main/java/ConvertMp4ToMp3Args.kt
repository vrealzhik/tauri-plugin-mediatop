package com.plugin.mediatop

import app.tauri.annotation.InvokeArg

@InvokeArg
class ConvertMp4ToMp3Args {
    var inputPath: String = ""
    var outputPath: String = ""
}