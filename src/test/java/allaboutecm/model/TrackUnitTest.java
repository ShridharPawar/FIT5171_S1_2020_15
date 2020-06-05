package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.sound.midi.Instrument;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TrackUnitTest {
    private Track track;

    @BeforeEach
    public void setUp(){
        track = new Track("Numb",4);
    }

    @Test
    @DisplayName("Should construct Track object.")
    public void shouldConstructTrackObject()
    {
        assertNotNull(track,"Track object should not be null.");
    }

    @Test
    @DisplayName("Negative test for the constructor.")
    public void testConstructorForTrack()
    {
        Track track1 = new Track("Shadow of the day",100);
        assertNotNull(track1);
     }

    @Test
    @DisplayName("Negative track length for the constructor.")
    public void testConstructorForTrackNegative()
    {
        Track track1 = new Track("Shadow of the day",1);
        assertNotNull(track1);
    }

    @Test
    @DisplayName("Negative track name length for the constructor.")
    public void testConstructorForTrackNameNegative()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Track("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqdq",4));
        assertEquals(exception.getMessage(),"Name of the track should not exceed 40 characters.");
    }

    @Test
    @DisplayName("Testing for constructor")
    public void testingConstructor()
    {
       Track track1;
       track1 = new Track("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",4);
       assertNotNull(track1);
    }

    @Test
    @DisplayName("Track name cannot be null.")
    public void trackNameCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> track.setName(null));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @Test
    @DisplayName("Objects are same if the track name and its length are same.")
    public void sameNameAndTimeMeansSameTrack() {
        Track track1 = new Track("Numb",4);
        assertEquals(track, track1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Track name cannot be empty or blank.")
    public void trackNameCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setName(arg));
        assertEquals(exception.getMessage(),"Name cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqd"})
    @DisplayName("Track name length positive test case.")
    public void trackNameLength40(String arg1) {

        track.setName(arg1);
        assertEquals(track.getName(),arg1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqdq"})
    @DisplayName("Track name length should not exceed 40 characters.")
    public void trackNameLength41(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setName(arg));
        assertEquals(exception.getMessage(),"Name of the track should not exceed 40 characters.");
    }


    @ParameterizedTest
    @DisplayName("Positive test case for track name.")
    @ValueSource(strings = {"Shadow of the day."})
    public void positiveTrackName(String arg){
        track.setName(arg);
        assertEquals(track.getName(),arg);
    }

    @ParameterizedTest
    @ValueSource(strings = {"26tegre","8.gggggggggg2"})
    @DisplayName("Track length should have just numbers.")
    public void randomStringTrackLength(String arg){
        assertThrows(NumberFormatException.class,()->track.setLength(Double.parseDouble(arg)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {101,0})
    @DisplayName("Track length should be less than or equal to 100 mins and greater than 0 mins.")
    public void trackLengthShouldBeValid(double arg){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->track.setLength(arg));
        assertEquals(exception.getMessage(),"Not a valid track length.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {100,1})
    @DisplayName("Invalid track length.")
    public void trackLengthIsValid(double arg){
        track.setLength(arg);
        assertEquals(track.getLengthInMinutes(),arg);
    }

    @ParameterizedTest
    @ValueSource(doubles = {99.8,1.2})
    @DisplayName("Check if it is setting the valid track length.")
    public void positiveTrackLength(double arg){
        track.setLength(arg);
        assertEquals(track.getLengthInMinutes(),arg);
    }




}
