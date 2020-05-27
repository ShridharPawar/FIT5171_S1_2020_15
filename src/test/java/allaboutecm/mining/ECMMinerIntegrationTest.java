package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO: perform integration testing of both ECMMiner and the DAO classes together.
 */
class ECMMinerIntegrationTest {
    private static final String TEST_DB = "target/test-data/test-db.neo4j";
    private static Session session;
    private static SessionFactory sessionFactory;
    private ECMMiner ecmMiner;
    private static DAO dao;
    private Album album1;
    private Album album2;
    private Album album3;
    private Album album4;
    private Musician musician1;
    private Musician musician2;
    private Musician musician3;
    private Musician musician4;
    private URL url1;
    private URL url2;

    @BeforeAll
    public static void setUp() {
        Configuration configuration = new Configuration.Builder().build();
        sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        session = sessionFactory.openSession();
        dao = new Neo4jDAO(session);
    }

    /*
     Purging the database*/

    @AfterEach
    public void tearDownEach() {
        session.purgeDatabase();
    }


    /*Purging the database*/

    @AfterAll
    public static void tearDown() throws IOException {
        session.purgeDatabase();
        session.clear();
        sessionFactory.close();
        File testDir = new File(TEST_DB);
        if (testDir.exists()) {
//            FileUtils.deleteDirectory(testDir.toPath());
        }
    }

    @Test
    public void daoIsNotEmpty() {
        assertNotNull(dao);
    }

