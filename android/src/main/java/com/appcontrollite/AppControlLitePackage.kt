package com.appcontrollite

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class AppControlLitePackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext)
    : List<NativeModule> = listOf(AppControlLiteModule(reactContext))

  override fun createViewManagers(reactContext: ReactApplicationContext)
    : List<ViewManager<*, *>> = emptyList()
}
