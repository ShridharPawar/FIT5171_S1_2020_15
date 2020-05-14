package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
public class Review extends Entity{

    @Convert(URLConverter.class)
    @Property(name="websiteUrl")
    private URL websiteUrl;

    @Property(name="review")
    private String review;

    @Property(name="ratingOutOf100")
    private double ratingOutOf100;

    public Review(URL websiteUrl,double ratingOutOf100)
    {
        notNull(ratingOutOf100);
        notNull(websiteUrl);
        if(!(Double.toString(ratingOutOf100).matches("[0-9.]+")))
        {
            throw new NumberFormatException("Rating should be just in numbers.");
        }
        if(ratingOutOf100>100 || ratingOutOf100<1)
        {
            throw new IllegalArgumentException("Not a valid rating.");
        }
        this.websiteUrl = websiteUrl;
        this.ratingOutOf100 = ratingOutOf100;
        review = "The song is nice.";
    }

    public URL getUrl(){return websiteUrl;}

    public String getReview(){return review;}

    public double getRating(){return ratingOutOf100;}

    public void setUrl(URL websiteUrl) throws IOException {
        notNull(websiteUrl);
        if(!(websiteUrl.toString().contains("https://")))
        {
            websiteUrl=new URL("https://google.com");
        }
        HttpURLConnection connectionString = (HttpURLConnection) websiteUrl.openConnection();
        connectionString.setRequestMethod("GET");
        int codeInResponse = connectionString.getResponseCode();
        if(!(codeInResponse==200))
        {
            throw new UnknownHostException("Not a valid URL.");
        }
        this.websiteUrl = websiteUrl;
    }

    public void setReview(String review)
    {
        notNull(review,"Object is null.");
        this.review = review;
    }

    public void setRating(double ratingOutOf100)
    {
        notNull(ratingOutOf100);
        if(!(Double.toString(ratingOutOf100).matches("[0-9.]+")))
        {
            throw new NumberFormatException("Rating should be just in numbers.");
        }
        if(ratingOutOf100>100 || ratingOutOf100<1)
        {
            throw new IllegalArgumentException("Not a valid rating.");
        }
        this.ratingOutOf100 = ratingOutOf100;
    }




}
