package cz.cvut.fit.bap.parser.controller.scrapper;


import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.dto.ContractDto;
import cz.cvut.fit.bap.parser.controller.dto.OfferDto;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


class ProcurementResultScrapperTest {
    private final HtmlFileCreator htmlFileCreator = new HtmlFileCreator();

    @Test
    void getParticipants() throws IOException {
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult.html"));
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);

        OfferDto offer1 = new OfferDto(new BigDecimal("266400.00"),
                "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846394",
                "Nej.cz s.r.o.", Currency.CZK);
        OfferDto offer2 = new OfferDto(new BigDecimal("378048.00"),
                "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846393",
                "O2 Czech Republic a.s.", Currency.CZK);
        List<OfferDto> expectedOffers = Arrays.asList(offer1, offer2);
        List<OfferDto> actualOffers = procurementResultScrapper.getParticipants();

        Assertions.assertEquals(2, actualOffers.size());
        for (int i = 0; i < expectedOffers.size(); i++) {
            Assertions.assertEquals(expectedOffers.get(i), actualOffers.get(i));
        }
    }

    @Test
    void getSuppliers() throws IOException {
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek";
        Document document = Jsoup.parse(
                htmlFileCreator.ensureCreatedHtmlFile(url, "ProcurementResult.html"));
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);

        ContractDto expectedContract = new ContractDto(new BigDecimal("266400.00"),
                "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846408",
                "Nej.cz s.r.o.", Currency.CZK, LocalDate.of(2022, 5, 30));

        List<ContractDto> contractDtos = procurementResultScrapper.getSuppliers();

        Assertions.assertEquals(1, contractDtos.size());
        Assertions.assertEquals(expectedContract, contractDtos.get(0));
    }

    @Test
    void participantPriceNull() {
        String html = """
                <div class="gov-content-block" title="List of Participants"><h2>List of Participants</h2>
                    <table class="gov-table gov-table--tablet-block gov-sortable-table">
                        <tr class="gov-table__row">
                            <td class="gov-table__cell gov-table__cell--second" title="Nej.cz s.r.o." style="width:100%"
                                data-title="Official name">Nej.cz s.r.o.
                            </td>
                            <td class="gov-table__cell" title="Praha" style="width:100%" data-title="Municipality">Praha</td>
                            <td class="gov-table__cell" title="xxxxxx" style="width:100%"
                                data-title="Bid price excl. VAT">xxxxxx
                            </td>
                            <td class="gov-table__cell gov-table__cell--last" title="CZK" style="width:100%"
                                data-title="Currency">CZK
                            </td>
                            <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                                style="display:none;visibility:hidden"><a class="gov-link gov-link--has-arrow"
                                                                          aria-label="Show detail Nej.cz s.r.o."
                                                                          href="/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846394"><span
                                    class="gov-table__row-button-text">Detail</span></a></td>
                        </tr>
                    </table>
                </div>
                """;
        Document document = Jsoup.parse(html);
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);
        OfferDto expectedOfferDto = new OfferDto(null,
                "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846394",
                "Nej.cz s.r.o.", Currency.CZK);
        List<OfferDto> actualOfferDtos = procurementResultScrapper.getParticipants();
        Assertions.assertEquals(1, actualOfferDtos.size());
        Assertions.assertEquals(expectedOfferDto, actualOfferDtos.get(0));
    }

    @Test
    void testMultipleSuppliers() {
        String html = """
                <div class="gov-content-block" title="Supplier with Whom the Contract Has Been Entered into">
                            <table class="gov-table gov-table--tablet-block gov-sortable-table">
                                <tbody class="gov-table__body">
                                <tr class="gov-table__row">
                                    <td class="gov-table__cell" data-title="Closing date of the contract" title="26/05/2023"
                                        style="width: 130px;">26. 05. 2023
                                    </td>
                                    <td class="gov-table__cell" data-title="Official name" title="supplier1" style="width: 100%;">
                                        supplier1
                                    </td>
                                    <td class="gov-table__cell" data-title="Contractual price excl. VAT" title="1,00"
                                        style="width: 100%;">1,00
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--last" data-title="Currency" title="CZK"
                                        style="width: 100%;">CZK
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                                        style="display: none; visibility: hidden;"><a class="gov-link gov-link--has-arrow"
                                                                                      aria-label="Show detail Netfox s.r.o."
                                                                                      href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261"><span
                                            class="gov-table__row-button-text">Detail</span></a></td>
                                </tr>
                                        <tr class="gov-table__row">
                                    <td class="gov-table__cell" data-title="Closing date of the contract" title="26/05/2023"
                                        style="width: 130px;">23. 05. 2020
                                    </td>
                                    <td class="gov-table__cell" data-title="Official name" title="supplier2" style="width: 100%;">
                                        supplier2
                                    </td>
                                    <td class="gov-table__cell" data-title="Contractual price excl. VAT" title="1,00"
                                        style="width: 100%;">1,00
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--last" data-title="Currency" title="CZK"
                                        style="width: 100%;">EUR
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                                        style="display: none; visibility: hidden;"><a class="gov-link gov-link--has-arrow"
                                                                                      aria-label="Show detail Netfox s.r.o."
                                                                                      href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539262"><span
                                            class="gov-table__row-button-text">Detail</span></a></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    """;
        Document document = Jsoup.parse(html);
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);
        ContractDto expectedOfferDto1 = new ContractDto(new BigDecimal("1.00"),
                "/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261",
                "supplier1", Currency.CZK, LocalDate.of(2023, 5, 26));
        ContractDto expectedOfferDto2 = new ContractDto(new BigDecimal("1.00"),
                "/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539262",
                "supplier2", Currency.EUR, LocalDate.of(2020, 5, 23));

        List<ContractDto> actualOffers = procurementResultScrapper.getSuppliers();
        Assertions.assertEquals(2, actualOffers.size());
        Assertions.assertEquals(expectedOfferDto1, actualOffers.get(0));
        Assertions.assertEquals(expectedOfferDto2, actualOffers.get(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    <td class="gov-table__cell" data-title="Official name" title="" style="width: 100%;">
                    </td>
                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                        style="display: none; visibility: hidden;">
                        <a class="gov-link gov-link--has-arrow" aria-label="Show detail Netfox s.r.o."
                            href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261"><span
                                class="gov-table__row-button-text">Detail</span></a>
                    </td>
                    """,
            """
                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                        style="display: none; visibility: hidden;">
                        <a class="gov-link gov-link--has-arrow" aria-label="Show detail Netfox s.r.o."
                            href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261"><span
                                class="gov-table__row-button-text">Detail</span></a>
                    </td>
                    """,
            """
                    <td class="gov-table__cell" data-title="Official name" title="Netfox s.r.o." style="width: 100%;">
                        Netfox s.r.o.
                    </td>
                    """
    })
    void testInvalidSupplier(String arg) {

        String html = """
                < div class= "gov-content-block"title= "Supplier with Whom the Contract Has Been Entered into" >
                <table class= "gov-table gov-table--tablet-block gov-sortable-table" >
                <tbody class= "gov-table__body" >""" + arg + "</tbody>\n </table>\n </div>";

        Document document = Jsoup.parse(html);
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);
        Assertions.assertThrows(MissingHtmlElementException.class,
                procurementResultScrapper::getSuppliers);
    }

    @Test
    void getBigDecimalFromStringWrongFormat() {
        String html = """
                        <div class="gov-content-block"title= "Supplier with Whom the Contract Has Been Entered into" >
                <table class= "gov-table gov-table--tablet-block gov-sortable-table" >
                <tbody class= "gov-table__body" >
                <tr class= "gov-table__row" >
                <td class= "gov-table__cell"data-title="Closing date of the contract"title= "26/05/2023"
                style= "width: 130px;" > 26. 05. 2023
                </td>
                <td class= "gov-table__cell"data-title="Official name"title= "Netfox s.r.o."style= "width: 100%;" >
                Netfox s.r.o.
                </td>
                <td class= "gov-table__cell"data-title="Contractual price excl. VAT"title= ""
                style= "width: 100%;" >
                </td>
                <td class= "gov-table__cell gov-table__cell--last"data-title="Currency"title= "CZK"
                style= "width: 100%;" > CZK
                </td>
                <td class= "gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                style= "display: none; visibility: hidden;" ><a class= "gov-link gov-link--has-arrow"
                aria-label="Show detail Netfox s.r.o."
                href= "/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261" ><span
                class= "gov-table__row-button-text" > Detail </span></a></td>
                </tr>
                </tbody>
                </table>
                </div>
                """;
        Document document = Jsoup.parse(html);
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);
        ContractDto expectedOfferDto = new ContractDto(null,
                "/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261",
                "Netfox s.r.o.", Currency.CZK, LocalDate.of(2023, 5, 26));

        List<ContractDto> actualOffers = procurementResultScrapper.getSuppliers();
        Assertions.assertEquals(1, actualOffers.size());
        Assertions.assertEquals(expectedOfferDto, actualOffers.get(0));
    }
}