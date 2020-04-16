package allaboutecm.model;

import com.google.common.collect.Sets;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Webpage extends Entity{
      private URL url;
      private String name;
      private String biography;

      public Webpage(String name,URL url)
      {
          notNull(name);
          notBlank(name);
          notNull(url);
          this.url = url;
          this.name = name;
          biography = "I love making songs.";
      }

      public String getName(){return name;}

      public URL getUrl(){return url;}

      public String getBiography(){return biography;}

      public void setUrl(URL url) throws IOException {
          notNull(url);
          if(!(url.toString().contains("https://")))
          {
              url=new URL("https://google.com");
          }
          HttpURLConnection connectionString = (HttpURLConnection) url.openConnection();
          connectionString.setRequestMethod("GET");
          int codeInResponse = connectionString.getResponseCode();
          if(!(codeInResponse==200))
          {
              throw new UnknownHostException("Not a valid URL.");
          }

          this.url = url;
      }

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

      public void setBiography(String biography)
      {
           notNull(biography,"Biography object cannot be null.");
           this.biography = biography;
      }



}
