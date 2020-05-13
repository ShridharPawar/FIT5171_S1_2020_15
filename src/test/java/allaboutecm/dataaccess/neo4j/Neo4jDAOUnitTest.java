package allaboutecm.dataaccess.neo4j;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.MusicianInstrument;
import allaboutecm.model.Musician;

import com.google.common.collect.Sets;
import allaboutecm.model.Track;
import org.junit.jupiter.api.*;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;
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

    @Test
    public void successfulCreationAndLoadingOfMusicalInstrument()
    {
        //Checking there is no musicalInstrument exist
        assertEquals(0, dao.loadAll(MusicalInstrument.class).size());

        MusicalInstrument musicalInstrument = new MusicalInstrument("Violin");
        dao.createOrUpdate(musicalInstrument);

        MusicalInstrument loadedMusicalInstrument = dao.load(MusicalInstrument.class, musicalInstrument.getId());

        assertNotNull(loadedMusicalInstrument.getId());
        assertEquals(musicalInstrument, loadedMusicalInstrument);

        assertEquals(1, dao.loadAll(MusicalInstrument.class).size());
    }

    //Validate if the musicalInstrument could be created and loaded successfully
    @Test
    public void successfulCreationAndLoadingOfMusicalInstrument() throws IOException {

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
    public void deteleMusicanInstrument() throws IOException {
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

    /*
     Deleting the Musician would delete the instrument with he or she together
     */
    /*@Test
    public void deleteMusicianWithInstrumentTogether()
    {
        Musician musician = new Musician("Keith Jarrett");
        dao.createOrUpdate(musician);

        MusicianInstrument anInstrument = new MusicianInstrument();
        anInstrument.setMusician(musician);
        dao.createOrUpdate(anInstrument);

        assertNotNull(dao.load(Musician.class, musician.getId()));
        assertNotNull(dao.loadAll(MusicianInstrument.class));

        dao.delete(musician);
        dao.delete(anInstrument);
        assertNull(dao.load(Musician.class, musician.getId()));
        //assertNull(dao.load(MusicianInstrument.class, anInstrument.getId()));
    }*?
    /*
     Searching musician from database by name
     */
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
}