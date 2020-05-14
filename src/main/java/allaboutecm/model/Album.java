package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents an album released by ECM records.
 *
 * See {@https://www.ecmrecords.com/catalogue/143038750696/the-koln-concert-keith-jarrett}
 */
@NodeEntity
public class Album extends Entity {

    @Property(name="releaseYear")
    private int releaseYear;

    @Property(name="recordNumber")
    private String recordNumber;

    @Property(name="albumName")
    private String albumName;

    /**
     * CHANGE: instead of a set, now featuredMusicians is a list,
     * to better represent the order in which musicians are featured in an album.
     */
    @Relationship(type="featuredMusicians")
    private List<Musician> featuredMusicians;

    @Relationship(type="instruments")
    private Set<MusicianInstrument> instruments;

    @Convert(URLConverter.class)
    @Property(name="albumURL")
    private URL albumURL;

    @Relationship(type="tracks")
    private Set<Track> tracks;

    @Property(name="genre")
    private String genre;

    @Property(name="style")
    private String style;

    @Property(name="releaseFormat")
    private String releaseFormat;

    @Relationship(type="Reviews")
    private Set<Review> Reviews;

    @Relationship(type="concerts")
    private Set<Concert> concerts;

    @Property(name="sales")
    private int sales;


    public Album(int releaseYear, String recordNumber, String albumName) {
        notNull(recordNumber,"Record number should not be null.");
        notNull(albumName,"Album name cannot be null.");
        notBlank(recordNumber);
        notBlank(albumName);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if(!Integer.toString(releaseYear).matches("[0-9-]+"))
        {
            throw new NumberFormatException("Release year should have just numbers.");
        }
        if(releaseYear>year || releaseYear<1500)
        {
            throw new IllegalArgumentException("Not a valid year.");
        }
        String[] recordParts = recordNumber.split(" ");
        if(!recordNumber.startsWith("ECM") || recordParts.length<2)
        {
            throw new IllegalArgumentException("Record number should start with ECM.");
        }
        if(albumName.length()>40)
        {
            throw new IllegalArgumentException("Album's length is too big.");
        }

        this.releaseYear = releaseYear;
        this.recordNumber = recordNumber;
        this.albumName = albumName;
        this.genre = "Jazz";
        this.albumURL = null;
        this.style="contemporary Jazz";
        this.releaseFormat="CD";
        this.Reviews = Sets.newHashSet();
        this.featuredMusicians = new ArrayList<Musician>();
        this.instruments = Sets.newHashSet();
        this.tracks = Sets.newHashSet();
        this.concerts = Sets.newHashSet();
        this.sales = 0;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        notNull(recordNumber);
        notBlank(recordNumber);
        String[] recordParts = recordNumber.split(" ");
        if(!recordNumber.startsWith("ECM") || recordParts.length<2)
        {
            throw new IllegalArgumentException("Record number should start with ECM.");
        }
        this.recordNumber = recordNumber;
    }

    public List<Musician> getFeaturedMusicians() {
        return featuredMusicians;
    }

    public void setFeaturedMusicians(List<Musician> featuredMusicians) {
        notNull(featuredMusicians);
        Set<Musician> set = new HashSet<Musician>(featuredMusicians);
        if(set.size()<featuredMusicians.size())
        {
            throw new IllegalArgumentException("Duplicate musicians in the list!");
        }
        for(Musician mus:featuredMusicians)
        {
            if(mus.equals(null))
            {
                throw new NullPointerException("Object within the set should not be null");
            }
        }
        this.featuredMusicians = featuredMusicians;
    }

    public Set<MusicianInstrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<MusicianInstrument> instruments)
    {
        notNull(instruments);
        for(MusicianInstrument musins:instruments)
        {
            if(musins.equals(null))
            {
                throw new NullPointerException("Object within the set should not be null");
            }
        }
        this.instruments = instruments;
    }

    public URL getAlbumURL() {
        return albumURL;
    }

    public void setAlbumURL(URL albumURL) throws IOException {
        notNull(albumURL);
        if(!(albumURL.toString().contains("https://")))
        {
            albumURL=new URL("https://google.com");
        }
        HttpURLConnection connectionString = (HttpURLConnection) albumURL.openConnection();
        connectionString.setRequestMethod("GET");
        int codeInResponse = connectionString.getResponseCode();
        if(!albumURL.toString().toLowerCase().contains("ecm")||!(codeInResponse==200))
        {
            throw new UnknownHostException("Not a valid URL.");
        }

        this.albumURL = albumURL;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks)
    {
        notNull(tracks,"Object should not be null.");
        for(Track track:tracks)
        {
            if(track.equals(null))
            {
                throw new NullPointerException("Track should not be null.");
            }
        }
        this.tracks = tracks;
    }

    public void setConcerts(Set<Concert> concerts)
    {
        notNull(concerts,"Object should not be null.");
        for(Concert concert:concerts)
        {
            if(concert.equals(null))
            {
                throw new NullPointerException("Object should not be null.");
            }
        }
        this.concerts = concerts;
    }

    public Set<Concert> getConcerts(){return concerts;}

    public void setSales(int sales)
    {
        notNull(sales);
        if(sales<0)
        {
            throw new IllegalArgumentException("Sales number should be greater than or equal to 0.");
        }
        if(!(Integer.toString(sales).matches("[0-9]+")))
        {
            throw new NumberFormatException("Sales should be just in numbers.");
        }
        this.sales = sales;
    }

    public int getSales() {
        return sales;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear)
    {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if(!Integer.toString(releaseYear).matches("[0-9-]+"))
        {
            throw new NumberFormatException("Release year should have just numbers.");
        }
        if(releaseYear>year || releaseYear<1500)
        {
            throw new IllegalArgumentException("Not a valid year.");
        }
        this.releaseYear = releaseYear;
    }


    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        notNull(albumName);
        notBlank(albumName,"Album name cannot be empty.");
        if(albumName.length()>40)
        {
            throw new IllegalArgumentException("Album's length is too big.");
        }
        this.albumName = albumName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return releaseYear == album.releaseYear &&
                recordNumber.equals(album.recordNumber) &&
                albumName.equals(album.albumName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseYear, recordNumber, albumName);
    }

    public String getGenre(){return genre;}

    public String getStyle(){return style;}

    public String getReleaseFormat(){return releaseFormat;}

    public Set<Review> getReviews(){return Reviews;}



    public void setGenre(String genre)
    {
        notNull(genre,"Genre cannot be null.");
        notBlank(genre,"Genre cannot be blank.");
        if(genre.length()>30)
        {
            throw new IllegalArgumentException("Genre should not exceed 30 characters.");
        }
        this.genre=genre;
    }

    public void setStyle(String style)
    {
        notNull(style,"Style cannot be null.");
        if(style.length()>30)
        {
            throw new IllegalArgumentException("Style should not exceed 30 characters.");
        }
        this.style=style;
    }

    public void setReleaseFormat(String releaseFormat)
    {
        notNull(releaseFormat,"Release format cannot be null.");
        notBlank(releaseFormat,"Release format cannot be blank.");
        if(releaseFormat.length()>20)
        {
            throw new IllegalArgumentException("Release format should not exceed 20 characters.");
        }
        this.releaseFormat=releaseFormat;
    }

    public void setReviews(Set<Review> Reviews)
    {
        notNull(Reviews);
        for(Review rev:Reviews)
        {
            if(rev.equals(null))
            {
                throw new NullPointerException("Object within the Review set should not be null.");
            }
        }
        this.Reviews=Reviews;
    }
}
