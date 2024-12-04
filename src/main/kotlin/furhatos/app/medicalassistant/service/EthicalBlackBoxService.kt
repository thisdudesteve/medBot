package furhatos.app.medicalassistant.service

import furhatos.app.medicalassistant.model.Medication
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EthicalBlackBoxService {
    private val logDirectory = "logs"
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")

    init {
        // Create logs directory if it doesn't exist
        File(logDirectory).mkdirs()
    }

    /**
     * Records an interaction with timestamp and details
     */
    fun recordInteraction(
        timestamp: LocalDateTime = LocalDateTime.now(),
        medications: List<Medication>? = null,
        furhatPrompt: String,
        userResponse: String = ""
    ) {
        val logFile = getLogFile()

        val medicationInfo = medications?.joinToString(", ") { it.name } ?: "No medications"

        val logEntry = """
            |=== Interaction Log ===
            |Timestamp: ${timestamp.format(dateTimeFormatter)}
            |Medications: $medicationInfo
            |Furhat: $furhatPrompt
            |User: $userResponse
            |====================
            |
        """.trimMargin()

        logFile.appendText(logEntry)
    }

    /**
     * Gets the current log file (creates new file for each day)
     */
    private fun getLogFile(): File {
        val today = LocalDateTime.now()
        val fileName = "interaction_log_${today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.txt"
        return File(logDirectory, fileName)
    }

    /**
     * Retrieves all interactions for a specific day
     */
    fun getInteractionsForDay(date: LocalDateTime): List<String> {
        val fileName = "interaction_log_${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.txt"
        val file = File(logDirectory, fileName)

        return if (file.exists()) {
            file.readText().split("=== Interaction Log ===")
                .filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }
}