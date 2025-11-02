package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LendingNumberTest {
    @Test
    void ensureLendingNumberNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(null));
    }
    @Test
    void ensureLendingNumberNotBlank(){
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(""));
    }
    @Test
    void ensureLendingNumberNotWrongFormat(){
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("1/2024"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("24/1"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2024-1"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2024\\1"));
    }
    @Test
    void ensureLendingNumberIsSetWithString() {
        final var ln = new LendingNumber("2024/1");
        assertEquals("2024/1", ln.toString());
    }

    @Test
    void ensureLendingNumberIsSetWithSequential() {
        final LendingNumber ln = new LendingNumber(1);
        assertNotNull(ln);
        assertEquals(LocalDate.now().getYear() + "/1", ln.toString());
    }

    @Test
    void ensureLendingNumberIsSetWithYearAndSequential() {
        final LendingNumber ln = new LendingNumber(2024,1);
        assertNotNull(ln);
    }

    @Test
    void ensureSequentialCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(2024,-1));
    }

    @Test
    void ensureYearCannotBeInTheFuture() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(LocalDate.now().getYear()+1,1));
    }

    @Test
    void ensureYearCannotBeBefore1970() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber(1969,1));
    }

    @Test
    void ensureYearCanBe1970() {
        final LendingNumber ln = new LendingNumber(1970,1);
        assertNotNull(ln);
        assertEquals("1970/1", ln.toString());
    }

    @Test
    void ensureYearCanBeCurrentYear() {
        final LendingNumber ln = new LendingNumber(LocalDate.now().getYear(),1);
        assertNotNull(ln);
    }

    @Test
    void ensureSequentialCanBeZero() {
        final LendingNumber ln = new LendingNumber(2024,0);
        assertNotNull(ln);
        assertEquals("2024/0", ln.toString());
    }

    @Test
    void ensureSequentialCanBeLargeNumber() {
        final LendingNumber ln = new LendingNumber(2024,999999);
        assertNotNull(ln);
        assertEquals("2024/999999", ln.toString());
    }

    @Test
    void ensureLendingNumberStringConstructorWorksWithLargeSequential() {
        final LendingNumber ln = new LendingNumber("2024/999999");
        assertEquals("2024/999999", ln.toString());
    }

    @Test
    void ensureLendingNumberStringConstructorFailsWithInvalidSeparator() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2024:1"));
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2024.1"));
    }

    @Test
    void ensureLendingNumberStringConstructorFailsWithMissingYear() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("/1"));
    }

    @Test
    void ensureLendingNumberStringConstructorFailsWithMissingSequential() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2024/"));
    }

    @Test
    void ensureLendingNumberStringConstructorFailsWithOnlySeparator() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("/"));
    }

    @Test
    void ensureLendingNumberStringConstructorFailsWithNonNumericYear() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("abcd/1"));
    }

    @Test
    void ensureLendingNumberStringConstructorFailsWithNonNumericSequential() {
        assertThrows(IllegalArgumentException.class, () -> new LendingNumber("2024/abc"));
    }

    @Test
    void ensureTwoLendingNumbersWithSameValuesAreEqual() {
        final LendingNumber ln1 = new LendingNumber("2024/1");
        final LendingNumber ln2 = new LendingNumber(2024, 1);
        assertEquals(ln1.toString(), ln2.toString());
    }

    @Test
    void ensureLendingNumberToStringReturnsCorrectFormat() {
        final LendingNumber ln = new LendingNumber(2024, 42);
        assertEquals("2024/42", ln.toString());
    }

}