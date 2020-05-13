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

    @BeforeEach
    public void setUp() {

        dao = mock(Neo4jDAO.class);
        ecmMiner = new ECMMiner(dao);
    }

    @Test
    public void mostTalentedMusician(){
        Musician musician1 = new Musician("Mozart");
        Musician musician2 = new Musician("Beethoven");
        Musician musician3 = new Musician("Bach");
        Musician musician4 = new Musician("Chopin");
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Guitar"),new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(musician2, Sets.newHashSet(new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument4 = new MusicianInstrument(musician4, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument1,musicianInstrument2,musicianInstrument3,musicianInstrument4));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(1);
    }


    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOne() {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }

    @Test
    public void shouldReturnZeroSizeWhenNoMusiciansAreLoaded() {
       List<Musician> musicians = ecmMiner.mostProlificMusicians(-1, -1, -1);
        assertEquals(0, musicians.size());
     }

   @Test
    public void mostSocialMusicians()
    {
        Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Album album2 = new Album(2018, "ECM 1064/66", "Meteora");
        Album album3 = new Album(2014, "ECM 1064/67", "Minutes to Midnight");
        Album album4 = new Album(2015, "ECM 1064/68", "Gasolina");
        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Mike Shinoda");
        Musician musician3 = new Musician("Chester Bennington");
        Musician musician4 = new Musician("Bon Jovi");
        Musician musician5 = new Musician("Chris Martin");
        Musician musician6 = new Musician("Daddy Yanky");
        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album1,album2));
        musician3.setAlbums(Sets.newHashSet(album1));
        musician4.setAlbums(Sets.newHashSet(album2));
        musician5.setAlbums(Sets.newHashSet(album3));
        musician6.setAlbums(Sets.newHashSet(album1,album4));
        album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3,musician6));
        album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician4));
        album3.setFeaturedMusicians(Lists.newArrayList(musician5));
        album4.setFeaturedMusicians(Lists.newArrayList(musician6));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4,musician5,musician6));
        List<Musician> musicians = ecmMiner.mostSocialMusicians(6);
        int a=1;
       }

     @Test
     public void mostSimilarAlbums()
     {
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
         when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3));
         List<Album> albums = ecmMiner.mostSimilarAlbums(2,albumToBeChecked);
         List<Album> expectedAlbums = Lists.newArrayList(new Album(2016, "ECM 1064/66", "Meteora"),new Album(2017, "ECM 1064/67", "Minutes to midnight"));
         assertEquals(albums.size(),2);
     }

    @Test
    public void busiestYear()
     {
         Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
         Album album2 = new Album(2016, "ECM 1064/66", "Meteora");
         Album album3 = new Album(2016, "ECM 1064/67", "Minutes to midnight");
         Album album4 = new Album(1975, "ECM 1064/68", "Shadow of the day");
         Album album5 = new Album(1974, "ECM 1064/69", "Gasolina");
         when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5));
         List<Integer> years = ecmMiner.busiestYears(2);
         List<Integer> expectedYears = Lists.newArrayList(2016,1975);
         assertEquals(years,expectedYears);

     }

}