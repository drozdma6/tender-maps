package cz.cvut.fit.bap.parser.integrated;

import cz.cvut.fit.bap.parser.business.*;
import cz.cvut.fit.bap.parser.controller.ProcurementController;
import cz.cvut.fit.bap.parser.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


@SpringBootTest(properties = {"core.pool.size=5"})
public class ProcurementSaveIntTest{
    @Autowired
    private ProcurementController procurementController;

    @Autowired
    private ProcurementService procurementService;

    @Autowired
    private ContractingAuthorityService contractingAuthorityService;

    @Autowired
    private ContactPersonService contactPersonService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OfferService offerService;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void procurementSaveIntegrationTest() {
        String systemNumber = "N006/23/V00006411";
        Assertions.assertFalse(procurementService.existsBySystemNumber(systemNumber));
        procurementController.save(systemNumber);
        Assertions.assertTrue(procurementService.existsBySystemNumber(systemNumber));

        Assertions.assertTrue(companyService.readByName("SEDASPORT, s.r.o.").isPresent());
        Assertions.assertTrue(companyService.readByName("KINOEXPORT s.r.o.").isPresent());
        Assertions.assertTrue(offerService.readById(1L).isPresent());
        Assertions.assertTrue(offerService.readById(2L).isPresent());

        Company supplier = companyService.readByName("KINOEXPORT s.r.o.").get();
        Company participant = companyService.readByName("SEDASPORT, s.r.o.").get();
        Offer firstOffer = offerService.readById(1L).get();
        Offer secondOffer = offerService.readById(2L).get();

        // workaround for hibernate lazy initialization inside @Transactional
        // order of generated offer ids might differ due to parallel scrapping
        if (firstOffer.getRejectedDueTooLow()) { // is SEDASPORT's offer
            participant.setOffers(Set.of(firstOffer));
            supplier.setOffers(Set.of(secondOffer));
        } else {
            supplier.setOffers(Set.of(firstOffer));
            participant.setOffers(Set.of(secondOffer));
        }

        verifyProcurement(systemNumber);
        verifyContractingAuthority();
        verifyContactPerson();
        verifySupplier(supplier);
        verifyParticipant(participant);
        verifyParticipantOffers(participant.getOffers());
        verifySupplierOffers(supplier.getOffers());
    }

    private void verifyParticipantOffers(Set<Offer> offers) {
        Assertions.assertEquals(1, offers.size());
        Offer offer = offers.iterator().next();
        Assertions.assertEquals(false, offer.getAssociationOfSuppliers());
        Assertions.assertEquals(true, offer.getRejectedDueTooLow());
        Assertions.assertEquals(false, offer.getWithdrawn());
        Assertions.assertNull(offer.getPrice());
        Assertions.assertNull(offer.getPriceVAT());
    }

    private void verifySupplierOffers(Set<Offer> offers) {
        Assertions.assertEquals(1, offers.size());
        Offer offer = offers.iterator().next();
        Assertions.assertEquals(false, offer.getAssociationOfSuppliers());
        Assertions.assertEquals(false, offer.getRejectedDueTooLow());
        Assertions.assertEquals(false, offer.getWithdrawn());
        Assertions.assertNull(offer.getPrice());
        Assertions.assertNull(offer.getPriceVAT());
    }

