# hello-kotlin-todo

Jetpack Compose 기반의 단일 화면 To-Do 샘플 앱입니다. 간단한 상태 관리와 Material 3 컴포넌트를 실습하기 위한 최소 구성이며, Android Studio에서 바로 실행하거나 `./gradlew`로 빌드할 수 있습니다.

## 특징
- **Jetpack Compose**: Activity에서 Compose로 화면을 렌더링하고 `HomeScreen`에서 상태 호이스팅 패턴을 사용합니다.
- **경량 상태 관리**: `mutableStateListOf`와 `rememberSaveable`을 사용해 화면 회전에도 리스트 상태가 유지되도록 구성했습니다.
- **UI 구성 요소**: `LazyColumn`, `OutlinedTextField`, `Button`, `Checkbox` 등 기본 Material 3 컴포넌트 활용.
- **미리보기**: `HomePreview`를 통해 다양한 상태를 즉시 확인할 수 있습니다.

## 실행 방법
```bash
cd android/hello-kotlin-todo
./gradlew tasks                  # wrapper 동작 확인
./gradlew :app:assembleDebug     # 디버그 빌드
./gradlew :app:installDebug      # 에뮬레이터/기기에 설치 (adb 연결 필요)
adb shell am start -n com.example.hellotodo/.MainActivity
```

## 학습 확장 아이디어
- ViewModel + StateFlow로 상태를 옮겨 테스트와 라이프사이클 대응 강화
- Room/Datastore를 이용해 간단한 영속화 추가
- Compose UI 테스트(`androidTest/`) 추가하여 체크박스 토글/입력 검증

## 프로젝트 구조
```
app/
├── src/main/java/com/example/hellotodo
│   ├── MainActivity.kt      # Activity 및 상태 보유
│   ├── HomeScreen.kt        # Compose UI 및 콜백
│   └── ui/theme/...         # 테마 스켈레톤
├── src/main/res/values
│   ├── colors.xml           # 테마 색상
│   └── strings.xml          # 문자열 리소스
└── AndroidManifest.xml
```

> Android SDK/Platform이 설치되어 있어야 실제 빌드가 가능합니다. CI에서 Android 빌드를 추가할 경우 `ANDROID_HOME`/`JAVA_HOME` 설정 및 SDK 설치 단계를 포함하세요.
