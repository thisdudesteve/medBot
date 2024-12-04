package furhatos.app.medicalassistant.flow.main

import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.app.medicalassistant.flow.Parent
import furhatos.app.medicalassistant.setting.TestDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Greeting state with enhanced time-specific greetings and gestures
 */
val Greeting: State = state(Parent) {

    // Helper function to determine time of day from test time
    fun getTimeOfDay(timeStr: String): String {
        val time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
        return when {
            time.hour in 5..11 -> "morning"
            time.hour in 12..16 -> "afternoon"
            time.hour in 17..21 -> "evening"
            else -> "night"
        }
    }

    onEntry {
        // Initial welcoming gesture combination
        furhat.gesture(Gestures.OpenEyes)
        furhat.gesture(Gestures.BigSmile)
            // Get appropriate time-based greeting
            val timeOfDay = getTimeOfDay(TestDateTime.currentTime)

            // First greeting with time-specific phrase
            furhat.say {
                random {
                    +"Good $timeOfDay! I'm your medical assistant."
                    +"Good $timeOfDay! I'm here to help you with your medications."
                }
            }


        // Short pause for natural interaction
        delay(200)

        // Additional welcoming gesture
        furhat.gesture(Gestures.Nod)
        furhat.say {
            random {
                    +"I hope you're having a pleasant $timeOfDay."
                    +"It's nice to see you this $timeOfDay."
                }
            }


        // Another short pause
        delay(300)

        // Confirm test scenario with attention gesture
        furhat.gesture(Gestures.GazeAway)
        furhat.gesture(Gestures.Thoughtful)
        furhat.say("Let me check your medication schedule for ${TestDateTime.currentTime} on ${TestDateTime.currentDate}")



        // Final gesture before transition
        furhat.gesture(Gestures.Smile)
            furhat.say {
                random {
                    +"I'll help you stay on track with your medications."
                    +"I'm here to make sure you take the right medications at the right time."
                    +"Let's review your medications together."
                }
            }


        // Transition to medication check
        goto(MedicationCheckState)
    }

    // Handle unexpected user input during greeting
    onResponse {
        furhat.gesture(Gestures.Nod)
            furhat.say("Let me check your medications.")

        goto(MedicationCheckState)
    }

    // Handle no response
    onNoResponse {
        furhat.gesture(Gestures.Smile)
            furhat.say("I'll proceed with checking your medications.")

        goto(MedicationCheckState)
    }
}