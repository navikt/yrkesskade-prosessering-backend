package no.nav.yrkesskade.prosessering.internal

import no.nav.yrkesskade.prosessering.TestAppConfig
import no.nav.yrkesskade.prosessering.domene.Status
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import no.nav.yrkesskade.prosessering.task.TaskStep1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.transaction.TestTransaction

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestAppConfig::class])
@DataJpaTest(excludeAutoConfiguration = [FlywayAutoConfiguration::class])
class TaskWorkerTest {

    @Autowired
    private lateinit var repository: TaskRepository

    @Autowired
    private lateinit var worker: TaskWorker

    @AfterEach
    fun clear() {
        repository.deleteAll()
    }

    @Test
    fun `skal behandle task`() {
        val task1 = Task(TaskStep1.TASK_1, "{'a'='b'}").plukker()
        repository.save(task1)
        assertThat(task1.status).isEqualTo(Status.PLUKKET)
        TestTransaction.flagForCommit()
        TestTransaction.end()
        worker.doActualWork(task1.id)
        val findByIdOrNull = repository.findByIdOrNull(task1.id)
        assertThat(findByIdOrNull?.status).isEqualTo(Status.FERDIG)
        assertThat(findByIdOrNull?.logg).hasSize(4)
    }

}
