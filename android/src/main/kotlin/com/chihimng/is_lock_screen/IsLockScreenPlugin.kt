package com.chihimng.is_lock_screen

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class IsLockScreenPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    context = binding.applicationContext
    // Use binaryMessenger instead of dartExecutor
    channel = MethodChannel(binding.binaryMessenger, "is_lock_screen")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "isLockScreen" -> {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val inKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()
        val isLocked = if (inKeyguardRestrictedInputMode) {
          true
        } else {
          val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            !powerManager.isInteractive
          } else {
            !powerManager.isScreenOn
          }
        }
        result.success(isLocked)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
