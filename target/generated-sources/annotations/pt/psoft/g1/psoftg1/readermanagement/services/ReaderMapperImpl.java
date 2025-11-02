package pt.psoft.g1.psoftg1.readermanagement.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-02T22:56:23+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ReaderMapperImpl extends ReaderMapper {

    @Override
    public Reader createReader(CreateReaderRequest request) {
        if ( request == null ) {
            return null;
        }

        String username = null;
        String password = null;

        username = request.getUsername();
        password = request.getPassword();

        Reader reader = new Reader( username, password );

        reader.setName( request.getFullName() );

        return reader;
    }

    @Override
    public ReaderDetails createReaderDetails(int readerNumber, Reader reader, CreateReaderRequest request, String photoURI, List<Genre> interestList) {
        if ( reader == null && request == null && photoURI == null && interestList == null ) {
            return null;
        }

        String birthDate = null;
        String phoneNumber = null;
        boolean gdpr = false;
        boolean marketing = false;
        boolean thirdParty = false;
        if ( request != null ) {
            birthDate = request.getBirthDate();
            phoneNumber = request.getPhoneNumber();
            gdpr = request.getGdpr();
            marketing = request.getMarketing();
            thirdParty = request.getThirdParty();
        }
        int readerNumber1 = 0;
        readerNumber1 = readerNumber;
        Reader reader1 = null;
        reader1 = reader;
        List<Genre> interestList1 = null;
        List<Genre> list = interestList;
        if ( list != null ) {
            interestList1 = new ArrayList<Genre>( list );
        }

        String photoURI1 = null;

        ReaderDetails readerDetails = new ReaderDetails( readerNumber1, reader1, birthDate, phoneNumber, gdpr, marketing, thirdParty, photoURI1, interestList1 );

        readerDetails.setPhoto( photoURI );

        return readerDetails;
    }
}
