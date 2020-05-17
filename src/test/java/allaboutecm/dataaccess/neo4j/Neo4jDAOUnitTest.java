package allaboutecm.dataaccess.neo4j;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.org.apache.regexp.internal.RE;
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

    /**
     * To Validate if there is data in dao.
     */
    @Test
    public void daoIsNotEmpty() {
        assertNotNull(dao);
    }

    /**
     * To Validate if musician is created successfully.
     */
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
    }

    /**
     * To Validate if musician and their album are created successfully.
     */
    @Test
    public void successfulCreationOfMusicianAndAlbum() throws IOException {
        assertEquals(0,dao.loadAll(Musician.class).size());
        assertEquals(0,dao.loadAll(Album.class).size());
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

    /**
     * To Validate if musician and their webpage are created successfully.
     */
    @Test
    public void successfulCreationOfMusicianAndWebPages() throws IOException{
        assertEquals(0,dao.loadAll(Musician.class).size());
        assertEquals(0,dao.loadAll(Webpage.class).size());
        URL url = new URL("https://www.garypeacock.org/");
        Webpage webpage = new Webpage("Gary Peacock", url);
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        musician.setWebpages(Sets.newHashSet(webpage));
        dao.createOrUpdate(webpage);
        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());
        Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getWebpages(), loadedMusician.getWebpages());
    }

    /**
     * To Validate if the album are created successfully.
     */
    @Test
    public void successfulCreationAndLoadingOfAlbum(){
        assertEquals(0, dao.loadAll(Album.class).size());

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");

        dao.createOrUpdate(album);
        Album loadedAlbum = dao.load(Album.class, album.getId());

        assertNotNull(loadedAlbum.getId());
        assertEquals(album, loadedAlbum);
        assertEquals(1, dao.loadAll(Album.class).size());
    }

    /**
     * To Validate if musician and their album are created successfully.
     */
    @Test
    public void successfulCreationOfAlbumAndFeaturedMusicians() throws IOException{
        assertEquals(0,dao.loadAll(Musician.class).size());
        assertEquals(0,dao.loadAll(Album.class).size());
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        album.setFeaturedMusicians(Lists.newArrayList(musician));
        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getFeaturedMusicians(), loadedAlbum.getFeaturedMusicians());
    }

    /**
     * To Validate if album and their instrument are created successfully.
     */
    @Test
    public void successfulCreationOfAlbumAndInstruments(){
        assertEquals(0,dao.loadAll(MusicianInstrument.class).size());
        assertEquals(0,dao.loadAll(Album.class).size());
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        MusicianInstrument musicianInstrument = new MusicianInstrument(new Musician("Keith Jarrett"), Sets.newHashSet(
                new MusicalInstrument("Violin"),
                new MusicalInstrument("Guitar")));
        album.setInstruments(Sets.newHashSet(musicianInstrument));
        dao.createOrUpdate(album);
        dao.createOrUpdate(musicianInstrument);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getInstruments(), loadedAlbum.getInstruments());
    }

    /**
     * To Validate if album and its track are created successfully.
     */
    @Test
    public void successfulCreationOfAlbumAndTracks()
    {
        assertEquals(0,dao.loadAll(Track.class).size());
        assertEquals(0,dao.loadAll(Album.class).size());
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Track track = new Track("Yesterday Once More", 5.31);
        album.setTracks(Sets.newHashSet(track));
        dao.createOrUpdate(album);
        dao.createOrUpdate(track);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getTracks(), loadedAlbum.getTracks());
    }

    /**
     * To Validate if album and it review are created successfully.
     */
    @Test
    public void successfulCreationOfAlbumAndReviews() throws IOException
    {
        assertEquals(0,dao.loadAll(Review.class).size());
        assertEquals(0,dao.loadAll(Album.class).size());
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        URL url = new URL("https://www.sputnikmusic.com/review/48517/Linkin-Park-Meteora/");
        Review review = new Review(url,98);
        album.setReviews(Sets.newHashSet(review));
        dao.createOrUpdate(album);
        dao.createOrUpdate(review);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getReviews(), loadedAlbum.getReviews());
    }

    /**
     * To Validate if album and its concerts are created successfully.
     */
    @Test
    public void successfulCreationOfAlbumAndConcerts() throws IOException
    {
        assertEquals(0,dao.loadAll(Concert.class).size());
        assertEquals(0,dao.loadAll(Album.class).size());
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Concert concert = new Concert("International festival","India");
        album.setConcerts(Sets.newHashSet(concert));
        dao.createOrUpdate(album);
        dao.createOrUpdate(concert);
        Collection<Album> albums = dao.loadAll(Album.class);
        assertEquals(1, albums.size());
        Album loadedAlbum = albums.iterator().next();
        assertEquals(album, loadedAlbum);
        assertEquals(album.getConcerts(), loadedAlbum.getConcerts());
    }

    /**
     * To Validate if musicalInstrument are created successfully.
     */
    @Test
    public void successfulCreationOfMusicianInstrument()
    {
        assertEquals(0,dao.loadAll(Musician.class).size());
        assertEquals(0,dao.loadAll(MusicalInstrument.class).size());
        assertEquals(0,dao.loadAll(MusicianInstrument.class).size());
        Musician musician = new Musician("Keith Jarrett");
        HashSet<MusicalInstrument> musicalInstruments = Sets.newHashSet(
                new MusicalInstrument("Violin"),
                new MusicalInstrument("Guitar"));
        MusicianInstrument musicianInstrument = new MusicianInstrument(musician, musicalInstruments);
        dao.createOrUpdate(musicianInstrument);
        MusicianInstrument loadedMusicianInstrument = dao.load(MusicianInstrument.class, musicianInstrument.getId());
        assertNotNull(loadedMusicianInstrument);
        assertEquals(musicianInstrument, loadedMusicianInstrument);
        assertEquals(1,dao.loadAll(MusicianInstrument.class).size());
     }

    /**
     * To Validate if the musicalInstrument could be created and loaded successfully.
     */
    @Test
    public void successfulCreationAndLoadingOfMusicalInstrument()
    {

        assertEquals(0, dao.loadAll(MusicalInstrument.class).size());

        MusicalInstrument musicalInstrument = new MusicalInstrument("Flute");

        dao.createOrUpdate(musicalInstrument);
        MusicalInstrument loadedMusicalInstrument = dao.load(MusicalInstrument.class, musicalInstrument.getId());

        assertNotNull(loadedMusicalInstrument.getId());
        assertEquals(musicalInstrument, loadedMusicalInstrument);

        assertEquals(1, dao.loadAll(MusicalInstrument.class).size());
    }

    /**
     * To Validate if the Track could be created and loaded to database successfully
     */
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

    /**
     * To Validate if the WebPage could be created and loaded to database successfully
     */
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

    /**
     * To Validate if the Review could be created and loaded to database successfully
     */
    @Test
    public void successfulCreationAndLoadingOfReview() throws IOException{
        assertEquals(0,dao.loadAll(Review.class).size());
        URL url = new URL("https://www.sputnikmusic.com/review/48517/Linkin-Park-Meteora/");
        Review review = new Review(url,98);
        dao.createOrUpdate(review);
        Review loadedReview = dao.load(Review.class,review.getId());
        assertEquals(review,loadedReview);
        assertEquals(1, dao.loadAll(Review.class).size());
    }

    /**
     * To Validate if the Concert could be created and loaded to database successfully
     */
    @Test
    public void successfulCreationAndLoadingOfConcert(){
        assertEquals(0,dao.loadAll(Concert.class).size());
        Concert concert = new Concert("International Tokyo festival","Tokyo");
        dao.createOrUpdate(concert);
        Concert loadedConcert = dao.load(Concert.class,concert.getId());
        assertEquals(concert,loadedConcert);
        assertEquals(1, dao.loadAll(Concert.class).size());
    }

    /**
     * To Validate if the Concert and corresponding Musician could be created and loaded to database successfully
     */
    @Test
    public void successfulCreationOfConcertAndMusician() throws IOException
    {
        assertEquals(0,dao.loadAll(Concert.class).size());
        assertEquals(0,dao.loadAll(Musician.class).size());
        Musician musician = new Musician("Chester Bennington");
        Concert concert = new Concert("International festival","India");
        concert.setMusicians(Sets.newHashSet(musician));
        dao.createOrUpdate(musician);
        dao.createOrUpdate(concert);
        Collection<Concert> concerts = dao.loadAll(Concert.class);
        assertEquals(1, concerts.size());
        Concert loadedConcert = concerts.iterator().next();
        assertEquals(concert, loadedConcert);
        assertEquals(concert.getMusicians(), loadedConcert.getMusicians());
    }

    /**
     * To Validate if the musician information could be updated to database successfully
     */
    @DisplayName("Musician attribute could be updated")
    @Test
    public void updatingMusicianInfo() throws IOException
    {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        dao.createOrUpdate(musician);
        musician.setName("Eli Granger");
        Musician loadMusician = dao.load(Musician.class, musician.getId());
        assertEquals(musician.getName(), loadMusician.getName());
    }

    /**
     * To Validate if the Album information could be updated to database successfully
     */
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

    /**
     * To Validate if the Instrument infoamtion could be updated to database successfully
     */
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

    /**
     * To Validate if the Musician Instrumrnt could be created and loaded to database successfully
     */
    @DisplayName("Musician Instrument attributes could be updated")
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

    /**
     * To Validate if the Review infoamtion could be updated to database successfully
     */
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

    /**
     * To Validate if the Track infoamtion could be updated to database successfully
     */
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

    /**
     * To Validate if the Page URL infoamtion could be updated to database successfully
     */
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

    /**
     * To Validate if the Concert infoamtion could be updated to database successfully
     */
    @DisplayName("Concert attributes could be updated")
    @Test
    public void updateConcertInfo()
    {
        Concert concert = new Concert("International ultrasonic festival","Tokyo");
        dao.createOrUpdate(concert);
        concert.setCountry("India");
        concert.setCity("Mumbai");
        Concert loadedConcert = dao.load(Concert.class, concert.getId());
        assertEquals(concert.getCountry(), loadedConcert.getCountry());
        assertEquals(concert.getCity(), loadedConcert.getCity());
    }

    /**
     * To Validate if the musician could be deleted without their
     * album removal from database successfully
     */
    @Test
    public void deletingMusicianWithoutAlbum() throws IOException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        Musician musician1 = new Musician("Mike Shinoda");
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(musician);
        dao.createOrUpdate(musician1);
        dao.createOrUpdate(album);

        assertNotNull(dao.load(Musician.class, musician.getId()));
        assertNotNull(dao.loadAll(Album.class));

        dao.delete(musician);

        assertNull(dao.load(Musician.class, musician.getId()));
        assertNotNull(dao.load(Album.class, album.getId()));
    }

    /**
     * To Validate if the musicalInstrument could be deleted from database successfully
     */
    @Test
    public void deleteMusicalInstrument() throws IOException {
        MusicalInstrument musicalInstrument = new MusicalInstrument("Mozart");

        dao.createOrUpdate(musicalInstrument);

        assertNotNull(dao.load(MusicalInstrument.class, musicalInstrument.getId()), "musicalInstrument saved");

        dao.delete(musicalInstrument);

        assertNull(dao.load(MusicalInstrument.class, musicalInstrument.getId()));
    }

    /**
     * To Validate if the musicianInstrument could be deleted from database successfully
     */
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

    /**
     * To Validate if the album could be deleted from database successfully
     */
    @DisplayName("Deleting created Album would be successful")
    @Test
    public void successfullyDeleteAlbum()
    {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate(album);

        assertNotNull(dao.loadAll(Album.class));

        dao.delete(album);
        assertNull(dao.load(Album.class, album.getId()));
    }

    /**
     * To Validate if created Track could be deleted from database successfully
     */
    @DisplayName("Deleting created track successfully")
    @Test
    public void successfullyDeleteTrack()
    {
        Track track = new Track("KÖLN, JANUARY 24, 1975, PART I", 26.02);
        dao.createOrUpdate(track);

        assertNotNull(dao.loadAll(Track.class));

        dao.delete(track);
        assertNull(dao.load(Track.class, track.getId()));

    }

    /**
     * To Validate if created Webpage could be deleted from database successfully
     */
    @DisplayName("Deleting created web page successfully")
    @Test
    public void successfullyDeleteWebPage() throws IOException {
        URL url = new URL("https://www.garypeacock.org/");
        Webpage webpage = new Webpage("Gary Peacock", url);
        dao.createOrUpdate(webpage);

        assertNotNull(dao.loadAll(Webpage.class));

        dao.delete(webpage);
        assertNull(dao.load(Webpage.class, webpage.getId()));
        assertEquals(0,dao.loadAll(Webpage.class).size());
    }

    /**
     * To Validate if created Review could be deleted from database successfully
     */
    @DisplayName("Validate if the Review could be deleted successfully")
    @Test
    public void successfullyDeleteReview() throws IOException {
        URL websiteUrl = new URL("https://www.garypeacock.org/");
        double ratingOutOf100 = new Double(9.1);
        Review review = new Review(websiteUrl,ratingOutOf100);
        dao.createOrUpdate(review);

        assertNotNull(dao.loadAll(Review.class));

        dao.delete(review);
        assertNull(dao.load(Review.class, review.getId()));
        assertEquals(0,dao.loadAll(Review.class).size());
    }

    /**
     * To Validate if created Concert could be deleted from database successfully
     */
    @DisplayName("Deleting created concert successfully.")
    @Test
    public void successfullyDeleteConcert()
    {
        Concert concert = new Concert("International Tokyo Festival","Japan");
        dao.createOrUpdate(concert);
        assertNotNull(dao.loadAll(Concert.class));
        dao.delete(concert);
        assertNull(dao.load(Concert.class, concert.getId()));
    }

    /**
     * To Validate if created Musician could be queried by name from database.
     */
    @Test
    public void searchMusicianByName()
    {
        Musician musician = new Musician("Keith Jarrett");
        dao.createOrUpdate(musician);

        Musician foundMusician = dao.findMusicianByName(musician.getName());

        assertEquals(musician, foundMusician);
    }

    /**
     * To Validate if created MusicalInstrument could be queried by name from database.
     */
    @Test
    public void searchMusicalInstrumentByName(){
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        dao.createOrUpdate(musicalInstrument);

        MusicalInstrument findMusicalInstrument = dao.findMusicalInstrumentByName(musicalInstrument.getName());

        assertEquals(musicalInstrument, findMusicalInstrument);
    }

    /**
     * To Validate if created Album could be queried by name from database.
     */
    @Test
    public void searchAlbumByAlbumName() {
        Album album = new Album(1975,"ECM 1064/65", "The Köln Concert");
        Album album1 = new Album(1976,"ECM 1064/66", "Meteora");
        dao.createOrUpdate((album));
        dao.createOrUpdate((album1));

        Album findAlbum = dao.findAlbumByAlbumName(album.getAlbumName());

        assertEquals(album, findAlbum);
    }

    /**
     * To Validate if created Album could be queried by its release year from database.
     */
    @Test
    public void searchAlbumByReleaseYear() {
        Album album = new Album(1985,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByReleaseYear(album.getReleaseYear());

        assertEquals(album, findAlbum);
    }

    /**
     * To Validate if created Album could be queried by its genre from database.
     */
    @Test
    public void searchAlbumByGenre() {
        Album album = new Album(1995,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByGenre(album.getGenre());

        assertEquals(album, findAlbum);
    }

    /**
     * To Validate if created Album could be queried by its record number from database.
     */
    @Test
    public void searchAlbumByRecordNumber() {
        Album album = new Album(1995,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByRecordNumber(album.getRecordNumber());

        assertEquals(album, findAlbum);
    }

    /**
     * To Validate if created Album could be queried by its style from database.
     */
    @Test
    public void searchAlbumByStyle() {
        Album album = new Album(1990,"ECM 1064/65", "The Köln Concert");
        dao.createOrUpdate((album));

        Album findAlbum = dao.findAlbumByStyle(album.getStyle());

        assertEquals(album, findAlbum);
    }

    /**
     * To Validate if created Track could be queried by its name from database.
     */
    @Test
    public void searchTrackByName() {
        Track track = new Track("Yesterday Once More",5.29);
        dao.createOrUpdate((track));

        Track findTrack = dao.findTrackByName(track.getName());

        assertEquals(track, findTrack);
    }

    /**
     * To Validate if created Concert could be queried by its name from database.
     */
    @Test
    public void searchConcertByName() {
        Concert concert = new Concert("Tokyo Festival","Japan");
        dao.createOrUpdate((concert));
        Concert findConcert = dao.findConcertByName(concert.getConcertName());
        assertEquals(concert, findConcert);
    }

    /**
     * To Validate if created Concert could be queried by its country from database.
     */
    @Test
    public void searchConcertByCountry() {
        Concert concert = new Concert("Tokyo Festival","Japan");
        dao.createOrUpdate((concert));
        Concert findConcert = dao.findConcertByCountry(concert.getCountry());
        assertEquals(concert, findConcert);
    }

    /**
     * To Validate if created Concert could be queried by its city from database.
     */
    @Test
    public void searchConcertByCity() {
        Concert concert = new Concert("Tokyo Festival","Japan");
        concert.setCity("Mumbai");
        dao.createOrUpdate((concert));
        Concert findConcert = dao.findConcertByCity(concert.getCity());
        assertEquals(concert, findConcert);
    }

    /**
     * To Validate if the same musician is saved once in database.
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

    /**
     * To Validate if the same Musical instrument is saved once in database.
     */
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

    /**
     * To Validate if the same album is saved once in database.
     */
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

    /**
     * To Validate if multiple musicians are saved at a time.
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

    /**
     * To Validate if multiple instruments are saved at a time.
     */
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
}