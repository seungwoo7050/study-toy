# Android Kotlin Track

학습자가 **안드로이드 네이티브(Kotlin)** 개발을 연습할 수 있도록 최소한의 Compose 샘플 앱과 가이드를 제공합니다. Android Studio에서 열어 바로 빌드/실행하거나, Gradle wrapper(`./gradlew`)로 CLI 빌드를 수행할 수 있습니다.

## 포함 프로젝트

| 프로젝트 | 설명 | 주요 학습 포인트 |
|----------|------|------------------|
| `hello-kotlin-todo` | Jetpack Compose 기반의 단일 화면 To-Do 샘플 | Activity/Compose 연결, 상태 호이스팅, 리스트/폼 UI, 미리보기 | 

## 빠른 시작

1. **Android Studio 설치**: Arctic Fox 이상을 권장합니다. SDK Platform 34, Build-Tools 34, Android Emulator(옵션)를 설치하세요.
2. **프로젝트 열기**: `android/hello-kotlin-todo`를 Android Studio로 Open(또는 `./gradlew tasks`로 wrapper 정상 동작 확인).
3. **에뮬레이터/디바이스 준비**: `adb devices`로 연결 상태를 확인합니다.
4. **실행**: Android Studio의 Run(▶️) 또는 CLI `./gradlew :app:installDebug` 후 `adb shell am start -n com.example.hellotodo/.MainActivity`.
5. **학습 가이드**: [guides/ANDROID_KOTLIN.md](../guides/ANDROID_KOTLIN.md)를 따라 UI/상태/테스트 단계를 진행합니다.

## 디렉터리 구조
```
android/
└── hello-kotlin-todo/
    ├── app/                     # 실제 앱 모듈
    │   ├── src/main/java/...    # Kotlin 소스 (MainActivity, UI 컴포저블)
    │   ├── src/main/res/        # 리소스(문자열/테마)
    │   └── AndroidManifest.xml  # 매니페스트
    ├── build.gradle.kts         # 루트 빌드 스크립트
    ├── settings.gradle.kts      # Gradle 설정
    └── gradle/… + gradlew       # Gradle wrapper
```

## 테스트
- `./gradlew lint` : 기본 정적분석
- `./gradlew test` : JVM 단위 테스트 (필요 시 `app/src/test`에 추가)
- `./gradlew connectedAndroidTest` : 디바이스/에뮬레이터 연결 시 UI 테스트

> 참고: Android 빌드는 SDK가 필요하므로 CI에서는 Android 전용 워크플로우를 추가하거나 로컬에서 실행하세요.
