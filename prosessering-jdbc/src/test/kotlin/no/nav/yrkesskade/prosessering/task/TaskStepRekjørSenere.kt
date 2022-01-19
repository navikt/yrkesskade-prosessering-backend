package no.nav.yrkesskade.prosessering.task

import no.nav.yrkesskade.prosessering.AsyncTaskStep
import no.nav.yrkesskade.prosessering.TaskStepBeskrivelse
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.error.RekjørSenereException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@TaskStepBeskrivelse(taskStepType = TaskStepRekjørSenere.TYPE, beskrivelse = "")
class TaskStepRekjørSenere : AsyncTaskStep {

    override fun doTask(task: Task) {
        throw RekjørSenereException("årsak", LocalDate.of(2088,1,1).atStartOfDay())
    }

    override fun onCompletion(task: Task) {}

    companion object {

        const val TYPE = "TaskStepRekjørSenere"
    }
}
