package allaboutecm.model;


import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import java.util.*;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
public class Track extends Entity{

    @Property(name="name")
    private String name;

    @Property(name="lengthInMinutes")
    private double lengthInMinutes;


    public Track(String name,double lengthInMinutes)
    {
        notNull(name);
        notBlank(name);
        if(name.length()>40)
        {
            throw new IllegalArgumentException("Name of the track should not exceed 40 characters.");
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
        int a = name.length();
        if(a>40)
        {
            throw new IllegalArgumentException("Name of the track should not exceed 40 characters.");
        }
        this.name=name;

    }

    public void setLength(double length)
    {
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
