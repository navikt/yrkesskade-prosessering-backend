package no.nav.yrkesskade.prosessering

import no.nav.yrkesskade.prosessering.domene.PropertiesWrapper
import no.nav.yrkesskade.prosessering.domene.asProperties
import no.nav.yrkesskade.prosessering.domene.asString
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class StringTilPropertiesWrapperConverter : Converter<String, PropertiesWrapper> {

    override fun convert(p0: String): PropertiesWrapper? {
        return PropertiesWrapper(p0.asProperties())
    }
}

@WritingConverter
class PropertiesWrapperTilStringConverter : Converter<PropertiesWrapper, String> {

    override fun convert(taskPropertiesWrapper: PropertiesWrapper): String? {
        return taskPropertiesWrapper.properties.asString()
    }

}
