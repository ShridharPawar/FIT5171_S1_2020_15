package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.sound.midi.Instrument;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MusicianInstrumentUnitTest {
    private MusicianInstrument musicianInstrument;


    @BeforeEach
    public void setUp() {
        Set<MusicalInstrument> instruments = new HashSet<>();
        instruments.add(new MusicalInstrument("Guitar"));
        musicianInstrument = new MusicianInstrument(new Musician("Mike Shidona"),instruments);
    }

    @Test
    @DisplayName("Negative test for the constructor.")
    public void testConstructorForMusicianInstrument()
    {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new MusicianInstrument(new Musician("Mike Shidona"),null));
        assertEquals(exception.getMessage(),"Object cannot be null.");
    }

    @Test
    @DisplayName("Should construct Musicianinstrument object.")
    public void shouldConstructMusicianInstrumentObject()
    {
        assertNotNull(musicianInstrument,"Musicianinstrument object should not be null.");
    }

    @Test
    @DisplayName("Musician cannot be null")
    public void musicianCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusician(null));
    }

    @Test
    @DisplayName("Objects are same if the musician name and the instrument are same.")
    public void sameMusicianAndSameMusicianInstrumentPair() {
        Musician newMusician = new Musician("Mike Shidona");
        Set<MusicalInstrument> instruments = new HashSet<>();
        instruments.add(new MusicalInstrument("Guitar"));
        MusicianInstrument newMusicianInstrument = new MusicianInstrument(newMusician,instruments);
        assertEquals(musicianInstrument, newMusicianInstrument);
    }

    @Test
    @DisplayName("Positive test case to set Musician.")
    public void positiveMusician() {
        Musician musician = new Musician("Chester Bennington");
        musicianInstrument.setMusician(musician);
        assertEquals(musicianInstrument.getMusician(),musician);
    }

    @Test
    @DisplayName("Musical instrument cannot be null.")
    public void musicalInstrumentCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusicalInstruments(null));
    }

    @Test
    @DisplayName("Object in set cannot be null.")
    public void objectInMusicalInstrumentCannotBeNull() {
        Set<MusicalInstrument> instruments = new HashSet<>();
        instruments.add(null);
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusicalInstruments(instruments));
    }

    @Test
    @DisplayName("Positive test case to set musical instrument.")
    public void positiveMusicalInstrument() {
        Set<MusicalInstrument> instruments = new HashSet<>();
        instruments.add(new MusicalInstrument("Guitar"));
        musicianInstrument.setMusicalInstruments(instruments);
        assertEquals(musicianInstrument.getMusicalInstruments(),instruments);
    }




}
