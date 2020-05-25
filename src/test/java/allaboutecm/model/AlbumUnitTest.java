package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AlbumUnitTest {
    private Album album;
    private Set<Group> groups;

    @BeforeEach
    public void setUp() {

        album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        groups = new HashSet<>();
    }

    @Test
    @DisplayName("Negative test for the constructor.")
    public void testConstructorForAlbum()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Album(1448,"ECM 123","Meteora"));
        assertEquals(exception.getMessage(),"Not a valid year.");
    }

    @Test
    @DisplayName("Should construct Album object.")
    public void shouldConstructAlbumObject()
    {
        assertNotNull(album,"Album object should not be null.");
    }

    @Test
    @DisplayName("Album name cannot be null.")
    public void albumNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> album.setAlbumName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Album name cannot be empty or blank.")
    public void albumNameCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
        assertEquals(exception.getMessage(),"Album name cannot be empty.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Minutes to Midnight."})
    @DisplayName("Check if it is setting the valid album name.")
    public void positiveAlbumName(String arg) {
        album.setAlbumName(arg);
        assertEquals(album.getAlbumName(),arg);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Meteoraffffffffffffffffffefdfddffrfrfrfrfdfhdfjdbfhbfhfrfbfrbfrfrfr frn dfnrdbgfhrgfrdjfndfjdf"})
    @DisplayName("Check the length of the album name.")
    public void limitedLengthOfAlbumName(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
        assertEquals(exception.getMessage(),"Album's length is too big.");
    }

    @Test
    @DisplayName("Objects are same if the release year, recordnumber and albumname are same.")
    public void sameYearAndNameAndNumberMeansSameAlbum() {
        Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        assertEquals(album, album1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"23de","98er"})
    @DisplayName("Release year should have just numbers.")
    public void randomStringReleaseYear(String arg){
        assertThrows(NumberFormatException.class,()->album.setReleaseYear(Integer.parseInt(arg)));
    }

    @ParameterizedTest
    @ValueSource(ints = {2021232787,2021,-2020,1499})
    @DisplayName("Release year should be between 1500 and current year.")
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
    @ValueSource(strings = {"909", "ECM23"})
    @DisplayName("Record number should start with ECM and should have atleast 2 parts separated by whitespace.")
    public void recordNumberShouldStartWithECM(String arg){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->album.setRecordNumber(arg));
        assertEquals(exception.getMessage(),"Record number should start with ECM.");
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
        List<Musician> musicians = new ArrayList<>();
        musicians.add(null);
        assertThrows(NullPointerException.class,()->album.setFeaturedMusicians(musicians));
    }

    @Test
    @DisplayName("Positive test to check if the musicians list has been set.")
    public void duplicateFeaturedMusicians(){
        List<Musician> musicians = new ArrayList<>();
        musicians.add(new Musician("Mike Shinoda"));
        musicians.add(new Musician("Mike Shinoda"));
        assertThrows(IllegalArgumentException.class,()->album.setFeaturedMusicians(musicians));
    }

    @Test
    @DisplayName("Positive test to check if the musicians list has been set.")
    public void positiveTestFeaturedMusicians(){
        List<Musician> musicians = new ArrayList<>();
        musicians.add(new Musician("Mike Shinoda"));
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
        Set<MusicalInstrument> musInstruments = new HashSet<>();
        musInstruments.add(new MusicalInstrument("Guitar"));
        instruments.add(new MusicianInstrument(new Musician("Chester Bennington"),musInstruments));
        album.setInstruments(instruments);
        assertEquals(album.getInstruments(),instruments);
    }

    @Test
    @DisplayName("URL cannot be null.")
    public void urlCannotBeNull()
    {
        assertThrows(NullPointerException.class,()->album.setAlbumURL(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://www.testfakewebsiteecm.com"})
    @DisplayName("URL should atleast contain 'ecm' and it should not be fake.")
    public void invalidURL(String arg) throws MalformedURLException {
        if(!arg.contains("https://")){arg="https://google.com";}
        java.net.URL url = new java.net.URL(arg);
        assertThrows(UnknownHostException.class,()->album.setAlbumURL(url));
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
    @DisplayName("Track set cannot be null.")
    public void albumTracksCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setTracks(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInTracks(){
        Set<Track> tracks = Sets.newHashSet();
        tracks.add(null);
        assertThrows(NullPointerException.class,()->album.setTracks(tracks));
    }

    @Test
    @DisplayName("Valid album tracks.")
    public void positiveTracks(){
        Set<Track> tracks = Sets.newHashSet();
        tracks.add(new Track("Numb",6));
        album.setTracks(tracks);
        assertEquals(album.getTracks(),tracks);
    }

    @Test
    @DisplayName("Genre cannot be null.")
    public void genreCannotBeNull() {
        assertThrows(NullPointerException.class, () -> album.setGenre(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Genre cannot be empty or blank.")
    public void genreCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setGenre(arg));
        assertEquals(exception.getMessage(),"Genre cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Contempoaryffgfg45fnjnjngkrngkngkngkngkntkgnkgjkkntjgn"})
    @DisplayName("Genre name length should not exceed 30 characters.")
    public void genreLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setGenre(arg));
        assertEquals(exception.getMessage(),"Genre should not exceed 30 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for genre.")
    @ValueSource(strings = {"Contemporary"})
    public void positiveGenre(String arg){
        album.setGenre(arg);
        assertEquals(album.getGenre(),arg);
    }

    @Test
    @DisplayName("Style cannot be null.")
    public void styleCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> album.setStyle(null));
        assertEquals(exception.getMessage(),"Style cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ContemporaryJazzdfjfnjnjgjfbgbhbbbjnjbgbghnhhjhhjuukikikjkjmjkjhkjkjkjkjkjjgbnjgnbjg"})
    @DisplayName("Style length should not exceed 30 characters.")
    public void setStyleLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setStyle(arg));
        assertEquals(exception.getMessage(),"Style should not exceed 30 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for style.")
    @ValueSource(strings = {"Contemporary Jazz"})
    public void positiveStyle(String arg){
        album.setStyle(arg);
        assertEquals(album.getStyle(),arg);
    }

    @Test
    @DisplayName("Release format cannot be null.")
    public void releaseFormatCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> album.setReleaseFormat(null));
        assertEquals(exception.getMessage(),"Release format cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Release format cannot be empty or blank.")
    public void releaseFormatCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setReleaseFormat(arg));
        assertEquals(exception.getMessage(),"Release format cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"CDdfjfnjnjgjfbgbhbbbjnjbgbghnhhjhhjuukikikjkjm"})
    @DisplayName("Release format length should not exceed 20 characters.")
    public void setReleaseFormatLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> album.setReleaseFormat(arg));
        assertEquals(exception.getMessage(),"Release format should not exceed 20 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for release format.")
    @ValueSource(strings = {"CD"})
    public void positiveReleaseFormat(String arg){
        album.setReleaseFormat(arg);
        assertEquals(album.getReleaseFormat(),arg);
    }

    @Test
    @DisplayName("Reviews cannot be null.")
    public void reviewsCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setReviews(null));
    }

    @Test
    @DisplayName("Check if any object within the Review set is null.")
    public void nullObjectInReviews(){
        Set<Review> reviews = new HashSet<>();
        reviews.add(null);
        assertThrows(NullPointerException.class,()->album.setReviews(reviews));
    }

    @Test
    @DisplayName("Positive test to check if the reviews have been set.")
    public void positiveTestReviews() throws MalformedURLException {
        Set<Review> reviews = new HashSet<>();
        reviews.add(new Review(new URL("https://rottentomatoes.com"),77));
        album.setReviews(reviews);
        assertEquals(album.getReviews(),reviews);
    }

    @Test
    @DisplayName("Concerts cannot be null.")
    public void concertsCannotBeNull(){
        assertThrows(NullPointerException.class,()->album.setConcerts(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInConcerts(){
        Set<Concert> concerts = Sets.newHashSet();
        concerts.add(null);
        assertThrows(NullPointerException.class,()->album.setConcerts(concerts));
    }

    @Test
    @DisplayName("Valid concerts.")
    public void positiveConcerts(){
        Set<Concert> concert = Sets.newHashSet();
        concert.add(new Concert("Tokyo festival","Japan"));
        album.setConcerts(concert);
        assertEquals(album.getConcerts(),concert);
    }

    @ParameterizedTest
    @ValueSource(strings = {"26gfgfg7","8.fgfg2"})
    @DisplayName("Sales should have just numbers.")
    public void randomSales(String arg){
        assertThrows(NumberFormatException.class,()->album.setSales(Integer.parseInt(arg)));
    }

    @ParameterizedTest
    @ValueSource(ints = {-2})
    @DisplayName("Sales should be greater than or equal to 0.")
    public void salesShouldBeValid(int arg){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->album.setSales(arg));
        assertEquals(exception.getMessage(),"Sales number should be greater than or equal to 0.");
    
    }

    @ParameterizedTest
    @ValueSource(ints = {59999})
    @DisplayName("Check if it is setting the valid sales number.")
    public void positiveSales(int arg){
        album.setSales(arg);
        assertEquals(album.getSales(),arg);
    }

    @Test
    @DisplayName("Groups should be same as Assigned")
    public void groupsShouldBeSameAsAssigned(){
        groups.add(new Group("Linkin Park"));
        album.setFeaturedGroup(groups);
        assertEquals(groups, album.getFeaturedGroup());
    }


    //setGroups()
    @Test
    @DisplayName("Groups should be correctly set")
    public void groupsShouldBeCorrectlySet(){
        Set<Group> newGroups = new HashSet<>();
        newGroups.add(new Group("TestGroup"));
        album.setFeaturedGroup(newGroups);
        assertEquals(newGroups, album.getFeaturedGroup());
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInGroup(){
        Set<Group> groups = Sets.newHashSet();
        groups.add(null);
        assertThrows(NullPointerException.class,()->album.setFeaturedGroup(groups));
    }
}