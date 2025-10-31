package pt.psoft.g1.psoftg1.lendingmanagement.api;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-31T15:58:57+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class LendingViewMapperImpl extends LendingViewMapper {

    @Override
    public LendingView toLendingView(Lending lending) {
        if ( lending == null ) {
            return null;
        }

        LendingView lendingView = new LendingView();

        lendingView.set_links( lendingToLendingLinksView( lending ) );
        lendingView.setLendingNumber( map( lending.getLendingNumber() ) );
        lendingView.setBookTitle( map( lendingBookTitle( lending ) ) );
        lendingView.setReturnedDate( lending.getReturnedDate() );
        lendingView.setDaysOverdue( mapOpt( lending.getDaysOverdue() ) );
        lendingView.setDaysUntilReturn( mapOpt( lending.getDaysUntilReturn() ) );
        lendingView.setLimitDate( lending.getLimitDate() );
        lendingView.setStartDate( lending.getStartDate() );

        lendingView.setFineValueInCents( lending.getFineValueInCents().orElse(null) );

        return lendingView;
    }

    @Override
    public List<LendingView> toLendingView(List<Lending> lendings) {
        if ( lendings == null ) {
            return null;
        }

        List<LendingView> list = new ArrayList<LendingView>( lendings.size() );
        for ( Lending lending : lendings ) {
            list.add( toLendingView( lending ) );
        }

        return list;
    }

    @Override
    public LendingsAverageDurationView toLendingsAverageDurationView(Double lendingsAverageDuration) {
        if ( lendingsAverageDuration == null ) {
            return null;
        }

        LendingsAverageDurationView lendingsAverageDurationView = new LendingsAverageDurationView();

        lendingsAverageDurationView.setLendingsAverageDuration( lendingsAverageDuration );

        return lendingsAverageDurationView;
    }

    protected LendingLinksView lendingToLendingLinksView(Lending lending) {
        if ( lending == null ) {
            return null;
        }

        LendingLinksView lendingLinksView = new LendingLinksView();

        lendingLinksView.setSelf( mapLendingLink( lending ) );
        lendingLinksView.setBook( mapBookLink( lending.getBook() ) );
        lendingLinksView.setReader( mapReaderLink( lending.getReaderDetails() ) );

        return lendingLinksView;
    }

    private Title lendingBookTitle(Lending lending) {
        if ( lending == null ) {
            return null;
        }
        Book book = lending.getBook();
        if ( book == null ) {
            return null;
        }
        Title title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
