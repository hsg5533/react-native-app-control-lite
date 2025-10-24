#import "AppControlLiteModule.h"
#import <React/RCTBridge.h>

#if __has_include(<React/RCTReloadCommand.h>)
  #import <React/RCTReloadCommand.h>
  #define HAS_RCT_RELOAD 1
#else
  #define HAS_RCT_RELOAD 0
#endif

#if __has_include(<React/RCTDevSettings.h>)
  #import <React/RCTDevSettings.h>
  #define HAS_DEVSETTINGS 1
#else
  #define HAS_DEVSETTINGS 0
#endif

@implementation AppControlLite
RCT_EXPORT_MODULE(AppControlLite);

@synthesize bridge = _bridge;

+ (BOOL)requiresMainQueueSetup
{
  // 초기화 시 메인큐가 꼭 필요하진 않지만, 안전하게 NO 유지
  return NO;
}

RCT_EXPORT_METHOD(reload:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    dispatch_async(dispatch_get_main_queue(), ^{
#if HAS_RCT_RELOAD
      // RN의 공식 Reload 커맨드(버전별 유무 고려)
      RCTTriggerReloadCommandListeners(@"Manual reload from AppControlLite");
      resolve(@(YES));
      return;
#elif HAS_DEVSETTINGS
      // 일부 버전/환경(DEV)에선 DevSettings를 통해 리로드 가능
      RCTDevSettings *dev = [self.bridge moduleForClass:[RCTDevSettings class]];
      if (dev && [dev respondsToSelector:@selector(reload)]) {
        [dev reload];
        resolve(@(YES));
        return;
      }
#endif
      // 릴리즈 빌드(DevSupport 꺼짐) 또는 헤더 부재 시
      reject(@"E_RELOAD_UNAVAILABLE",
             @"Reload command is not available in this build/config.",
             nil);
    });
  } @catch (NSException *exception) {
    reject(@"E_RELOAD", exception.reason, nil);
  }
}

RCT_EXPORT_METHOD(isReady:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  // 간단한 헬스체크
  resolve(@(YES));
}

@end
