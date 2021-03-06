package no.nav.yrkesskade.prosessering.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.nav.yrkesskade.prosessering.TestAppConfig
import no.nav.yrkesskade.prosessering.domene.Loggtype
import no.nav.yrkesskade.prosessering.domene.Status
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import no.nav.yrkesskade.prosessering.task.TaskStep1
import no.nav.yrkesskade.prosessering.task.TaskStep2
import no.nav.yrkesskade.prosessering.task.TaskStepMedFeil
import no.nav.yrkesskade.prosessering.task.TaskStepMedFeilMedTriggerTid0
import no.nav.yrkesskade.prosessering.task.TaskStepRekjørSenere
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
import java.time.LocalDate
import java.util.UUID

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestAppConfig::class])
@DataJpaTest(excludeAutoConfiguration = [FlywayAutoConfiguration::class])
class TaskStepExecutorServiceTest {

    @Autowired
    private lateinit var repository: TaskRepository

    @Autowired
    private lateinit var taskStepExecutorService: TaskStepExecutorService

    @AfterEach
    fun clear() {
        repository.deleteAll()
    }

    @Test
    fun `skal håndtere feil`() {
        var savedTask = Task(TaskStepMedFeil.TYPE, "{'a'='b'}")
        repository.save(savedTask)
        TestTransaction.flagForCommit()
        TestTransaction.end()

        assertThat(savedTask.status).isEqualTo(Status.UBEHANDLET)

        taskStepExecutorService.pollAndExecute()

        savedTask = repository.findById(savedTask.id).orElseThrow()
        assertThat(savedTask.status).isEqualTo(Status.KLAR_TIL_PLUKK)
        assertThat(savedTask.logg.filter { it.type == Loggtype.FEILET }).hasSize(1)
    }

    @Test
    fun `kommer rekjøre tasker som har 0 i triggerTidVedFeilISekunder direkte`() {
        var savedTask = Task(TaskStepMedFeilMedTriggerTid0.TYPE, "{'a'='b'}")
        repository.save(savedTask)
        TestTransaction.flagForCommit()
        TestTransaction.end()

        assertThat(savedTask.status).isEqualTo(Status.UBEHANDLET)

        taskStepExecutorService.pollAndExecute()

        savedTask = repository.findById(savedTask.id).orElseThrow()
        assertThat(savedTask.status).isEqualTo(Status.FEILET)
        assertThat(savedTask.logg.filter { it.type == Loggtype.FEILET }).hasSize(3)
    }

    @Test
    fun `skal håndtere samtidighet`() {
        repeat(100) {
            val task2 = Task(TaskStep2.TASK_2, "{'a'='b'}")
            repository.save(task2)
        }
        TestTransaction.flagForCommit()
        TestTransaction.end()

        runBlocking {
            val launch = GlobalScope.launch {
                repeat(10) { taskStepExecutorService.pollAndExecute() }
            }
            val launch2 = GlobalScope.launch {
                repeat(10) { taskStepExecutorService.pollAndExecute() }
            }

            launch.join()
            launch2.join()
        }
        val findAll = repository.findAll()
        findAll.filter { it.status != Status.FERDIG || it.logg.size > 4 }.forEach {
            assertThat(it.status).isEqualTo(Status.FERDIG)
            assertThat(it.logg.size).isEqualTo(4)
        }

    }

    @Test
    internal fun `rekjørSenere - skal rekjøre tasks som kaster RekjørSenereException`() {
        val task = repository.save(Task(TaskStepRekjørSenere.TYPE, UUID.randomUUID().toString()))
        TestTransaction.flagForCommit()
        TestTransaction.end()

        taskStepExecutorService.pollAndExecute()

        val lagretTask = repository.findByIdOrNull(task.id)!!
        assertThat(lagretTask.triggerTid).isEqualTo(LocalDate.of(2088, 1, 1).atStartOfDay())
        assertThat(lagretTask.status).isEqualTo(Status.KLAR_TIL_PLUKK)
    }

    @Test
    internal fun `skal kjøre task 2 direkte når pollAndExecute er ferdig`() {
        val task = repository.save(Task(TaskStep1.TASK_1, UUID.randomUUID().toString()))
        TestTransaction.flagForCommit()
        TestTransaction.end()

        taskStepExecutorService.pollAndExecute()

        val tasks = repository.findAll()
        val task2 = tasks.single { it.id != task.id }
        assertThat(task2.status).isEqualTo(Status.FERDIG)
    }

}
