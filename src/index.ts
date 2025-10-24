import {
  NativeModules,
  Platform,
  BackHandler,
  DevSettings,
} from "react-native";

type NativeType = {
  restart?: () => Promise<boolean>;
  recreate?: () => Promise<boolean>;
  exitApp?: () => Promise<boolean>;
  reload?: () => Promise<boolean>;
  isReady?: () => Promise<boolean>;
};

const AppControlLite = (NativeModules as Record<string, unknown>)
  ?.AppControlLite as NativeType | undefined;

function has<K extends keyof NativeType>(method: K): boolean {
  return !!AppControlLite && typeof AppControlLite[method] === "function";
}

export async function safeRestart(): Promise<boolean> {
  try {
    if (Platform.OS === "android") {
      if (has("restart")) return await AppControlLite!.restart!();
      if (has("recreate")) return await AppControlLite!.recreate!();
      BackHandler.exitApp();
      return true;
    } else {
      if (has("reload")) return await AppControlLite!.reload!();
      if (typeof DevSettings?.reload === "function") {
        DevSettings.reload();
        return true;
      }
      return false;
    }
  } catch {
    if (Platform.OS === "android") BackHandler.exitApp();
    return Platform.OS === "android";
  }
}

export async function appExit(): Promise<boolean> {
  try {
    if (Platform.OS === "android") {
      if (has("exitApp")) return await AppControlLite!.exitApp!();
      BackHandler.exitApp();
      return true;
    }
    return false;
  } catch {
    if (Platform.OS === "android") BackHandler.exitApp();
    return Platform.OS === "android";
  }
}

export async function isModuleReady(): Promise<boolean> {
  try {
    return !!(await AppControlLite?.isReady?.());
  } catch {
    return false;
  }
}
