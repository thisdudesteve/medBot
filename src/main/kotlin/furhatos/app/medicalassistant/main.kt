package furhatos.app.medicalassistant

import furhatos.app.medicalassistant.flow.Init
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class MedicalAssistantSkill : Skill() {
    override fun start() {
        Flow().run(Init)  // Start with Init state which transitions to IdleState
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}