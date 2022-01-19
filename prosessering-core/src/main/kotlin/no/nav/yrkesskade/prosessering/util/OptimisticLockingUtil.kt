package no.nav.yrkesskade.prosessering.util

import org.springframework.dao.OptimisticLockingFailureException

inline fun isOptimisticLocking(e: Exception): Boolean =
        e is OptimisticLockingFailureException || e.cause is OptimisticLockingFailureException
