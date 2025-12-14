package com.example.hellotodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.hellotodo.ui.theme.HelloKotlinTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloKotlinTodoTheme {
                val tasks = remember { mutableStateListOf<Task>() }
                var draft by rememberSaveable { mutableStateOf("") }

                HomeScreen(
                    tasks = tasks,
                    draft = draft,
                    onDraftChange = { draft = it },
                    onAdd = { text ->
                        if (text.isNotBlank()) {
                            tasks.add(Task(id = System.currentTimeMillis(), title = text.trim()))
                            draft = ""
                        }
                    },
                    onToggle = { id ->
                        val index = tasks.indexOfFirst { it.id == id }
                        if (index >= 0) {
                            tasks[index] = tasks[index].copy(done = !tasks[index].done)
                        }
                    }
                )
            }
        }
    }
}

data class Task(
    val id: Long,
    val title: String,
    val done: Boolean = false
)
