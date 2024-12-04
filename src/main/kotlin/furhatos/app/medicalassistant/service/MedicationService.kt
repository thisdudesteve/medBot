package furhatos.app.medicalassistant.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import furhatos.app.medicalassistant.model.Medication
import furhatos.app.medicalassistant.setting.TestDateTime
import java.io.InputStreamReader

class MedicationService {
    private val medications: List<Medication> = loadMedications()

    /**
     * Loads medications from JSON file
     */
    private fun loadMedications(): List<Medication> {
        try {
            val inputStream = javaClass.classLoader.getResourceAsStream("medications.json")
                ?: throw Exception("Cannot find medications.json")

            val reader = InputStreamReader(inputStream)
            val medicationsType = object : TypeToken<MedicationData>() {}.type
            val medicationData: MedicationData = Gson().fromJson(reader, medicationsType)

            return medicationData.medications
        } catch (e: Exception) {
            println("Error loading medications: ${e.message}")
            return emptyList()
        }
    }

    /**
     * Gets medications scheduled for the current test time
     */
    fun getMedicationsForDateTime(time: String, date: String): List<Medication> {
        return medications.filter { medication ->
            medication.schedule.time == time &&
                    medication.schedule.daysOfWeek.contains(date.uppercase())
        }
    }

    /**
     * Gets all medications for a specific time
     */
    fun getMedicationsForTime(time: String): List<Medication> {
        return medications.filter { it.schedule.time == time }
    }

    /**
     * Gets morning medications (before 12:00)
     */
    fun getMorningMedications(): List<Medication> {
        return medications.filter {
            val hour = it.schedule.time.split(":")[0].toInt()
            hour < 12
        }
    }

    /**
     * Gets evening medications (after 18:00)
     */
    fun getEveningMedications(): List<Medication> {
        return medications.filter {
            val hour = it.schedule.time.split(":")[0].toInt()
            hour >= 18
        }
    }
}

// Data classes for JSON deserialization
private data class MedicationData(
    val medications: List<Medication>
)
