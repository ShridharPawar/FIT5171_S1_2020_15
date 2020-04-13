package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.sound.midi.Instrument;
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
        musician = new Musician("Guitar");
    }

    @Test
    @DisplayName("Musician name cannot be null")
    public void musicianNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musician.setName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Musician name cannot be empty or blank")
    public void musicianNameCannotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> musician.setName(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mike", "Chester"})
    @DisplayName("Musician name should contain atleast 2 parts separated by a space.")
    public void musicianNameShouldHaveTwoParts(String arg) {
        assertThrows(IllegalArgumentException.class, () -> musician.setName(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mike Shinoda", "Chester Bennington"})
    @DisplayName("A valid musician name.")
    public void validMusicianName(String arg) {
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
    @ValueSource(strings = {"https://"})
    @DisplayName("URL should atleast contains 'https://'")
    public void urlCannotBeEmptyOrBlank(String arg) throws MalformedURLException {
        java.net.URL url = new java.net.URL(arg);
        assertThrows(IllegalArgumentException.class,()->musician.setMusicianUrl(url));
    }

    @Test
    @DisplayName("Musician albums cannot be null.")
    public void musicianAlbumsCannotBeNull(){

        assertThrows(NullPointerException.class,()->musician.setAlbums(null));
    }

    @Test
    @DisplayName("Valid instruments.")
    public void validMusicianAlbums(){
        Set<Album> musicianAlbums = new HashSet<>();
        musician.setAlbums(musicianAlbums);
        assertEquals(musician.getAlbums(),musicianAlbums);
    }
}
