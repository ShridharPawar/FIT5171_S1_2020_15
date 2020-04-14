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
    @ValueSource(strings = {"Minutes to Midnight"})
    @DisplayName("Check if it is setting the valid album name.")
    public void positiveAlbumName(String arg) {
        album.setAlbumName(arg);
        assertEquals(album.getAlbumName(),arg);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Meteoraffffffffffffffffffefdfddffrfrfrfrfdfhdfjdbfhbfhfrfbfrbfrfrfr frn dfnrdbgfhrgfrdjfndfjdf"})
    @DisplayName("Check the length of the album name.")
    public void limitedLengthOfAlbumName(String arg) {
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
    }

    @Test
    public void sameNameAndNumberMeansSameAlbum() {
        Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        assertEquals(album, album1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"23de","98er"})
    @DisplayName("Release year should have just numbers.")
    public void randomStringReleaseYear(String arg){
        assertThrows(NumberFormatException.class,()->album.setReleaseYear(Integer.parseInt(arg)));
        //assertEquals(exception.getMessage(),"Release year should have just numbers.");
    }

    @ParameterizedTest
    @ValueSource(ints = {20212,2021,-2020,1499,2021})
    @DisplayName("Release year should be between 1500 and 2020.")
    public void releaseYearShouldBeValid(int arg){
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->album.setReleaseYear(arg));
       assertEquals(exception.getMessage(),"Not a valid year.");
    }

    @ParameterizedTest
    @ValueSource(ints = {2020,1994,1501,2019,1500})
    @DisplayName("Check if it is setting the valid year input.")
    public void positiveReleaseYear(int arg){
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
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInFeaturedMusicians(){
        Set<Musician> musicians = new HashSet<>();
        musicians.add(null);
        assertThrows(NullPointerException.class,()->album.setFeaturedMusicians(musicians));
    }

    @Test
    @DisplayName("Positive test to check if the musicians list has been set.")
    public void positiveTestFeaturedMusicians(){
        Set<Musician> musicians = new HashSet<>();
        Musician musician = new Musician("Mike Shinoda");
        musicians.add(musician);
        album.setFeaturedMusicians(musicians);
        assertEquals(album.getFeaturedMusicians(),musicians);
    }

    @Test
    @DisplayName("Musician instruments cannot be null.")
    public void musicianInstrumentsCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setInstruments(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInMusicianInstruments(){
        Set<MusicianInstrument> instruments = new HashSet<>();
        instruments.add(null);
        assertThrows(NullPointerException.class,()->album.setInstruments(instruments));
    }

    @Test
    @DisplayName("Positive test to check if the Musicianinstrument is valid.")
    public void positiveMusicianInstruments(){
        Set<MusicianInstrument> instruments = new HashSet<>();
        Musician musician = new Musician("Chester Bennington");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Guitar");
        MusicianInstrument musicianInstrument = new MusicianInstrument(musician,musicalInstrument);
        instruments.add(musicianInstrument);
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
    @ValueSource(strings = {"https://google.com","https://fakeurl.com"})
    @DisplayName("URL should atleast contains 'https://','ecm' and it should not be fake")
    public void invalidURL(String arg) throws MalformedURLException {
        java.net.URL url = new java.net.URL(arg);
        assertThrows(IllegalArgumentException.class,()->album.setAlbumURL(url));
    }

    @ParameterizedTest
    @DisplayName("Positive test case for URL.")
    @ValueSource(strings = {"https://www.ecmrecords.com/catalogue/143038750696/the-koln-concert-keith-jarrett"})
    public void positiveURL(String arg) throws IOException {
        java.net.URL url = new java.net.URL(arg);
        album.setAlbumURL(url);
        assertEquals(album.getAlbumURL(),url);
    }

    @Test
    @DisplayName("Albums song track list cannot be null.")
    public void albumTracksCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setTracks(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInTracks(){
        List<String> tracks  = new ArrayList<String>();
        tracks.add(null);
        assertThrows(NullPointerException.class,()->album.setTracks(tracks));
    }

    @Test
    @DisplayName("Valid album tracks.")
    public void positiveTracks(){
        List<String> tracks  = new ArrayList<String>();
        tracks.add("Shadow of the day.");
        album.setTracks(tracks);
        assertEquals(album.getTracks(),tracks);
    }



}