package no.nav.yrkesskade.prosessering.task

import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.error.TaskExceptionUtenStackTrace
import org.springframework.stereotype.Service

@Service
@TaskStepBeskrivelse(taskStepType = TaskStepExceptionUtenStackTrace.TYPE, beskrivelse = "")
class TaskStepExceptionUtenStackTrace : AsyncTaskStep {

    override fun doTask(task: Task) {
        throw TaskExceptionUtenStackTrace("feilmelding")
    }

    override fun onCompletion(task: Task) {}

    companion object {

        const val TYPE = "TaskStepExceptionUtenStackTrace"
    }
}
