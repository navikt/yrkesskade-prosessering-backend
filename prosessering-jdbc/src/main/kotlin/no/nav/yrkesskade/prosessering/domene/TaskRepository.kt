package no.nav.yrkesskade.prosessering.domene

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
interface TaskRepository : ITaskRepostitory<Task>
