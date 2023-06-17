package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

class ProcurementListScrapperTest{
    @Test
    void getProcurementDto() throws IOException{
        String fileName = "ProcurementList.html";
        File file = new File("src/test/resources/testFiles/" + fileName);
        if(!file.exists()){
            throw new IOException("Missing test data files");
        }

        Document document = Jsoup.parse(file);
        ProcurementListScrapper procurementListScrapper = new ProcurementListScrapper(document);

        String systemNumber0 = "N006/23/V00013199";
        String systemNumber1 = "N006/22/V00002876";
        String systemNumberLast = "N006/23/V00011191";

        List<String> actualSystemNumbers = procurementListScrapper.getProcurementSystemNumbers();

        Assertions.assertEquals(50, actualSystemNumbers.size());
        Assertions.assertEquals(systemNumber0, actualSystemNumbers.get(0));
        Assertions.assertEquals(systemNumber1, actualSystemNumbers.get(1));
        Assertions.assertEquals(systemNumberLast, actualSystemNumbers.get(49));
    }
}