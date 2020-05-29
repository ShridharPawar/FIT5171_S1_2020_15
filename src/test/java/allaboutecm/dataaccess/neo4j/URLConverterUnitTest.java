package allaboutecm.dataaccess.neo4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by muscaestar on 5/8/20
 *
 * @author muscaestar
 */
class URLConverterUnitTest {

    public static final String VALID_URL_STRING = "http://monash.edu";
    URLConverter urlConverter;

    @BeforeEach
    void setUp() {
        urlConverter = new URLConverter();
    }

    @Test
    void toGraphPropertyShouldReturnTheStringValueOfUrl() throws MalformedURLException {
        URL validUrl = new URL(VALID_URL_STRING);
        String result = urlConverter.toGraphProperty(validUrl);
        assertEquals(VALID_URL_STRING, result);

    }

    @Test
    void toGraphPropertyShouldReturnNullWithInputNull() {
        String result = urlConverter.toGraphProperty(null);
        assertNull(result);
    }

    @Test
    void toEntityAttributeShouldReturnNullWithInputNull() {
        URL resultUrl = urlConverter.toEntityAttribute(null);
        assertNull(resultUrl);
    }

    @Test
    void toEntityAttributeShouldReturnUrlWithValidStringValue() throws MalformedURLException {
        URL resultUrl = urlConverter.toEntityAttribute(VALID_URL_STRING);
        assertEquals(new URL(VALID_URL_STRING), resultUrl);
    }

}