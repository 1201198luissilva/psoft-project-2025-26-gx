package pt.psoft.g1.psoftg1.lendingmanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.services.CreateLendingRequest;
import pt.psoft.g1.psoftg1.lendingmanagement.services.SetLendingReturnedRequest;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive integration tests for LendingController (system-level opaque-box testing)
 * Tests the controller + service + domain + repository + gateways integration
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LendingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LendingRepository lendingRepository;
    
    @Autowired
    private ReaderRepository readerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private GenreRepository genreRepository;
    
    @Autowired
    private AuthorRepository authorRepository;

    private Lending lending;
    private ReaderDetails readerDetails;
    private Reader reader;
    private Book book;
    private Author author;
    private Genre genre;

    @BeforeEach
    public void setUp() {
        author = new Author("Manuel Antonio Pina",
                "Manuel António Pina foi um jornalista e escritor português, premiado em 2011 com o Prémio Camões",
                null);
        authorRepository.save(author);

        genre = new Genre("Género");
        genreRepository.save(genre);

        List<Author> authors = List.of(author);
        book = new Book("9782826012092",
                "O Inspetor Max",
                "conhecido pastor-alemão que trabalha para a Judiciária, vai ser fundamental para resolver um importante caso de uma rede de malfeitores que quer colocar uma bomba num megaconcerto de uma ilustre cantora",
                genre,
                authors,
                null);
        bookRepository.save(book);

        reader = Reader.newReader("manuel@gmail.com", "Manuelino123!", "Manuel Sarapinto das Coives");
        userRepository.save(reader);

        readerDetails = new ReaderDetails(1,
                reader,
                "2000-01-01",
                "919191919",
                true,
                true,
                true,
                null,
                null);
        readerRepository.save(readerDetails);

        lending = Lending.newBootstrappingLending(book,
                readerDetails,
                LocalDate.now().getYear(),
                999,
                LocalDate.of(LocalDate.now().getYear(), 1, 1),
                null,
                15,
                300);
        lendingRepository.save(lending);
    }

    @AfterEach
    public void tearDown() {
        lendingRepository.delete(lending);
        readerRepository.delete(readerDetails);
        userRepository.delete(reader);
        bookRepository.delete(book);
        genreRepository.delete(genre);
        authorRepository.delete(author);
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testCreateLending() throws Exception {
        CreateLendingRequest request = new CreateLendingRequest(
                "9782826012092",
                LocalDate.now().getYear() + "/1"
        );

        mockMvc.perform(post("/api/lendings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lendingNumber").exists())
                .andExpect(jsonPath("$.title").value("O Inspetor Max"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testGetLendingByNumber() throws Exception {
        mockMvc.perform(get("/api/lendings/" + LocalDate.now().getYear() + "/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lendingNumber").value(LocalDate.now().getYear() + "/999"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testGetLendingByNumberNotFound() throws Exception {
        mockMvc.perform(get("/api/lendings/9999/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "manuel@gmail.com", roles = {"READER"})
    public void testSetLendingReturned() throws Exception {
        // First, create a new lending that hasn't been returned
        int year = LocalDate.now().getYear();
        int seq = 1000;
        var newLending = Lending.newBootstrappingLending(book,
                readerDetails,
                year,
                seq,
                LocalDate.now().minusDays(5),
                null,
                15,
                300);
        lendingRepository.save(newLending);

        SetLendingReturnedRequest request = new SetLendingReturnedRequest("Great book!");

        mockMvc.perform(patch("/api/lendings/" + year + "/" + seq)
                        .header("If-Match", String.valueOf(newLending.getVersion()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedDate").exists());

        lendingRepository.delete(newLending);
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testSetLendingReturnedWithoutIfMatch() throws Exception {
        SetLendingReturnedRequest request = new SetLendingReturnedRequest(null);

        mockMvc.perform(patch("/api/lendings/" + LocalDate.now().getYear() + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testGetAverageDuration() throws Exception {
        // Create some returned lendings
        var lending1 = Lending.newBootstrappingLending(book,
                readerDetails,
                2023,
                1,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 10),
                15,
                300);
        lendingRepository.save(lending1);

        mockMvc.perform(get("/api/lendings/avgDuration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageDuration").exists());

        lendingRepository.delete(lending1);
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testGetOverdueLendings() throws Exception {
        // Create an overdue lending
        var overdueLending = Lending.newBootstrappingLending(book,
                readerDetails,
                2023,
                100,
                LocalDate.of(2023, 1, 1),
                null,
                15,
                300);
        lendingRepository.save(overdueLending);

        mockMvc.perform(get("/api/lendings/overdue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\":1,\"limit\":10}"))
                .andExpect(status().isOk());

        lendingRepository.delete(overdueLending);
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testSearchLendings() throws Exception {
        String searchRequest = "{"
                + "\"page\": {\"number\": 1, \"limit\": 10},"
                + "\"query\": {"
                + "  \"readerNumber\": \"" + LocalDate.now().getYear() + "/1\","
                + "  \"isbn\": \"9782826012092\","
                + "  \"startDate\": \"" + LocalDate.now().minusDays(30).toString() + "\","
                + "  \"endDate\": \"" + LocalDate.now().toString() + "\""
                + "}"
                + "}";

        mockMvc.perform(post("/api/lendings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(searchRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateLendingWithoutAuthentication() throws Exception {
        CreateLendingRequest request = new CreateLendingRequest(
                "9782826012092",
                LocalDate.now().getYear() + "/1"
        );

        mockMvc.perform(post("/api/lendings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "otherreader@gmail.com", roles = {"READER"})
    public void testGetLendingByNumberAccessDeniedForOtherReader() throws Exception {
        mockMvc.perform(get("/api/lendings/" + LocalDate.now().getYear() + "/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
