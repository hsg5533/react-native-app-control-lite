#import "AppControlLiteModule.h"
#import <React/RCTBridge.h>
#import <React/RCTReloadCommand.h>

@implementation AppControlLite
RCT_EXPORT_MODULE(AppControlLite);

RCT_EXPORT_METHOD(reload:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    RCTTriggerReloadCommandListeners(@"Manual reload");
    resolve(@(YES));
  } @catch (NSException *exception) {
    reject(@"E_RELOAD", exception.reason, nil);
  }
}

RCT_EXPORT_METHOD(isReady:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  resolve(@(YES));
}

@end