    private void verifyProcurement(String systemNumber) {
        Assertions.assertTrue(procurementService.existsBySystemNumber(systemNumber));
        Assertions.assertTrue(procurementService.readById(1L).isPresent());
        Procurement procurement = procurementService.readById(1L).get();

        Assertions.assertEquals("Otevřené řízení", procurement.getTypeOfProcedure());
        Assertions.assertEquals("Dodávka sedadel do hlavního sálu Městského divadla Zlín, příspěvková organizace včetně výměny podlahové krytiny", procurement.getName());
        Assertions.assertEquals("N006/23/V00006411", procurement.getSystemNumber());
        Assertions.assertEquals("Above-limit public contract", procurement.getPublicContractRegime());
        Assertions.assertEquals(LocalDate.of(2023, 4, 11), procurement.getBidsSubmissionDeadline());
        Assertions.assertEquals(LocalDate.of(2023, 3, 13), procurement.getDateOfPublication());
        Assertions.assertEquals(LocalDate.of(2023, 5, 30), procurement.getDateOfContractClose());
        Assertions.assertEquals(false, procurement.getAssociationOfSuppliers());
        Assertions.assertEquals(new BigDecimal("11321593.16"), procurement.getContractPrice());
        Assertions.assertEquals(new BigDecimal("13699127.72"), procurement.getContractPriceVAT());
        Assertions.assertEquals(new BigDecimal("11321593.16"), procurement.getContractPriceWithAmendments());
        Assertions.assertEquals(new BigDecimal("13699127.72"), procurement.getContractPriceWithAmendmentsVAT());
        Assertions.assertEquals("Zlínský kraj", procurement.getPlaceOfPerformance());
        Assertions.assertEquals("39111200-5", procurement.getCodeFromNipezCodeList());
        Assertions.assertEquals("Divadelní sedadla", procurement.getNameFromNipezCodeList());
        Assertions.assertEquals("valerielukasova@zlin.eu", procurement.getContactPerson().getEmail());
        Assertions.assertEquals("KINOEXPORT s.r.o.", procurement.getSupplier().getName());
    }

    private void verifyContractingAuthority() {
        Assertions.assertTrue(contractingAuthorityService.readByName("Městské divadlo Zlín, příspěvková organizace").isPresent());
        ContractingAuthority authority = contractingAuthorityService.readByName("Městské divadlo Zlín, příspěvková organizace").get();

        Assertions.assertEquals("Zlín", authority.getAddress().getCity());
        Assertions.assertEquals("76001", authority.getAddress().getPostalCode());
        Assertions.assertEquals("třída Tomáše Bati", authority.getAddress().getStreet());
        Assertions.assertEquals("CZ", authority.getAddress().getCountryCode());
        Assertions.assertEquals("4091", authority.getAddress().getLandRegistryNumber());
        Assertions.assertNull(authority.getAddress().getLongitude());
        Assertions.assertNull(authority.getAddress().getLatitude());
        Assertions.assertEquals("https://nen.nipez.cz/profil/MDZ", authority.getUrl());
    }

    private void verifyContactPerson() {
        Assertions.assertTrue(contactPersonService.readByEmail("valerielukasova@zlin.eu").isPresent());
        ContactPerson contactPerson = contactPersonService.readByEmail("valerielukasova@zlin.eu").get();

        Assertions.assertEquals("Valerie", contactPerson.getName());
        Assertions.assertEquals("Lukášová", contactPerson.getSurname());
        Assertions.assertEquals("Městské divadlo Zlín, příspěvková organizace", contactPerson.getContractingAuthority().getName());
    }

    private void verifySupplier(Company supplier) {
        Assertions.assertEquals("Korytná", supplier.getAddress().getCity());
        Assertions.assertEquals("68752", supplier.getAddress().getPostalCode());
        Assertions.assertEquals("382", supplier.getAddress().getLandRegistryNumber());
        Assertions.assertNull(supplier.getAddress().getCountryCode());
        Assertions.assertNull(supplier.getAddress().getStreet());
        Assertions.assertNull(supplier.getAddress().getBuildingNumber());
        Assertions.assertNull(supplier.getAddress().getLongitude());
        Assertions.assertNull(supplier.getAddress().getLatitude());
        Assertions.assertEquals("25315943", supplier.getOrganisationId());
        Assertions.assertEquals("CZ25315943", supplier.getVATIdNumber());
    }

    private void verifyParticipant(Company company) {
        final double epsilon = 0.0001;
        Assertions.assertEquals("SEDASPORT, s.r.o.", company.getName());
        Assertions.assertEquals("Myjava", company.getAddress().getCity());
        Assertions.assertEquals("14", company.getAddress().getBuildingNumber());
        Assertions.assertEquals("907 01", company.getAddress().getPostalCode());
        Assertions.assertEquals("Staromyjavská", company.getAddress().getStreet());
        Assertions.assertEquals("SK", company.getAddress().getCountryCode());
        Assertions.assertEquals(17.5627872, company.getAddress().getLongitude(), epsilon);
        Assertions.assertEquals(48.7680207, company.getAddress().getLatitude(), epsilon);
        Assertions.assertNull(company.getOrganisationId());
        Assertions.assertNull(company.getVATIdNumber());
    }
}
