package furhatos.app.medicalassistant.flow

import furhatos.app.medicalassistant.flow.main.IdleState
import furhatos.flow.kotlin.*
import furhatos.util.Language
import furhatos.flow.kotlin.voice.Voice
import org.intellij.lang.annotations.Language
import java.util.Locale.GERMAN

val Parent: State = state {
    init {
        furhat.voice = Voice("Male", Language.GERMAN)
    }

    onUserEnter(instant = true) {
        furhat.attend(it)
    }

    onUserLeave(instant = true) {
        when {
            users.count == 0 -> goto(IdleState)
            users.current == it -> furhat.attend(users.other)
        }
    }
}