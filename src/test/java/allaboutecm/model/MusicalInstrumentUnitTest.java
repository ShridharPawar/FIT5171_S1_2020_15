package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MusicalInstrumentUnitTest {
      private MusicalInstrument musicalInstrument;

    @BeforeEach
    public void setUp()
    {
        musicalInstrument = new MusicalInstrument("Guitar");
    }

    /**
     * Equivalent class test for name
     * Case 1: name could not be null
     */
    @Test
    @DisplayName("Musical Instrument cannot be null")
    public void musicalInstrumentCannotBeNull()
    {
        assertThrows(NullPointerException.class, () -> musicalInstrument.setName(null));
    }

    /**
     * Equivalent class test for name
     * Case 2: name could not be empty
     */
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Musical Instrument cannot be empty or blank")
<<<<<<< Updated upstream
    public void musicalInstrumentCannotBeEmptyOrBlank(String arg) {
=======
    public void albumNameCannotBeEmptyOrBlank(String arg)
    {
>>>>>>> Stashed changes
        assertThrows(IllegalArgumentException.class, () -> musicalInstrument.setName(arg));
    }

}
