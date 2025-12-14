# Android Kotlin 학습 가이드

Jetpack Compose와 현대 Android 앱 설계를 빠르게 익히기 위한 가이드입니다. `android/hello-kotlin-todo` 샘플을 따라가며 단계별로 UI/상태/테스트를 확장할 수 있습니다.

## 0. 사전 준비
- **IDE**: Android Studio (Arctic Fox 이상)
- **SDK**: Android 34 / Build-Tools 34 / Platform-Tools
- **디바이스**: Emulator(API 30 이상) 또는 실기기 + USB 디버깅
- **CLI**: `adb`, `./gradlew`

## 1. 프로젝트 둘러보기
- `MainActivity` : Activity ↔ Compose 브릿지, `setContent { ... }`
- `HomeScreen` : 상태 호이스팅 예제, 리스트 + 입력 폼
- `ui/theme/*` : Material 3 테마 스켈레톤 (Color/Type/Theme)

## 2. 필수 Compose 패턴
1) **상태 호이스팅**
```kotlin
@Composable
fun HomeScreen(
    tasks: List<Task>,
    onAdd: (String) -> Unit,
    onToggle: (Long) -> Unit
)
```
- 외부에서 상태를 전달하고, UI는 이벤트 콜백만 발생시켜 테스트가 쉽고 재사용 가능합니다.

2) **미리보기(Preview)**
- `@Preview(showBackground = true)`로 빠르게 UI를 확인하세요.
- `HomePreview()`를 수정해 다른 케이스(빈 리스트, 긴 텍스트)를 추가해 보세요.

3) **리스트 + 입력 폼**
- `LazyColumn`으로 스크롤 가능한 리스트를 구성하고, `OutlinedTextField`/`Button` 조합으로 입력 UI를 만듭니다.

## 3. 권장 실습 시나리오
- [ ] **기능 추가**: 완료 필터(전체/미완료/완료) 토글 추가
- [ ] **UI 개선**: `Snackbar`로 추가/에러 메시지 표시
- [ ] **테스트**: `HomeScreen`을 Compose 테스트로 검증 (`compose-bom` + `ui-test-junit4` 추가)
- [ ] **데이터 계층**: 간단한 `ViewModel` + `StateFlow` 버전으로 확장하여 화면 회전 시 상태 유지 실험
- [ ] **빌드/배포**: `./gradlew assembleRelease` + keystore 서명 흐름 학습

## 4. CLI 빌드/테스트 예시
```bash
cd android/hello-kotlin-todo
./gradlew tasks            # wrapper 동작 확인
./gradlew :app:assembleDebug
./gradlew :app:lint        # 정적분석
./gradlew :app:testDebugUnitTest
# 에뮬레이터 실행 중일 때
./gradlew :app:connectedAndroidTest
```

## 5. 참고 링크
- 공식 Compose 가이드: https://developer.android.com/jetpack/compose/documentation
- MAD Skills Playlist: https://www.youtube.com/playlist?list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE
- Testing Compose: https://developer.android.com/jetpack/compose/testing
