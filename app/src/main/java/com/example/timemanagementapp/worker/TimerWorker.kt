package com.example.timemanagementapp.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.timemanagementapp.R
import com.example.timemanagementapp.data.GoalDao
import com.example.timemanagementapp.data.GoalsDatabase
import com.example.timemanagementapp.data.GoalsRepository

private const val TAG = "TimerWorker"

class TimerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return try {
            val goalId = inputData.getInt("goalId", -1)
            val goalTitle = inputData.getString("goalTitle")

            if(goalId == -1 || goalTitle == null)
                return Result.failure()
            Log.d(TAG, "Timer finished for goalId=$goalId")

//            val db = GoalsDatabase.getDatabase(applicationContext)
//            val goalDao = db.goalDao()
//            val goal = goalDao.getGoalOnce(goalId)

            showTaskFinishedNotification(goalId, goalTitle)

            //TODO mark goal as complete through room repo

            Result.success()
        } catch (e: Exception){
            Log.e(TAG, "Worker failed", e)
            Result.retry()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showTaskFinishedNotification(goalId: Int, goalTitle: String){
        val context = applicationContext
        val channelId = "task_timer_channel"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.outline_calendar_check_24)
            .setContentTitle("Task Complete")
            .setContentText("Your $goalTitle task is done.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(goalId, notification)
    }
}