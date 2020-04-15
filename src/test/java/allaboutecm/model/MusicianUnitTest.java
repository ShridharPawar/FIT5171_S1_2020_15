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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MusicianUnitTest {
    private Musician musician;

    @BeforeEach
    public void setUp() {

        musician = new Musician("Chester Bennington");
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

    /*@ParameterizedTest
    @ValueSource(strings = {"Mike", "Chester"})
    @DisplayName("Musician name should contain atleast 2 parts separated by a space.")
    public void musicianNameShouldHaveTwoParts(String arg) {
        assertThrows(IllegalArgumentException.class, () -> musician.setName(arg));
    }*/

    @ParameterizedTest
    @ValueSource(strings = {"Mike5 Shinoda","Chester*+ Bennington"})
    @DisplayName("Musician name should not have certain special characters or numbers.")
    public void musicianNameShouldNotHaveSpecialCharacters(String arg) {
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
    @ValueSource(strings = {"https://google.com"})
    @DisplayName("URL should atleast contain 'https://' and 'ecm' and should be a valid url")
    public void invalidURL(String arg) throws MalformedURLException {
        java.net.URL url = new java.net.URL(arg);
        assertThrows(IllegalArgumentException.class,()->musician.setMusicianUrl(url));
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
        Set<Album> musicianAlbums = new HashSet<>();
        Album album = new Album(2019,"ECM 339","Meteora");
        musicianAlbums.add(album);
        musician.setAlbums(musicianAlbums);
        assertEquals(musician.getAlbums(),musicianAlbums);
    }
}
