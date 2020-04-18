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


    public Track(String name,double lengthInMinutes)
    {
        notNull(name);
        notBlank(name);
        if(name.length()>40)
        {
            throw new IllegalArgumentException("Name of the track should not exceed 40 characters.");
        }
        if(!Double.toString(lengthInMinutes).matches("[0-9.]+"))
        {
            throw new NumberFormatException("Track length should be in numbers.");
        }
        if(lengthInMinutes>100 || lengthInMinutes<1)
        {
            throw new IllegalArgumentException("Not a valid track length.");
        }
        this.name = name;
        this.lengthInMinutes=lengthInMinutes;
    }

    public String getName(){return name;}

    public double getLengthInMinutes(){return lengthInMinutes;}

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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return name.equals(track.name) &&
                lengthInMinutes == track.lengthInMinutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,lengthInMinutes);
    }

}
