package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MusicianUnitTest {
    private Musician musician;

    @BeforeEach
    public void setUp() {
        musician = new Musician("Chester Bennington");
    }

    @Test
    @DisplayName("Should construct Musician object.")
    public void shouldConstructMusicianObject()
    {
        assertNotNull(musician,"Musician object should not be null.");
    }

    @Test
    @DisplayName("Negative test for the constructor.")
    public void testConstructorForMusician()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Musician("Mike7"));
        assertEquals(exception.getMessage(),"Please input an appropriate name.");
    }

    @Test
    @DisplayName("Musician name cannot be null.")
    public void musicianNameCannotBeNull() {
       NullPointerException exception = assertThrows(NullPointerException.class, () -> musician.setName(null));
       assertEquals(exception.getMessage(),"Object is null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Musician name cannot be empty or blank.")
    public void musicianNameCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> musician.setName(arg));
        assertEquals(exception.getMessage(),"Name cannot be blank.");
    }

    @Test
    public void sameNameMeansSameMusician() {
        Musician musician1 = new Musician("Chester Bennington");
        assertEquals(musician, musician1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mike5 Shinoda","Chester*+ Bennington"})
    @DisplayName("Musician name should not have certain special characters or numbers.")
    public void musicianNameShouldNotHaveSpecialCharacters(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> musician.setName(arg));
        assertEquals(exception.getMessage(),"Please input an appropriate name.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mi","fngjfgjfgjfngjkfngkfngkfngkfjghfgjnfkvnfjgnfjgfjkgfkgjfngkfngjfngkfngfjksdcdvffgfgfgfgfg"})
    @DisplayName("Musician name length should be between 3 and 40 characters.")
    public void musicianNameLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> musician.setName(arg));
        assertEquals(exception.getMessage(),"Please input an appropriate name.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Dénes Várjon","Mike"})
    @DisplayName("A positive test case for musician name, checking the french letters as well.")
    public void positiveMusicianName(String arg) {
        musician.setName(arg);
        assertEquals(musician.getName(),arg);
    }

    @Test
    @DisplayName("URL cannot be null")
    public void urlCannotBeNull()
    {
      assertThrows(NullPointerException.class,()->musician.setMusicianUrl(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://www.testfakewebsiteecm.com"})
    @DisplayName("Should be a valid url.")
    public void invalidURL(String arg) throws MalformedURLException {
        if(!arg.contains("https://")){arg="https://google.com";}
        java.net.URL url = new java.net.URL(arg);
        assertThrows(UnknownHostException.class,()->musician.setMusicianUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Positive test case for URL.")
    @ValueSource(strings = {"https://www.ecmrecords.com/artists/1435045745"})
    public void positiveURL(String arg) throws IOException {
        java.net.URL url = new java.net.URL(arg);
        musician.setMusicianUrl(url);
        assertEquals(musician.getMusicianUrl(),url);
    }

    @Test
    @DisplayName("Musician albums cannot be null.")
    public void musicianAlbumsCannotBeNull(){
        assertThrows(NullPointerException.class,()->musician.setAlbums(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInMusicianAlbums(){
        Set<Album> musicianAlbums = new HashSet<>();
        musicianAlbums.add(null);
        assertThrows(NullPointerException.class,()->musician.setAlbums(musicianAlbums));
    }

    @Test
    @DisplayName("Positive test for musician albums.")
    public void positiveMusicianAlbums(){
        Set<Album> musAlbum = new HashSet<>();
        musAlbum.add(new Album(2019,"ECM 339","Meteora"));
        musician.setAlbums(musAlbum);
        assertEquals(musician.getAlbums(),musAlbum);
    }

    @Test
    @DisplayName("Biography cannot be null.")
    public void biographyCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> musician.setBiography(null));
        assertEquals(exception.getMessage(),"Biography object cannot be null.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for biography.")
    @ValueSource(strings = {"Hi, I am Chester, I am from California and I like playing my guitar."})
    public void positiveBiography(String arg) throws IOException {
        musician.setBiography(arg);
        assertEquals(musician.getBiography(),arg);
    }

    @Test
    @DisplayName("Musician web pages cannot be null.")
    public void musicianWebpagesCannotBeNull(){
        assertThrows(NullPointerException.class,()->musician.setWebpages(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInWebPages(){
        Set<Webpage> webPages = new HashSet<>();
        webPages.add(null);
        assertThrows(NullPointerException.class,()->musician.setWebpages(webPages));
    }

    @Test
    @DisplayName("Positive test for web pages.")
    public void positiveWebPages() throws IOException {
        Set<Webpage> webPage = new HashSet<>();
        webPage.add(new Webpage("Chester Bennington's Website",new URL("https://en.wikipedia.org/wiki/Chester_Bennington")));
        musician.setWebpages(webPage);
        assertEquals(musician.getWebpages(),webPage);
    }

}
