package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.sound.midi.Instrument;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WebpageUnitTest {
    private Webpage webpage;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        URL url = new URL("https://en.wikipedia.org/wiki/Chester_Bennington");
        webpage = new Webpage("Chester Bennington's Website",url);
    }

    @Test
    @DisplayName("Should construct Webpage object.")
    public void shouldConstructWebpageObject()
    {
        assertNotNull(webpage,"Webpage object should not be null.");
    }

    @Test
    @DisplayName("Web page name cannot be null.")
    public void webPageNameCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> webpage.setName(null));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Web page name cannot be empty or blank.")
    public void webPageNameCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> webpage.setName(arg));
        assertEquals(exception.getMessage(),"Name cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hi my name is Mikefnjnjngkrngkngkngkngkntkgnkgntjgntgntgtgtmgtgtg"})
    @DisplayName("Web page name length should not exceed 50 characters.")
    public void webPageNameLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> webpage.setName(arg));
        assertEquals(exception.getMessage(),"Name of the webpage should not exceed 50 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for web page name.")
    @ValueSource(strings = {"My life."})
    public void positiveWebPageName(String arg){
        webpage.setName(arg);
        assertEquals(webpage.getName(),arg);
    }

    @Test
    @DisplayName("Same url means same web page.")
    public void sameUrlMeansSameWebPage() throws MalformedURLException {
        URL url = new URL("https://en.wikipedia.org/wiki/Chester_Bennington");
        Webpage webpage1 = new Webpage("Chester Bennington's Website",url);
        assertEquals(webpage1, webpage);
    }

    @Test
    @DisplayName("URL cannot be null")
    public void urlCannotBeNull()
    {
        assertThrows(NullPointerException.class,()->webpage.setUrl(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://www.testfakewebsite.com"})
    @DisplayName("URL should be a valid url.")
    public void invalidURL(String arg) throws MalformedURLException {
        if(!arg.contains("https://")){arg="https://google.com";}
        java.net.URL url = new java.net.URL(arg);
        assertThrows(UnknownHostException.class,()->webpage.setUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Positive test case for URL.")
    @ValueSource(strings = {"https://en.wikipedia.org/wiki/Mike_Shinoda"})
    public void positiveURL(String arg) throws IOException {
        java.net.URL url = new java.net.URL(arg);
        webpage.setUrl(url);
        assertEquals(webpage.getUrl(),url);
    }

    @Test
    @DisplayName("Biography cannot be null.")
    public void biographyCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> webpage.setBiography(null));
        assertEquals(exception.getMessage(),"Biography object cannot be null.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for biography.")
    @ValueSource(strings = {"Hi, I am Chester, I am from California and I like playing my guitar."})
    public void positiveBiography(String arg) throws IOException {
        webpage.setBiography(arg);
        assertEquals(webpage.getBiography(),arg);
    }



}
