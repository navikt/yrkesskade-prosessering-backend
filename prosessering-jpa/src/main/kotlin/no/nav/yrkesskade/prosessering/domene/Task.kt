package no.nav.yrkesskade.prosessering.domene

import no.nav.familie.log.IdUtils
import no.nav.familie.log.mdc.MDCConstants
import no.nav.yrkesskade.prosessering.TaskFeil
import no.nav.yrkesskade.prosessering.util.stringToMd5Hex
import org.slf4j.MDC
import java.io.IOException
import java.time.LocalDateTime
import java.util.Properties
import javax.persistence.CascadeType
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Version

@Entity
@Table(name = "TASK")
data class Task(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        override val id: Long = 0L,
        override val payload: String,
        override val payloadHash: String,
        @Enumerated(EnumType.STRING)
        override val status: Status = Status.UBEHANDLET,
        @Enumerated(EnumType.STRING)
        override val avvikstype: Avvikstype? = null,
        override val opprettetTid: LocalDateTime = LocalDateTime.now(),
        override val triggerTid: LocalDateTime = LocalDateTime.now(),
        override val type: String,
        @Convert(converter = PropertiesToStringConverter::class)
        override val metadata: Properties = Properties().apply {
            this[MDCConstants.MDC_CALL_ID] =
                    MDC.get(MDCConstants.MDC_CALL_ID)
                    ?: IdUtils.generateId()
        },
        @Version
        override val versjon: Long = 0,
        // Setter fetch til eager fordi AsyncTask ikke får lastet disse hvis ikke den er prelastet.
        @OneToMany(fetch = FetchType.EAGER,
                   cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH],
                   orphanRemoval = true)
        @JoinColumn(name = "task_id", nullable = false)
        override val logg: MutableList<TaskLogg> = mutableListOf(TaskLogg(type = Loggtype.UBEHANDLET))

) : ITask() {

    constructor (type: String, payload: String, properties: Properties = Properties()) :
            this(type = type,
                 payload = payload,
                 payloadHash = stringToMd5Hex(payload),
                 metadata = properties.apply {
                     this[MDCConstants.MDC_CALL_ID] =
                             MDC.get(MDCConstants.MDC_CALL_ID)
                             ?: IdUtils.generateId()
                 })

    override fun avvikshåndter(avvikstype: Avvikstype,
                               årsak: String,
                               endretAv: String): Task {

        return copy(status = Status.AVVIKSHÅNDTERT,
                    avvikstype = avvikstype,
                    logg = (logg + TaskLogg(type = Loggtype.AVVIKSHÅNDTERT,
                                            melding = årsak,
                                            endretAv = endretAv)).toMutableList())
    }

    override fun behandler(): Task {
        return copy(status = Status.BEHANDLER, logg = (logg + TaskLogg(type = Loggtype.BEHANDLER)).toMutableList())
    }

    override fun klarTilPlukk(endretAv: String, melding: String?): Task {
        return copy(status = Status.KLAR_TIL_PLUKK,
                    logg = (logg + TaskLogg(type = Loggtype.KLAR_TIL_PLUKK,
                                            endretAv = endretAv,
                                            melding = melding)).toMutableList())
    }

    override fun plukker(): Task {
        return copy(status = Status.PLUKKET, logg = (logg + TaskLogg(type = Loggtype.PLUKKET)).toMutableList())
    }

    override fun ferdigstill(): Task {
        return copy(status = Status.FERDIG, logg = (logg + TaskLogg(type = Loggtype.FERDIG)).toMutableList())
    }

    override fun feilet(feil: TaskFeil, maxAntallFeil: Int, settTilManuellOppfølgning: Boolean): Task {
        if (this.status == Status.MANUELL_OPPFØLGING) {
            return this.copy(logg = (logg + TaskLogg(type = Loggtype.MANUELL_OPPFØLGING,
                                                     melding = feil.writeValueAsString())).toMutableList())
        }

        val nyStatus = nyFeiletStatus(maxAntallFeil, settTilManuellOppfølgning)

        return try {
            this.copy(status = nyStatus,
                      logg = (logg + TaskLogg(type = Loggtype.FEILET, melding = feil.writeValueAsString())).toMutableList())

        } catch (e: IOException) {
            this.copy(status = nyStatus, logg = (logg + TaskLogg(type = Loggtype.FEILET)).toMutableList())
        }
    }

    override fun medTriggerTid(triggerTid: LocalDateTime): Task {
        return this.copy(triggerTid = triggerTid)
    }

    override fun toString(): String {
        return "Task(id=$id, status=$status, opprettetTid=$opprettetTid, triggerTid=$triggerTid, type='$type', versjon=$versjon)"
    }

}
