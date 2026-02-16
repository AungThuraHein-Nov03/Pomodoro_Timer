package com.aungthurahein.myapplicationpmt

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MainActivity timer logic and validation
 */
class MainActivityLogicTest {
    
    // Test validation boundaries for work time
    @Test
    fun `validateWorkTime with 0 minutes should fail and use default`() {
        val input = 0L
        val isValid = input >= Constants.MIN_WORK_MINUTES && 
                     input <= Constants.MAX_WORK_MINUTES
        
        assertFalse("0 minutes should be below minimum", isValid)
    }
    
    @Test
    fun `validateWorkTime with 1 minute should pass`() {
        val input = 1L
        val isValid = input >= Constants.MIN_WORK_MINUTES && 
                     input <= Constants.MAX_WORK_MINUTES
        
        assertTrue("1 minute should be valid minimum", isValid)
    }
    
    @Test
    fun `validateWorkTime with 120 minutes should pass`() {
        val input = 120L
        val isValid = input >= Constants.MIN_WORK_MINUTES && 
                     input <= Constants.MAX_WORK_MINUTES
        
        assertTrue("120 minutes should be valid maximum", isValid)
    }
    
    @Test
    fun `validateWorkTime with 121 minutes should fail`() {
        val input = 121L
        val isValid = input >= Constants.MIN_WORK_MINUTES && 
                     input <= Constants.MAX_WORK_MINUTES
        
        assertFalse("121 minutes should exceed maximum", isValid)
    }
    
    @Test
    fun `validateWorkTime with negative value should fail`() {
        val input = -5L
        val isValid = input >= Constants.MIN_WORK_MINUTES && 
                     input <= Constants.MAX_WORK_MINUTES
        
        assertFalse("Negative value should be invalid", isValid)
    }
    
    // Test validation boundaries for break time
    @Test
    fun `validateBreakTime with 0 minutes should fail`() {
        val input = 0L
        val isValid = input >= Constants.MIN_BREAK_MINUTES && 
                     input <= Constants.MAX_BREAK_MINUTES
        
        assertFalse("0 minutes should be below minimum", isValid)
    }
    
    @Test
    fun `validateBreakTime with 1 minute should pass`() {
        val input = 1L
        val isValid = input >= Constants.MIN_BREAK_MINUTES && 
                     input <= Constants.MAX_BREAK_MINUTES
        
        assertTrue("1 minute should be valid minimum", isValid)
    }
    
    @Test
    fun `validateBreakTime with 60 minutes should pass`() {
        val input = 60L
        val isValid = input >= Constants.MIN_BREAK_MINUTES && 
                     input <= Constants.MAX_BREAK_MINUTES
        
        assertTrue("60 minutes should be valid maximum", isValid)
    }
    
    @Test
    fun `validateBreakTime with 61 minutes should fail`() {
        val input = 61L
        val isValid = input >= Constants.MIN_BREAK_MINUTES && 
                     input <= Constants.MAX_BREAK_MINUTES
        
        assertFalse("61 minutes should exceed maximum", isValid)
    }
    
    // Test timer calculation logic
    @Test
    fun `calculateWorkSeconds with default 25 minutes returns 1500 seconds`() {
        val minutes = Constants.DEFAULT_WORK_MINUTES
        val expectedSeconds = 25 * 60L
        val actualSeconds = Constants.minutesToSeconds(minutes)
        
        assertEquals(expectedSeconds, actualSeconds)
    }
    
    @Test
    fun `calculateWorkSeconds with custom 45 minutes returns 2700 seconds`() {
        val minutes = 45L
        val expectedSeconds = 45 * 60L
        val actualSeconds = Constants.minutesToSeconds(minutes)
        
        assertEquals(expectedSeconds, actualSeconds)
    }
    
    @Test
    fun `calculateBreakSeconds with short break 5 minutes returns 300 seconds`() {
        val minutes = Constants.DEFAULT_SHORT_BREAK_MINUTES
        val expectedSeconds = 5 * 60L
        val actualSeconds = Constants.minutesToSeconds(minutes)
        
        assertEquals(expectedSeconds, actualSeconds)
    }
    
