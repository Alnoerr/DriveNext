package dev.alnoer.drivenext.presentation.auth

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object AuthValidation {
    fun isEmailValid(value: String): Boolean =
        Regex("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)*\\.(com|ru)$", RegexOption.IGNORE_CASE)
            .matches(value.trim())

    fun date(value: String, pattern: String): LocalDate? = try {
        LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT))
    } catch (_: Exception) {
        null
    }

    fun isBirthDateValid(value: String): Boolean =
        date(value, "MM/dd/uuuu")?.let { it.isBefore(LocalDate.now()) } == true

    fun isIssueDateValid(value: String, birthDate: String): Boolean =
        date(value, "dd/MM/uuuu")?.let {
            !it.isAfter(LocalDate.now()) && date(birthDate, "MM/dd/uuuu")?.isBefore(it) == true
        } == true
}
