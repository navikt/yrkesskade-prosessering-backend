package no.nav.yrkesskade.prosessering.domene

import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: ITaskRepostitory<ITask>
