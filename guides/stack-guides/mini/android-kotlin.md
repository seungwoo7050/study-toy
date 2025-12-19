# Android Kotlin 학습 가이드

Jetpack Compose와 현대 Android 앱 설계를 빠르게 익히기 위한 가이드입니다. `android/hello-kotlin-todo` 샘플을 따라가며 단계별로 UI/상태/테스트를 확장할 수 있습니다.

## 0. 사전 준비
- **IDE**: Android Studio (최신 안정 버전 권장)
- **SDK**: Android 34 (또는 최신) / Build-Tools / Platform-Tools
- **디바이스**: Emulator(API 30 이상) 또는 실기기 + USB 디버깅
- **CLI**: `adb`, `./gradlew`

### 0.1 새 프로젝트로 시작하는 경우(Compose 템플릿)

샘플 프로젝트가 없다면, 아래 기준으로 새 프로젝트를 만들고 문서의 예제를 붙여가며 진행해도 된다.

1) Android Studio → New Project  
2) “Empty Activity (Compose)” 또는 “Empty Compose Activity” 선택  
3) Minimum SDK: API 30+ 권장(학습용)  
4) 생성 후 앱 실행(에뮬레이터/실기기)까지 먼저 확인

> 목표: “빌드/실행이 된다”를 먼저 확보하고, 그 다음 UI/상태/테스트를 확장한다.


## 1. 프로젝트 둘러보기
- `MainActivity` : Activity ↔ Compose 브릿지, `setContent { ... }`
- `HomeScreen` : 상태 호이스팅 예제, 리스트 + 입력 폼
- `ui/theme/*` : Material 3 테마 스켈레톤 (Color/Type/Theme)

### 1.1 추천 폴더 구조(학습용)

```text
app/src/main/java/...
  ui/
    HomeScreen.kt
    components/
      TaskItem.kt
  model/
    Task.kt
  vm/
    HomeViewModel.kt
```


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

## 2.1 최소 동작 스켈레톤(복붙용)

샘플 프로젝트가 없거나, 현재 코드가 너무 달라서 막히면 아래 코드로 “동작하는 최소 Todo”를 먼저 만든 뒤 확장하세요.

### 2.1.1 모델

```kotlin
// model/Task.kt
data class Task(
    val id: Long,
    val title: String,
    val done: Boolean = false
)
```

### 2.1.2 UI (상태 호이스팅 유지)

```kotlin
// ui/HomeScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Task

@Composable
fun HomeScreen(
    tasks: List<Task>,
    onAdd: (String) -> Unit,
    onToggle: (Long) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Todo", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("할 일을 입력하세요") },
                singleLine = true
            )
            Button(
                onClick = {
                    val trimmed = text.trim()
                    if (trimmed.isNotEmpty()) {
                        onAdd(trimmed)
                        text = ""
                    }
                }
            ) { Text("추가") }
        }

        Spacer(Modifier.height(12.dp))

        if (tasks.isEmpty()) {
            Text("할 일이 없습니다.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { onToggle(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, onToggle: () -> Unit) {
    Surface(
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(checked = task.done, onCheckedChange = { onToggle() })
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
```

### 2.1.3 ViewModel + StateFlow (회전/재구성 대응)

```kotlin
// vm/HomeViewModel.kt
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import model.Task

class HomeViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun add(title: String) {
        val id = System.currentTimeMillis()
        _tasks.update { prev -> listOf(Task(id = id, title = title)) + prev }
    }

    fun toggle(id: Long) {
        _tasks.update { prev ->
            prev.map { t -> if (t.id == id) t.copy(done = !t.done) else t }
        }
    }
}
```

### 2.1.4 MainActivity 연결 예시

```kotlin
// MainActivity.kt (핵심 부분만)
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import ui.HomeScreen
import vm.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: HomeViewModel = viewModel()
            val tasks by vm.tasks.collectAsState()

            HomeScreen(
                tasks = tasks,
                onAdd = vm::add,
                onToggle = vm::toggle
            )
        }
    }
}
```


## 3. 권장 실습 시나리오
- [ ] **기능 추가**: 완료 필터(전체/미완료/완료) 토글 추가
- [ ] **UI 개선**: `Snackbar`로 추가/에러 메시지 표시
- [ ] **테스트**: `HomeScreen`을 Compose 테스트로 검증 (`compose-bom` + `ui-test-junit4` 추가)
- [ ] **데이터 계층**: 간단한 `ViewModel` + `StateFlow` 버전으로 확장하여 화면 회전 시 상태 유지 실험
- [ ] **빌드/배포**: `./gradlew assembleRelease` + keystore 서명 흐름 학습

### 3.1 Snackbar(Scaffold) 예시 스니펫

```kotlin
// (아이디어) add가 성공/실패했을 때 Snackbar 띄우기
// HomeScreen 내부 또는 상위에서 Scaffold를 두고 SnackbarHostState를 사용
val snackbarHostState = remember { SnackbarHostState() }

Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) }
) { padding ->
    // content...
}

// 예: 버튼 클릭 후
// scope.launch { snackbarHostState.showSnackbar("추가 완료") }
```

### 3.2 Compose UI 테스트 최소 예시(복붙용)

```kotlin
// androidTest/.../HomeScreenTest.kt
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import ui.HomeScreen
import model.Task

class HomeScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun showsEmptyMessage_whenNoTasks() {
        rule.setContent {
            HomeScreen(
                tasks = emptyList(),
                onAdd = {},
                onToggle = {}
            )
        }
        rule.onNodeWithText("할 일이 없습니다.").assertExists()
    }

    @Test
    fun showsTaskTitle_whenTasksExist() {
        rule.setContent {
            HomeScreen(
                tasks = listOf(Task(id = 1L, title = "React 공부")),
                onAdd = {},
                onToggle = {}
            )
        }
        rule.onNodeWithText("React 공부").assertExists()
    }
}
```


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
