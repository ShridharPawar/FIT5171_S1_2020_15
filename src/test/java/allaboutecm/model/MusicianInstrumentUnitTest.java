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
    @DisplayName("Musician cannot be null")
    public void validMusician() {
        Musician musician = new Musician("Chester");
        musicianInstrument.setMusician(musician);
        assertEquals(musicianInstrument.getMusician(),musician);
    }

    @Test
    @DisplayName("Musician cannot be null")
    public void musicalInstrumentCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusicalInstrument(null));
    }

    @Test
    @DisplayName("Musician cannot be null")
    public void validMusicalInstrument() {
        MusicalInstrument musicalInstrument = new MusicalInstrument("Piano");
        musicianInstrument.setMusicalInstrument(musicalInstrument);
        assertEquals(musicianInstrument.getMusicalInstrument(),musicalInstrument);
    }




}
