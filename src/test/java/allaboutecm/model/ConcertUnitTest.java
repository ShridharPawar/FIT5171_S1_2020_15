package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ConcertUnitTest {
    private Concert concert;

    @BeforeEach
    public void setUp() {
        concert = new Concert("Tokyo Festival", "Japan");
    }

    @Test
    @DisplayName("Should construct Concert object.")
    public void shouldConstructConcertObject()
    {
        assertNotNull(concert,"Concert object should not be null.");
    }

    @Test
    @DisplayName("Concert name cannot be null.")
    public void concertNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> concert.setConcertName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Concert name cannot be empty or blank.")
    public void concertNameCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> concert.setConcertName(arg));
        assertEquals(exception.getMessage(),"Concert name cannot be empty.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Tokyo Festival."})
    @DisplayName("Check if it is setting the valid concert name.")
    public void positiveConcertName(String arg) {
        concert.setConcertName(arg);
        assertEquals(concert.getConcertName(),arg);
    }

    @Test
    @DisplayName("City cannot be null.")
    public void cityCannotBeNull() {
        assertThrows(NullPointerException.class, () -> concert.setCity(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Concert name cannot be empty or blank.")
    public void cityCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> concert.setCity(arg));
        assertEquals(exception.getMessage(),"City cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mumbai"})
    @DisplayName("Check if it is setting the valid city.")
    public void positiveCity(String arg) {
        concert.setCity(arg);
        assertEquals(concert.getCity(),arg);
    }

    @Test
    @DisplayName("Country cannot be null.")
    public void countryCannotBeNull() {
        assertThrows(NullPointerException.class, () -> concert.setCountry(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Country cannot be empty or blank.")
    public void countryCannotBeEmptyOrBlank(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> concert.setCountry(arg));
        assertEquals(exception.getMessage(),"Country cannot be blank.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"India"})
    @DisplayName("Check if it is setting the valid country.")
    public void positiveCountry(String arg) {
        concert.setCountry(arg);
        assertEquals(concert.getCountry(),arg);
    }

    @Test
    @DisplayName("Musicians of a concert cannot be null.")
    public void musiciansCannotBeNull(){
        assertThrows(NullPointerException.class,()->concert.setMusicians(null));
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInMusicians(){
        Set<Musician> musicians = Sets.newHashSet();
        musicians.add(null);
        assertThrows(NullPointerException.class,()->concert.setMusicians(musicians));
    }

    @Test
    @DisplayName("Positive test to check if the musicians list has been set.")
    public void positiveTestFeaturedMusicians(){
        Set<Musician> musicians = Sets.newHashSet();
        musicians.add(new Musician("Mike Shinoda"));
        concert.setMusicians(musicians);
        assertEquals(concert.getMusicians(),musicians);
    }

    @Test
    @DisplayName("Concert date cannot be null.")
    public void concertDateCannotBeNull() {
        assertThrows(NullPointerException.class, () -> concert.setConcertDate(null));
    }

    @Test
    @DisplayName("Concert date cannot be a past date.")
    public void concertDateCannotBePastDate() throws ParseException {
        String sDate1="1998-12-31";
        Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(sDate1);
        assertThrows(IllegalArgumentException.class, () -> concert.setConcertDate(date1));
    }

    @Test
    @DisplayName("Positive test case for concert date.")
    public void positiveConcertDate() throws ParseException {
        String sDate1="2021-12-31";
        Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(sDate1);
        concert.setConcertDate(date1);
        assertEquals(concert.getConcertDate(),date1);
    }



}
