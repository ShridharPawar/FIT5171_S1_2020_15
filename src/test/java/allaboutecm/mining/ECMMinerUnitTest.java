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
import org.junit.jupiter.api.DisplayName;
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
    private MusicianInstrument musicianInstrument1;
    private MusicianInstrument musicianInstrument2;
    private MusicianInstrument musicianInstrument3;
    private MusicianInstrument musicianInstrument4;
    private MusicianInstrument musicianInstrument5;


    @BeforeEach
    public void setUp() {
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
    }

    /**
     * Most prolific musician test cases
     */
    @ParameterizedTest
    @ValueSource(ints = {-10, -1, 0})
    public void mostProlificMusicianReturnEmptyWithInvalidK(int arg)
    {
        musician1.setAlbums(Sets.newHashSet(album1));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(arg, 1960, 2010);
        assertEquals(0, musicians.size());
    }

    @Test
    public void nullIsPassedToProlific() {
        when(dao.loadAll(Musician.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostProlificMusicians(4, -1, -1));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @Test
    public void mostProlificMusicianReturnEmptyWithInvalidStartAndEndYears()
    {
        musician1.setAlbums(Sets.newHashSet(album2));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(1, 2018, 1970);
        assertEquals(0, musicians.size());
    }

    @Test
    public void mostProlificMusicianReturnExistingMusiciansWhileKOverTheCollection()
    {
        musician1.setAlbums(Sets.newHashSet(album1, album2, album3));
        musician2.setAlbums(Sets.newHashSet(album3, album4, album5));

        when((dao.loadAll(Musician.class))).thenReturn(Sets.newHashSet(musician1, musician2));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(3, 1970, 2018);
        assertEquals(2, musicians.size());
    }
    //These two might be same
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
    public void shouldReturnZeroSizeWhenNoMusiciansAreLoaded()
    {
        List<Musician> musicians = ecmMiner.mostProlificMusicians(-1, -1, -1);
        assertEquals(0, musicians.size());
    }


    /*actual = 0, or do we need this one
    @DisplayName("The result would be k while the existing prolific musicians are more than k value")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4})
    public void mostProlificMusicianReturnKValueWhileKLessThanMusicians(int arg)
    {
        musician1.setAlbums(Sets.newHashSet(album1, album2));
        musician2.setAlbums(Sets.newHashSet(album3, album4));
        musician3.setAlbums(Sets.newHashSet(album1, album3));
        musician4.setAlbums(Sets.newHashSet(album2, album4));
        musician5.setAlbums(Sets.newHashSet(album3, album5));

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician2, musician3, musician4, musician5));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(arg,1960, 2019);
        assertEquals(arg, musicians.size());
    }*/

    /**
     * Busiest year test cases
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

    @DisplayName("Busiest year would not exceed the existing albums")
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 10})
    public void busiestYearReturnK(int arg)
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1, album2, album3, album4, album5));

        List<Integer> busiestYears = ecmMiner.busiestYears(arg);
        assertEquals(3, busiestYears.size());
    }

    @DisplayName("Returned busiest year would be the number of existing albums when k over it")
    @Test
    public void shouldReturnTheBusiestYearWhenOnlyOne()
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1));
        List<Integer> years = ecmMiner.busiestYears(999);
        assertEquals(1,years.size());
        assertTrue(years.contains(album1.getReleaseYear()));
    }

    @DisplayName("Returned null busiest year while there is no album existing")
    @Test
    public void busiestYearWhenNullPassed()
    {
        when(dao.loadAll(Album.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.busiestYears(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @DisplayName("Invalid k would return null busiest years collection")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    public void busiestYearWhenNegativeOrZeroParameterPassed(int arg)
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4,album5));
        List<Integer> years = ecmMiner.busiestYears(arg);
        assertEquals(0,years.size());
    }

    /**
     *Most Talented Musician Test cases
     */
    @DisplayName("Invalid k for most talented musicians would return null")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    public void mostTalentedMusicianReturnNullWithInvalidK(int arg)//change the method name
    {
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument3));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician3));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        assertEquals(0, musicians.size());
    }

    @DisplayName("When k surpass the existed musicians, it would return existing musicians only")
    @ParameterizedTest
    @ValueSource(ints = {3, 4, 10})
    public void mostTalentedMusicianReturnExistingMusiciansWhenKExceedIt(int arg)
    {
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Guitar"),new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(musician2, Sets.newHashSet(new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));

        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument1, musicianInstrument2));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician2));

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        assertEquals(2, musicians.size());
    }

    @DisplayName("When existing musicians surpass the k, it would return k musicians only")
    @ParameterizedTest
    @ValueSource(ints = {1, 3, 4})
    public void mostTalentedMusicianReturnKMusiciansWhenExistingMusiciansExceedK(int arg)
    {
        MusicianInstrument musicianInstrument1 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Guitar"),new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument2 = new MusicianInstrument(musician2, Sets.newHashSet(new MusicalInstrument("Piano"),new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument3 = new MusicianInstrument(musician3, Sets.newHashSet(new MusicalInstrument("Drums")));
        MusicianInstrument musicianInstrument4 = new MusicianInstrument(musician4, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        MusicianInstrument musicianInstrument5 = new MusicianInstrument(musician1, Sets.newHashSet(new MusicalInstrument("Synthesizer")));
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument1,musicianInstrument2,musicianInstrument3,musicianInstrument4,musicianInstrument5));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        assertEquals(arg, musicians.size());
    }

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

    @DisplayName("Testing while there is null value in musician and musician instrument")
    @Test
    public void whenNullIsPassedToTalented()
    {
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(null);
        when(dao.loadAll(Musician.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostTalentedMusicians(5));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @DisplayName("positive test for most social musicians")
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

    @DisplayName("Positive test for most social musicians")
    @Test
    public void positiveMostSocialMusicians()
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1,musician2,musician3,musician4,musician5));
        List<Musician> musicians = ecmMiner.mostSocialMusicians(2);
        assertTrue(musicians.contains(musician1));
        assertTrue(musicians.contains(musician2));
        assertEquals(2, musicians.size());
     }

     @DisplayName("Most social musician return null with invalid k value")
     @ParameterizedTest
     @ValueSource(ints = {-20, -1, 0})
     public void mostSocialMusiciansReturnNullWithInvalidK(int arg)
     {
         musician1.setAlbums(Sets.newHashSet(album1,album2));
         musician2.setAlbums(Sets.newHashSet(album1,album2));
         musician3.setAlbums(Sets.newHashSet(album1));
         musician4.setAlbums(Sets.newHashSet(album2));
         album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3));
         album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician4));

         when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1, album2));
         when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician2, musician3, musician4));

         List<Musician> musicians = ecmMiner.mostSocialMusicians(arg);
         assertEquals(0, musicians.size());
     }
    //These two are same, please delete one
    @ParameterizedTest
    @ValueSource(ints = {0,-100})
    public void whenZeroOrNegativeParameterIsPassedToSocial(int arg) {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician5));
        List<Musician> musicians = ecmMiner.mostTalentedMusicians(arg);
        assertEquals(0, musicians.size());
    }

     /*
    @DisplayName("Most social musician return existing musicians when k surpass it")
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 10})
    public void mostSocialMusicianReturnExistingMusiciansWhenKExceed(int arg)
    {
        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album1,album2));
        musician3.setAlbums(Sets.newHashSet(album1));
        album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3));
        album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2));

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1, album2));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician2, musician3));

        List<Musician> musicians = ecmMiner.mostSocialMusicians(arg);
        assertEquals(3, musicians.size());
    }*/

    @DisplayName("Most social musician return k musicians when k less than existing musicians")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void mostSocialMusiciansReturnKMusiciansWhenExistingMusiciansExceedK(int arg)
    {
        musician1.setAlbums(Sets.newHashSet(album1,album2));
        musician2.setAlbums(Sets.newHashSet(album1,album2));
        musician3.setAlbums(Sets.newHashSet(album1));
        musician4.setAlbums(Sets.newHashSet(album2));
        album1.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician3));
        album2.setFeaturedMusicians(Lists.newArrayList(musician1,musician2,musician4));

        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1, album2));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician2, musician3, musician4));

        List<Musician> musicians = ecmMiner.mostSocialMusicians(arg);
        assertEquals(arg, musicians.size());
    }

    @DisplayName("Most social musician return one when there is only one existing musician")
    @Test
    public void shouldReturnTheSocialMusicianWhenThereIsOnlyOne()
    {
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album1,album2,album3,album4));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician5));
        List<Musician> musicians = ecmMiner.mostSocialMusicians(999);
        assertTrue(musicians.contains(musician5));
        assertEquals(1, musicians.size());
    }

    @DisplayName("Testing while there is null value in musician and album")
    @Test
    public void whenNullIsPassedToSocialMusician() {
        when(dao.loadAll(Album.class)).thenReturn(null);
        when(dao.loadAll(Musician.class)).thenReturn(null);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ecmMiner.mostSocialMusicians(2));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    /**
     *Most similar Album Test cases
     */
    @DisplayName("Positive test for most similar albums")
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

    @DisplayName("Return 0 when albums are unique")
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

}