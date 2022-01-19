package no.nav.yrkesskade.prosessering.task

import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import org.springframework.stereotype.Service

@Service
@TaskStepBeskrivelse(taskStepType = TaskStepFeilManuellOppfølgning.TASK_FEIL_1,
                     beskrivelse = "Dette er task 1",
                     settTilManuellOppfølgning = true,
                     triggerTidVedFeilISekunder = 0)
class TaskStepFeilManuellOppfølgning : AsyncTaskStep {


    override fun doTask(task: Task) {
        error("Feiler")
    }

    companion object {

        const val TASK_FEIL_1 = "taskFeilManuellOppfølgning1"
    }

}
