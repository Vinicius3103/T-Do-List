package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Category
import com.example.data.Task
import com.example.ui.TodoViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TodoAppScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Visual asset translator helper
fun getTranslate(key: String, lang: String): String {
    return when (lang) {
        "EN" -> when (key) {
            "app_title" -> "Listify"
            "settings" -> "Settings"
            "view_mode" -> "View Mode"
            "theme" -> "Theme Style"
            "light" -> "Light"
            "dark" -> "Dark"
            "about" -> "About App"
            "about_desc" -> "Listify - Geometric Balance Edition\nVersion 1.0.0 (Build 2026)\n\nDeveloped with Kotlin, Jetpack Compose, and Room Database."
            "email_prompt" -> "Enter your Google account email to sync"
            "sync_button" -> "Sync with Google"
            "synced_as" -> "Synced: "
            "sync_desc" -> "Saves your categories and tasks securely to easily organize and access them in real-time."
            "login_prompt" -> "Google Cloud Sync"
            "logout" -> "Disconnect"
            "add_fab" -> "Create Item"
            "task_tab" -> "Task"
            "category_tab" -> "Category"
            "task_name_field" -> "Task Name"
            "category_name_field" -> "Category Name"
            "select_cat_dropdown" -> "Choose Category"
            "ok" -> "Ok"
            "cancel" -> "Cancel"
            "no_tasks" -> "No pending tasks"
            "no_tasks_helper" -> "Click the floating + button at the bottom right to start organizing your list! ✨"
            "tasks_in" -> "Tasks in "
            "delete_cat_title" -> "Delete Category"
            "delete_cat_desc" -> "This action is irreversible and will permanently delete the category and all its associated tasks."
            "delete_cat_confirm" -> "To confirm, type the exact category name: "
            "delete_cat_btn" -> "Delete"
            "language" -> "App Language"
            "close" -> "Close"
            else -> key
        }
        else -> when (key) {
            "app_title" -> "Listify"
            "settings" -> "Configurações"
            "view_mode" -> "Modo Visualização"
            "theme" -> "Modo do Tema"
            "light" -> "Claro"
            "dark" -> "Escuro"
            "about" -> "Sobre o App"
            "about_desc" -> "Listify - Edição Geometric Balance\nVersão 1.0.0 (Build 2026)\n\nDesenvolvido com Kotlin, Jetpack Compose e Banco de dados Room."
            "email_prompt" -> "Digite seu e-mail do Google para sincronização"
            "sync_button" -> "Sincronizar com o Google"
            "synced_as" -> "Sincronizado: "
            "sync_desc" -> "Sincronize sua conta do Google para armazenar e organizar suas tarefas com facilidade de qualquer dispositivo."
            "login_prompt" -> "Sincronização Nuvem"
            "logout" -> "Desconectar"
            "add_fab" -> "Criar Item"
            "task_tab" -> "Tarefa"
            "category_tab" -> "Categoria"
            "task_name_field" -> "Nome da Tarefa"
            "category_name_field" -> "Nome da Categoria"
            "select_cat_dropdown" -> "Categoria da Tarefa"
            "ok" -> "Ok"
            "cancel" -> "Cancelar"
            "no_tasks" -> "Nenhuma tarefa pendente"
            "no_tasks_helper" -> "Clique no botão + flutuante abaixo para começar a organizar sua lista! ✨"
            "tasks_in" -> "Tarefas em "
            "delete_cat_title" -> "Excluir Categoria"
            "delete_cat_desc" -> "Esta ação é irreversível e excluirá permanentemente a categoria e todas as suas tarefas."
            "delete_cat_confirm" -> "Para confirmar, digite abaixo o nome exato da categoria para prosseguir: "
            "delete_cat_btn" -> "Excluir permanentemente"
            "language" -> "Idioma do App"
            "close" -> "Fechar"
            else -> key
        }
    }
}

