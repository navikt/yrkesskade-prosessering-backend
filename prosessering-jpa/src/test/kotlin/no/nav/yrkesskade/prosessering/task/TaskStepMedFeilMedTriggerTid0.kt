package no.nav.yrkesskade.prosessering.task

import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import org.springframework.stereotype.Service

@Service
@TaskStepBeskrivelse(taskStepType = TaskStepMedFeilMedTriggerTid0.TYPE,
                     beskrivelse = "Task med feil",
                     triggerTidVedFeilISekunder = 0)
class TaskStepMedFeilMedTriggerTid0 : AsyncTaskStep {

    override fun doTask(task: Task) {
        error("Feil")
    }

    companion object {

        const val TYPE = "taskMedFeilMedTriggerTid0"
    }
}
