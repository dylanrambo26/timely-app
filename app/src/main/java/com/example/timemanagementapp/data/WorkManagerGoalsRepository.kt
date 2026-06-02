package com.example.timemanagementapp.data

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.timemanagementapp.worker.TimerWorker
import java.util.concurrent.TimeUnit

class WorkManagerGoalsRepository(
   context: Context
) : TimerRepository {
    companion object {
        private const val CURRENT_TASK_TIMER = "current_task_timer"
    }
    private val workManager = WorkManager.getInstance(context)

    override fun scheduleTimerJob(goal: Goal){
        val durationMillis = (goal.hours * 60L + goal.minutes) * 60_000L

        val request =
            OneTimeWorkRequestBuilder<TimerWorker>()
                .setInitialDelay(durationMillis, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        "goalId" to goal.goalID
                    )
                )
                .build()

        workManager
            .enqueueUniqueWork(
                CURRENT_TASK_TIMER,
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    override fun cancelWork(){
        workManager.cancelUniqueWork(CURRENT_TASK_TIMER)
    }
}
