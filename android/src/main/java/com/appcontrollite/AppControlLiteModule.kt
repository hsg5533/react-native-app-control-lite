package com.appcontrollite

import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil
import kotlin.system.exitProcess

class AppControlLiteModule(private val reactContext: ReactApplicationContext)
  : ReactContextBaseJavaModule(reactContext) {

  override fun getName() = "AppControlLite"

  /**
   * 깔끔한 무애니메이션 재시작:
   * - UI 스레드에서 런처 액티비티를 새 태스크로 시작
   * - 기존 태스크 비우기
   * - 애니메이션 제거
   * - 약간의 지연 뒤 프로세스 종료(잔여 리소스 정리)
   */
  @ReactMethod
  fun restart(promise: Promise) {
    try {
      val pm = reactContext.packageManager
      val launchIntent = pm.getLaunchIntentForPackage(reactContext.packageName)
        ?: throw Exception("Launch intent not found")

      val component: ComponentName = launchIntent.component
        ?: throw Exception("Launch component not found")

      val restartIntent = Intent.makeRestartActivityTask(component).apply {
        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
      }

      UiThreadUtil.runOnUiThread {
        try {
          currentActivity?.overridePendingTransition(0, 0)
        } catch (_: Throwable) { /* no-op */ }

        reactContext.startActivity(restartIntent)

        Handler(Looper.getMainLooper()).postDelayed({
          promise.resolve(true)
          exitProcess(0)
        }, 250)
      }

    } catch (e: Exception) {
      promise.reject("E_RESTART", e)
    }
  }

  @ReactMethod
  fun recreate(promise: Promise) {
    try {
      UiThreadUtil.runOnUiThread {
        currentActivity?.let {
          it.overridePendingTransition(0, 0)
          it.recreate()
          it.overridePendingTransition(0, 0)
        }
        promise.resolve(true)
      }
    } catch (e: Exception) {
      promise.reject("E_RECREATE", e)
    }
  }

  @ReactMethod
  fun exitApp(promise: Promise) {
    try {
      UiThreadUtil.runOnUiThread {
        try { currentActivity?.overridePendingTransition(0, 0) } catch (_: Throwable) {}
        promise.resolve(true)
        exitProcess(0)
      }
    } catch (e: Exception) {
      promise.reject("E_EXIT", e)
    }
  }

  @ReactMethod
  fun isReady(promise: Promise) {
    promise.resolve(true)
  }
}
