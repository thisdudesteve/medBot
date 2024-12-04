package furhatos.app.medicalassistant.flow.main

import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.app.medicalassistant.flow.Parent
import furhatos.app.medicalassistant.setting.TestDateTime
import furhatos.app.medicalassistant.service.MedicationService
import furhatos.app.medicalassistant.service.EthicalBlackBoxService
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes

/**
 * State for checking and announcing medications
 */
val MedicationCheckState: State = state(Parent) {

    lateinit var medicationService: MedicationService
    lateinit var ethicalBlackBox: EthicalBlackBoxService

    onEntry {
        // Initialize services
        medicationService = MedicationService()
        ethicalBlackBox = EthicalBlackBoxService()

        // Get medications for current test time
        val medications = medicationService.getMedicationsForDateTime(
            TestDateTime.currentTime,
            TestDateTime.currentDate
        )

        when {
            medications.isEmpty() -> {
                // No medications scheduled
                furhat.gesture(Gestures.Shake)
                    furhat.say {
                        random {
                            +"I don't see any medications scheduled for this time."
                            +"There are no medications due at ${TestDateTime.currentTime}."
                        }
                    }


                // Record the check in ethical black box
                ethicalBlackBox.recordInteraction(
                    InteractionRecord(
                        timestamp = SetTestDateTime.time,
                        medication = null,
                        furhatPrompt = "No medications scheduled",
                        userResponse = ""
                    )
                )

                // Ask if user wants to check another time
                furhat.gesture(Gestures.Thoughtful)
                furhat.ask("Would you like to check another time?")

            }

            medications.size == 1 -> {
                // Single medication scheduled
                val medication = medications.first()

                furhat.gesture(Gestures.Smile)
                furhat.say("I see you have one medication scheduled for ${TestDateTime.currentTime}.")


                delay(300)

                furhat.gesture(Gestures.Nod)
                furhat.say("It's time to take your ${medication.name}.")


                // Record the interaction
                ethicalBlackBox.recordInteraction(
                    InteractionRecord(
                        timestamp = System.currentTimeMillis(),
                        medication = medication,
                        furhatPrompt = "Time to take ${medication.name}",
                        userResponse = ""
                    )
                )

                goto(MedicationInstructionsState(medications))
            }

            else -> {
                // Multiple medications scheduled
                furhat.gesture(Gestures.Smile)
                furhat.say("You have ${medications.size} medications scheduled for ${TestDateTime.currentTime}.")


                delay(300)

                // List all medications
                furhat.gesture(Gestures.Thoughtful)
                furhat.say("You need to take: ${medications.joinToString(", ") { it.name }}")


                // Record the interaction
                ethicalBlackBox.recordInteraction(
                    InteractionRecord(
                        timestamp = System.currentTimeMillis(),
                        medication = medications.first(), // Record first med with note about multiple
                        furhatPrompt = "Multiple medications due: ${medications.joinToString(", ") { it.name }}",
                        userResponse = ""
                    )
                )

                goto(MedicationInstructionsState(medications))
            }
        }
    }

    // Handle response for checking another time
    onResponse<Yes> {
        furhat.gesture(Gestures.Nod)
        furhat.say("Alright, let's check another time.")

        goto(IdleState)
    }

    onResponse<No> {
        furhat.gesture(Gestures.Smile)
        furhat.say {
                random {
                    +"Okay, let me know if you need anything else."
                    +"Alright, I'll be here if you need to check later."
                }
            }

        goto(IdleState)
    }

    // Handle unexpected responses
    onResponse {
        furhat.gesture(Gestures.Thoughtful)
            furhat.ask("Would you like to check another time? Please say yes or no.")

        reentry()
    }

    // Handle no response
    onNoResponse {
        furhat.gesture(Gestures.Thoughtful)
            furhat.ask("Should we check another time? Please say yes or no.")

        reentry()
    }
}

/**
 * Data class for storing interaction records
 */
data class InteractionRecord(
    val timestamp: Long,
    val medication: String,
    val furhatPrompt: String,
    val userResponse: String
)
