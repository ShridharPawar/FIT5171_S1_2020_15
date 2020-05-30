package allaboutecm.model;

import com.google.common.collect.Sets;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
public class Concert extends Entity{

    @Property(name="concertName")
    private String concertName;

    @Property(name="city")
    private String city;

    @Property(name="country")
    private String country;

    @Relationship(type="musicians")
    private Set<Musician> musicians;

    @Property(name="concertDate")
    private Date concertDate;

    public Concert(String concertName,String country)
    {
        notNull(concertName);
        notNull(country);
        notBlank(concertName);
        notBlank(country);
        this.concertName = concertName;
        this.country = country;
        this.city = "Melbourne";
        this.musicians = Sets.newHashSet();
        this.concertDate = new Date();
    }

    public String getConcertName(){return concertName;}

    public String getCity(){return city;}

    public Date getConcertDate(){return concertDate;}

    public String getCountry(){return country;}

    public Set<Musician> getMusicians(){return musicians;}

    public void setConcertName(String concertName)
    {
        notNull(concertName,"Concert name cannot be null.");
        notBlank(concertName,"Concert name cannot be empty.");
        this.concertName = concertName;
    }

    public void setCity(String city)
    {
        notNull(city,"City cannot be null.");
        notBlank(city,"City cannot be blank.");
        this.city = city;
    }

    public void setConcertDate(Date concertDate)
    {
        notNull(concertDate);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        if(currentDate.compareTo(concertDate)>0)
        {
          throw new IllegalArgumentException("Concert date cannot be a past date.");
        }

        this.concertDate = concertDate;
    }

    public void setCountry(String country)
    {
        notNull(country,"Country cannot be null.");
        notBlank(country,"Country cannot be blank.");
        this.country = country;
    }

    public void setMusicians(Set<Musician> musicians)
    {
        notNull(musicians);
        for(Musician mus:musicians)
        {
            if(mus==null)
            {
                throw new NullPointerException("Object within the set should not be null");
            }
        }
        this.musicians = musicians;
    }


}
