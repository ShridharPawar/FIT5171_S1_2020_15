package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
public class Webpage extends Entity{

      @Convert(URLConverter.class)
      @Property(name="url")
      private URL url;

      @Property(name="name")
      private String name;

      public Webpage(String name,URL url){
          notNull(name);
          notBlank(name);
          notNull(url);
          if(name.length()>50)
          {
              throw new IllegalArgumentException("Name of the webpage should not exceed 50 characters.");
          }
          this.url = url;
          this.name = name;

      }

      public String getName(){return name;}

      public URL getUrl(){return url;}

    public void setName(String name)
    {
        notNull(name,"Object is null.");
        notBlank(name,"Name cannot be blank.");
        if(name.length()>50)
        {
            throw new IllegalArgumentException("Name of the webpage should not exceed 50 characters.");
        }
        this.name = name;
    }

    public void setUrl(URL url) throws IOException {
        notNull(url);
        if(!(url.toString().contains("https://")))
        {
            url=new URL("https://google.com");
        }
        HttpURLConnection connectionString = (HttpURLConnection) url.openConnection();
        connectionString.setRequestMethod("GET");
        int codeInResponse = connectionString.getResponseCode();
        if(codeInResponse!=200)
        {
            throw new UnknownHostException("Not a valid URL.");
        }

        this.url = url;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Webpage webpage = (Webpage) o;
        return url.equals(webpage.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }



}
