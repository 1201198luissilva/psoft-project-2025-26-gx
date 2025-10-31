package pt.psoft.g1.psoftg1.authormanagement.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookShortView;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-31T15:58:57+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class AuthorViewMapperImpl extends AuthorViewMapper {

    @Override
    public AuthorView toAuthorView(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorView authorView = new AuthorView();

        authorView.setAuthorNumber( author.getAuthorNumber() );
        authorView.setBio( map( author.getBio() ) );
        authorView.setName( map( author.getName() ) );
        authorView.setPhoto( map( author.getPhoto() ) );

        authorView.set_links( mapLinks(author) );

        return authorView;
    }

    @Override
    public List<AuthorView> toAuthorView(List<Author> authors) {
        if ( authors == null ) {
            return null;
        }

        List<AuthorView> list = new ArrayList<AuthorView>( authors.size() );
        for ( Author author : authors ) {
            list.add( toAuthorView( author ) );
        }

        return list;
    }

    @Override
    public CoAuthorView toCoAuthorView(Author author, List<Book> books) {
        if ( author == null && books == null ) {
            return null;
        }

        Map<String, Object> links = null;
        String name = null;
        if ( author != null ) {
            links = mapLinks( author );
            name = map( author.getName() );
        }
        List<BookShortView> books1 = null;
        books1 = toBookShortView( books );

        CoAuthorView coAuthorView = new CoAuthorView( name, links, books1 );

        return coAuthorView;
    }

    @Override
    public BookShortView toBookShortView(Book book) {
        if ( book == null ) {
            return null;
        }

        BookShortView bookShortView = new BookShortView();

        bookShortView.set_links( mapShortBookLink( book ) );
        bookShortView.setIsbn( map( book.getIsbn() ) );
        bookShortView.setTitle( map( book.getTitle() ) );

        return bookShortView;
    }

    @Override
    public List<BookShortView> toBookShortView(List<Book> books) {
        if ( books == null ) {
            return null;
        }

        List<BookShortView> list = new ArrayList<BookShortView>( books.size() );
        for ( Book book : books ) {
            list.add( toBookShortView( book ) );
        }

        return list;
    }

    @Override
    public AuthorCoAuthorBooksView toAuthorCoAuthorBooksView(Author author, List<CoAuthorView> coauthors) {
        if ( author == null && coauthors == null ) {
            return null;
        }

        AuthorView author1 = null;
        author1 = toAuthorView( author );
        List<CoAuthorView> coauthors1 = null;
        List<CoAuthorView> list = coauthors;
        if ( list != null ) {
            coauthors1 = new ArrayList<CoAuthorView>( list );
        }

        AuthorCoAuthorBooksView authorCoAuthorBooksView = new AuthorCoAuthorBooksView( author1, coauthors1 );

        return authorCoAuthorBooksView;
    }
}
