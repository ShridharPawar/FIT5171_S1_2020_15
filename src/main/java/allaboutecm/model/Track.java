package allaboutecm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
//import com.sun.deploy.security.SelectableSecurityManager;
import sun.security.x509.OtherName;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Track extends Entity{
     private String name;
     private double lengthInMinutes;
     private String genre;
     private String style;
     private String releaseFormat;
     private Set<Review> Reviews;

    public Track(String name,String genre)
    {
        notNull(name);
        notBlank(name);
        notNull(genre);
        notBlank(genre);
        this.genre = genre;
        this.name = name;
        lengthInMinutes=3;
        style="contemporary Jazz";
        releaseFormat="CD";
        Reviews = Sets.newHashSet();
    }

    public String getName(){return name;}

    public double getLengthInMinutes(){return lengthInMinutes;}

    public String getGenre(){return genre;}

    public String getStyle(){return style;}

    public String getReleaseFormat(){return releaseFormat;}

    public Set<Review> getReviews(){return Reviews;}

    public void setName(String name)
    {
        notNull(name,"Object is null.");
        notBlank(name,"Name cannot be blank.");
        if(name.length()>40)
        {
            throw new IllegalArgumentException("Name of the track should not exceed 40 characters.");
        }
        this.name=name;
    }

    public void setLength(double length)
    {
        if(!Double.toString(length).matches("[0-9.]+"))
        {
            throw new NumberFormatException("Track length should be in numbers.");
        }
        if(length>100 || length<1)
        {
            throw new IllegalArgumentException("Not a valid track length.");
        }
        this.lengthInMinutes=length;
    }

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
        notBlank(style,"Style cannot be blank.");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return name == track.name &&
                genre.equals(track.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,genre);
    }

}
