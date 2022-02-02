package no.nav.yrkesskade.prosessering.rest

import no.nav.yrkesskade.prosessering.TestAppConfig
import no.nav.yrkesskade.prosessering.domene.Status
import no.nav.yrkesskade.prosessering.domene.Task
import no.nav.yrkesskade.prosessering.domene.TaskRepository
import no.nav.yrkesskade.prosessering.task.TaskStep1
import no.nav.yrkesskade.prosessering.task.TaskStep2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestAppConfig::class])
@DataJpaTest(excludeAutoConfiguration = [FlywayAutoConfiguration::class])
internal class TaskControllerIntegrasjonTest {

    @Autowired
    lateinit var restTaskService: RestTaskService
    @Autowired
    lateinit var repository: TaskRepository

    lateinit var taskController: TaskController

    @BeforeEach
    fun setup() {
        taskController = TaskController(restTaskService)
        //        every { taskController.hentBrukernavn() } returns "" ""

    }

    @AfterEach
    fun resetDatabaseInnhold() {
        repository.deleteAll()
    }

    @Test
    fun `skal bare rekjøre tasker status FEILET`() {
        var ubehandletTask = Task(type = TaskStep1.TASK_1, payload = "{'a'='b'}", status = Status.UBEHANDLET)
        var taskSomSkalRekjøres = Task(type = TaskStep2.TASK_2, payload = "{'a'='1'}", status = Status.FEILET)
        var avvikshåndtert = Task(type = TaskStep2.TASK_2, payload = "{'a'='1'}", status = Status.AVVIKSHÅNDTERT)
        ubehandletTask = repository.save(ubehandletTask)
        taskSomSkalRekjøres = repository.save(taskSomSkalRekjøres)
        avvikshåndtert = repository.save(avvikshåndtert)

        val response = taskController.rekjørTasks(Status.FEILET)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(repository.findById(taskSomSkalRekjøres.id).get().status).isEqualTo(Status.KLAR_TIL_PLUKK)
        assertThat(repository.findById(ubehandletTask.id).get().status).isEqualTo(Status.UBEHANDLET)
        assertThat(repository.findById(avvikshåndtert.id).get().status).isEqualTo(Status.AVVIKSHÅNDTERT)
    }


}
