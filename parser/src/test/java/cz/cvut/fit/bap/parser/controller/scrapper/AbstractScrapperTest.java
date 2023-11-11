package cz.cvut.fit.bap.parser.controller.scrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AbstractScrapperTest{
    private AbstractScrapper abstractScrapper;

    @BeforeEach
    public void setUp(){
        abstractScrapper = Mockito.mock(AbstractScrapper.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testGetNullIfEmpty_NullInput(){
        String input = null;
        String result = abstractScrapper.getNullIfEmpty(input);
        assertNull(result);
    }

    @Test
    public void testGetNullIfEmpty_EmptyInput(){
        String input = "";
        String result = abstractScrapper.getNullIfEmpty(input);
        assertNull(result);
    }

    @Test
    public void testGetNullIfEmpty_NonEmptyInput(){
        String input = "sample text";
        String result = abstractScrapper.getNullIfEmpty(input);
        assertEquals(input, result);
    }

    @Test
    void testRemoveUrlParameters(){
        String url = "/p:abc/def/?param=value";
        String expectedModifiedUrl = "/def/?param=value";

        String modifiedUrl = abstractScrapper.removeUrlParameters(url);

        assertEquals(expectedModifiedUrl, modifiedUrl);
    }

    @Test
    void testConvertYesNoToBoolean() {
        String yes = "yes";
        String no = "no";
        String capitalYes = "YES";
        String randomCapitalNo = "nO";
        String random = "fasfa";
        assertTrue(abstractScrapper.convertYesNoToBoolean(yes));
        assertTrue(abstractScrapper.convertYesNoToBoolean(capitalYes));
        assertFalse(abstractScrapper.convertYesNoToBoolean(no));
        assertFalse(abstractScrapper.convertYesNoToBoolean(randomCapitalNo));
        assertNull(abstractScrapper.convertYesNoToBoolean(random));
        assertNull(abstractScrapper.convertYesNoToBoolean(null));
    }

    @Test
    void testGetBigDecimalFromString() {
        assertEquals(new BigDecimal(1000), abstractScrapper.getBigDecimalFromString("1000"));
        assertEquals(new BigDecimal(1000000), abstractScrapper.getBigDecimalFromString("1 000 000"));
        assertEquals(new BigDecimal("1000.156"), abstractScrapper.getBigDecimalFromString("1 000,156"));
        assertNull(abstractScrapper.getBigDecimalFromString("test"));
    }
}