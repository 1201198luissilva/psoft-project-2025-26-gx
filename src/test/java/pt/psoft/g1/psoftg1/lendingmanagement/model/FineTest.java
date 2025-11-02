package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Fine domain class (transparent-box testing)
 */
class FineTest {
    private static final ArrayList<Author> authors = new ArrayList<>();
    private static Book book;
    private static ReaderDetails readerDetails;
    private static final int FINE_VALUE_PER_DAY_IN_CENTS = 200;
    private static final int LENDING_DURATION = 15;

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
    void ensureFineIsCreatedForOverdueLending(){
        // Create an overdue lending
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Fine fine = new Fine(lending);
        assertNotNull(fine);
        assertEquals(FINE_VALUE_PER_DAY_IN_CENTS, fine.getFineValuePerDayInCents());
        assertTrue(fine.getCentsValue() > 0);
        assertEquals(lending, fine.getLending());
    }

    @Test
    void ensureFineValueIsCalculatedCorrectly(){
        int daysLate = 10;
        // Create a lending that's 10 days late
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(LENDING_DURATION + daysLate),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Fine fine = new Fine(lending);
        int expectedFineValue = FINE_VALUE_PER_DAY_IN_CENTS * daysLate;
        assertEquals(expectedFineValue, fine.getCentsValue());
    }

    @Test
    void ensureFineCannotBeCreatedForNonOverdueLending(){
        // Create a lending that's not overdue
        Lending lending = new Lending(book, readerDetails, 1, LENDING_DURATION, FINE_VALUE_PER_DAY_IN_CENTS);
        
        assertThrows(IllegalArgumentException.class, () -> new Fine(lending));
    }

    @Test
    void ensureFineCannotBeCreatedWithNullLending(){
        assertThrows(NullPointerException.class, () -> new Fine(null));
    }

    @Test
    void ensureFineStoresFineValuePerDayAtCreationTime(){
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Fine fine = new Fine(lending);
        // The fine should store the fine value per day from the lending
        assertEquals(lending.getFineValuePerDayInCents(), fine.getFineValuePerDayInCents());
    }

    @Test
    void ensureFineCanBeSetWithDifferentLending(){
        // Create two overdue lendings
        Lending lending1 = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Lending lending2 = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                2,
                LocalDate.now().minusDays(25),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Fine fine = new Fine(lending1);
        assertEquals(lending1, fine.getLending());
        
        fine.setLending(lending2);
        assertEquals(lending2, fine.getLending());
    }

    @Test
    void ensureFineValueIsPositive(){
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Fine fine = new Fine(lending);
        assertTrue(fine.getCentsValue() >= 0);
    }

    @Test
    void ensureFineValuePerDayIsPositive(){
        Lending lending = Lending.newBootstrappingLending(
                book,
                readerDetails,
                2024,
                1,
                LocalDate.now().minusDays(30),
                null,
                LENDING_DURATION,
                FINE_VALUE_PER_DAY_IN_CENTS
        );
        
        Fine fine = new Fine(lending);
        assertTrue(fine.getFineValuePerDayInCents() >= 0);
    }
}
