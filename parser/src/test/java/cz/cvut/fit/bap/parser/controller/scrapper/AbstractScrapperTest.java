package cz.cvut.fit.bap.parser.controller.scrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}