package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@PropertySource({"classpath:config/library.properties"})
class LendingTest {
    private static final ArrayList<Author> authors = new ArrayList<>();
    private static Book book;
    private static ReaderDetails readerDetails;
    @Value("${lendingDurationInDays}")
    private int lendingDurationInDays;
    @Value("${fineValuePerDayInCents}")
    private int fineValuePerDayInCents;

    @BeforeAll
    public static void setup(){
        Author author = new Author("Manuel Antonio Pina",
                "Manuel António Pina foi um jornalista e escritor português, premiado em 2011 com o Prémio Camões",
                null);
        authors.add(author);
        book = new Book("9782826012092",
                "O Inspetor Max",
                "conhecido pastor-alemão que trabalha para a Judiciária, vai ser fundamental para resolver um importante caso de uma rede de malfeitores que quer colocar uma bomba num megaconcerto de uma ilustre cantora",
                new Genre("Romance"),
                authors,
                null);
        readerDetails = new ReaderDetails(1,
                Reader.newReader("manuel@gmail.com", "Manuelino123!", "Manuel Sarapinto das Coives"),
                "2000-01-01",
                "919191919",
                true,
                true,
                true,
                null,
                null);
    }

    @Test
    void ensureBookNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Lending(null, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents));
    }

    @Test
    void ensureReaderNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Lending(book, null, 1, lendingDurationInDays, fineValuePerDayInCents));
    }

    @Test
    void ensureValidReaderNumber(){
        assertThrows(IllegalArgumentException.class, () -> new Lending(book, readerDetails, -1, lendingDurationInDays, fineValuePerDayInCents));
    }

    @Test
    void testSetReturned(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        lending.setReturned(0,null);
        assertEquals(LocalDate.now(), lending.getReturnedDate());
    }

    @Test
    void testGetDaysDelayed(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(0, lending.getDaysDelayed());
    }

    @Test
    void testGetDaysUntilReturn(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(Optional.of(lendingDurationInDays), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysOverDue(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(Optional.empty(), lending.getDaysOverdue());
    }

    @Test
    void testGetTitle() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals("O Inspetor Max", lending.getTitle());
    }

    @Test
    void testGetLendingNumber() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(LocalDate.now().getYear() + "/1", lending.getLendingNumber());
    }

    @Test
    void testGetBook() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(book, lending.getBook());
    }

    @Test
    void testGetReaderDetails() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(readerDetails, lending.getReaderDetails());
    }

    @Test
    void testGetStartDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(LocalDate.now(), lending.getStartDate());
    }

    @Test
    void testGetLimitDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(LocalDate.now().plusDays(lendingDurationInDays), lending.getLimitDate());
    }

    @Test
    void testGetReturnedDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertNull(lending.getReturnedDate());
    }

    @Test
    void testSetReturnedWithCommentary(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        String commentary = "Great book!";
        lending.setReturned(0, commentary);
        assertEquals(LocalDate.now(), lending.getReturnedDate());
    }

    @Test
    void testSetReturnedThrowsExceptionWhenAlreadyReturned(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        lending.setReturned(0, null);
        assertThrows(IllegalArgumentException.class, () -> lending.setReturned(0, "Another comment"));
    }

    @Test
    void testSetReturnedThrowsStaleObjectStateException(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertThrows(org.hibernate.StaleObjectStateException.class, () -> lending.setReturned(999, null));
    }

    @Test
    void testGetDaysDelayedWithOverdueLending(){
        // Create a lending that is already overdue (started in the past)
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                15,
                fineValuePerDayInCents
        );
        assertTrue(lending.getDaysDelayed() > 0);
    }

    @Test
    void testGetDaysDelayedWithReturnedLateLending(){
        // Create a lending that was returned late
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(10),
                15,
                fineValuePerDayInCents
        );
        assertEquals(5, lending.getDaysDelayed()); // 30-15-10 = 5 days late
    }

    @Test
    void testGetDaysUntilReturnWhenAlreadyReturned(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        lending.setReturned(0, null);
        assertEquals(Optional.empty(), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysUntilReturnWhenOverdue(){
        // Create an overdue lending
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                15,
                fineValuePerDayInCents
        );
        assertEquals(Optional.empty(), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysOverdueWhenOverdue(){
        // Create an overdue lending
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                15,
                fineValuePerDayInCents
        );
        assertTrue(lending.getDaysOverdue().isPresent());
        assertTrue(lending.getDaysOverdue().get() > 0);
    }

    @Test
    void testGetFineValueInCents(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(Optional.empty(), lending.getFineValueInCents());
    }

    @Test
    void testGetFineValueInCentsWhenOverdue(){
        // Create an overdue lending (started 20 days ago with 15 day duration = 5 days overdue)
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(20),
                null,
                15,
                200 // Use explicit value instead of property
        );
        assertTrue(lending.getFineValueInCents().isPresent());
        assertTrue(lending.getFineValueInCents().get() > 0);
        assertEquals(5 * 200, lending.getFineValueInCents().get()); // 5 days * 200 cents/day
    }

    @Test
    void testGetFineValuePerDayInCents(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(fineValuePerDayInCents, lending.getFineValuePerDayInCents());
    }

    @Test
    void testNewBootstrappingLendingWithNullBook(){
        assertThrows(IllegalArgumentException.class, () -> 
            Lending.newBootstrappingLending(null, readerDetails, 2024, 1, LocalDate.now(), null, 15, 200));
    }

    @Test
    void testNewBootstrappingLendingWithNullReader(){
        assertThrows(IllegalArgumentException.class, () -> 
            Lending.newBootstrappingLending(book, null, 2024, 1, LocalDate.now(), null, 15, 200));
    }

    @Test
    void testNewBootstrappingLendingCreatesCorrectly(){
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate returnedDate = LocalDate.of(2024, 1, 11);
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                5,
                startDate,
                returnedDate,
                15,
                200
        );
        assertNotNull(lending);
        assertEquals("2024/5", lending.getLendingNumber());
        assertEquals(startDate, lending.getStartDate());
        assertEquals(returnedDate, lending.getReturnedDate());
        assertEquals(startDate.plusDays(15), lending.getLimitDate());
    }

    @Test
    void testGetVersion(){
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents);
        assertEquals(0, lending.getVersion());
    }

}
