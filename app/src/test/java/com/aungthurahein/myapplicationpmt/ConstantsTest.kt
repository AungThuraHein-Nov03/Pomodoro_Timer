package com.aungthurahein.myapplicationpmt

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Constants object
 */
class ConstantsTest {

    // ==================== Timer Defaults Tests ====================
    
    @Test
    fun `DEFAULT_WORK_MINUTES is 25`() {
        assertEquals(25L, Constants.DEFAULT_WORK_MINUTES)
    }
    
    @Test
    fun `DEFAULT_SHORT_BREAK_MINUTES is 5`() {
        assertEquals(5L, Constants.DEFAULT_SHORT_BREAK_MINUTES)
    }
    
    @Test
    fun `DEFAULT_LONG_BREAK_MINUTES is 15`() {
        assertEquals(15L, Constants.DEFAULT_LONG_BREAK_MINUTES)
    }
    
    @Test
    fun `SESSIONS_FOR_LONG_BREAK is 4`() {
        assertEquals(4L, Constants.SESSIONS_FOR_LONG_BREAK)
    }
    
    // ==================== Timer Conversion Tests ====================
    
    @Test
    fun `minutesToSeconds with 0 returns 0`() {
        assertEquals(0L, Constants.minutesToSeconds(0L))
    }
    
    @Test
    fun `minutesToSeconds with 1 returns 60`() {
        assertEquals(60L, Constants.minutesToSeconds(1L))
    }
    
    @Test
    fun `minutesToSeconds with default work minutes returns correct seconds`() {
        val expected = 25 * 60L
        assertEquals(expected, Constants.minutesToSeconds(Constants.DEFAULT_WORK_MINUTES))
    }
    
    @Test
    fun `minutesToSeconds with default short break minutes returns correct seconds`() {
        val expected = 5 * 60L
        assertEquals(expected, Constants.minutesToSeconds(Constants.DEFAULT_SHORT_BREAK_MINUTES))
    }
    
    @Test
    fun `minutesToSeconds with default long break minutes returns correct seconds`() {
        val expected = 15 * 60L
        assertEquals(expected, Constants.minutesToSeconds(Constants.DEFAULT_LONG_BREAK_MINUTES))
    }
    
    @Test
    fun `minutesToSeconds with 120 minutes returns 7200 seconds`() {
        assertEquals(7200L, Constants.minutesToSeconds(120L))
    }
    
    // ==================== Input Validation Limits Tests ====================
    
    @Test
    fun `MIN_WORK_MINUTES is 1`() {
        assertEquals(1L, Constants.MIN_WORK_MINUTES)
    }
    
    @Test
    fun `MAX_WORK_MINUTES is 120`() {
        assertEquals(120L, Constants.MAX_WORK_MINUTES)
    }
    
    @Test
    fun `MIN_BREAK_MINUTES is 1`() {
        assertEquals(1L, Constants.MIN_BREAK_MINUTES)
    }
    
    @Test
    fun `MAX_BREAK_MINUTES is 60`() {
        assertEquals(60L, Constants.MAX_BREAK_MINUTES)
    }
    
    // ==================== SharedPreferences Keys Tests ====================
    
    @Test
    fun `PREFS_NAME is correct`() {
        assertEquals("pomodoro_prefs", Constants.PREFS_NAME)
    }
    
    @Test
    fun `KEY_TASK_NAME is correct`() {
        assertEquals("task_name", Constants.KEY_TASK_NAME)
    }
    
    @Test
    fun `KEY_LAST_DATE is correct`() {
        assertEquals("last_date", Constants.KEY_LAST_DATE)
    }
    
    @Test
    fun `KEY_SESSIONS_TODAY is correct`() {
        assertEquals("sessions_today", Constants.KEY_SESSIONS_TODAY)
    }
    
    // ==================== Error Messages Tests ====================
    
    @Test
    fun `ERROR_INVALID_WORK_TIME contains expected text`() {
        assertTrue(Constants.ERROR_INVALID_WORK_TIME.contains("1-120"))
        assertTrue(Constants.ERROR_INVALID_WORK_TIME.contains("minutes"))
    }
    
    @Test
    fun `ERROR_INVALID_BREAK_TIME contains expected text`() {
        assertTrue(Constants.ERROR_INVALID_BREAK_TIME.contains("1-60"))
        assertTrue(Constants.ERROR_INVALID_BREAK_TIME.contains("minutes"))
    }
}
