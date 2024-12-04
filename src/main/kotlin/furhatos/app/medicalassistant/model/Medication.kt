package furhatos.app.medicalassistant.model

data class Medication(
    val name: String,
    val dosage: String,
    val frequency: String,
    val schedule: Schedule,
    val instructions: String
)

data class Schedule(
    val time: String,
    val daysOfWeek: List<String>
)