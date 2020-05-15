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
    private Album album5;
    private Musician musician1;
    private Musician musician2;
    private Musician musician3;
    private Musician musician4;
    private Musician musician5;
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
        url1 = new URL("https://www.imdb.com/");
        url2 = new URL("https://www.rottentomatoes.com/");
    }

    @Test
    public void positiveBusiestYear()
    {
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        List<Integer> years = ecmMiner.busiestYears(1);
        List<Integer> expectedYears = Lists.newArrayList(2016);
        assertEquals(years,expectedYears);
    }

    @Test
    public void shouldReturnTheBusiestYearWhenOnlyOne()
    {
        dao.createOrUpdate(album1);
        List<Integer> years = ecmMiner.busiestYears(999);
        assertEquals(1,years.size());
        assertTrue(years.contains(album1.getReleaseYear()));
    }

    @Test
    public void busiestYearWhenNoAlbumIsCreated()
    {
        List<Integer> years = ecmMiner.busiestYears(2);
        assertEquals(0,years.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0,-100})
    public void busiestYearWhenNegativeOrZeroParameterPassed(int arg)
    {
        dao.createOrUpdate(album1);
        List<Integer> years = ecmMiner.busiestYears(arg);
        assertEquals(years.size(),0);
    }

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

    @Test
    public void whenNoAlbumExistsForAlbumSimilarity()
    {
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        albumToBeChecked.setGenre("Rock");
        albumToBeChecked.setInstruments(Sets.newHashSet(new MusicianInstrument(musician1,Sets.newHashSet(new MusicalInstrument("Guitar"))),new MusicianInstrument(musician3,Sets.newHashSet(new MusicalInstrument("Drums")))));
        List<Album> albums = ecmMiner.mostSimilarAlbums(3,albumToBeChecked);
        assertEquals(0,albums.size());
    }

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

    @Test
    public void positiveProlificMusician()
    {
        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);
        List<Musician> musicians = ecmMiner.mostProlificMusicians(2, 1974, 2017);
        assertEquals(2, musicians.size());
        assertTrue(musicians.equals(Lists.newArrayList(musician1,musician2)));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void mostProlificMusicianReturnEmptyWithInvalidK(int arg)
    {
        musician1.setAlbums(Sets.newHashSet(album1));
        dao.createOrUpdate(musician1);
        List<Musician> musicians = ecmMiner.mostProlificMusicians(arg, 1960, 2010);
        assertEquals(0, musicians.size());
    }

    @Test
    public void whenNoMusicianIsCreatedForProlific() {
        List<Musician> musicians = ecmMiner.mostProlificMusicians(3, 1960, 2010);
        assertEquals(0, musicians.size());
    }

    @Test
    public void shouldReturnTheProlificMusicianWhenThereIsOnlyOne()
    {
        musician1.setAlbums(Sets.newHashSet(album1));
        dao.createOrUpdate(musician1);
        List<Musician> musicians = ecmMiner.mostProlificMusicians(999, 1960, 2018);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician1));
    }

    @Test
    public void mostProlificMusicianReturnEmptyListWithInvalidStartAndEndYears()
    {
        musician1.setAlbums(Sets.newHashSet(album2));
        dao.createOrUpdate(musician1);
        List<Musician> musicians = ecmMiner.mostProlificMusicians(1, 2018, 1970);
        assertEquals(0, musicians.size());
    }

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
        MusicianInstrument musicianInstrument5 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);dao.createOrUpdate(musician3);dao.createOrUpdate(musician4);
        dao.createOrUpdate(musicianInstrument1);dao.createOrUpdate(musicianInstrument2);dao.createOrUpdate(musicianInstrument3);
        dao.createOrUpdate(musicianInstrument4);dao.createOrUpdate(musicianInstrument5);
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(1);
        assertEquals(1, musicians.size());
        assertTrue(musicians.equals(Lists.newArrayList(musician1)));
    }

    @DisplayName("Return one musician when there is only one existing")
    @Test
    public void shouldReturnTheTalentedMusicianWhenThereIsOnlyOne()
    {
        Musician musician3 = new Musician("Chester Bennington");
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Drums")));
        dao.createOrUpdate(musician3);
        dao.createOrUpdate(musicianInstrument3);
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(999);
        assertEquals(1, musicians.size());
        assertTrue(musicians.equals(Lists.newArrayList(musician3)));
    }

    @DisplayName("Testing while there is no value in musician and musician instrument")
    @Test
    public void whenNoMusicianOrMusicianInstrumentCreatedForTalented()
    {
        List<Musician> musicians =  ecmMiner.mostTalentedMusicians(5);
        assertEquals(musicians.size(),0);
     }

    /**
     *Most Talented Musician Test cases
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
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        assertEquals(0, musicians.size());
    }

    @DisplayName("Positive test for most social musicians")
    @Test
    public void positiveMostSocialMusicians()
    {
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);dao.createOrUpdate(musician3);
        dao.createOrUpdate(musician4);dao.createOrUpdate(musician5);
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
        assertEquals(2, musicians.size());
    }

    @DisplayName("Most social musician return musician with 0 size with invalid k value")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void mostSocialMusiciansReturnWithZeroSizeWithInvalidK(int arg)
    {
        dao.createOrUpdate(musician1);dao.createOrUpdate(musician2);
        List<Musician> musicians = ecmMiner.mostSocialMusicians(arg);
        assertEquals(0, musicians.size());
    }

    @DisplayName("Most social musician return one when there is only one existing musician and k exceeds the total number")
    @Test
    public void shouldReturnTheSocialMusicianWhenThereIsOnlyOne()
    {
        dao.createOrUpdate(musician5);
        List<Musician> musicians = ecmMiner.mostSocialMusicians(999);
        assertTrue(musicians.contains(musician5));
        assertEquals(1, musicians.size());
    }

    @DisplayName("Testing while there is no value in musician and album.")
    @Test
    public void whenNullIsPassedToSocialMusician() {
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertEquals(0, musicians.size());
    }

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

    @DisplayName("Testing while there is nothing in Album.")
    @Test
    public void whenNoValueOfAlbumIsPassedToHighestRatedAlbum() {
        List<Album> albums =  ecmMiner.highestRatedAlbums(2);
        assertEquals(albums.size(),0);
    }

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

    @DisplayName("Best selling albums returns albums with 0 size with invalid k value.")
    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    public void bestSellingAlbumsWithInvalidK(int arg){
        album1.setSales(1000);
        dao.createOrUpdate(album1);
        List<Album> albums = ecmMiner.bestSellingAlbums(arg);
        assertEquals(0,albums.size());
    }

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

    @DisplayName("Testing while there is null value in Album.")
    @Test
    public void whenNullIsPassedToHighestSellingAlbum() {
        List<Album> albums =  ecmMiner.bestSellingAlbums(2);
        assertEquals(albums.size(),0);
    }

}