package furhatos.app.medicalassistant.flow

import furhatos.app.medicalassistant.flow.main.Greeting
import furhatos.app.medicalassistant.flow.main.IdleState
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users
import furhatos.flow.kotlin.voice.Voice

val Init: State = state() {
    init {
        /** Set the persona for the interaction **/
        furhat.voice = Voice("Matthew-Neural")
        furhat.character = "Alex"
        /**
         * Start interaction flow with IdleState
         */
        goto(IdleState)
    }
}