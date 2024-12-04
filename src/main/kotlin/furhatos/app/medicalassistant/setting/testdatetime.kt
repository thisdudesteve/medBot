package furhatos.app.medicalassistant.setting

import java.time.LocalTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Singleton object to manage test date and time settings
 */
object TestDateTime {
    private var _currentTime: String = ""
    private var _currentDate: String = ""

    // Time formatter for 24-hour format
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Date formatter for full date format
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.GERMAN)

    var currentTime: String
        get() = _currentTime
        set(value) {
            _currentTime = validateAndFormatTime(value)
        }

    var currentDate: String
        get() = _currentDate
        set(value) {
            _currentDate = validateAndFormatDate(value)
        }

    /**
     * Validates and formats the time string
     */
    private fun validateAndFormatTime(time: String): String {
        try {
            // Parse the time string to ensure it's valid
            val parsedTime = LocalTime.parse(time, timeFormatter)
            // Return formatted time
            return parsedTime.format(timeFormatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid time format. Please use HH:mm format (e.g., 07:00)")
        }
    }

    /**
     * Validates and formats the date string
     */
    private fun validateAndFormatDate(date: String): String {
        try {
            // If it's already a valid German weekday, return it
            if (isValidGermanWeekday(date)) {
                return date.uppercase()
            }

            // Try to parse as a date and get the weekday
            val parsedDate = LocalDate.parse(date)
            return parsedDate.format(dateFormatter).uppercase()
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format")
        }
    }

    /**
     * Checks if the string is a valid German weekday
     */
    private fun isValidGermanWeekday(day: String): Boolean {
        val validDays = setOf(
            "MONTAG", "DIENSTAG", "MITTWOCH", "DONNERSTAG",
            "FREITAG", "SAMSTAG", "SONNTAG"
        )
        return day.uppercase() in validDays
    }

    /**
     * Validates if both time and date are set
     */
    fun isConfigured(): Boolean {
        return _currentTime.isNotEmpty() && _currentDate.isNotEmpty()
    }

    /**
     * Formats current datetime for display
     */
    fun getFormattedDateTime(): String {
        return if (isConfigured()) {
            "$_currentTime Uhr am $_currentDate"
        } else {
            "Noch nicht konfiguriert"
        }
    }

    /**
     * Resets the test datetime
     */
    fun reset() {
        _currentTime = ""
        _currentDate = ""
    }

    /**
     * Gets the hour from current time (useful for medication scheduling)
     */
    fun getCurrentHour(): Int {
        return if (_currentTime.isNotEmpty()) {
            LocalTime.parse(_currentTime, timeFormatter).hour
        } else {
            -1
        }
    }

    /**
     * Compares current time with a medication time
     */
    fun isTimeMatch(medicationTime: String): Boolean {
        if (!isConfigured()) return false

        try {
            val testTime = LocalTime.parse(_currentTime, timeFormatter)
            val medTime = LocalTime.parse(medicationTime, timeFormatter)
            return testTime == medTime
        } catch (e: DateTimeParseException) {
            return false
        }
    }
}