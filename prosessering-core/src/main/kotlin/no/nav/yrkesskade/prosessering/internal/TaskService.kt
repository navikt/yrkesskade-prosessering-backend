package no.nav.yrkesskade.prosessering.internal

import no.nav.yrkesskade.prosessering.domene.ITask
import no.nav.yrkesskade.prosessering.domene.Status
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TaskService(val taskRepository: TaskRepository)  {

    fun findById(id: Long): ITask {
        return taskRepository.findByIdOrNull(id) ?: error("Task med id: $id ikke funnet.")
    }

    fun save(task: ITask): ITask {
        return taskRepository.save(task)
    }

    fun finnAlleTasksKlareForProsessering(page: Pageable): List<ITask> {
        return taskRepository.findByStatusInAndTriggerTidBeforeOrderByOpprettetTidDesc(listOf(Status.KLAR_TIL_PLUKK,
                                                                                              Status.UBEHANDLET),
                                                                                       LocalDateTime.now(),
                                                                                       page)
    }

    fun finnAlleFeiledeTasks(): List<ITask> {
        return taskRepository.findByStatus(Status.FEILET)
    }

    fun finnAllePlukkedeTasks(): List<ITask> {
        return taskRepository.findByStatus(Status.PLUKKET)
    }

    fun finnTasksMedStatus(status: List<Status>, page: Pageable): List<ITask> {
        return taskRepository.findByStatusIn(status, page)
    }

    fun finnTasksKlarForSletting(eldreEnnDato: LocalDateTime): List<ITask> {
        return taskRepository.findByStatusAndTriggerTidBefore(Status.FERDIG, eldreEnnDato)
    }

    fun finnTasksTilFrontend(status: List<Status>, page: Pageable): List<ITask> {
        return taskRepository.findByStatusIn(status, page)
    }

    fun delete(it: ITask) {
        taskRepository.delete(it)
    }

}