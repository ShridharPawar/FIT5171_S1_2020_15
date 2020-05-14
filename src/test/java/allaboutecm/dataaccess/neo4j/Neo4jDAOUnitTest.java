package allaboutecm.dataaccess.neo4j;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.*;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: add test cases to adequately test the Neo4jDAO class.
 */
class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db.neo4j";

    private static DAO dao;
    private static Session session;
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void setUp() {
        // See @https://neo4j.com/docs/ogm-manual/current/reference/ for more information.

        // To use an impermanent embedded data store which will be deleted on shutdown of the JVM,
        // you just omit the URI attribute.

        // Impermanent embedded store
        Configuration configuration = new Configuration.Builder().build();

        // Disk-based embedded store
        // Configuration configuration = new Configuration.Builder().uri(new File(TEST_DB).toURI().toString()).build();

        // HTTP data store, need to install the Neo4j desktop app and create & run a database first.
        // Configuration configuration = new Configuration.Builder().uri("http://neo4j:password@localhost:7474").build();

        //Activate database
        sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        session = sessionFactory.openSession();

        dao = new Neo4jDAO(session);
    }

    /*
     Purging the database
     */
    @AfterEach
    public void tearDownEach() {
        session.purgeDatabase();
    }

    /*
     Purging the database
     */
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

    @Test
    public void successfulCreationAndLoadingOfMusician() throws IOException {
        assertEquals(0, dao.loadAll(Musician.class).size());

        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);
        Musician loadedMusician = dao.load(Musician.class, musician.getId());

        assertNotNull(loadedMusician.getId());
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());

        assertEquals(1, dao.loadAll(Musician.class).size());

