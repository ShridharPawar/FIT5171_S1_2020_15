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

public class MusicalInstrumentUnitTest {
      private MusicalInstrument musicalInstrument;

    @BeforeEach
    public void setUp() {
        musicalInstrument = new MusicalInstrument("Guitar");
    }

    @Test
    @DisplayName("Should construct Musical instrument object.")
    public void shouldConstructMusicalInstrumentObject()
    {
        assertNotNull(musicalInstrument,"Musical instrument object should not be null.");
    }

    @Test
    @DisplayName("Negative test for the constructor.")
    public void testConstructorForMusicalInstrument()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new MusicalInstrument("Guitarfrfvfvffgfgfgfgfgfgfgfgvffg232rfdvdvfvfvfgfvdvfvfgrfvfvfvfv"));
        assertEquals(exception.getMessage(),"Instrument name length cannot be more than 40 characters.");
    }

    @Test
    @DisplayName("Musical Instrument cannot be null.")
    public void musicalInstrumentCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musicalInstrument.setName(null));
    }

    @Test
    @DisplayName("Objects are same if the musical instruments are same.")
    public void sameNameMeansSameInstrument() {
        MusicalInstrument musInstrument = new MusicalInstrument("Guitar");
        assertEquals(musicalInstrument, musInstrument);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Musical Instrument cannot be empty or blank")
    public void musicalInstrumentCannotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> musicalInstrument.setName(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Guitar version7465747485748574578754599586758675876857685768568"})
    @DisplayName("Musical Instrument name length should not be more than 40 characters.")
    public void musicalInstrumentLength(String arg) {
        assertThrows(IllegalArgumentException.class, () -> musicalInstrument.setName(arg));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Guitar Version 7"})
    @DisplayName("Positive test to check instrument name's length.")
    public void positiveMusicalInstrumentName(String arg) {
        musicalInstrument.setName(arg);
        assertEquals(musicalInstrument.getName(),arg);
    }



}
