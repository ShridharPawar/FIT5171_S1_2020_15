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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackUnitTest {
    private Track track;

    @BeforeEach
    public void setUp(){
        track = new Track("Numb","Rock");
    }

    @Test
    @DisplayName("Track name cannot be null.")
    public void trackNameCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> track.setName(null));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Track name cannot be empty or blank.")
    public void trackNameCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setName(arg));
        assertEquals(exception.getMessage(),"Name cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shadowdffgfg45fnjnjngkrngkngkngkngkntkgnkgjkkntjgn"})
    @DisplayName("Track name length should not exceed 40 characters.")
    public void trackNameLength(String arg) {
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
    @DisplayName("Track length should be less than 100 mins and greater than 0 mins.")
    public void trackLengthShouldBeValid(double arg){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->track.setLength(arg));
        assertEquals(exception.getMessage(),"Not a valid track length.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {99.8,1.2})
    @DisplayName("Check if it is setting the valid track length.")
    public void positiveTrackLength(double arg){
        track.setLength(arg);
        assertEquals(track.getLengthInMinutes(),arg);
    }

    @Test
    @DisplayName("Genre cannot be null.")
    public void genreCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> track.setGenre(null));
        assertEquals(exception.getMessage(),"Genre cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Genre cannot be empty or blank.")
    public void genreCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setGenre(arg));
        assertEquals(exception.getMessage(),"Genre cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Contempoaryffgfg45fnjnjngkrngkngkngkngkntkgnkgjkkntjgn"})
    @DisplayName("Genre name length should not exceed 30 characters.")
    public void genreLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setGenre(arg));
        assertEquals(exception.getMessage(),"Genre should not exceed 30 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for genre.")
    @ValueSource(strings = {"Contemporary."})
    public void positiveGenre(String arg){
        track.setGenre(arg);
        assertEquals(track.getGenre(),arg);
    }

    @Test
    @DisplayName("Style cannot be null.")
    public void styleCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> track.setStyle(null));
        assertEquals(exception.getMessage(),"Style cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Style cannot be empty or blank.")
    public void styleCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setStyle(arg));
        assertEquals(exception.getMessage(),"Style cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ContemporaryJazzdfjfnjnjgjfbgbhbbbjnjbgbghnhhjhhjuukikikjkjmjkjhkjkjkjkjkjjgbnjgnbjg"})
    @DisplayName("Style length should not exceed 30 characters.")
    public void setStyleLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setStyle(arg));
        assertEquals(exception.getMessage(),"Style should not exceed 30 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for style.")
    @ValueSource(strings = {"Contemporary Jazz"})
    public void positiveStyle(String arg){
        track.setStyle(arg);
        assertEquals(track.getStyle(),arg);
    }

    @Test
    @DisplayName("Release format cannot be null.")
    public void releaseFormatCannotBeNUll() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> track.setReleaseFormat(null));
        assertEquals(exception.getMessage(),"Release format cannot be null.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Release format cannot be empty or blank.")
    public void releaseFormatCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setReleaseFormat(arg));
        assertEquals(exception.getMessage(),"Release format cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"CDdfjfnjnjgjfbgbhbbbjnjbgbghnhhjhhjuukikikjkjm"})
    @DisplayName("Release format length should not exceed 20 characters.")
    public void setReleaseFormatLength(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> track.setReleaseFormat(arg));
        assertEquals(exception.getMessage(),"Release format should not exceed 20 characters.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for release format.")
    @ValueSource(strings = {"CD"})
    public void positiveReleaseFormat(String arg){
        track.setReleaseFormat(arg);
        assertEquals(track.getReleaseFormat(),arg);
    }

    @Test
    @DisplayName("Reviews cannot be null.")
    public void reviewsCannotBeNull(){
        assertThrows(NullPointerException.class,()->track.setReviews(null));
    }

    @Test
    @DisplayName("Check if any object within the Review set is null.")
    public void nullObjectInReviews(){
        Set<Review> reviews = new HashSet<>();
        reviews.add(null);
        assertThrows(NullPointerException.class,()->track.setReviews(reviews));
   }

    @Test
    @DisplayName("Positive test to check if the reviews have been set.")
    public void positiveTestReviews() throws MalformedURLException {
        Set<Review> reviews = new HashSet<>();
        Review review = new Review(returnURL(),77);
        reviews.add(review);
        track.setReviews(reviews);
        assertEquals(track.getReviews(),reviews);
    }

    public URL returnURL() throws MalformedURLException {
        URL url = new URL("https://rottentomatoes.com");
        return url;
    }





}
