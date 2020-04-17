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

public class ReviewUnitTest {
    private Review review;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        URL url = new URL("https://www.imdb.com/title/tt7198138/");
        review = new Review(url,92);
    }

    @Test
    @DisplayName("Should construct Review object.")
    public void shouldConstructReviewObject()
    {
        assertNotNull(review,"Review object should not be null.");
    }

    @Test
    @DisplayName("URL cannot be null")
    public void urlCannotBeNull()
    {
        assertThrows(NullPointerException.class,()->review.setUrl(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://www.testfakewebsite.com"})
    @DisplayName("URL should be a valid url.")
    public void invalidURL(String arg) throws MalformedURLException {
        if(!arg.contains("https://")){arg="https://google.com";}
        java.net.URL url = new java.net.URL(arg);
        assertThrows(UnknownHostException.class,()->review.setUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Positive test case for URL.")
    @ValueSource(strings = {"https://www.imdb.com/title/tt7198138/"})
    public void positiveURL(String arg) throws IOException {
        java.net.URL url = new java.net.URL(arg);
        review.setUrl(url);
        assertEquals(review.getUrl(),url);
    }

    @Test
    @DisplayName("Review cannot be null.")
    public void reviewCannotBeNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> review.setReview(null));
        assertEquals(exception.getMessage(),"Object is null.");
    }

    @ParameterizedTest
    @DisplayName("Positive test case for review.")
    @ValueSource(strings = {"I loved this song."})
    public void positiveReview(String arg){
        review.setReview(arg);
        assertEquals(review.getReview(),arg);
    }

    @ParameterizedTest
    @ValueSource(strings = {"26gfgfg7","8.fgfg2"})
    @DisplayName("Track length should have just numbers.")
    public void randomRating(String arg){
       assertThrows(NumberFormatException.class,()->review.setRating(Double.parseDouble(arg)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {101,0})
    @DisplayName("Rating should be less than 100 and greater than 0.")
    public void ratingShouldBeValid(double arg){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,()->review.setRating(arg));
        assertEquals(exception.getMessage(),"Not a valid rating.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {99.8,1.2})
    @DisplayName("Check if it is setting the valid rating.")
    public void positiveRating(double arg){
        review.setRating(arg);
        assertEquals(review.getRating(),arg);
    }
}
