package com.example.timemanagementapp.data

import android.content.Context


/**
 * App container for Dependency injection.
 */
interface AppContainer{
    val goalsRepository: GoalsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val goalsRepository: GoalsRepository by lazy {
        OfflineGoalsRepository(GoalsDatabase.getDatabase(context).goalDao())
    }
}