    @BeforeEach
    public void beforeEachSetUp() throws MalformedURLException {
        ecmMiner = new ECMMiner(dao);
        album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        album2 = new Album(2016, "ECM 1064/66", "Meteora");
        album3 = new Album(2016, "ECM 1064/67", "Minutes to midnight");
        album4 = new Album(2016, "ECM 1064/68", "Shadow of the day");
        musician1 = new Musician("Keith Jarrett");
        musician2 = new Musician("Mike Shinoda");
        musician3 = new Musician("Chester Bennington");
        musician4 = new Musician("Bon Jovi");
        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album1,album2));
        musician3.setAlbums(Sets.newHashSet(album1));
        musician4.setAlbums(Sets.newHashSet(album2));
        album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3));
        album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician4));
        url1 = new URL("https://www.imdb.com/");
        url2 = new URL("https://www.rottentomatoes.com/");
    }

    /**
     * To Validate if if can pass test when the positive year is given.
     */
    @Test
    public void positiveBusiestYear()
    {
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        List<Integer> years = ecmMiner.busiestYears(1);
        List<Integer> expectedYears = Lists.newArrayList(2016);
        assertEquals(years,expectedYears);
    }

  /**
     * To Validate if it can pass when there is 0 or minus numbers are given.
     */
    @ParameterizedTest
    @ValueSource(ints = {0,-100})
    public void busiestYearWhenNegativeOrZeroParameterPassed(int arg)
    {
        dao.createOrUpdate(album1);
        //List<Integer> years = ecmMiner.busiestYears(arg);
        //assertEquals(years.size(),0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ecmMiner.busiestYears(arg));
        assertEquals(exception.getMessage(),"Number of Years cannot be Zero or negative.");
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
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
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
        dao.createOrUpdate(album3);
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
        dao.createOrUpdate(album3);
        List<Album> albums = ecmMiner.mostSimilarAlbums(999,albumToBeChecked);
        assertTrue(albums.contains(album3));
        assertEquals(albums.size(),1);
    }

    /**
     * To Validate what method will return when there is no ablum existed.
     */
    @Test
    public void whenNoAlbumExistsForAlbumSimilarity()
    {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        albumToBeChecked.setGenre("Rock");
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        List<Album> albums = ecmMiner.mostSimilarAlbums(3,albumToBeChecked);
        assertEquals(0,albums.size());
    }

    /**
     * To Validate if what method will return when 0 or minus numbers are given.
     */
    @DisplayName("Most similar album with invalid k value")
    @ParameterizedTest
    @ValueSource(ints = {0,-100})
    public void whenZeroOrNegativeParameterIsPassedToSimilarAlbums(int arg) {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        album3.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        album3.setGenre("Rock");
        albumToBeChecked.setGenre("Rock");
        dao.createOrUpdate(album3);
        List<Album> albums = ecmMiner.mostSimilarAlbums(arg,albumToBeChecked);
        assertEquals(0, albums.size());
    }

    /**
     * To Validate if it can pass test when positive number of prolific musician is given.
     */
    @Test
    public void positiveProlificMusician()
    {
        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);
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
        dao.createOrUpdate(musician1);
        //List<Musician> musicians = ecmMiner.mostProlificMusicians(arg, 1960, 2010);
        //assertEquals(0, musicians.size());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostProlificMusicians(arg, 1960, 2010));
        assertEquals(exception.getMessage(),"k should be positive");
    }

    /**
     * To Validate what method will return for prolific musician when there is
     * only one musician.
     */
    @Test
    public void shouldReturnTheProlificMusicianWhenThereIsOnlyOne()
    {
        musician1.setAlbums(Sets.newHashSet(album1));
        dao.createOrUpdate(musician1);
        List<Musician> musicians = ecmMiner.mostProlificMusicians(999, 1960, 2018);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician1));
    }

    /**
     * To Validate if it can pass test when positive number of Talented musician is given.
     */
    @DisplayName("positive test for most Talented musician.")
    @Test
    public void positiveMostTalentedMusician()
    {
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Mike Shinoda");
        Musician musician3 = new Musician("Chester Bennington");
        Musician musician4 = new Musician("Bon Jovi");
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Guitar"),new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(musician2, Sets.newHashSet(new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Drums")));
        MusicianInstrument musicianInstrument4 = new MusicianInstrument(musician4, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);dao.createOrUpdate(musician3);dao.createOrUpdate(musician4);
        dao.createOrUpdate(musicianInstrument1);dao.createOrUpdate(musicianInstrument2);dao.createOrUpdate(musicianInstrument3);
        dao.createOrUpdate(musicianInstrument4);
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(1);
        assertEquals(1, musicians.size());
    }

 /**
     * To Validate what method will return for most talented musician when there is invalid K.
     */
    @DisplayName("Invalid k for most talented musicians would return with size 0.")
    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    public void mostTalentedMusicianReturnNullWithInvalidK(int arg)
    {
        Musician musician3 = new Musician("Chester Bennington");
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Drums")));
        dao.createOrUpdate(musician3);
        dao.createOrUpdate(musicianInstrument3);
        //List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        //assertEquals(0, musicians.size());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostTalentedMusicians(arg));
        assertEquals(exception.getMessage(),"k should be positive");
    }

    /**
     * To Validate if it can pass test when positive number of Social musician is given.

    @DisplayName("Positive test for most social musicians")
    @Test
    public void positiveMostSocialMusicians()
    {
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);dao.createOrUpdate(musician3);
        dao.createOrUpdate(musician4);
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
        assertEquals(2, musicians.size());
    }

    /**
     * To Validate if it can return 0 size for most social musician when invalid k is given.

    @DisplayName("Most social musician return musician with 0 size with invalid k value")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void mostSocialMusiciansReturnWithZeroSizeWithInvalidK(int arg)
    {
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);
        List<Musician> musicians = ecmMiner.mostSocialMusicians(arg);
        assertEquals(0, musicians.size());
    }



    /**
     * To Validate if it can pass test when there is null for musician and album.

    @DisplayName("Testing while there is no value in musician and album.")
    @Test
    public void whenNullIsPassedToSocialMusician() {
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertEquals(0, musicians.size());
    }*/

    @Test
    public void shouldReturnRightMusicianWithMostSocialMusician(){

        Album albumA = new Album(1979, "ECM 1134", "PATH");
        Album albumB = new Album(1982, "ECM 1223", "PATHS, PRINTS");
        Album albumC = new Album(1979, "ECM 1135", "PHOTO WITH ...");

        Musician musicianA = new Musician("Keith Jarrett");
        Musician musicianB = new Musician("Old Man");
        Musician musicianC = new Musician("Charlie Haden");
        Musician musicianD = new Musician("Gary Peacock");

        albumA.setFeaturedMusicians(Lists.newArrayList(musicianA,musicianB));
        albumB.setFeaturedMusicians(Lists.newArrayList(musicianA,musicianC,musicianD));
        albumC.setFeaturedMusicians(Lists.newArrayList(musicianB,musicianD));

        dao.createOrUpdate(albumA);
        dao.createOrUpdate(albumB);
        dao.createOrUpdate(albumC);

        List<Musician> musicians = ecmMiner.mostSocialMusicians(3);
        assertEquals(3, musicians.size());
        assertTrue(musicians.contains(musicianA));
        assertTrue(musicians.contains(musicianB));
        assertTrue(musicians.contains(musicianD));
    }

    @Test
    public void shouldReturnTheSocialMusicianWhenThereIsOnlyTwo(){
        Musician musicianA = new Musician("Keith Jarrett");
        Musician musicianB = new Musician("Old Man");
        Album albumA = new Album(1979, "ECM 1134", "PATH");
        albumA.setFeaturedMusicians(Lists.newArrayList(musicianA,musicianB));
        dao.createOrUpdate(albumA);

        List<Musician> musicians = ecmMiner.mostSocialMusicians(5);
        assertEquals(2, musicians.size());
        assertTrue(musicians.contains(musicianA));
        assertTrue(musicians.contains(musicianB));
    }

    @Test
    public void shouldThrowIllegalArgWhenKIsNegativeForMostSocialMusician() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSocialMusicians(-1));
        assertEquals("k should be positive", e.getMessage());
    }

    /**
     * To Validate if it can pass test when positive number of highest rate is given.
     */
    @Test
    public void positiveHighestRatedAlbums() throws MalformedURLException {
        album1.setReviews(Sets.newHashSet(new Review(url1,98),new Review(url2,48)));
        album2.setReviews(Sets.newHashSet(new Review(url1,98),new Review(url2,98)));
        album3.setReviews(Sets.newHashSet(new Review(url2,99)));
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        List<Album> albums = ecmMiner.highestRatedAlbums(1);
        assertTrue(albums.contains(album3));
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
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
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
        dao.createOrUpdate(album1);
        List<Album> albums = ecmMiner.highestRatedAlbums(999);
        assertEquals(1, albums.size());
        assertTrue(albums.contains(album1));
    }

    /**
     * To Validate if method will return for highest rated album when no value is given.
     */
    @DisplayName("Testing while there is nothing in Album.")
    @Test
    public void whenNoValueOfAlbumIsPassedToHighestRatedAlbum() {
        List<Album> albums =  ecmMiner.highestRatedAlbums(2);
        assertEquals(albums.size(),0);
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
        dao.createOrUpdate(album1);dao.createOrUpdate(album2);dao.createOrUpdate(album3);
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
        dao.createOrUpdate(album1);
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
        dao.createOrUpdate(album1);
        List<Album> albums = ecmMiner.bestSellingAlbums(999);
        assertEquals(1, albums.size());
        assertTrue(albums.contains(album1));
    }

    /**
     * To Validate if it can pass test when null value is given.
     */
    @DisplayName("Testing while there is null value in Album.")
    @Test
    public void whenNullIsPassedToHighestSellingAlbum() {
        List<Album> albums =  ecmMiner.bestSellingAlbums(2);
        assertEquals(albums.size(),0);
    }

    /**
     * To Validate if it can pass test when positive number of
     * most popular musician is given.

    @Test
    public void positiveMostPopularPerformer()
    {
        Concert concert1 = new Concert("Tokyo Festival","Japan");
        Concert concert2 = new Concert("Ultrasonic Festival","India");
        concert1.setMusicians(Sets.newHashSet(musician1,musician2,musician3));
        concert2.setMusicians(Sets.newHashSet(musician4,musician1));
        dao.createOrUpdate(concert1);dao.createOrUpdate(concert2);
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);dao.createOrUpdate(musician3);dao.createOrUpdate(musician4);
        List<Musician> musicians = ecmMiner.mostPopularPerformer(1);
        assertTrue(musicians.contains(musician1));
        assertEquals(1,musicians.size());
    }

    /**
     * To Validate if it will return most popular musician when invalid K is given.

    @DisplayName("Most popular musicians return musicians with 0 size with invalid k value.")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void postPopularPerformerWithInvalidK(int arg){
        Concert concert1 = new Concert("Tokyo Festival","Japan");
        concert1.setMusicians(Sets.newHashSet(musician1));
        dao.createOrUpdate(concert1);dao.createOrUpdate(musician1);
        List<Musician> musicians = ecmMiner.mostPopularPerformer(arg);
        assertEquals(0,musicians.size());
    }

    /**
     * To Validate if it can return most popular performer when there is no musician given.

    @DisplayName("Most popular performer returned when there is only one existing musician and k exceeds the total number.")
    @Test
    public void shouldReturnTheMostPopularPerformerWhenThereIsOnlyOne()
    {
        Concert concert1 = new Concert("Tokyo Festival","Japan");
        concert1.setMusicians(Sets.newHashSet(musician1));
        dao.createOrUpdate(concert1);dao.createOrUpdate(musician1);
        List<Musician> musicians = ecmMiner.mostPopularPerformer(999);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician1));
    }*/

}