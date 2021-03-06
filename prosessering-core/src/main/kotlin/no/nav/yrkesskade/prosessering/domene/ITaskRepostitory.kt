package no.nav.yrkesskade.prosessering.domene

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

@NoRepositoryBean
interface ITaskRepostitory<T : ITask> : PagingAndSortingRepository<T, Long> {

    fun findByStatusInAndTriggerTidBeforeOrderByOpprettetTidDesc(status: List<Status>,
                                                                 triggerTid: LocalDateTime,
                                                                 page: Pageable): List<T>

    fun findByStatus(status: Status): List<T>

    fun findByStatusIn(status: List<Status>, page: Pageable): List<T>

    fun findByStatusAndTriggerTidBefore(status: Status, triggerTid: LocalDateTime): List<T>
}