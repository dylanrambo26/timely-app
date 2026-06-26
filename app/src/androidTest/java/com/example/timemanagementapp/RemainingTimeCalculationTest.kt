package com.example.timemanagementapp

import com.example.timemanagementapp.util.calculateRemainingTime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemainingTimeCalculationTest {
    private val startTime = 1_000_000L

    //Current Time = Duration
    @Test
    fun returnsDone_whenTimeExpired(){
        val result = calculateRemainingTime(
            startTimeMillis = startTime,
            hours = 0,
            minutes = 1,
            currentTimeMillis = startTime + 60_000L
        )

        assertTrue(result.isDone)
        assertEquals(0, result.remainingMinutes)
    }

    //Current Time = 30 seconds
    @Test
    fun returnsLessThanOneMinuteState_whenTimeLessThanMinute(){
        val result = calculateRemainingTime(
            startTimeMillis = startTime,
            hours = 0,
            minutes = 1,
            currentTimeMillis = startTime + 30_000L
        )

        assertFalse(result.isDone)
        assertEquals(0, result.remainingMinutes)
    }

    //CurrentTime = 30 minutes
    @Test
    fun returnsCorrectMinutesRemaining_whenTimeMoreThanMinute(){
        val result = calculateRemainingTime(
            startTimeMillis = startTime,
            hours = 1,
            minutes = 0,
            currentTimeMillis = startTime + 30 * 60_000L
        )

        assertFalse(result.isDone)
        assertEquals(30, result.remainingMinutes)
    }
}