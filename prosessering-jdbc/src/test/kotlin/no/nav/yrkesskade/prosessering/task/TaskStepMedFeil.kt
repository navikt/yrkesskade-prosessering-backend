package no.nav.yrkesskade.prosessering.task

import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import org.springframework.stereotype.Service

@Service
@TaskStepBeskrivelse(taskStepType = TaskStepMedFeil.TYPE,
                     beskrivelse = "Task med feil")
class TaskStepMedFeil : AsyncTaskStep {

    override fun doTask(task: Task) {
        error("Feil")
    }

    companion object {

        const val TYPE = "taskMedFeil"
    }
}
