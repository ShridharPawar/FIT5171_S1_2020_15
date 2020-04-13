package allaboutecm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.deploy.security.SelectableSecurityManager;
import sun.security.x509.OtherName;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**0
 * Represents an album released by ECM records.
 *
 * See {@https://www.ecmrecords.com/catalogue/143038750696/the-koln-concert-keith-jarrett}
 */
public class Album extends Entity {

    private int releaseYear;

    private String recordNumber;

    private String albumName;

    private Set<Musician> featuredMusicians;

    private Set<MusicianInstrument> instruments;

    private URL albumURL;

    private List<String> tracks;

    public Album(int releaseYear, String recordNumber, String albumName) {
        notNull(recordNumber);
        notNull(albumName);

        notBlank(recordNumber);
        notBlank(albumName);

        this.releaseYear = releaseYear;
        this.recordNumber = recordNumber;
        this.albumName = albumName;

        this.albumURL = null;

        featuredMusicians = Sets.newHashSet();
        instruments = Sets.newHashSet();
        tracks = Lists.newArrayList();
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        notNull(recordNumber);
        notBlank(recordNumber);
        if(!recordNumber.startsWith("ECM"))
        {
            throw new IllegalArgumentException("Record number should start with ECM.");
        }
        this.recordNumber = recordNumber;
    }

    public Set<Musician> getFeaturedMusicians() {
        return featuredMusicians;
    }

    public void setFeaturedMusicians(Set<Musician> featuredMusicians) {
        notNull(featuredMusicians);
        for(Musician mus:featuredMusicians)
        {
             if(mus.equals(null))
             {
                 throw new IllegalArgumentException("Object within the set should not be null");
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
                throw new IllegalArgumentException("Object within the set should not be null");
            }
        }
        this.instruments = instruments;
    }

    public URL getAlbumURL() {
        return albumURL;
    }

    public void setAlbumURL(URL albumURL) {
        notNull(albumURL);
        if(!albumURL.toString().toLowerCase().startsWith("https://")||!albumURL.toString().toLowerCase().contains("ecm"))
        {
            throw new IllegalArgumentException("Not a valid URL.");
        }

        this.albumURL = albumURL;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks)
    {
        notNull(tracks);
        for(String track:tracks)
        {
            if(track.equals(null))
            {
                throw new IllegalArgumentException("Track should not be null.");
            }
        }
        this.tracks = tracks;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear)
    {
        if(releaseYear>2020 || releaseYear<1500)
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
        notBlank(albumName);

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
}