//        dao.delete(musician);
//        assertEquals(0, dao.loadAll(Musician.class).size());
    }

    @Test
    public void successfulCreationOfMusicianAndAlbum() throws IOException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());
        Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(musician.getAlbums(), loadedMusician.getAlbums());
    }

    @Test
    public void successfulCreationOfMusicianInstrument()
    {
        Musician musician = new Musician("Keith Jarrett");
        HashSet<MusicalInstrument> musicalInstruments = Sets.newHashSet(
                new MusicalInstrument("Violin"),
                new MusicalInstrument("Guitar"));
        MusicianInstrument musicianInstrument = new MusicianInstrument(musician, musicalInstruments);

        dao.createOrUpdate(musician);
        dao.createOrUpdate(musicianInstrument);

        Musician loadedMusician = dao.load(Musician.class, musician.getId());
        assertNotNull(loadedMusician);
        assertEquals(musician, loadedMusician);
        assertEquals(1,dao.loadAll(Musician.class).size());

        MusicianInstrument loadedMusicianInstrument = dao.load(MusicianInstrument.class, musicianInstrument.getId());
        assertEquals(musicianInstrument, loadedMusicianInstrument);
    }

    //Validate if the musicalInstrument could be created and loaded successfully
    @Test
    public void successfulCreationAndLoadingOfMusicalInstrument(){

        assertEquals(0, dao.loadAll(MusicalInstrument.class).size());

        MusicalInstrument musicalInstrument = new MusicalInstrument("Flute");

        dao.createOrUpdate(musicalInstrument);
        MusicalInstrument loadedMusicalInstrument = dao.load(MusicalInstrument.class, musicalInstrument.getId());

        assertNotNull(loadedMusicalInstrument.getId());
        assertEquals(musicalInstrument, loadedMusicalInstrument);

        assertEquals(1, dao.loadAll(MusicalInstrument.class).size());
    }

    //Validate if the Track could be created and loaded to database successfully
    @Test
    public void successfulCreationAndLoadingOfTrack() {
        assertEquals(0,dao.loadAll(Track.class).size());

        Track track = new Track("Yesterday Once More", 5.31);

        dao.createOrUpdate(track);
        Track loadedTrack = dao.load(Track.class, track.getId());

        assertNotNull(loadedTrack.getId());
        assertEquals(track, loadedTrack);

        assertEquals(1, dao.loadAll(Track.class).size());
    }

    @Test
    public void successfulCreatingAndLoadingOfWebPage() throws IOException {
        assertEquals(0,dao.loadAll(Webpage.class).size());
        URL url = new URL("https://www.garypeacock.org/");
        Webpage webpage = new Webpage("Gary Peacock", url);
        dao.createOrUpdate(webpage);

        Webpage loadedWebPage = dao.load(Webpage.class, webpage.getId());

        assertEquals(webpage, loadedWebPage);
        assertEquals(1, dao.loadAll(Webpage.class).size());
    }

    /*
    Testing saving musician process with same values could only saved once -- SM
    */
    @DisplayName("Same musician could only be one data in database")
    @Test
    public void SameMusiciansWouldSaveOnce() throws IOException
    {
        Musician mu1 = new Musician("Keith Jarrett");
        mu1.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        dao.createOrUpdate(mu1);

        Musician mu2 = new Musician("Keith Jarrett");
        mu2.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        dao.createOrUpdate(mu2);

        Collection<Musician> musicians = dao.loadAll(Musician.class);

        assertEquals(1,musicians.size());
        assertEquals(mu1.getName(), musicians.iterator().next().getName());
    }

    @DisplayName("Same instrument can be dispalyed only once")
    @Test
    public void SameMusicalInstrumentWouldSaveOnce() throws IOException
    {
        MusicalInstrument mi1 = new MusicalInstrument("Piano");
        dao.createOrUpdate(mi1);

        MusicalInstrument mi2 = new MusicalInstrument("Piano");
        dao.createOrUpdate(mi2);

        Collection<MusicalInstrument> musicalInstruments = dao.loadAll(MusicalInstrument.class);

        assertEquals(1,musicalInstruments.size());
        assertEquals(mi1.getName(), musicalInstruments.iterator().next().getName());
    }

    @DisplayName("Same album could be saved only once")
    @Test
    public void sameAlbumWouldSaveOnce()
    {
        Album a1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate(a1);
        Album a2 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate(a2);

        Collection<Album> albums = dao.loadAll(Album.class);

        assertEquals(1, albums.size());
        assertEquals(a1.getAlbumName(), albums.iterator().next().getAlbumName());
        assertEquals(a1.getReleaseYear(), albums.iterator().next().getReleaseYear());
        assertEquals(a1.getRecordNumber(), albums.iterator().next().getRecordNumber());
    }

    /*
     Testing multiple musicians data could be uploaded at a time
     */
    @DisplayName("Multiple musicians could be created in database once")
    @Test
    public void saveMultipleMusiciansOnce()
    {
        HashSet<Musician> musicianSet = Sets.newHashSet(
                new Musician("Lily Lee"),
                new Musician("Tresa Will"),
                new Musician("Ingrid Yu"),
                new Musician("Lucas Collins")
        );

        for (Musician musician : musicianSet)
        {
            dao.createOrUpdate(musician);
        }

        Collection<Musician> multipleLoad = dao.loadAll(Musician.class);

        assertEquals(musicianSet.size(), multipleLoad.size(), "Musicians could not be loaded");
        //Checking the loaded musicians collection contains all objects from updated collection
        for (Musician musician : multipleLoad)
        {
            assertTrue(musicianSet.contains(musician), musician.getName());
        }
    }

    // Validate if more than one instruments could be saved at a time
    @Test
    public void saveMultipleInstrumentAtATime() {
        HashSet<MusicalInstrument> musicalInstrumentSet = Sets.newHashSet(
                new MusicalInstrument("Piano"),
                new MusicalInstrument("Flute"),
                new MusicalInstrument("Violin"),
                new MusicalInstrument("Guitar")
        );

        for (MusicalInstrument musicalInstrument : musicalInstrumentSet){
            dao.createOrUpdate(musicalInstrument);
        }
        for (MusicalInstrument musicalInstrument : musicalInstrumentSet)
        {
            assertTrue(musicalInstrumentSet.contains(musicalInstrument), musicalInstrument.getName());
        }
    }

    /*
     Musician attribute could be updated
     */
    @DisplayName("Musician attribute could be updated")
    @Test
    public void updatingMusicianInfo() throws IOException
    {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        //Musician musician = new Musician("Elizabeth Wolf");
        //musician.setMusicianUrl(new URL("https://www.elizabethwolf.org/"));
        dao.createOrUpdate(musician);

        musician.setName("Eli Granger");

        Musician loadMusician = dao.load(Musician.class, musician.getId());
        assertEquals(musician.getName(), loadMusician.getName());
    }

    @DisplayName("Album attributes could be updated ")
    @Test
    public void updateAlbumInfo()
    {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate(album);

        album.setAlbumName("When Will the Blues Leave");
        album.setReleaseYear(1990);
        album.setRecordNumber("ECM 2642");

        Album loadedAlbum = dao.load(Album.class, album.getId());
        assertEquals(album.getAlbumName(), loadedAlbum.getAlbumName());
        assertEquals(album.getReleaseYear(), loadedAlbum.getReleaseYear());
        assertEquals(album.getRecordNumber(), loadedAlbum.getRecordNumber());
    }

    @DisplayName("Musical Instrument attributes could be updated ")
    @Test
    public void updateMusicalInstrumentInfo()
    {
        MusicalInstrument musicalInstrument = new MusicalInstrument("Violin");
        dao.createOrUpdate(musicalInstrument);

        musicalInstrument.setName("Cello");

        MusicalInstrument loadedAlInstrument = dao.load(MusicalInstrument.class, musicalInstrument.getId());
        assertEquals(musicalInstrument.getName(), loadedAlInstrument.getName());
    }

    @DisplayName("Musician Instrument attributes could be updated ")
    @Test
    public void updateMusicianInstrumentInfo()
    {
        MusicianInstrument musicianInstrument = new MusicianInstrument(new Musician("Gary Peacock"),
                Sets.newHashSet(new MusicalInstrument("Guitar")));
        dao.createOrUpdate(musicianInstrument);

        musicianInstrument.setMusician(new Musician("Keith Jarrett"));
        musicianInstrument.setMusicalInstruments(Sets.newHashSet(new MusicalInstrument("Violin")));

        MusicianInstrument loadedInstrument = dao.load(MusicianInstrument.class, musicianInstrument.getId());
        assertEquals(musicianInstrument.getMusician(), loadedInstrument.getMusician());
        assertEquals(musicianInstrument.getMusicalInstruments(), loadedInstrument.getMusicalInstruments());
    }

    @DisplayName("Review attributes could be updated")
    @Test
    public void updateReviewInfo() throws IOException {
        Review review = new Review(new URL("https://www.garypeacock.org/"), 9.1);
        dao.createOrUpdate(review);

        review.setUrl(new URL("https://www.keithjarrett.org/"));
        review.setRating(8.0);
        review.setReview("This is a good song.");

        Review loadedReview = dao.load(Review.class, review.getId());

        assertEquals(review.getUrl(), loadedReview.getUrl());
        assertEquals(review.getRating(), loadedReview.getRating());
        assertEquals(review.getReview(), loadedReview.getReview());
    }

    @DisplayName("Track attributes could be updated")
    @Test
    public void updateTrackInfo()
    {
        Track track = new Track("KÖLN, JANUARY 24, 1975, PART I", 26.02);
        dao.createOrUpdate(track);

        track.setName("Yesterday Once More");
        track.setLength(5.31);

        Track loadedTrack = dao.load(Track.class, track.getId());

        assertEquals(track.getName(), loadedTrack.getName());
        assertEquals(track.getLengthInMinutes(), loadedTrack.getLengthInMinutes());
    }

    @DisplayName("Web Page URL could be updated ")
    @Test
    public void updateWebPageInfo() throws IOException {
        URL url = new URL("https://www.garypeacock.org/");
        Webpage webpage = new Webpage("Gary Peacock", url);
        dao.createOrUpdate(webpage);

        webpage.setUrl(new URL("https://www.keithjarrett.org/"));

        Webpage loadedWebPage = dao.load(Webpage.class, webpage.getId());
        assertEquals(webpage.getUrl(), loadedWebPage.getUrl());
    }

    // Validate if the instruments could be updated
    @DisplayName("MusicalInstrument could be updated")
    @Test
    public void InstrumentCouldBeUpdate(){
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");

        dao.createOrUpdate(musicalInstrument);

        musicalInstrument.setName("Flute");

        MusicalInstrument loadMusicalInstrument = dao.load(MusicalInstrument.class, musicalInstrument.getId());
        assertEquals(musicalInstrument.getName(), loadMusicalInstrument.getName());
    }

    /*
     Testing deleting musician would not delete the album together
     */
    @Test
    public void deletingMusicianWithoutAlbum() throws IOException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);

        assertNotNull(dao.load(Musician.class, musician.getId()));
        assertNotNull(dao.loadAll(Album.class));

        dao.delete(musician);

        assertNull(dao.load(Musician.class, musician.getId()));
        assertNotNull(dao.load(Album.class, album.getId()));
        //assertTrue(dao.loadAll(Musician.class).isEmpty());
        //assertFalse(dao.loadAll(Album.class).isEmpty());
    }

    //Validate if remove MusicalInstrument successfully
    @Test
    public void deleteMusicalInstrument() throws IOException {
        MusicalInstrument musicalInstrument = new MusicalInstrument("Mozart");

        dao.createOrUpdate(musicalInstrument);

        assertNotNull(dao.load(MusicalInstrument.class, musicalInstrument.getId()), "musicalInstrument saved");

        dao.delete(musicalInstrument);

        assertNull(dao.load(MusicalInstrument.class, musicalInstrument.getId()));
    }

    @Test
    public void deleteMusicianInstrument() throws IOException {
        Musician musician = new Musician("Mozart");
        MusicianInstrument musicianInstrument = new MusicianInstrument(musician,
                Sets.newHashSet(new MusicalInstrument("Guitar")));

        //dao.createOrUpdate(musician);
        dao.createOrUpdate(musicianInstrument);

        //assertNotNull(dao.load(Musician.class, musician.getId()));
        assertNotNull(dao.load(MusicianInstrument.class, musicianInstrument.getId()));

        dao.delete(musicianInstrument);

        //assertNotNull(dao.load(Musician.class, musician.getId()));
        assertNull(dao.load(MusicianInstrument.class, musicianInstrument.getId()));

    }

    /*
    Deleting the created album would be successful
     */
    @DisplayName("Deleting created Album would be successful")
    @Test
    public void successfulDeleteAlbum()
    {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate(album);

        assertNotNull(dao.loadAll(Album.class));

        dao.delete(album);
        assertNull(dao.load(Album.class, album.getId()));
    }

    /*
    Deleting the created track would be successful
     */
    @DisplayName("Deleting created track successfully")
    @Test
    public void successfulDeleteTrack()
    {
        Track track = new Track("KÖLN, JANUARY 24, 1975, PART I", 26.02);
        dao.createOrUpdate(track);

        assertNotNull(dao.loadAll(Track.class));

        dao.delete(track);
        //assertNull(dao.load(Track.class, track.getId()));
        assertEquals(0,dao.loadAll(Track.class).size());
    }

    /*
    Deleting the created web page would be successful
     */
    @DisplayName("Deleting created web page successfully")
    @Test
    public void successfulDeleteWebPage() throws IOException {
        URL url = new URL("https://www.garypeacock.org/");
        Webpage webpage = new Webpage("Gary Peacock", url);
        dao.createOrUpdate(webpage);

        assertNotNull(dao.loadAll(Webpage.class));

        dao.delete(webpage);
        assertNull(dao.load(Webpage.class, webpage.getId()));
        assertEquals(0,dao.loadAll(Webpage.class).size());
    }

    @DisplayName("Validate if the Review could be deleted successfully")
    @Test
    public void successfulDeleteReview() throws IOException {
        URL websiteUrl = new URL("https://www.garypeacock.org/");
        String review = new String("The song is nice!");
        double ratingOutOf100 = new Double(9.1);

        dao.createOrUpdate(review);

        assertNotNull(dao.loadAll(Review.class));

        dao.delete(review);
        assertEquals(0,dao.loadAll(Review.class).size());
    }

    @Test
    public void searchMusicianByName()
    {
        Musician musician = new Musician("Keith Jarrett");
        dao.createOrUpdate(musician);

        Musician foundMusician = dao.findMusicianByName(musician.getName());

        assertEquals(musician, foundMusician);
    }

    // Validate if instrument could be searched by its name
    @Test
    public void searchMusicalInstrumentByName(){
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        dao.createOrUpdate(musicalInstrument);

        MusicalInstrument findMusicalInstrument = dao.findMusicalInstrumentByName(musicalInstrument.getName());

        assertEquals(musicalInstrument, findMusicalInstrument);
    }

    @Test
    public void searchAlbumByAlbumName() {
        Album album = new Album(1975,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByAlbumName(album.getAlbumName());

        assertEquals(album, findAlbum);
    }

    @Test
    public void searchAlbumByReleaseYear() {
        Album album = new Album(1985,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByReleaseYear(album.getReleaseYear());

        assertEquals(album, findAlbum);
    }

    @Test
    public void searchAlbumByGenre() {
        Album album = new Album(1995,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByGenre(album.getGenre());

        assertEquals(album, findAlbum);
    }

    @Test
    public void searchAlbumByStyle() {
        Album album = new Album(1990,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByStyle(album.getStyle());

        assertEquals(album, findAlbum);
    }

    @Test
    public void searchTrackByName() {
        Track track = new Track("Yesterday Once More",5.29);
        dao.createOrUpdate((track));

        Track findTrack = dao.findTrackByName(track.getName());

        assertEquals(track, findTrack);
    }

}

//creation and loading of every entity

//deletion of review

//search
//Album: search by albumname, search by release year, search by genre, search by style
//Musicalinstrument: search by musicalinstrument name
//Musician: search by musicianname,
//track: search by name,

//do we need to update for every entity, do we need to save for every entity
//ask if the objects in ecmminer are created properly
//what to do about url converter?
//do we need to write searchbyname in dao file as well?
//what do you mean by integration testing approach? Like bottom up and top down etc?


//Tasks:
//Daniel: Search, deletion of review
//Shuming: Test cases for ecm miner, update
//Shridhar: Most talented musician, test cases for ecm miner for integration testing