package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import com.google.common.collect.Sets;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * An artist that has been featured in (at least) one ECM record.
 *
 * See {@https://www.ecmrecords.com/artists/1435045745}
 */
@NodeEntity
public class Musician extends Entity {
    @Property(name="name")
    private String name;

    @Convert(URLConverter.class)
    @Property(name="musicianURL")
    private URL musicianUrl;

    @Relationship(type="albums")
    private Set<Album> albums;

    @Relationship(type="personalWebpages")
    private Set<Webpage> personalWebpages;

    @Property(name="biography")
    private String biography;

    public Musician() {
    }

    public Musician(String name) {
        notNull(name);
        notBlank(name);
        String[] names = name.split(" ");
        boolean letter = true;
        for(String name1:names)
        {
            if(!name1.toLowerCase().matches("^[a-záäâèëéàêîïôüùû,/&à]*$")) //covering the french letters as well
            {
                letter = false;
                break;
            }
        }
        if(!letter || name.length()<3 || name.length()>40)
        {
            throw new IllegalArgumentException("Please input an appropriate name.");
        }
        this.name = name;
        this.musicianUrl = null;
        this.biography = "I love making songs.";
        this.personalWebpages = Sets.newLinkedHashSet();
        this.albums = Sets.newLinkedHashSet();
    }

    public String getName() {
        return name;
    }

    public void setName(String musicianName)
    {
        notNull(musicianName,"Object is null.");
        notBlank(musicianName,"Name cannot be blank.");
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
        if(!letter || musicianName.length()<3 || musicianName.length()>40)
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
        if(!(musicianUrl.toString().contains("https://")))
        {
            musicianUrl=new URL("https://google.com");
        }
        HttpURLConnection connectionString = (HttpURLConnection) musicianUrl.openConnection();
        connectionString.setRequestMethod("GET");
        int codeInResponse = connectionString.getResponseCode();
        if(!(codeInResponse==200))
        {
            throw new UnknownHostException("Not a valid URL.");
        }
        this.musicianUrl = musicianUrl;
    }

    public String getBiography(){return biography;}

    public void setBiography(String biography)
    {
        notNull(biography,"Biography object cannot be null.");
        this.biography = biography;
    }

    public Set<Webpage> getWebpages(){return personalWebpages;}

    public void setWebpages(Set<Webpage> personalWebpages) {
        notNull(personalWebpages);
        for(Webpage webpage:personalWebpages)
        {
            if(webpage.equals(null))
            {
                throw new NullPointerException("Object within the set should not be null");
            }
        }
        this.personalWebpages = personalWebpages;
    }
}
