package pt.psoft.g1.psoftg1.lendingmanagement.services;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-08T22:19:33+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Microsoft)"
)
@Component
public class LendingMapperImpl extends LendingMapper {

    @Override
    public void update(SetLendingReturnedRequest request, Lending lending) {
        if ( request == null ) {
            return;
        }
    }
}
