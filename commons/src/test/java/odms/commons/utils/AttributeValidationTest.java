package odms.commons.utils;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttributeValidationTest {
    @Test
    public void testValidNhi() {
        assertTrue(AttributeValidation.validateNHI("ABC1234"));
    }

    @Test
    public void testInvalidNhi() {
        assertFalse(AttributeValidation.validateNHI("ABCD234"));
    }

    @Test
    public void testEmptyNhi() {
        assertFalse(AttributeValidation.validateNHI(""));
    }

    @Test
    public void testValidEmail() {
        assertTrue(AttributeValidation.validateEmail("eli26@uclive.ac.nz"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(AttributeValidation.validateEmail("yoohoo"));
    }

    @Test
    public void testShortValidEmail() {
        assertTrue(AttributeValidation.validateEmail("e@a.nz"));
    }

    @Test
    public void testInternationalNumber() {
        assertTrue(AttributeValidation.validatePhoneNumber("+44 20 8673 5497"));
    }

    @Test
    public void testTextInPhoneNum() {
        assertFalse(AttributeValidation.validatePhoneNumber("+64AAAAA"));
    }

    @Test
    public void testValidCellNum() {
        assertTrue(AttributeValidation.validateCellNumber("+642040989225"));
    }

    @Test
    public void testInternationalCellNum() {
        assertTrue(AttributeValidation.validateCellNumber("+61491570158"));
    }

    @Test
    public void testInvalidCellNum() {
        assertFalse(AttributeValidation.validateCellNumber("AAAASDsadsdaAD';"));
    }

    @Test
    public void testValidDateOfDeath() {
        assertTrue(AttributeValidation.validateDateOfDeath(LocalDate.of(1995, 5, 8), LocalDate.of(2015, 6, 9)));
    }

    @Test
    public void testFutureDateOfDeath() {
        assertFalse(AttributeValidation.validateDateOfDeath(LocalDate.now().minusYears(10), LocalDate.now().plusDays(1)));
    }

    @Test
    public void testDoDBeforeDoB() {
        assertFalse(AttributeValidation.validateDateOfDeath(LocalDate.now().minusDays(5), LocalDate.now().minusDays(6)));
    }

    @Test
    public void testValidateEligibleOrganDateToday() {
        assertFalse(AttributeValidation.validateEligibleOrganDate(LocalDate.now()));
    }
    @Test
    public void testValidateEligibleOrganDateNull() {
        assertTrue(AttributeValidation.validateEligibleOrganDate(null));
    }
}
