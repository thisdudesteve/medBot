package furhatos.app.medicalassistant.flow.main

import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.app.medicalassistant.flow.Parent
import furhatos.app.medicalassistant.model.Medication
import furhatos.nlu.common.Yes
import furhatos.nlu.common.No

val MedicationInstructionsState = { medications: List<Medication> -> state(Parent) {

    var currentMedicationIndex = 0

    fun presentCurrentMedication() {
        val medication = medications[currentMedicationIndex]

        // Present the medication with its instructions
        furhat.gesture(Gestures.Thoughtful)
        furhat.say("${medication.name}, ${medication.dosage}.")


        delay(300)

        if (medication.instructions.isNotEmpty()) {
            furhat.gesture(Gestures.Nod)
            furhat.say("Wichtiger Hinweis: ${medication.instructions}")

        }

        if (currentMedicationIndex < medications.size - 1) {
            delay(500)
            furhat.ask("Möchten Sie die Anweisungen für dieses Medikament noch einmal hören?")
        } else {
            delay(500)
            furhat.ask("Soll ich die Anweisungen wiederholen?")
        }
    }

    onEntry {
        furhat.gesture(Gestures.Smile)
            when (medications.size) {
                1 -> furhat.say("Lassen Sie uns die Anweisungen für Ihr Medikament durchgehen.")
                else -> furhat.say("Lassen Sie uns die Anweisungen für Ihre ${medications.size} Medikamente durchgehen.")
            }


        delay(300)
        presentCurrentMedication()
    }

    onResponse<Yes> {
        // Repeat current medication instructions
        presentCurrentMedication()
    }

    onResponse<No> {
        if (currentMedicationIndex < medications.size - 1) {
            // Move to next medication
            currentMedicationIndex++
            furhat.gesture(Gestures.Nod)
            furhat.say("Gut, lassen Sie uns zum nächsten Medikament übergehen.")

            delay(300)
            presentCurrentMedication()
        } else {
            // All medications covered
            furhat.gesture(Gestures.BigSmile)
            furhat.say("Alles klar. Vergessen Sie nicht, Ihre Medikamente wie besprochen einzunehmen.")

            goto(IdleState)
        }
    }

    // Handle unexpected responses
    onResponse {
        furhat.gesture(Gestures.Thoughtful)
        furhat.ask("Bitte antworten Sie mit ja oder nein. Möchten Sie die Anweisungen noch einmal hören?")

        reentry()
    }

    // Handle no response
    onNoResponse {
        furhat.gesture(Gestures.Thoughtful)
            furhat.say("Ich wiederhole die Anweisungen zur Sicherheit.")
            delay(300)
            presentCurrentMedication()

    }
}}