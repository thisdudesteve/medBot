package furhatos.app.medicalassistant.flow.main

import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.app.medicalassistant.flow.Parent
import furhatos.app.medicalassistant.setting.TestDateTime
import furhatos.nlu.Intent
import furhatos.nlu.common.Time
import furhatos.nlu.common.Date
import furhatos.util.Language

/**
 * Custom intent for setting test date and time
 */
class SetTestDateTime : Intent() {
    var time: Time? = null
    var date: Date? = null

    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "Set test time to @time on @date",
            "Change time to @time and date to @date",
            "Use @time @date for testing",
            "Set the time to @time and date to @date for testing"
        )
    }
}

/**
 * Idle state for test setup
 */
val IdleState: State = state(Parent) {

    onEntry {
        furhat.gesture(Gestures.Thoughtful)
        furhat.say("I'm in test mode. Please specify the time and date for the current scenario.")

        // Reset test parameters on entry
        TestDateTime.currentTime = ""
        TestDateTime.currentDate = ""
    }

    onResponse<SetTestDateTime> {
        val time = it.intent.time
        val date = it.intent.date

        when {
            time == null && date == null -> {
                furhat.gesture(Gestures.Shake)
                furhat.say("I need both time and date. Please specify both.")

                reentry()
            }
            time == null -> {
                furhat.gesture(Gestures.Shake)
                furhat.say("Please specify the time as well.")

                reentry()
            }
            date == null -> {
                furhat.gesture(Gestures.Shake)
                furhat.say("Please specify the date as well.")

                reentry()
            }
            else -> {
                TestDateTime.currentTime = time.toString()
                TestDateTime.currentDate = date.toString()

                furhat.gesture(Gestures.Nod)
                furhat.say("Test scenario set to ${time.toString()} on ${date.toString()}")


                // Transition to Greeting state after successful setup
                goto(Greeting)
            }
        }
    }

    onResponse {
        furhat.gesture(Gestures.Shake)
        furhat.say("I didn't understand that. Please say something like 'Set test time to 9:00 AM on Monday'")

        reentry()
    }

    onNoResponse {
        furhat.gesture(Gestures.Thoughtful)
        furhat.say("Please specify when you would like to test. For example, say 'Set test time to 9:00 AM on Monday'")

        reentry()
    }
}