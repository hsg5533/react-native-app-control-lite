package com.appcontrollite

import android.content.ComponentName
import android.content.Intent
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.UiThreadUtil

class AppControlLiteModule(private val appContext: ReactApplicationContext) 
  : ReactContextBaseJavaModule(appContext) {

  override fun getName() = "AppControlLite"

  /**
   * 앱 재시작 (RN 0.77 / 0.81 공용, Android 15 대응)
   *
   * - 런처 액티비티 인텐트로 새 태스크 시작
   * - 기존 액티비티 스택 정리
   * - 프로세스를 직접 kill 하지 않음 (exitProcess 제거)
   */
  @ReactMethod
  fun restart(promise: Promise) {
    try {
      val pm = appContext.packageManager
      val launchIntent = pm.getLaunchIntentForPackage(appContext.packageName)
        ?: throw Exception("Launch intent not found")
      val component: ComponentName = launchIntent.component
        ?: throw Exception("Launch component not found")
      val restartIntent = Intent(launchIntent).apply {
        component?.let { setComponent(it) }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
      }

      UiThreadUtil.runOnUiThread {
        val activity = reactApplicationContext.currentActivity
        try {
          activity?.overridePendingTransition(0, 0)
        } catch (_: Throwable) {
          // no-op
        }
        if (activity != null) {
          // 현재 액티비티 기준으로 재시작
          activity.startActivity(restartIntent)
        } else {
          // Activity 없으면 Context 기준으로라도 시작
          appContext.startActivity(restartIntent)
        }
        promise.resolve(true)
      }
    } catch (e: Exception) {
      promise.reject("E_RESTART", e)
    }
  }

  /**
   * 현재 Activity recreate (테마/언어 변경 등)
   */
  @ReactMethod
  fun recreate(promise: Promise) {
    try {
      UiThreadUtil.runOnUiThread {
        val activity = reactApplicationContext.currentActivity
        if (activity == null) {
          promise.reject("E_RECREATE", "No current activity")
          return@runOnUiThread
        }
        try {
          activity.overridePendingTransition(0, 0)
          activity.recreate()
          activity.overridePendingTransition(0, 0)
          promise.resolve(true)
        } catch (e: Exception) {
          promise.reject("E_RECREATE", e)
        }
      }
    } catch (e: Exception) {
      promise.reject("E_RECREATE", e)
    }
  }

  /**
   * 앱 종료
   * - 여기서는 finishAffinity() + 프로세스 종료를 유지 (원래 의도대로)
   */
  @ReactMethod
  fun exitApp(promise: Promise) {
    try {
      UiThreadUtil.runOnUiThread {
        val activity = reactApplicationContext.currentActivity
        try {
          activity?.overridePendingTransition(0, 0)
        } catch (_: Throwable) {
          // no-op
        }
        promise.resolve(true)
        activity?.finishAffinity()
        // 완전 종료가 목적이니까 여기서는 그대로 사용
        kotlin.system.exitProcess(0)
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