@Composable
fun TodoAppScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val currentTasks by viewModel.currentTasks.collectAsStateWithLifecycle()

    // Config states
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedLang by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    // UI Interactive States
    var isDrawerOpen by remember { mutableStateOf(false) }
    var showCreatorDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var selectedCategoriesToDelete by remember { mutableStateOf(setOf<Long>()) }

    // Color Theme Styling Map
    val bgColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9)
    val cardBgColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val supportingTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
    val borderColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9)
    val inputBgColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val accentTextColor = if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0F172A)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Card container following "Geometric Balance"
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Toolbar Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 18.dp, end = 18.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Brand Segment
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Logo",
                                tint = if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0F172A),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = getTranslate("app_title", selectedLang),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        // Toolbar right actions: Category Deletion and Hamburger Menu toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Category Deletion button (only visible if categories are not empty)
                            if (categories.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        selectedCategoriesToDelete = if (selectedCategoryId != null) {
                                            setOf(selectedCategoryId!!)
                                        } else {
                                            emptySet()
                                        }
                                        showDeleteConfirmDialog = true
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkTheme) Color(0xFF451A03) else Color(0xFFFEF2F2))
                                        .border(
                                            1.dp,
                                            if (isDarkTheme) Color(0xFF78350F) else Color(0xFFFEE2E2),
                                            CircleShape
                                        )
                                        .testTag("delete_category_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Excluir Categoria",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            // Hamburger Menu Toggle Button
                            IconButton(
                                onClick = { isDrawerOpen = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9))
                                    .testTag("hamburger_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir Configurações",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = borderColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )

                    // Categories visual component depending on active viewMode preference
                    if (viewMode == "Chips") {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(end = 16.dp)
                        ) {
                            items(categories) { category ->
                                val isSelected = category.id == selectedCategoryId
                                val contentColor = if (isSelected) {
                                    if (isDarkTheme) Color(0xFF0F172A) else Color.White
                                } else textColor

                                val bgColorChip = if (isSelected) {
                                    if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                } else {
                                    if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(60.dp))
                                        .background(bgColorChip)
                                        .clickable { viewModel.selectCategory(category.id) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .testTag("category_chip_${category.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category.name,
                                        color = contentColor,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        // Dropdown Selector visualization format
                        var isDropdownExpanded by remember { mutableStateOf(false) }
                        val activeCategory = categories.find { it.id == selectedCategoryId }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(60.dp))
                                    .background(if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                    .border(1.dp, borderColor, RoundedCornerShape(60.dp))
                                    .clickable { isDropdownExpanded = true }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = activeCategory?.name ?: "...",
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Expandir Dropdown",
                                    tint = textColor
                                )
                            }

                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(cardBgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                category.name,
                                                color = textColor,
                                                fontWeight = if (category.id == selectedCategoryId) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectCategory(category.id)
                                            isDropdownExpanded = false
                                        },
                                        modifier = Modifier.testTag("dropdown_item_${category.id}")
                                    )
                                }
                            }
                        }
                    }

                    // Display tag of the selected Category list
                    val activeCategory = categories.find { it.id == selectedCategoryId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getTranslate("tasks_in", selectedLang),
                            fontSize = 13.sp,
                            color = supportingTextColor
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = activeCategory?.name ?: "...",
                            fontSize = 13.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Task List container holding scroll list AND FAB
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        if (currentTasks.isEmpty()) {
                            // Customized empty state layout
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Lista Vazia",
                                    tint = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = getTranslate("no_tasks", selectedLang),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = getTranslate("no_tasks_helper", selectedLang),
                                    fontSize = 13.sp,
                                    color = supportingTextColor,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp) // extra padding to avoid overlapping the Floating Button!
                            ) {
                                items(currentTasks, key = { it.id }) { task ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF8FAFC))
                                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                            .testTag("task_item_${task.id}"),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Custom Geometric Checkbox Circle
                                        val checkedBg = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                        val uncheckedBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
                                        val checkBorder = if (task.isCompleted) {
                                            if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                        } else {
                                            if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (task.isCompleted) checkedBg else uncheckedBg)
                                                .border(2.dp, checkBorder, CircleShape)
                                                .clickable { viewModel.toggleTaskCompletion(task) }
                                                .testTag("task_checkbox_${task.id}"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (task.isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Concluída",
                                                    tint = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Task description text
                                        Text(
                                            text = task.title,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 6.dp),
                                            fontSize = 15.sp,
                                            color = if (task.isCompleted) supportingTextColor else textColor,
                                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                        )

                                        // Delete closing Cross button
                                        IconButton(
                                            onClick = { viewModel.deleteTask(task.id) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .testTag("delete_task_button_${task.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Excluir Tarefa",
                                                tint = if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Floating action button redondo, destacado e flutuando no canto inferior direito
                        FloatingActionButton(
                            onClick = { showCreatorDialog = true },
                            containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                            contentColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(56.dp)
                                .testTag("floating_add_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = getTranslate("add_fab", selectedLang),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Custom Settings & Language Right Hamburger Drawer overlay
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isDrawerOpen = false }
                    .testTag("drawer_backdrop")
            )
        }

        // Slide out side panel calculation
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(
                visible = isDrawerOpen,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it })
            ) {
                // Drawer Layout
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(290.dp)
                        .clickable(enabled = false) {}
                        .testTag("drawer_container"),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp)
                    ) {
                        // Title segment
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = accentTextColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = getTranslate("settings", selectedLang),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

                            IconButton(
                                onClick = { isDrawerOpen = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar menu",
                                    tint = textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = borderColor, modifier = Modifier.padding(vertical = 12.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Theme Switching Option (Modo do Tema)
                            Column {
                                Text(
                                    text = getTranslate("theme", selectedLang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = supportingTextColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(60.dp))
                                        .background(if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                        .padding(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Light Mode block
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(if (!isDarkTheme) (if (isDarkTheme) Color(0xFF1E293B) else Color.White) else Color.Transparent)
                                            .clickable { if (isDarkTheme) viewModel.toggleTheme() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getTranslate("light", selectedLang),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (!isDarkTheme) Color(0xFF0F172A) else supportingTextColor
                                        )
                                    }
                                    // Dark Mode block
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(if (isDarkTheme) (if (isDarkTheme) Color.White else Color(0xFF0F172A)) else Color.Transparent)
                                            .clickable { if (!isDarkTheme) viewModel.toggleTheme() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getTranslate("dark", selectedLang),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkTheme) Color(0xFF0F172A) else supportingTextColor
                                        )
                                    }
                                }
                            }

                            // View Mode Option (Chips vs Dropdown Seletor)
                            Column {
                                Text(
                                    text = getTranslate("view_mode", selectedLang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = supportingTextColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(60.dp))
                                        .background(if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                        .padding(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // View format 1: Chips
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(if (viewMode == "Chips") (if (isDarkTheme) Color.White else Color(0xFF0F172A)) else Color.Transparent)
                                            .clickable { viewModel.setViewMode("Chips") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Chips",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (viewMode == "Chips") (if (isDarkTheme) Color(0xFF0F172A) else Color.White) else supportingTextColor
                                        )
                                    }
                                    // View format 2: Seletor
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(if (viewMode == "Selector") (if (isDarkTheme) Color.White else Color(0xFF0F172A)) else Color.Transparent)
                                            .clickable { viewModel.setViewMode("Selector") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (selectedLang == "EN") "Selector" else "Seletor",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (viewMode == "Selector") (if (isDarkTheme) Color(0xFF0F172A) else Color.White) else supportingTextColor
                                        )
                                    }
                                }
                            }

                            // Language Setup Dropdown Option (Idioma do App)
                            Column {
                                Text(
                                    text = getTranslate("language", selectedLang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = supportingTextColor
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                var isLangExpanded by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(60.dp))
                                            .background(if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                            .clickable { isLangExpanded = true }
                                            .padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (selectedLang == "PT-BR") "Português (PT-BR)" else "English (EN)",
                                            fontSize = 13.sp,
                                            color = textColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = textColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = isLangExpanded,
                                        onDismissRequest = { isLangExpanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .background(cardBgColor)
                                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Português (PT-BR)", color = textColor, fontSize = 13.sp) },
                                            onClick = {
                                                viewModel.setLanguage("PT-BR")
                                                isLangExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("English (EN)", color = textColor, fontSize = 13.sp) },
                                            onClick = {
                                                viewModel.setLanguage("EN")
                                                isLangExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // "Sobre o App" Button option
                            Button(
                                onClick = { showAboutDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                                    contentColor = textColor
                                ),
                                shape = RoundedCornerShape(60.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = getTranslate("about", selectedLang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Google synchronization segment at bottom of Drawer
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                                .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = getTranslate("login_prompt", selectedLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = getTranslate("sync_desc", selectedLang),
                                fontSize = 11.sp,
                                color = supportingTextColor,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isLoggedIn) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Ativo",
                                            color = Color(0xFF10B981),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = userEmail ?: "vinirock1234@gmail.com",
                                            color = textColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.logout() },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isDarkTheme) Color(0xFF451A03) else Color(0xFFFEF2F2))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "Desconectar",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { showLoginDialog = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                    ),
                                    shape = RoundedCornerShape(60.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (selectedLang == "EN") "Sync Account" else "Sincronizar",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color(0xFF0F172A) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. CREATOR DIALOG (Tarefa / Categoria Selector)
        if (showCreatorDialog) {
            Dialog(onDismissRequest = { showCreatorDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var creatorTab by remember { mutableStateOf("Tarefa") } // "Tarefa" or "Categoria"

                        // Tab selection Segment
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(60.dp))
                                .background(if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9))
                                .padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // "Tarefa" toggle option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(
                                        if (creatorTab == "Tarefa") {
                                            if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                        } else Color.Transparent
                                    )
                                    .clickable { creatorTab = "Tarefa" }
                                    .testTag("tab_switch_task"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getTranslate("task_tab", selectedLang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (creatorTab == "Tarefa") {
                                        if (isDarkTheme) Color(0xFF0F172A) else Color.White
                                    } else supportingTextColor
                                )
                            }

                            // "Categoria" toggle option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(
                                        if (creatorTab == "Categoria") {
                                            if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                        } else Color.Transparent
                                    )
                                    .clickable { creatorTab = "Categoria" }
                                    .testTag("tab_switch_category"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getTranslate("category_tab", selectedLang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (creatorTab == "Categoria") {
                                        if (isDarkTheme) Color(0xFF0F172A) else Color.White
                                    } else supportingTextColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dynamic Tab views
                        if (creatorTab == "Tarefa") {
                            var taskName by remember { mutableStateOf("") }
                            var dialogCategoryId by remember { mutableStateOf<Long?>(selectedCategoryId) }

                            // If no category is loaded yet, select first category id
                            LaunchedEffect(categories) {
                                if (dialogCategoryId == null && categories.isNotEmpty()) {
                                    dialogCategoryId = categories.first().id
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // 1. Task Name Text Field
                                OutlinedTextField(
                                    value = taskName,
                                    onValueChange = { taskName = it },
                                    label = { Text(getTranslate("task_name_field", selectedLang), fontSize = 13.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("dialog_task_input"),
                                    shape = RoundedCornerShape(60.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = accentTextColor,
                                        unfocusedBorderColor = borderColor,
                                        focusedContainerColor = inputBgColor,
                                        unfocusedContainerColor = inputBgColor,
                                        focusedTextColor = textColor,
                                        unfocusedTextColor = textColor,
                                        focusedLabelColor = accentTextColor,
                                        unfocusedLabelColor = supportingTextColor
                                    )
                                )

                                // 2. Category Selector Dropdown
                                var isCategoryMenuExpanded by remember { mutableStateOf(false) }
                                val selectedCat = categories.find { it.id == dialogCategoryId } ?: categories.firstOrNull()

                                Column {
                                    Text(
                                        text = getTranslate("select_cat_dropdown", selectedLang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = supportingTextColor,
                                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                    )

                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(60.dp))
                                                .background(inputBgColor)
                                                .border(1.dp, borderColor, RoundedCornerShape(60.dp))
                                                .clickable { isCategoryMenuExpanded = true }
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = selectedCat?.name ?: "...",
                                                color = textColor,
                                                fontSize = 14.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = textColor
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = isCategoryMenuExpanded,
                                            onDismissRequest = { isCategoryMenuExpanded = false },
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .background(cardBgColor)
                                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        ) {
                                            categories.forEach { category ->
                                                DropdownMenuItem(
                                                    text = { Text(category.name, color = textColor, fontSize = 13.sp) },
                                                    onClick = {
                                                        dialogCategoryId = category.id
                                                        isCategoryMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Bottom submit button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showCreatorDialog = false },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        border = BorderStroke(1.dp, borderColor),
                                        shape = RoundedCornerShape(60.dp)
                                    ) {
                                        Text(
                                            text = getTranslate("cancel", selectedLang),
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val targetCatId = dialogCategoryId ?: categories.firstOrNull()?.id
                                            if (taskName.trim().isNotEmpty() && targetCatId != null) {
                                                viewModel.addTaskToCategory(taskName, targetCatId)
                                                // Automatic select target category so the user sees their task being added!
                                                viewModel.selectCategory(targetCatId)
                                                showCreatorDialog = false
                                            }
                                        },
                                        enabled = taskName.trim().isNotEmpty(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("dialog_submit_task_button"),
                                        shape = RoundedCornerShape(60.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                            contentColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                                            disabledContainerColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
                                        )
                                    ) {
                                        Text(text = "Ok", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            // CATEGORIA tab mode
                            var categoryName by remember { mutableStateOf("") }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                OutlinedTextField(
                                    value = categoryName,
                                    onValueChange = {
                                        if (it.length <= 25) categoryName = it
                                    },
                                    label = { Text(getTranslate("category_name_field", selectedLang), fontSize = 13.sp) },
                                    singleLine = true,
                                    suffix = {
                                        Text(
                                            "${categoryName.length}/25",
                                            fontSize = 11.sp,
                                            color = supportingTextColor
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("dialog_category_input"),
                                    shape = RoundedCornerShape(60.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = accentTextColor,
                                        unfocusedBorderColor = borderColor,
                                        focusedContainerColor = inputBgColor,
                                        unfocusedContainerColor = inputBgColor,
                                        focusedTextColor = textColor,
                                        unfocusedTextColor = textColor,
                                        focusedLabelColor = accentTextColor,
                                        unfocusedLabelColor = supportingTextColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showCreatorDialog = false },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        border = BorderStroke(1.dp, borderColor),
                                        shape = RoundedCornerShape(60.dp)
                                    ) {
                                        Text(
                                            text = getTranslate("cancel", selectedLang),
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (categoryName.trim().isNotEmpty()) {
                                                viewModel.addCategory(categoryName)
                                                showCreatorDialog = false
                                            }
                                        },
                                        enabled = categoryName.trim().isNotEmpty(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("dialog_submit_category_button"),
                                        shape = RoundedCornerShape(60.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                            contentColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                                            disabledContainerColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
                                        )
                                    ) {
                                        Text(text = "Ok", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. SOBRE O APP DIALOG
        if (showAboutDialog) {
            Dialog(onDismissRequest = { showAboutDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = accentTextColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = getTranslate("about", selectedLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = getTranslate("about_desc", selectedLang),
                            fontSize = 13.sp,
                            color = supportingTextColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showAboutDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                contentColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White
                            )
                        ) {
                            Text(getTranslate("close", selectedLang), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. GOOGLE ACCOUNT SIGN IN SYNC DIALOG
        if (showLoginDialog) {
            var signEmail by remember { mutableStateOf("vinirock1234@gmail.com") }
            var signPassword by remember { mutableStateOf("") }
            var isPasswordVisible by remember { mutableStateOf(false) }

            Dialog(onDismissRequest = { showLoginDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Custom Google style visual symbol
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEA4335)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF4285F4)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFBBC05)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF34A853)))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (selectedLang == "EN") "Access & Cloud Sync" else "Acesso e Sincronização",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (selectedLang == "EN") 
                                "Register your account or sync with Google" 
                                else "Cadastre sua conta ou sincronize com o Google",
                            fontSize = 12.sp,
                            color = supportingTextColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email Field
                        OutlinedTextField(
                            value = signEmail,
                            onValueChange = { signEmail = it },
                            label = { Text("E-mail", fontSize = 13.sp) },
                            placeholder = { Text("email@gmail.com", color = supportingTextColor) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = supportingTextColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sync_email_input"),
                            shape = RoundedCornerShape(60.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentTextColor,
                                unfocusedBorderColor = borderColor,
                                focusedContainerColor = inputBgColor,
                                unfocusedContainerColor = inputBgColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedLabelColor = accentTextColor,
                                unfocusedLabelColor = supportingTextColor
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field
                        OutlinedTextField(
                            value = signPassword,
                            onValueChange = { signPassword = it },
                            label = { Text(if (selectedLang == "EN") "Password" else "Senha", fontSize = 13.sp) },
                            placeholder = { Text("••••••••", color = supportingTextColor) },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = supportingTextColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = if (isPasswordVisible) (if (selectedLang == "EN") "Hide" else "Ocultar") else (if (selectedLang == "EN") "Show" else "Mostrar"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentTextColor
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sync_password_input"),
                            shape = RoundedCornerShape(60.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentTextColor,
                                unfocusedBorderColor = borderColor,
                                focusedContainerColor = inputBgColor,
                                unfocusedContainerColor = inputBgColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedLabelColor = accentTextColor,
                                unfocusedLabelColor = supportingTextColor
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons: Cancel and Cadastrar (Register)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showLoginDialog = false },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(46.dp),
                                border = BorderStroke(1.dp, borderColor),
                                shape = RoundedCornerShape(60.dp)
                            ) {
                                Text(
                                    getTranslate("cancel", selectedLang),
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

                            Button(
                                onClick = {
                                    val email = signEmail.trim()
                                    val password = signPassword.trim()
                                    if (email.isNotEmpty() && password.isNotEmpty()) {
                                        viewModel.loginWithGoogle(email)
                                        showLoginDialog = false
                                    }
                                },
                                enabled = signEmail.trim().isNotEmpty() && signPassword.trim().isNotEmpty(),
                                modifier = Modifier
                                    .weight(2f)
                                    .height(46.dp)
                                    .testTag("login_register_button"),
                                shape = RoundedCornerShape(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                    contentColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                                    disabledContainerColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
                                )
                            ) {
                                Text(
                                    text = if (selectedLang == "EN") "Register" else "Cadastrar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Google Sign-In Accent Button
                        Button(
                            onClick = {
                                val email = signEmail.trim()
                                if (email.isNotEmpty()) {
                                    viewModel.loginWithGoogle(email)
                                    showLoginDialog = false
                                }
                            },
                            enabled = signEmail.trim().isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("login_google_button"),
                            shape = RoundedCornerShape(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                contentColor = textColor,
                                disabledContainerColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
                            ),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Google Sync",
                                tint = if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF4285F4),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedLang == "EN") "Sign in with Google" else "Logar com o Google",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. CATEGORY EXCLUSION CONFIRMATION DIALOG
        if (showDeleteConfirmDialog) {
            var confirmInputText by remember { mutableStateOf("") }

            val expectedConfirmText = remember(selectedCategoriesToDelete, categories) {
                if (selectedCategoriesToDelete.size == 1) {
                    categories.find { it.id == selectedCategoriesToDelete.first() }?.name ?: ""
                } else {
                    "Todas Selecionadas"
                }
            }

            val isConfirmed = remember(confirmInputText, expectedConfirmText, selectedCategoriesToDelete) {
                if (selectedCategoriesToDelete.isEmpty() || expectedConfirmText.trim().isEmpty()) {
                    false
                } else {
                    confirmInputText.trim().equals(expectedConfirmText.trim(), ignoreCase = true)
                }
            }

            Dialog(onDismissRequest = {
                showDeleteConfirmDialog = false
                confirmInputText = ""
            }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "🗑️ " + if (selectedLang == "EN") "Delete Categories" else "Excluir Categorias",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (selectedLang == "EN") 
                                "Select one or multiple categories to permanently delete them and all their tasks."
                                else "Selecione uma ou mais categorias para excluí-las permanentemente junto com suas tarefas.",
                            fontSize = 12.sp,
                            color = supportingTextColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(color = borderColor)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Selection list with checkboxes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(categories) { category ->
                                    val isChecked = selectedCategoriesToDelete.contains(category.id)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedCategoriesToDelete = if (isChecked) {
                                                    selectedCategoriesToDelete - category.id
                                                } else {
                                                    selectedCategoriesToDelete + category.id
                                                }
                                                confirmInputText = ""
                                            }
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                selectedCategoriesToDelete = if (checked == true) {
                                                    selectedCategoriesToDelete + category.id
                                                } else {
                                                    selectedCategoriesToDelete - category.id
                                                }
                                                confirmInputText = ""
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFFEF4444),
                                                checkmarkColor = Color.White
                                            ),
                                            modifier = Modifier.testTag("delete_checkbox_${category.id}")
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = category.name,
                                            color = textColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(color = borderColor)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic helper explanation texts
                        when {
                            selectedCategoriesToDelete.isEmpty() -> {
                                Text(
                                    text = if (selectedLang == "EN") "Please select at least one category." else "Por favor, selecione pelo menos uma categoria.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFEF4444),
                                    textAlign = TextAlign.Center
                                )
                            }
                            selectedCategoriesToDelete.size == 1 -> {
                                val catName = categories.find { it.id == selectedCategoriesToDelete.first() }?.name ?: ""
                                Text(
                                    text = (if (selectedLang == "EN") "To confirm, type the exact category name:\n" else "Para confirmar, digite o nome exato da categoria para prosseguir:\n") + "\"$catName\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                            else -> {
                                Text(
                                    text = (if (selectedLang == "EN") "To confirm, type exactly:\n" else "Para confirmar, escreva exatamente:\n") + "\"Todas Selecionadas\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }

                        if (selectedCategoriesToDelete.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = confirmInputText,
                                onValueChange = { confirmInputText = it },
                                placeholder = {
                                    Text(
                                        text = if (selectedCategoriesToDelete.size == 1) {
                                            categories.find { it.id == selectedCategoriesToDelete.first() }?.name ?: ""
                                        } else {
                                            "Todas Selecionadas"
                                        },
                                        color = supportingTextColor,
                                        fontSize = 14.sp
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("delete_confirm_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isConfirmed) Color(0xFF10B981) else Color(0xFFEF4444),
                                    unfocusedBorderColor = if (isConfirmed) Color(0xFF10B981) else borderColor,
                                    focusedContainerColor = inputBgColor,
                                    unfocusedContainerColor = inputBgColor,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor
                                ),
                                shape = RoundedCornerShape(60.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (isConfirmed) {
                                        viewModel.deleteCategories(selectedCategoriesToDelete.toList())
                                        showDeleteConfirmDialog = false
                                        confirmInputText = ""
                                    }
                                })
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showDeleteConfirmDialog = false
                                    confirmInputText = ""
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("delete_cancel_button"),
                                shape = RoundedCornerShape(60.dp),
                                border = BorderStroke(1.dp, borderColor),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor)
                            ) {
                                Text(getTranslate("cancel", selectedLang), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    if (isConfirmed) {
                                        viewModel.deleteCategories(selectedCategoriesToDelete.toList())
                                        showDeleteConfirmDialog = false
                                        confirmInputText = ""
                                    }
                                },
                                enabled = isConfirmed,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(48.dp)
                                    .testTag("delete_confirm_button"),
                                shape = RoundedCornerShape(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444),
                                    disabledContainerColor = if (isDarkTheme) Color(0xFF451A03) else Color(0xFFFCA5A5)
                                )
                            ) {
                                Text(
                                    text = if (selectedLang == "EN") "Delete Selected" else "Excluir Selecionadas",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isConfirmed) Color.White else supportingTextColor,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
