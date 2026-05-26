package com.example.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allCategories: Flow<List<Category>> = todoDao.getAllCategories()

    fun getTasks(categoryId: Long): Flow<List<Task>> = todoDao.getTasksForCategory(categoryId)

    suspend fun insertCategory(name: String): Long {
        return todoDao.insertCategory(Category(name = name))
    }

    suspend fun deleteCategory(categoryId: Long) {
        todoDao.safeDeleteCategoryAndTasks(categoryId)
    }

    suspend fun deleteCategories(categoryIds: List<Long>) {
        todoDao.deleteMultipleCategoriesAndTasks(categoryIds)
    }

    suspend fun insertTask(categoryId: Long, title: String): Long {
        return todoDao.insertTask(Task(categoryId = categoryId, title = title))
    }

    suspend fun updateTaskStatus(taskId: Long, isCompleted: Boolean) {
        todoDao.updateTaskStatus(taskId, isCompleted)
    }

    suspend fun deleteTask(taskId: Long) {
        todoDao.deleteTask(taskId)
    }
}
