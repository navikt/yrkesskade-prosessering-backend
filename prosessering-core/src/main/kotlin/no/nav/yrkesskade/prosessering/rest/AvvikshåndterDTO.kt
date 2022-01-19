package no.nav.yrkesskade.prosessering.rest

import no.nav.yrkesskade.prosessering.domene.Avvikstype

data class AvvikshåndterDTO(val avvikstype: Avvikstype,
                            val årsak: String)
