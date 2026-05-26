package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Category
import com.example.data.Task
import com.example.data.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository
    private val sharedPrefs = application.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

    // UI state for all categories
    val categories: StateFlow<List<Category>>

    // Live state of the selected category id
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    // Flag to prevent double seeding
    private var isSeedingInProgress = false

    // View mode: "Chips" or "Selector"
    private val _viewMode = MutableStateFlow("Chips")
    val viewMode: StateFlow<String> = _viewMode.asStateFlow()

    // App Preferences (Theme, Language, Sync accounts)
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("PT-BR")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    // Tasks for the currently selected category
    val currentTasks: StateFlow<List<Task>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TodoRepository(database.todoDao())

        // Load saved preferences
        _viewMode.value = sharedPrefs.getString("view_mode", "Chips") ?: "Chips"
        _isDarkTheme.value = sharedPrefs.getBoolean("is_dark_theme", false)
        _selectedLanguage.value = sharedPrefs.getString("selected_language", "PT-BR") ?: "PT-BR"
        _isLoggedIn.value = sharedPrefs.getBoolean("is_logged_in", false)
        _userEmail.value = sharedPrefs.getString("user_email", null)

        // Load saved category ID if it exists
        val savedCategoryId = sharedPrefs.getLong("selected_category_id", -1L)
        if (savedCategoryId != -1L) {
            _selectedCategoryId.value = savedCategoryId
        }

        categories = repository.allCategories
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Observe categories to seed default values if empty
        viewModelScope.launch {
            categories.collect { list ->
                if (list.isEmpty() && !isSeedingInProgress) {
                    isSeedingInProgress = true
                    val generalId = repository.insertCategory("Geral")
                    repository.insertTask(generalId, "Comprar pão 🥖")
                    repository.insertTask(generalId, "Estudar Kotlin 📝")

                    val workId = repository.insertCategory("Trabalho")
                    repository.insertTask(workId, "Reunião de equipe 💼")
                    repository.insertTask(workId, "Enviar relatório mensal 📊")

                    _selectedCategoryId.value = generalId
                    sharedPrefs.edit().putLong("selected_category_id", generalId).apply()
                    isSeedingInProgress = false
                } else if (list.isNotEmpty() && _selectedCategoryId.value == null) {
                    // Default to first category if none is selected
                    val firstId = list.first().id
                    _selectedCategoryId.value = firstId
                    sharedPrefs.edit().putLong("selected_category_id", firstId).apply()
                } else if (list.isNotEmpty() && _selectedCategoryId.value != null) {
                    // Check if selected category still exists, if not, select the first
                    val exists = list.any { it.id == _selectedCategoryId.value }
                    if (!exists) {
                        val firstId = list.first().id
                        _selectedCategoryId.value = firstId
                        sharedPrefs.edit().putLong("selected_category_id", firstId).apply()
                    }
                }
            }
        }

        // Map selected category ID to its live tasks Flow
        currentTasks = _selectedCategoryId
            .flatMapLatest { id ->
                if (id != null) {
                    repository.getTasks(id)
                } else {
                    flowOf(emptyList())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun selectCategory(id: Long) {
        _selectedCategoryId.value = id
        sharedPrefs.edit().putLong("selected_category_id", id).apply()
    }

    fun setViewMode(mode: String) {
        if (mode == "Chips" || mode == "Selector") {
            _viewMode.value = mode
            sharedPrefs.edit().putString("view_mode", mode).apply()
        }
    }

    fun addCategory(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isNotEmpty() && trimmedName.length <= 25) {
            viewModelScope.launch {
                val newId = repository.insertCategory(trimmedName)
                selectCategory(newId)
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategory(id)
            // After deletion, matching in the categories collector resets _selectedCategoryId
        }
    }

    fun deleteCategories(ids: List<Long>) {
        viewModelScope.launch {
            repository.deleteCategories(ids)
        }
    }

    fun addTask(title: String) {
        val trimmedTitle = title.trim()
        val currentCatId = _selectedCategoryId.value
        if (trimmedTitle.isNotEmpty() && currentCatId != null) {
            viewModelScope.launch {
                repository.insertTask(currentCatId, trimmedTitle)
            }
        }
    }

    fun addTaskToCategory(title: String, categoryId: Long) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isNotEmpty()) {
            viewModelScope.launch {
                repository.insertTask(categoryId, trimmedTitle)
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        sharedPrefs.edit().putBoolean("is_dark_theme", newTheme).apply()
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        sharedPrefs.edit().putString("selected_language", lang).apply()
    }

    fun loginWithGoogle(email: String) {
        _isLoggedIn.value = true
        _userEmail.value = email
        sharedPrefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .apply()
    }

    fun logout() {
        _isLoggedIn.value = false
        _userEmail.value = null
        sharedPrefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("user_email")
            .apply()
    }
}
