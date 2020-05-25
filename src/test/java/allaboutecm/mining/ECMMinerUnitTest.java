package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: perform unit testing on the ECMMiner class, by making use of mocking.
 */
class ECMMinerUnitTest {
    private DAO dao;
    private ECMMiner ecmMiner;
    private Album album1;
    private Album album2;
    private Album album3;
    private Album album4;
    private Album album5;
    private Musician musician1;
    private Musician musician2;
    private Musician musician3;
    private Musician musician4;
    private Musician musician5;
    private MusicianInstrument musicianInstrument1;
    private MusicianInstrument musicianInstrument2;
    private MusicianInstrument musicianInstrument3;
    private MusicianInstrument musicianInstrument4;
    private MusicianInstrument musicianInstrument5;
    private URL url1;
    private URL url2;

    // Build up testing environment with objects creation and values initialization
    @BeforeEach
    public void setUp() throws MalformedURLException {
        dao = mock(Neo4jDAO.class);
        ecmMiner = new ECMMiner(dao);
        album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        album2 = new Album(2016, "ECM 1064/66", "Meteora");
        album3 = new Album(2016, "ECM 1064/67", "Minutes to midnight");
        album4 = new Album(2016, "ECM 1064/68", "Shadow of the day");
        album5 = new Album(1975, "ECM 1064/69", "Gasolina");
        musician1 = new Musician("Keith Jarrett");
        musician2 = new Musician("Mike Shinoda");
        musician3 = new Musician("Chester Bennington");
        musician4 = new Musician("Bon Jovi");
        musician5 = new Musician("Chris Martin");
        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album1,album2));
        musician3.setAlbums(Sets.newHashSet(album1));
        musician4.setAlbums(Sets.newHashSet(album2));
        musician5.setAlbums(Sets.newHashSet(album3));
        album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3));
        album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician4));
        album3.setFeaturedMusicians(Lists.newArrayList(musician5));
        musicianInstrument1 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Guitar"),new MusicalInstrument("Piano")));
        musicianInstrument2 = new MusicianInstrument(musician2, Sets.newHashSet(new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));
        musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Drums")));
        musicianInstrument4 = new MusicianInstrument(musician4, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        musicianInstrument5 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        url1 = new URL("https://www.imdb.com/");
        url2 = new URL("https://www.rottentomatoes.com/");
    }

    /**
     * To Validate if it can pass test when positive number of prolific musician is given.
     */
    @Test
    public void positiveProlificMusician()
    {
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician5));
        List<Musician> musicians = ecmMiner.mostProlificMusicians(2, 1974, 2017);
        assertEquals(2, musicians.size());
        //assertTrue(musicians.equals(Lists.newArrayList(musician1,musician2)));
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
    }

    /**
     * To Validate what method will return when invalid K is given.
     */
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void mostProlificMusicianReturnEmptyWithInvalidK(int arg)
    {
        musician1.setAlbums(Sets.newHashSet(album1));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
        List<Musician> musicians = ecmMiner.mostProlificMusicians(arg, 1960, 2010);
        assertEquals(0, musicians.size());
        //IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(arg, 1960, 2010));
        //assertEquals(exception.getMessage(),"k should be positive");
    }

    /**
     * To Validate what it will return for Porlific when null value is given.
     */
    @Test
    public void nullIsPassedToProlific() {
        when(dao.loadAll(Musician.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostProlificMusicians(4, -1, -1));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate what method will return for prolific musician when there is
     * only one musician.
     */
    @Test
    public void shouldReturnTheProlificMusicianWhenThereIsOnlyOne()
    {
        musician1.setAlbums(Sets.newHashSet(album1));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
        List<Musician> musicians = ecmMiner.mostProlificMusicians(999, 1960, 2018);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician1));
    }

    /**
     * To Validate if method will return empty list when Invalid Start and EndYears are given.
     */
    @Test
    public void mostProlificMusicianReturnEmptyListWithInvalidStartAndEndYears()
    {
        musician1.setAlbums(Sets.newHashSet(album2));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(1, 2018, 1970);
        assertEquals(0, musicians.size());
    }

    /**
     * To Validate if if can pass test when the positive year is given.
     */
    @DisplayName("Positive test for busiest year")
    @Test
    public void positiveBusiestYear()
    {

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5));
        List<Integer> years = ecmMiner.busiestYears(1);
        List<Integer> expectedYears = Lists.newArrayList(2016);
        assertEquals(years,expectedYears);
    }

    /**
     * To Validate if if can return busiest year when there is only one year.
     */
    @DisplayName("Returned busiest year would be the number of existing albums when k over it")
    @Test
    public void shouldReturnTheBusiestYearWhenOnlyOne()
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Integer> years = ecmMiner.busiestYears(999);
        assertEquals(1,years.size());
        assertTrue(years.contains(album1.getReleaseYear()));
    }

    /**
     * To Validate if it wlll return busiest year when null value is given.
     */
    @DisplayName("Returned null busiest year while there is no album existing")
    @Test
    public void busiestYearWhenNullPassed()
    {
        when(dao.loadAll(Album.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.busiestYears(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate if it can pass when there is 0 or minus numbers are given.
     */
    @DisplayName("Invalid k would return null busiest years collection")
    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    public void busiestYearWhenNegativeOrZeroParameterPassed(int arg)
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5));
        List<Integer> years = ecmMiner.busiestYears(arg);
        assertEquals(0,years.size());
    }

    /**
     * To Validate if it can pass test when positive number of Talented musician is given.
     */
    @DisplayName("positive test for most Talented musician.")
    @Test
    public void positiveMostTalentedMusician()
    {
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument1,musicianInstrument2
                ,musicianInstrument3,musicianInstrument4,musicianInstrument5));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(1);
        assertEquals(1, musicians.size());
        assertTrue(musicians.equals(Lists.newArrayList(musician1)));
    }

    /**
     * To Validate if will one musician when there is only one musician.
     */
    @DisplayName("Return one musician when there is only one existing")
    @Test
    public void shouldReturnTheTalentedMusicianWhenThereIsOnlyOne()
    {
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument3));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician3));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(999);
        assertEquals(1, musicians.size());
        assertTrue(musicians.equals(Lists.newArrayList(musician3)));
    }

    /**
     * To Validate if will pass the test when null value is given.
     */
    @DisplayName("Testing while there is null value in musician and musician instrument")
    @Test
    public void whenNullIsPassedToTalented()
    {
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(null);
        when(dao.loadAll(Musician.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostTalentedMusicians(5));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate what method will return for most talented musician when
     * there is invalid K.
     */
    @DisplayName("Invalid k for most talented musicians would return null")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    public void mostTalentedMusicianReturnNullWithInvalidK(int arg)
    {
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument3));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician3));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        assertEquals(0, musicians.size());
    }

    /**
     * To Validate if it can pass test when positive number of Social musician is given.
     */
    @DisplayName("Positive test for most social musicians")
    @Test
    public void positiveMostSocialMusicians()
    {
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4,musician5));
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
        assertEquals(2, musicians.size());
     }

    /**
     * To Validate if it can return 0 size for most social musician when invalid k is given.
     */
     @DisplayName("Most social musician return musician with 0 size with invalid k value")
     @ParameterizedTest
     @ValueSource(ints = {-100, 0})
     public void mostSocialMusiciansReturnWithZeroSizeWithInvalidK(int arg)
     {
         when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4,musician5));
         List<Musician> musicians = ecmMiner.mostSocialMusicians(arg);
         assertEquals(0, musicians.size());
     }

    /**
     * To Validate if method will return most social musician when where is no one musician.
     */
    @DisplayName("Most social musician return one when there is only one existing musician and k exceeds the total number")
    @Test
    public void shouldReturnTheSocialMusicianWhenThereIsOnlyOne()
    {
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician5));
        List<Musician> musicians = ecmMiner.mostSocialMusicians(999);
        assertTrue(musicians.contains(musician5));
        assertEquals(1, musicians.size());
    }

    /**
     * To Validate if it can pass test when there is null for musician and album.
     */
    @DisplayName("Testing while there is null value in musician.")
    @Test
    public void whenNullIsPassedToSocialMusician() {
        when(dao.loadAll(Musician.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostSocialMusicians(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate if positive number of most similar album can pass test.
     */
     @Test
     public void positiveMostSimilarAlbums()
     {
         Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
         album1.setInstruments(Sets.newHashSet( new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician4,Sets.newHashSet(new MusicalInstrument("Synthesizer")))));
         album2.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
         album3.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
         albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
         album1.setGenre("Jazz");
         album2.setGenre("Rock");
         album3.setGenre("Rock");
         albumToBeChecked.setGenre("Rock");
         when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
         List<Album> albums = ecmMiner.mostSimilarAlbums(2,albumToBeChecked);
         assertTrue(albums.contains(album2));
         assertTrue(albums.contains(album3));
         assertEquals(albums.size(),2);
      }

    /**
     * To Validate if will return 0 when there is no similar ablum.
     */
    @DisplayName("Return 0 when albums are unique.")
    @Test
    public void shouldReturnSizeZeroWhenThereIsNoSimilarAlbum()
    {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        album3.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        album3.setGenre("Jazz");
        albumToBeChecked.setGenre("Rock");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album3));
        List<Album> albums = ecmMiner.mostSimilarAlbums(2,albumToBeChecked);
        assertEquals(albums.size(),0);
    }

    /**
     * To Validate if will return most similar when there is only one ablum.
     */
    @DisplayName("Most similar album return one when there is only one existing musician")
    @Test
    public void shouldReturnTheMostSimilarAlbumWhenThereIsOnlyOne()
    {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        album3.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        album3.setGenre("Rock");
        albumToBeChecked.setGenre("Rock");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album3));
        List<Album> albums = ecmMiner.mostSimilarAlbums(999,albumToBeChecked);
        assertTrue(albums.contains(album3));
        assertEquals(albums.size(),1);
    }

    /**
     * To Validate if will return most similar when null value is given.
     */
    @DisplayName("Testing while there is null value in album")
    @Test
    public void whenNullIsPassedToMostSimilarAlbums() {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        albumToBeChecked.setGenre("Rock");
        when(dao.loadAll(Album.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostSimilarAlbums(999,albumToBeChecked));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate if what method will return when 0 or minus numbers are given.
     */
    @DisplayName("Most similar album with invalid k value")
    @ParameterizedTest
    @ValueSource(ints = {0,-100})
    public void whenZeroOrNegativeParameterIsPassedToSimilar(int arg) {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        album3.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        album3.setGenre("Rock");
        albumToBeChecked.setGenre("Rock");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album3));
        List<Album> albums = ecmMiner.mostSimilarAlbums(arg,albumToBeChecked);
        assertEquals(0, albums.size());
    }

    /**
     * To Validate if it can pass test when positive number of highest rate is given.
     */
    @Test
    public void positiveHighestRatedAlbums() throws MalformedURLException
    {
        album1.setReviews(Sets.newHashSet(new Review(url1,98),new Review(url2,48.5)));
        album2.setReviews(Sets.newHashSet(new Review(url1,98),new Review(url2,98.5)));
        album3.setReviews(Sets.newHashSet(new Review(url2,99)));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
        List<Album> albums = ecmMiner.highestRatedAlbums(2);
        assertTrue(albums.contains(album3));
        assertTrue(albums.contains(album2));
    }

    /**
     * To Validate if method will return o when invalid k is given.
     */
    @DisplayName("Highest rated album returns albums with 0 size with invalid k value.")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void highestRatedAlbumsWithInvalidK(int arg) throws MalformedURLException {
        album1.setReviews(Sets.newHashSet(new Review(url1,98),new Review(url2,48)));
        album2.setReviews(Sets.newHashSet(new Review(url2,98),new Review(url1,50)));
         when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2));
        List<Album> albums = ecmMiner.highestRatedAlbums(arg);
        assertEquals(0, albums.size());
    }

    /**
     * To Validate if it will return highest rated album when there is only one existing album
     * and k exceeds the total number.
     */
    @DisplayName("Highest rated album returned when there is only one existing album and k exceeds the total number.")
    @Test
    public void shouldReturnTheHighestRatedAlbumWhenThereIsOnlyOne()
    {
        album1.setReviews(Sets.newHashSet(new Review(url1,98),new Review(url2,48)));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Album> albums = ecmMiner.highestRatedAlbums(999);
        assertEquals(1, albums.size());
        assertTrue(albums.contains(album1));
    }

    /**
     * To Validate if method will return for highest rated album when null value is given.
     */
    @DisplayName("Testing while there is null value in Album.")
    @Test
    public void whenNullIsPassedToHighestRatedAlbum() {
        when(dao.loadAll(Album.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.highestRatedAlbums(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate if it can pass test when positive number of best Selling album is given.
     */
    @Test
    public void positiveBestSellingAlbums() throws MalformedURLException
    {
        album1.setSales(1000);
        album2.setSales(400);
        album3.setSales(999);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
        List<Album> albums = ecmMiner.bestSellingAlbums(1);
        assertTrue(albums.contains(album1));
        assertEquals(1,albums.size());
    }

    /**
     * To Validate if best selling album will return 0 album when invalid K is given.
     */
    @DisplayName("Best selling albums returns albums with 0 size with invalid k value.")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void bestSellingAlbumsWithInvalidK(int arg){
        album1.setSales(1000);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Album> albums = ecmMiner.bestSellingAlbums(arg);
        assertEquals(0,albums.size());
    }

    /**
     * To validate method will return for highest selling album when
     * there is only one album.
     */
    @DisplayName("Highest selling album returned when there is only one existing album and k exceeds the total number.")
    @Test
    public void shouldReturnTheHighestSellingAlbumWhenThereIsOnlyOne()
    {
        album1.setSales(1000);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Album> albums = ecmMiner.bestSellingAlbums(999);
        assertEquals(1, albums.size());
        assertTrue(albums.contains(album1));
    }

    /**
     * To Validate if it can pass test when null value is given to Highest Selling Album.
     */
    @DisplayName("Testing while there is null value in Album.")
    @Test
    public void whenNullIsPassedToHighestSellingAlbum() {
        when(dao.loadAll(Album.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.bestSellingAlbums(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     * To Validate if it can pass test when positive number of
     * most popular musician is given.
     */
    @Test
    public void positiveMostPopularPerformer()
    {
        Concert concert1 = new Concert("Tokyo Festival","Japan");
        Concert concert2 = new Concert("Ultrasonic Festival","India");
        concert1.setMusicians(Sets.newHashSet(musician1,musician2,musician3));
        concert2.setMusicians(Sets.newHashSet(musician4,musician1));
        when(dao.loadAll(Concert.class)).thenReturn(Sets.newHashSet(concert1,concert2));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4));
        List<Musician> musicians = ecmMiner.mostPopularPerformer(1);
        assertTrue(musicians.contains(musician1));
        assertEquals(1,musicians.size());
    }

    /**
     * To Validate if it will return most popular musician when invalid K is given.
     */
    @DisplayName("Most popular performer return 0 size with invalid k value.")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void postPopularPerformerWithInvalidK(int arg){
        Concert concert1 = new Concert("Tokyo Festival","Japan");
        concert1.setMusicians(Sets.newHashSet(musician1));
        when(dao.loadAll(Concert.class)).thenReturn(Sets.newHashSet(concert1));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
        List<Musician> musicians = ecmMiner.mostPopularPerformer(arg);
        assertEquals(0,musicians.size());
    }

    /**
     * To Validate if it can return most popular performer when there is no musician given.
     */
    @DisplayName("Most popular performer returned when there is only one existing musician and k exceeds the total number.")
    @Test
    public void shouldReturnTheMostPopularPerformerWhenThereIsOnlyOne()
    {
        Concert concert1 = new Concert("Tokyo Festival","Japan");
        concert1.setMusicians(Sets.newHashSet(musician1));
        when(dao.loadAll(Concert.class)).thenReturn(Sets.newHashSet(concert1));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
        List<Musician> musicians = ecmMiner.mostPopularPerformer(999);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician1));
    }

    /**
     * To Validate if it can pass test when null value is given to Most Popular Performer.
     */
    @DisplayName("Testing while there is null value in performer.")
    @Test
    public void whenNullIsPassedToMostPopularPerformer() {
        when(dao.loadAll(Musician.class)).thenReturn(null);
        when(dao.loadAll(Concert.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostPopularPerformer(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }


}