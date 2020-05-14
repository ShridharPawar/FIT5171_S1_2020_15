package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @BeforeEach
    public void setUp() {
        dao = mock(Neo4jDAO.class);
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5));
        List<Integer> years = ecmMiner.busiestYears(2);
        List<Integer> expectedYears = Lists.newArrayList(2016,1975);
        assertEquals(years,expectedYears);
    }

    @Test
    public void shouldReturnTheBusiestYearWhenOnlyOne()
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Integer> years = ecmMiner.busiestYears(2);
        assertEquals(1,years.size());
        assertTrue(years.contains(album1.getReleaseYear()));
    }

    @Test
    public void busiestYearWhenNullPassed()
    {
        when(dao.loadAll(Album.class)).thenReturn(null);
        List<Integer> years = ecmMiner.busiestYears(2);
        assertEquals(years.size(),0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0,-100})
    public void busiestYearWhenNegativeOrZeroParameterPassed(int arg)
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5));
        List<Integer> years = ecmMiner.busiestYears(arg);
        assertEquals(years.size(),0);
    }

    @Test
    public void mostTalentedMusician()
    {
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Guitar"),new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(musician2, Sets.newHashSet(new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Drums")));
        MusicianInstrument musicianInstrument4 = new MusicianInstrument(musician4, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        MusicianInstrument musicianInstrument5 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument1,musicianInstrument2,musicianInstrument3,musicianInstrument4,musicianInstrument5));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(2);
        assertEquals(2, musicians.size());
        assertTrue(musicians.equals(Lists.newArrayList(musician1,musician2)));
    }


    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOne()
    {
        musician1.setAlbums(Sets.newHashSet(album1));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);
        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician1));
    }

    @Test
    public void shouldReturnZeroSizeWhenNoMusiciansAreLoaded() {
       List<Musician> musicians = ecmMiner.mostProlificMusicians(-1, -1, -1);
        assertEquals(0, musicians.size());
     }

    @Test
    public void mostSocialMusicians()
    {
        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album1,album2));
        musician3.setAlbums(Sets.newHashSet(album1));
        musician4.setAlbums(Sets.newHashSet(album2));
        musician5.setAlbums(Sets.newHashSet(album3));
        album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3));
        album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician4));
        album3.setFeaturedMusicians(Lists.newArrayList(musician5));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4,musician5));
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
        assertEquals(2, musicians.size());
       }

     @Test
     public void mostSimilarAlbums()
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



}