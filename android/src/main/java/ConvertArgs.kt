package com.plugin.mediatop

import app.tauri.annotation.InvokeArg

@InvokeArg
class ConvertArgs {
    var startTime: String = null 
    var duration: String = null
}