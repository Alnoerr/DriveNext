package dev.alnoer.drivenext

import dev.alnoer.drivenext.presentation.auth.AuthValidation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AuthValidationTest {
    @Test
    fun emailAcceptsSupportedDomains() {
        assertTrue(AuthValidation.isEmailValid("name@domainname.com"))
        assertTrue(AuthValidation.isEmailValid(" name@domainname.ru "))
        assertTrue(AuthValidation.isEmailValid("name@mail.domainname.COM"))
        assertFalse(AuthValidation.isEmailValid("name@domainname.org"))
        assertFalse(AuthValidation.isEmailValid("name@.com"))
        assertFalse(AuthValidation.isEmailValid("name domain@domainname.com"))
        assertFalse(AuthValidation.isEmailValid("name@-domainname.com"))
    }

    @Test
    fun birthDateUsesMonthFirstAndRejectsImpossibleDates() {
        assertTrue(AuthValidation.isBirthDateValid("12/31/2000"))
        assertTrue(AuthValidation.isBirthDateValid("02/29/2000"))
        assertFalse(AuthValidation.isBirthDateValid("31/12/2000"))
        assertFalse(AuthValidation.isBirthDateValid("02/29/2001"))
        assertFalse(AuthValidation.isBirthDateValid("02/30/2000"))
        assertFalse(AuthValidation.isBirthDateValid(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/uuuu"))))
    }

    @Test
    fun issueDateUsesDayFirstAndMustFollowBirthDate() {
        assertTrue(AuthValidation.isIssueDateValid("31/12/2020", "12/31/2000"))
        assertFalse(AuthValidation.isIssueDateValid("12/31/2020", "12/31/2000"))
        assertFalse(AuthValidation.isIssueDateValid("31/02/2020", "12/31/2000"))
        assertFalse(AuthValidation.isIssueDateValid("30/12/2000", "12/31/2000"))
        assertFalse(AuthValidation.isIssueDateValid(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu")), "12/31/2000"))
    }
}
