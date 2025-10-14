import { NativeModules, Platform, BackHandler } from 'react-native';

type NativeType = {
  restart?: () => Promise<boolean>;
  recreate?: () => Promise<boolean>;
  exitApp?: () => Promise<boolean>;
  reload?: () => Promise<boolean>;
  isReady: () => Promise<boolean>;
};

const { AppControlLite } = NativeModules as { AppControlLite?: NativeType };

function has(method: keyof NativeType) {
  return !!AppControlLite && typeof AppControlLite[method] === 'function';
}

export async function safeRestart() {
  try {
    if (Platform.OS === 'android') {
      if (has('restart')) return await AppControlLite!.restart!();
      if (has('recreate')) return await AppControlLite!.recreate!();
      BackHandler.exitApp();
      return true;
    } else {
      if (has('reload')) return await AppControlLite!.reload!();
      return false;
    }
  } catch {
    if (Platform.OS === 'android') BackHandler.exitApp();
    return false;
  }
}

export async function appExit() {
  if (Platform.OS === 'android' && has('exitApp')) {
    return AppControlLite!.exitApp!();
  }
  return false;
}

export async function isModuleReady() {
  try {
    return !!(await AppControlLite?.isReady?.());
  } catch {
    return false;
  }
}
