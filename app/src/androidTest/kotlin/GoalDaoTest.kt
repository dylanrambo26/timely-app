package com.example.timemanagementapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.timemanagementapp.data.Goal
import com.example.timemanagementapp.data.GoalDao
import com.example.timemanagementapp.data.GoalsDatabase
import com.example.timemanagementapp.util.GOALDAOTEST_TOTAL_MINUTES
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class GoalDaoTest {
    private lateinit var goalDao: GoalDao
    private lateinit var goalsDatabase: GoalsDatabase

    private var goal1 = Goal(1, 2,30, "Study")
    private var goal2 = Goal(2, 3,15, "Homework")
    @Before
    fun createDb(){
        val context: Context = ApplicationProvider.getApplicationContext()
        goalsDatabase = Room.inMemoryDatabaseBuilder(context, GoalsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        goalDao = goalsDatabase.goalDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        goalsDatabase.close()
    }

    private suspend fun addOneGoalToDb() {
        goalDao.insert(goal1)
    }
    private suspend fun addTwoGoalsToDb(){
        goalDao.insert(goal1)
        goalDao.insert(goal2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsGoalIntoDb() = runBlocking {
        addOneGoalToDb()
        val allGoals = goalDao.getAllGoals().first()
        assertEquals(allGoals[0], goal1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllGoals_returnsAllGoalsFromDb() = runBlocking {
        addTwoGoalsToDb()
        val allGoals = goalDao.getAllGoals().first()
        assertEquals(allGoals[0], goal1)
        assertEquals(allGoals[1], goal2)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateGoals_updatesGoalsInDb() = runBlocking {
        addTwoGoalsToDb()
        goalDao.update(Goal(1, 1,15,"Study"))
        goalDao.update(Goal(2,2,30,"Homework"))

        val allGoals = goalDao.getAllGoals().first()
        assertEquals(allGoals[0], Goal(1, 1,15,"Study"))
        assertEquals(allGoals[1],Goal(2,2,30,"Homework"))
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteGoals_deletesGoalsInDb() = runBlocking {
        addTwoGoalsToDb()
        goalDao.delete(goal1)
        goalDao.delete(goal2)
        val allGoals = goalDao.getAllGoals().first()
        assertTrue(allGoals.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoGetGoal_returnsGoalFromDb() = runBlocking {
        addOneGoalToDb()
        val goal = goalDao.getGoal(1)
        assertEquals(goal.first(), goal1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetSumOfTotalMinutes_returnsIntTotalMinutesFromDb() = runBlocking {
        addTwoGoalsToDb()
        val totalMinutes = goalDao.getSumOfTotalMinutes()
        assertEquals(totalMinutes.first(), GOALDAOTEST_TOTAL_MINUTES)
    }
}