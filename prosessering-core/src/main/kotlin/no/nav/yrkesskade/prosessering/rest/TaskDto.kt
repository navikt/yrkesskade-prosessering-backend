package no.nav.yrkesskade.prosessering.rest

import no.nav.yrkesskade.prosessering.domene.Avvikstype
import no.nav.yrkesskade.prosessering.domene.Loggtype
import no.nav.yrkesskade.prosessering.domene.Status
import java.time.LocalDateTime
import java.util.Properties

// Legger opp for fremtidlig mulighet for å legge inn metadata som sidnummer, etc
data class PaginableResponse<T>(val tasks: List<T>)

data class TaskDto(val id: Long,
                   val status: Status = Status.UBEHANDLET,
                   val avvikstype: Avvikstype?,
                   val opprettetTidspunkt: LocalDateTime,
                   val triggerTid: LocalDateTime?,
                   val taskStepType: String,
                   val metadata: Properties,
                   val payload: String,
                   val antallLogger: Int,
                   val sistKjørt: LocalDateTime?,
                   val callId: String)

data class TaskloggDto(val id: Long,
                       val endretAv: String?,
                       val type: Loggtype,
                       val node: String,
                       val melding: String?,
                       val opprettetTidspunkt: LocalDateTime)