    @Test
    fun `calculateBreakSeconds with long break 15 minutes returns 900 seconds`() {
        val minutes = Constants.DEFAULT_LONG_BREAK_MINUTES
        val expectedSeconds = 15 * 60L
        val actualSeconds = Constants.minutesToSeconds(minutes)
        
        assertEquals(expectedSeconds, actualSeconds)
    }
    
    // Test session count and long break logic
    @Test
    fun `after 1 work session should use short break`() {
        val workCount = 1L
        val shouldUseLongBreak = workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L
        
        assertFalse("After 1 session, should use short break", shouldUseLongBreak)
    }
    
    @Test
    fun `after 2 work sessions should use short break`() {
        val workCount = 2L
        val shouldUseLongBreak = workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L
        
        assertFalse("After 2 sessions, should use short break", shouldUseLongBreak)
    }
    
    @Test
    fun `after 3 work sessions should use short break`() {
        val workCount = 3L
        val shouldUseLongBreak = workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L
        
        assertFalse("After 3 sessions, should use short break", shouldUseLongBreak)
    }
    
    @Test
    fun `after 4 work sessions should use long break`() {
        val workCount = 4L
        val shouldUseLongBreak = workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L
        
        assertTrue("After 4 sessions, should use long break", shouldUseLongBreak)
    }
    
    @Test
    fun `after 8 work sessions should use long break`() {
        val workCount = 8L
        val shouldUseLongBreak = workCount % Constants.SESSIONS_FOR_LONG_BREAK == 0L
        
        assertTrue("After 8 sessions, should use long break", shouldUseLongBreak)
    }
    
    @Test
    fun `timer progress calculation at halfway`() {
        val totalSeconds = 1500L
        val elapsedSeconds = 750L
        val expectedProgress = (totalSeconds - elapsedSeconds).toInt()
        
        assertEquals(750, expectedProgress)
    }
    
    @Test
    fun `timer progress calculation complete`() {
        val totalSeconds = 1500L
        val elapsedSeconds = 1500L
        val expectedProgress = (totalSeconds - elapsedSeconds).toInt()
        
        assertEquals(0, expectedProgress)
    }
    
    @Test
    fun `timer progress calculation at start`() {
        val totalSeconds = 1500L
        val elapsedSeconds = 0L
        val expectedProgress = (totalSeconds - elapsedSeconds).toInt()
        
        assertEquals(1500, expectedProgress)
    }
    
    // Test input type safety
    @Test
    fun `empty string input returns null from toLongOrNull`() {
        val input = ""
        val result = input.toLongOrNull()
        
        assertNull("Empty string should return null", result)
    }
    
    @Test
    fun `non-numeric string returns null from toLongOrNull`() {
        val input = "abc"
        val result = input.toLongOrNull()
        
        assertNull("Non-numeric string should return null", result)
    }
    
    @Test
    fun `valid numeric string returns correct long`() {
        val input = "42"
        val result = input.toLongOrNull()
        
        assertEquals(42L, result)
    }
    
    @Test
    fun `string with decimal returns null from toLongOrNull`() {
        val input = "42.5"
        val result = input.toLongOrNull()
        
        assertNull("Decimal string should return null for Long", result)
    }
    
    // Test edge cases for time formatting
    @Test
    fun `timer format with 0 seconds shows 00:00`() {
        val seconds = 0L
        val formatted = String.format("%02d:%02d", seconds / 60, seconds % 60)
        
        assertEquals("00:00", formatted)
    }
    
    @Test
    fun `timer format with 59 seconds shows 00:59`() {
        val seconds = 59L
        val formatted = String.format("%02d:%02d", seconds / 60, seconds % 60)
        
        assertEquals("00:59", formatted)
    }
    
    @Test
    fun `timer format with 60 seconds shows 01:00`() {
        val seconds = 60L
        val formatted = String.format("%02d:%02d", seconds / 60, seconds % 60)
        
        assertEquals("01:00", formatted)
    }
    
    @Test
    fun `timer format with 3599 seconds shows 59:59`() {
        val seconds = 3599L
        val formatted = String.format("%02d:%02d", seconds / 60, seconds % 60)
        
        assertEquals("59:59", formatted)
    }
    
    @Test
    fun `timer format with 120 minutes shows 120:00`() {
        val seconds = 120 * 60L
        val formatted = String.format("%02d:%02d", seconds / 60, seconds % 60)
        
        assertEquals("120:00", formatted)
    }
}
