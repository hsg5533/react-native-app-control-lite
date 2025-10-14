# react-native-app-control-lite

경량 리스타트/리로드 네이티브 모듈입니다.  
`react-native-restart`의 완벽한 대체로, **릴리즈 빌드에서도 안전하게 동작**합니다.

- **Android**
  - `restart()` : 런처 액티비티 재기동 + 프로세스 종료
  - `recreate()` : 현재 액티비티만 재생성
  - `exitApp()` : 앱 즉시 종료
- **iOS**
  - `reload()` : JS 브리지 리로드 (정책상 완전 재시작 불가)
- **JS**
  - `safeRestart()` : 플랫폼별 최선의 방식으로 재시작
  - `appExit()` : 앱 종료
  - `isModuleReady()` : 모듈 로딩 확인

---

## 📦 설치 방법

### 1️⃣ 로컬 설치 (zip 혹은 소스 직접 포함)

1. `react-native-app-control-lite` 폴더를 프로젝트에 복사하거나 zip 파일을 풀어서 넣습니다.  
   예시 구조:
   ```
   your-project/
     packages/
       react-native-app-control-lite/
   ```

2. 로컬 경로로 패키지를 추가합니다.
   ```bash
   # yarn
   yarn add ./packages/react-native-app-control-lite

   # 또는 npm
   npm install ./packages/react-native-app-control-lite
   ```

3. iOS 빌드 준비:
   ```bash
   cd ios && pod install && cd ..
   ```

---

### 2️⃣ npm 패키지로 직접 설치 (공개 배포 시)

1. 모듈을 npm에 배포하려면:
   ```bash
   npm login
   npm publish --access public
   ```

2. 앱 프로젝트에서 아래처럼 바로 설치할 수 있습니다.
   ```bash
   # yarn
   yarn add react-native-app-control-lite

   # npm
   npm install react-native-app-control-lite
   ```

3. iOS 설정:
   ```bash
   cd ios && pod install && cd ..
   ```

---

### 3️⃣ Android Proguard 설정 (필수)

릴리즈 빌드 시 모듈이 삭제되지 않도록  
`android/app/proguard-rules.pro` 파일에 아래를 추가하세요:

```proguard
-keep class com.appcontrollite.** { *; }
```

---

## 🧩 사용 예시

```tsx
import { safeRestart, appExit, isModuleReady } from 'react-native-app-control-lite';

// 안전한 앱 재시작 (Android: restart → recreate → exitApp 순으로 폴백)
await safeRestart();

// Android 앱 종료
await appExit();

// 모듈 정상 로딩 확인
const ok = await isModuleReady();
console.log('Module ready:', ok);
```

기존 `react-native-restart` 교체 예시:

```diff
- import RNRestart from 'react-native-restart';
+ import { safeRestart } from 'react-native-app-control-lite';

- RNRestart.Restart();
+ safeRestart();
```

---

## ⚙️ API 요약

| 플랫폼 | 메서드 | 설명 |
|---------|--------|------|
| 공통 | `safeRestart()` | 플랫폼별 최적의 재시작 수행 |
| 공통 | `isModuleReady()` | 네이티브 모듈 로드 여부 |
| Android | `restart()` | 런처 액티비티 재시작 + 프로세스 종료 |
| Android | `recreate()` | 현재 액티비티 재생성 |
| Android | `exitApp()` | 프로세스 종료 |
| iOS | `reload()` | JS 브리지 리로드 (전체 앱 재시작은 불가) |

---

## 🧠 트러블슈팅

### 🔹 모듈이 undefined로 나오는 경우
- `npx react-native config | grep appcontrollite` 로 autolink 인식 여부 확인  
- `pod install` 실행 여부 점검  
- Android는 Proguard 규칙 추가 확인

### 🔹 릴리즈 빌드에서만 작동하지 않을 때
- Proguard 규칙 누락 여부 확인  
- `multiDexEnabled true` 설정 여부 확인 (`defaultConfig` 내부)

### 🔹 Hermes 사용
문제 없습니다. 소스맵 업로드로 스택 매핑을 하면 릴리즈 로그도 정상적으로 매핑됩니다.

---

## 🧑‍💻 라이선스
MIT  
Copyright © 2025
