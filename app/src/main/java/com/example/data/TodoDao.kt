package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // Categories
    @Query("SELECT * FROM categories ORDER BY createdAt ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Long)

    // Tasks
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY createdAt ASC")
    fun getTasksForCategory(categoryId: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, isCompleted: Boolean)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("DELETE FROM tasks WHERE categoryId = :categoryId")
    suspend fun deleteTasksForCategory(categoryId: Long)

    @Transaction
    suspend fun safeDeleteCategoryAndTasks(categoryId: Long) {
        deleteTasksForCategory(categoryId)
        deleteCategoryById(categoryId)
    }

    @Transaction
    suspend fun deleteMultipleCategoriesAndTasks(categoryIds: List<Long>) {
        categoryIds.forEach { id ->
            deleteTasksForCategory(id)
            deleteCategoryById(id)
        }
    }
}
