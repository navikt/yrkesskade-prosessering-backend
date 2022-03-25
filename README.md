# Yrkesskade-prosessering-backend

Prosesseringsmotor for yrkesskadeområdet.

## Generellt
* Oppdater status i task-tabellen til lengde 20: `ALTER TABLE task ALTER COLUMN status VARCHAR(20)  DEFAULT 'UBEHANDLET'::CHARACTER VARYING NOT NULL;`


## JDBC
* Må sette opp converters, eks:
```kotlin
@Configuration
class DatabaseConfiguration : AbstractJdbcConfiguration() {

    @Bean
    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(listOf(StringTilPropertiesWrapperConverter(),
                                            PropertiesWrapperTilStringConverter()))
    }
}
```

## JPA


![](https://github.com/navikt/yrkesskade-prosessering-backend/workflows/Build-Deploy/badge.svg)

# Henvendelser

Enten:
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

Eller:
Spørsmål knyttet til koden eller prosjektet kan stilles til yrkesskade@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #team-yrkesskade.

