package cz.cvut.fit.bap.parser.integrated;

import cz.cvut.fit.bap.parser.controller.OfferController;
import cz.cvut.fit.bap.parser.controller.builder.OfferBuilder;
import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.OfferData;
import cz.cvut.fit.bap.parser.controller.data.OfferDetailPageData;
import cz.cvut.fit.bap.parser.domain.Address;
import cz.cvut.fit.bap.parser.domain.Company;
import cz.cvut.fit.bap.parser.domain.Offer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(properties = {"core.pool.size=2"})
public class OfferControllerIntTest {
    @Autowired
    private OfferController offerController;

    @Test
    void getOffers() {
        List<OfferData> offerRows = getOfferRows();
        LocalDate contractCloseDate = LocalDate.of(2023, 2, 11);
        Address address1 = new Address("CZ", "Žďár nad Sázavou", "59101", "Jamská", "65", "2488", 49.55192, 15.95722);
        Address address2 = new Address(null, "Jindřichův Hradec", "37701", "Jiráskovo předměstí", null, "935", null, null);

        List<OfferDetailPageData> offerDetailPageDataList = expectedOfferDetailPageData();
        Company company1 = new Company(offerRows.get(0).companyName(), address1, offerDetailPageDataList.get(0).organisationId(), offerDetailPageDataList.get(0).VATIdNumber());
        Company company2 = new Company(offerRows.get(1).companyName(), address2, offerDetailPageDataList.get(1).organisationId(), offerDetailPageDataList.get(1).VATIdNumber());

        List<OfferBuilder> builders = List.of(
                new OfferBuilder(offerRows.get(0), offerDetailPageDataList.get(0)).company(company1),
                new OfferBuilder(offerRows.get(1), offerDetailPageDataList.get(1)).company(company2)
        );
        List<Offer> expectedOffers = builders.stream().map(OfferBuilder::build).toList();
        List<OfferBuilder> actualResult = offerController.getOffers(offerRows, contractCloseDate);
        List<Offer> actualOffers = actualResult.stream().map(OfferBuilder::build).toList();
        compareOffers(expectedOffers.get(0), actualOffers.get(0));
        compareOffers(expectedOffers.get(1), actualOffers.get(1));
        double epsilon = 0.0001;
        Assertions.assertEquals(expectedOffers.get(0).getCompany().getAddress().getLatitude(),
                actualOffers.get(0).getCompany().getAddress().getLatitude(), epsilon);
        Assertions.assertEquals(expectedOffers.get(0).getCompany().getAddress().getLongitude(),
                actualOffers.get(0).getCompany().getAddress().getLongitude(), epsilon);

        Assertions.assertEquals(expectedOffers.get(1).getCompany().getAddress().getLatitude(),
                actualOffers.get(1).getCompany().getAddress().getLatitude());
        Assertions.assertEquals(expectedOffers.get(1).getCompany().getAddress().getLongitude(),
                actualOffers.get(1).getCompany().getAddress().getLongitude());
    }

    private void compareOffers(Offer o1, Offer o2) {
        Assertions.assertEquals(o1.getPriceVAT(), o2.getPriceVAT());
        Assertions.assertEquals(o1.getPrice(), o2.getPrice());
        Assertions.assertEquals(o1.getRejectedDueTooLow(), o2.getRejectedDueTooLow());
        Assertions.assertEquals(o1.getWithdrawn(), o2.getWithdrawn());
        Assertions.assertEquals(o1.getAssociationOfSuppliers(), o2.getAssociationOfSuppliers());
        Assertions.assertEquals(o1.getCompany().getOrganisationId(), o2.getCompany().getOrganisationId());
        Assertions.assertEquals(o1.getCompany().getVATIdNumber(), o2.getCompany().getVATIdNumber());
        compareAddress(o1.getCompany().getAddress(), o2.getCompany().getAddress());
    }

    private void compareAddress(Address a1, Address a2) {
        Assertions.assertEquals(a1.getCountryCode(), a2.getCountryCode());
        Assertions.assertEquals(a1.getCity(), a2.getCity());
        Assertions.assertEquals(a1.getStreet(), a2.getStreet());
        Assertions.assertEquals(a1.getPostalCode(), a2.getPostalCode());
        Assertions.assertEquals(a1.getBuildingNumber(), a2.getBuildingNumber());
        Assertions.assertEquals(a1.getLandRegistryNumber(), a2.getLandRegistryNumber());
    }


    private List<OfferData> getOfferRows() {
        OfferData o1 = new OfferData(new BigDecimal("9243500.00"), new BigDecimal("11184635.00"), "/en/verejne-zakazky/detail-zakazky/N006-23-V00025735/vysledek/p:uca:page=2/detail-uverejneni/1883479421", "AQUASYS spol. s r.o.", Currency.CZK);
        OfferData o2 = new OfferData(new BigDecimal("8799964.93"), new BigDecimal("10647957.57"), "/en/verejne-zakazky/detail-zakazky/N006-23-V00025735/vysledek/p:uca:page=2/detail-uverejneni/1883479420", "TB BUILDING s.r.o.", Currency.CZK);
        return List.of(o1, o2);
    }

    private List<OfferDetailPageData> expectedOfferDetailPageData() {
        AddressData addressData1 = new AddressData("Česká republika", "Žďár nad Sázavou", "59101", "Jamská", "65", "2488", null);
        String organisationId1 = "25344447";
        String VATIdNumber1 = "CZ25344447";
        OfferDetailPageData offerDetailPageData1 = new OfferDetailPageData(addressData1, organisationId1, VATIdNumber1, false, false, false);

        AddressData addressData2 = new AddressData("Česká republika", "Jindřichův Hradec", "37701", "Jiráskovo předměstí", null, "935", null);
        String organisationId2 = "28066987";
        String VATIdNumber2 = "CZ28066987";
        OfferDetailPageData offerDetailPageData2 = new OfferDetailPageData(addressData2, organisationId2, VATIdNumber2, false, false, false);
        return List.of(offerDetailPageData1, offerDetailPageData2);
    }
}
