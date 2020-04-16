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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MusicianInstrumentUnitTest {
    private MusicianInstrument musicianInstrument;


    @BeforeEach
    public void setUp() {
        Musician musician = new Musician("Mike Shidona");
        MusicalInstrument musicalInstrument = new MusicalInstrument("Guitar");
        musicianInstrument = new MusicianInstrument(musician,musicalInstrument);
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
        MusicalInstrument newMusicalInstrument = new MusicalInstrument("Guitar");
        MusicianInstrument newMusicianInstrument = new MusicianInstrument(newMusician,newMusicalInstrument);
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
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusicalInstrument(null));
    }

    @Test
    @DisplayName("Positive test case to set musical instrument.")
    public void positiveMusicalInstrument() {
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        musicianInstrument.setMusicalInstrument(musicalInstrument);
        assertEquals(musicianInstrument.getMusicalInstrument(),musicalInstrument);
    }




}
