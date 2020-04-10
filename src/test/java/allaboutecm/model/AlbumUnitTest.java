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

class AlbumUnitTest {
    private Album album;

    @BeforeEach
    public void setUp() {
        album = new Album(1975, "ECM 1064/65", "The Köln Concert");
    }

    @Test
    @DisplayName("Album name cannot be null")
    public void albumNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> album.setAlbumName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Album name cannot be empty or blank")
    public void albumNameCannotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Meteora", "Minutes to Midnight"})
    @DisplayName("Check if it is setting the valid album name.")
    public void validAlbumName(String arg) {
        album.setAlbumName(arg);
        assertEquals(album.getAlbumName(),arg);
    }

    @Test
    public void sameNameAndNumberMeansSameAlbum() {
        Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        assertEquals(album, album1);
    }

    @ParameterizedTest
    @ValueSource(ints = {20212,2021,-2020})
    @DisplayName("Release year should be between 1500 and 2020.")
    public void releaseYearShouldBeValid(int arg){
       assertThrows(IllegalArgumentException.class,()->album.setReleaseYear(arg));
    }

    @ParameterizedTest
    @ValueSource(ints = {2020,1994})
    @DisplayName("Check if it is setting the valid year input.")
    public void validReleaseYear(int arg){
        album.setReleaseYear(arg);
        assertEquals(album.getReleaseYear(),arg);
    }

    @Test
    @DisplayName("Record number cannot be null")
    public void recordNumberCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setRecordNumber(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Record number cannot be empty or blank.")
    public void recordNumberCannotBeEmptyOrBlank(String arg){
        assertThrows(IllegalArgumentException.class,()->album.setRecordNumber(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"EC23", "909", "23ECM"})
    @DisplayName("Record number should start with ECM.")
    public void recordNumberShouldStartWithECM(String arg){
        assertThrows(IllegalArgumentException.class,()->album.setRecordNumber(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ECM 2021", "ECM 435"})
    @DisplayName("Check if it is setting the valid record number.")
    public void validRecordNumber(String arg){
        album.setRecordNumber(arg);
        assertEquals(album.getRecordNumber(),arg);
    }

    @Test
    @DisplayName("Musicians of an album cannot be null.")
    public void featuredMusiciansCannotBeNull(){
         assertThrows(NullPointerException.class,()->album.setFeaturedMusicians(null));
    }

    @Test
    @DisplayName("Valid musicians.")
    public void validFeaturedMusicians(){
        Set<Musician> musicians = new HashSet<>();
        album.setFeaturedMusicians(musicians);
        assertEquals(album.getFeaturedMusicians(),musicians);
    }

    @Test
    @DisplayName("Musician instruments cannot be null.")
    public void musicianInstrumentsCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setInstruments(null));
    }

    @Test
    @DisplayName("Valid instruments.")
    public void validMusicianInstruments(){
        Set<MusicianInstrument> instruments = new HashSet<>();
        album.setInstruments(instruments);
        assertEquals(album.getInstruments(),instruments);
    }

    @Test
    @DisplayName("URL cannot be null")
    public void urlCannotBeNull()
    {
        assertThrows(NullPointerException.class,()->album.setAlbumURL(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://"})
    @DisplayName("URL should atleast contains 'https://','ecm'")
    public void urlCannotBeEmptyOrBlank(String arg) throws MalformedURLException {
        java.net.URL url = new java.net.URL(arg);
        assertThrows(IllegalArgumentException.class,()->album.setAlbumURL(url));
    }

    @Test
    @DisplayName("Albums song track list cannot be null.")
    public void albumTracksCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setTracks(null));
    }

    @Test
    @DisplayName("Valid album tracks.")
    public void validTracks(){
        List<String> tracks  = new ArrayList<String>();
        album.setTracks(tracks);
        assertEquals(album.getTracks(),tracks);
    }



}