package no.nav.yrkesskade.prosessering.internal

import no.nav.yrkesskade.prosessering.TestAppConfig
import no.nav.yrkesskade.prosessering.domene.Status
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestAppConfig::class])
@DataJpaTest(excludeAutoConfiguration = [FlywayAutoConfiguration::class])
class TaskSchedulerTest {


    @Autowired
    private lateinit var tasksScheduler: TaskScheduler

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @AfterEach
    fun clear() {
        taskRepository.deleteAll()
    }

    @Test
    @Sql("classpath:sql-testdata/gamle_tasker_med_logg.sql")
    fun `skal slette gamle tasker med status FERDIG`() {
        tasksScheduler.slettTasksKlarForSletting()

        assertThat(taskRepository.findAll())
                .hasSize(1)
                .extracting("status").containsOnly(Status.KLAR_TIL_PLUKK)
    }

    @Test
    fun `skal ikke slette nye tasker`() {
        val nyTask = Task("type", "payload")
        nyTask.ferdigstill()
        taskRepository.save(nyTask)


        tasksScheduler.slettTasksKlarForSletting()

        taskRepository.findAll()
        assertThat(taskRepository.findAll())
                .filteredOn("id", nyTask.id)
                .isNotEmpty
    }

    @Test
    fun `skal sette tasker som har vært plukket i mer enn en time klar til plukk`() {
        var task = Task("type", "payload").plukker()
        task = task.copy(logg = mutableListOf(task.logg.last().copy(opprettetTid = LocalDateTime.now().minusMinutes(61))))
        val saved = taskRepository.save(task)

        tasksScheduler.settPermanentPlukketTilKlarTilPlukk()

        assertThat(taskRepository.findByIdOrNull(saved.id)!!.status).isEqualTo(Status.KLAR_TIL_PLUKK)
    }

    @Test
    fun `skal ikke gjøre noe med tasker som har vært plukket i mindre enn en time`() {
        var task = Task("type", "payload").plukker()
        task = task.copy(logg = mutableListOf(task.logg.last().copy(opprettetTid = LocalDateTime.now().minusMinutes(59))))
        val saved = taskRepository.save(task)

        tasksScheduler.settPermanentPlukketTilKlarTilPlukk()

        assertThat(taskRepository.findByIdOrNull(saved.id)!!.status).isEqualTo(Status.PLUKKET)
    }


}
