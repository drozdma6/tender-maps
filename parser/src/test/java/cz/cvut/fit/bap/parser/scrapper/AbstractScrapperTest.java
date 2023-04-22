package cz.cvut.fit.bap.parser.scrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
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
}