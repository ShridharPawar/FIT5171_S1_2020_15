package allaboutecm.model;

import com.google.common.collect.Sets;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * An artist that has been featured in (at least) one ECM record.
 *
 * See {@https://www.ecmrecords.com/artists/1435045745}
 */
public class Musician extends Entity {
    private String name;

    private URL musicianUrl;

    private Set<Album> albums;

    public Musician(String name) {
        notNull(name);
        notBlank(name);
        this.name = name;
        this.musicianUrl = null;

        albums = Sets.newLinkedHashSet();
    }

    public String getName() {
        return name;
    }

    public void setName(String musicianName)
    {
        notNull(musicianName);
        notBlank(musicianName);
        String[] names = musicianName.split(" ");
        boolean letter = true;
        for(String name:names)
        {
            if(!name.toLowerCase().matches("^[a-záäâèëéàêîïôüùû,/&à]*$")) //covering the french letters as well
            {
                letter = false;
                break;
            }
        }
        //if(names.length<2 || !letter)
        if(!letter)
        {
            throw new IllegalArgumentException("Please input an appropriate name.");
        }

        this.name=musicianName;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        notNull(albums);
        for(Album album:albums)
        {
            if(album.equals(null))
            {
                throw new NullPointerException("Object within the set should not be null");
            }
        }
        this.albums = albums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Musician that = (Musician) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public URL getMusicianUrl() {
        return musicianUrl;
    }

    public void setMusicianUrl(URL musicianUrl) throws IOException {
        notNull(musicianUrl);
        HttpURLConnection connectionString = (HttpURLConnection) musicianUrl.openConnection();
        connectionString.setRequestMethod("GET");
        int codeInResponse = connectionString.getResponseCode();
        if(!musicianUrl.toString().toLowerCase().startsWith("https://")||!musicianUrl.toString().toLowerCase().contains("ecm")||!(codeInResponse==200))
        {
            throw new IllegalArgumentException("Not a valid URL.");
        }
        this.musicianUrl = musicianUrl;
    }
}
