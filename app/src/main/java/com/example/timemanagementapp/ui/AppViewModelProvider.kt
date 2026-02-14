package com.example.timemanagementapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.timemanagementapp.TimelyApplication
import com.example.timemanagementapp.ui.goal.GoalListViewModel


//View model factory for each view model in Timely App
object AppViewModelProvider{
    val Factory = viewModelFactory {

        //Initializer for Home View Model
        initializer {
            GoalListViewModel(
                timelyApplication().container.goalsRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [TimelyApplication].
 */
fun CreationExtras.timelyApplication(): TimelyApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TimelyApplication)