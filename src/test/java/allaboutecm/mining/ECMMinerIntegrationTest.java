package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private DAO dao;
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

    @BeforeAll
    public static void setUp() {
        Configuration configuration = new Configuration.Builder().build();
        sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        session = sessionFactory.openSession();
    }

    @BeforeEach
    public void beforeEachSetUp() {
        dao = new Neo4jDAO(session);
        ecmMiner = new ECMMiner(dao);
        album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        album2 = new Album(2016, "ECM 1064/66", "Meteora");
        album3 = new Album(2016, "ECM 1064/67", "Minutes to midnight");
        album4 = new Album(1975, "ECM 1064/68", "Shadow of the day");
        album5 = new Album(1974, "ECM 1064/69", "Gasolina");
        musician1 = new Musician("Keith Jarrett");
        musician2 = new Musician("Mike Shinoda");
        musician3 = new Musician("Chester Bennington");
        musician4 = new Musician("Bon Jovi");
        musician5 = new Musician("Chris Martin");
    }



    @Test
    public void busiestYear()
    {
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        List<Integer> years = ecmMiner.busiestYears(2);
        List<Integer> expectedYears = Lists.newArrayList(2016,1975);
        assertEquals(years,expectedYears);
    }

    @Test
    public void mostSimilarAlbums()
    {
        assertEquals(0, dao.loadAll(Musician.class).size());
        Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Album album2 = new Album(2016, "ECM 1064/66", "Meteora");
        Album album3 = new Album(2017, "ECM 1064/67", "Minutes to midnight");
        Album albumToBeChecked = new Album(2014,"ECM 1064/68", "Shadow");
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(new Musician("Joe Vahn"),Sets.newHashSet(new MusicalInstrument("Guitar")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(new Musician("Mike"),Sets.newHashSet(new MusicalInstrument("Guitar")));
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(new Musician("Chester"),Sets.newHashSet(new MusicalInstrument("Drums")));
        MusicianInstrument musicianInstrument4 = new MusicianInstrument(new Musician("Keith"),Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        album1.setInstruments(Sets.newHashSet(musicianInstrument1,musicianInstrument4));
        album2.setInstruments(Sets.newHashSet(musicianInstrument1,musicianInstrument3));
        album3.setInstruments(Sets.newHashSet(musicianInstrument2,musicianInstrument3));
        albumToBeChecked.setInstruments(Sets.newHashSet(musicianInstrument1,musicianInstrument3));
        album1.setGenre("Jazz");
        album2.setGenre("Rock");
        album3.setGenre("Rock");
        albumToBeChecked.setGenre("Rock");
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        List<Album> albums = ecmMiner.mostSimilarAlbums(2,albumToBeChecked);
        assertEquals(albums.size(),2);
    }



}