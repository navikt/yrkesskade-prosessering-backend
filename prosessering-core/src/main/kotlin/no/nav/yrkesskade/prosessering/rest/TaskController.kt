package no.nav.yrkesskade.prosessering.rest

import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.sikkerhet.OIDCUtil
import no.nav.security.token.support.core.api.Unprotected
import no.nav.yrkesskade.prosessering.domene.Status
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Unprotected
class TaskController(private val restTaskService: RestTaskService, private val oidcUtil: OIDCUtil) {

    fun hentBrukernavn(): String {
//        return oidcUtil.getClaim("preferred_username")
        return ""
    }

    @GetMapping(path = ["/v2/task", "task/v2"])
    fun task2(@RequestParam status: Status?,
              @RequestParam(required = false) page: Int?): ResponseEntity<Ressurs<PaginableResponse<TaskDto>>> {
        val statuser: List<Status> = status?.let { listOf(it) } ?: Status.values().toList()
        return ResponseEntity.ok(restTaskService.hentTasks(statuser, hentBrukernavn(), page ?: 0))
    }

    @GetMapping(path = ["/task/logg/{id}"])
    fun tasklogg(@PathVariable id: Long,
                 @RequestParam(required = false) page: Int?): ResponseEntity<Ressurs<List<TaskloggDto>>> {
        return ResponseEntity.ok(restTaskService.hentTaskLogg(id, hentBrukernavn()))
    }

    @PutMapping(path = ["/task/rekjor"])
    fun rekjørTask(@RequestParam taskId: Long): ResponseEntity<Ressurs<String>> {
        return ResponseEntity.ok(restTaskService.rekjørTask(taskId, hentBrukernavn()))
    }

    @PutMapping(path = ["task/rekjorAlle"])
    fun rekjørTasks(@RequestHeader status: Status): ResponseEntity<Ressurs<String>> {
        return ResponseEntity.ok(restTaskService.rekjørTasks(status, hentBrukernavn()))
    }

    @PutMapping(path = ["/task/avvikshaandter"])
    fun avvikshåndterTask(@RequestParam taskId: Long,
                          @RequestBody avvikshåndterDTO: AvvikshåndterDTO): ResponseEntity<Ressurs<String>> {
        return ResponseEntity.ok(restTaskService.avvikshåndterTask(taskId,
                                                                   avvikshåndterDTO.avvikstype,
                                                                   avvikshåndterDTO.årsak,
                                                                   hentBrukernavn()))
    }
}
