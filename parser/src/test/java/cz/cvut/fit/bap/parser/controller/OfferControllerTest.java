package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.OfferService;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.Offer;
import cz.cvut.fit.bap.parser.domain.Procurement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest{
    @InjectMocks
    private OfferController offerController;

    @Mock
    private OfferService offerService;

    @Test
    void saveOffer(){
        BigDecimal price = new BigDecimal("1000");
        Procurement procurement = mock(Procurement.class);
        Company company = mock(Company.class);

        offerController.saveOffer(price, procurement, company);

        Offer expectedOffer = new Offer(price, procurement, company);
        verify(offerService).create(expectedOffer);
    }
}