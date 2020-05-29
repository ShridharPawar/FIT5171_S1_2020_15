package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.*;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.*;
import java.util.stream.Collectors;

import static allaboutecm.model.MusicalInstrument.*;


public class ECMMiner {
    private static Logger logger = LoggerFactory.getLogger(ECMMiner.class);

    private final DAO dao;
    private static String exceptionMessage = "Object is null.";
    private static String kExceptionMessage = "k should be positive";

    public ECMMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the most prolific musician in terms of number of albums released.
     *
     * @Param k the number of musicians to be returned.
     * @Param startYear, endYear between the two years [startYear, endYear].
     * When startYear/endYear is negative, that means startYear/endYear is ignored.
     */
    public List<Musician> mostProlificMusicians(int k, int startYear, int endYear) {
       if (k <= 0) {
            throw new IllegalArgumentException(kExceptionMessage);
        }
        startYear = (startYear < 0) ? 0 : startYear;
        endYear = (endYear < 0) ? Integer.MAX_VALUE : endYear;

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        if(musicians==null)
        {
            throw new NullPointerException(exceptionMessage);
        }

        Map<Musician, Integer> musicianAlbumNumMap = new HashMap<>();

        for (Musician m : musicians) {
            Set<Album> albums = m.getAlbums();
            int numOfValidAlbum = 0;
            for (Album album : albums) {
                if (album.getReleaseYear() >= startYear && album.getReleaseYear() <= endYear) {
                    numOfValidAlbum++;
                }
            }
            if (numOfValidAlbum != 0) {
                musicianAlbumNumMap.put(m, numOfValidAlbum);
            }
        }

        List<Musician> result = musicianAlbumNumMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        k = Math.min(k, result.size());
        return result.subList(0, k);
    }

    /**
     * Most talented musicians by the number of different musical instruments they play
     *
     * @Param k the number of musicians to be returned.
     * The most talented musician refers to the one who can play the largest number of instruments.
     */
    public List<Musician> mostTalentedMusicians(int k)
    {
        if (k <= 0) {
            throw new IllegalArgumentException(kExceptionMessage);
        }


        Collection<MusicianInstrument> musicianInstruments = dao.loadAll(MusicianInstrument.class);
        if(musicianInstruments==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        Map<Musician, Integer> musicianInstrumentMap = new HashMap<>();

        for(MusicianInstrument m : musicianInstruments){
            Musician musician = m.getMusician();
            Set<MusicalInstrument> instruments = m.getMusicalInstruments();
            int numOfInstruments = 0;
            for(MusicalInstrument i : instruments){
                numOfInstruments++;
            }
            if (numOfInstruments != 0) {
                musicianInstrumentMap.put(musician, numOfInstruments);
            }
        }

        List<Musician> result = musicianInstrumentMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        k = Math.min(k, result.size());
        return result.subList(0, k);

    }

    /** 
     * Musicians that collaborate the most widely, by the number of other musicians they work with on albums.
     *
     * @Param k the number of musicians to be returned.
     */

    public List<Musician> mostSocialMusicians(int k)
    {
        if (k <= 0) {
            throw new IllegalArgumentException(kExceptionMessage);
        }

        Collection<Album> albums = dao.loadAll(Album.class);
        if(albums==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        Map<Musician, Integer> musicianMap = new HashMap<>();
        Integer num;

        for(Album a: albums){
            List<Musician> musicians = a.getFeaturedMusicians();
            List<Musician> musiciansList = new ArrayList<>(musicians);
            if (!(musiciansList.isEmpty())) {
                for (int j = 0; j < musicians.size() ; j++) {
                    if (!musicianMap.containsKey(musiciansList.get(j)))
                        musicianMap.put(musiciansList.get(j), 1);
                    else {
                        num = musicianMap.get(musiciansList.get(j));
                        musicianMap.put(musiciansList.get(j), (num + 1));
                    }
                }
            }
        }
        List<Musician> result = musicianMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        k = Math.min(k, result.size());
        return result.subList(0, k);
    }

 /**
     * Busiest year in terms of number of albums released.
     *
     * @Param k the number of years to be returned.
     */

    public List<Integer> busiestYears(int k)
    {
       if (k <= 0)
            throw new IllegalArgumentException("Number of Years cannot be Zero or negative.");

        Collection<Album> albums = dao.loadAll(Album.class);
        if(albums==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        Map<Integer,Integer> yearMap = new HashMap<>();
        Integer count;
        for (Album album : albums) {
            Integer year = album.getReleaseYear();
            if (!yearMap.containsKey(year))
                yearMap.put(year, 1);
            else {
                count = yearMap.get(year);
                yearMap.put(year, (count + 1));
            }
        }

        List<Map.Entry<Integer, Integer>> sortList = new ArrayList<>(yearMap.entrySet());
        sortList.sort(Map.Entry.comparingByValue());
        Collections.reverse(sortList);

        for (int i = 0; i < sortList.size() -1 ; i++) {
            for(int j = 0;j < sortList.size() - i - 1; j++ ){
                if(sortList.get(j).getValue().equals(sortList.get(j + 1).getValue())
                        && sortList.get(j).getKey() < sortList.get(j+1).getKey() )
                    Collections.swap(sortList, j, j+1);
            }
        }

        if (k > sortList.size())
            k = sortList.size();

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            result.add(sortList.get(i).getKey());
        }
        return result;
    }

    /**
     * Most similar albums to a give album. The similarity here is such that:
     * if similar musical instruments are used in different albums and if the genre
     * of the album is same, then they are the most similar albums.
     *
     * @Param k the number of albums to be returned.
     * @Param album
     */

    public List<Album> mostSimilarAlbums(int k, Album album)
    {
        Collection<Album> albums = dao.loadAll(Album.class);
        if(albums==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        List<Album> similarAlbums = new ArrayList<>();
        Set<String> givenAlbumInstruments = new HashSet<>();
        String givenAlbumGenre = album.getGenre();
        for(MusicianInstrument i:album.getInstruments())
        {
            Set<MusicalInstrument> musicianInstruments = i.getMusicalInstruments();
            for(MusicalInstrument j:musicianInstruments)
            {
                givenAlbumInstruments.add(j.getName());
            }
        }

        for (Album a : albums)
        {
            Set<MusicianInstrument> musicianInstruments = a.getInstruments();
            Set<String> musicalInstruments = new HashSet<>();
            for(MusicianInstrument m : musicianInstruments)
            {
                for(MusicalInstrument i:m.getMusicalInstruments())
                {
                    musicalInstruments.add(i.getName());
                }
            }
            if(musicalInstruments.equals(givenAlbumInstruments) && a.getGenre().equals(givenAlbumGenre)&&!(0>=k))
            {
                similarAlbums.add(a);
                k--;
            }
        }
          return similarAlbums;
    }

    /**
     * To return the album with the highest rate.
     *
     * @Param k the number of albums to be returned.
     */
    public List<Album> highestRatedAlbums(int k)
    {
        Collection<Album> albums = dao.loadAll(Album.class);
        if(albums==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        Map<String, Double> albumMap = new HashMap<>();
        for(Album a:albums)
        {
            double count = 0;
            int totalReviews = 0;
            for(Review r:a.getReviews())
            {
               count=count+r.getRating();
               totalReviews++;
            }
            if(totalReviews!=0)
            {albumMap.put(a.getAlbumName(),(count/totalReviews));}

         }
        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(albumMap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> i1, Entry<String, Double> i2)
            {return i2.getValue().compareTo(i1.getValue());
            }});

        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for(Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        List<Album> results = new ArrayList<>();
        for(String name: sortedMap.keySet())
        {
            for(Album a : albums)
            {
                if(a.getAlbumName().equals(name)&& !(0>=k))
                {
                    results.add(a);
                    k--;
                }
            }
        }

        return results;
    }

    /**
     * To return the Album with the largest number of selling.
     *
     * @Param k the number of albums to be returned.
     */
    public List<Album> bestSellingAlbums(int k)
    {
        Collection<Album> albums = dao.loadAll(Album.class);
        if(albums==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        Map<String, Integer> albumMap = new HashMap<>();
        for(Album a:albums)
        {
            albumMap.put(a.getAlbumName(),a.getSales());
        }
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(albumMap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> i1, Entry<String, Integer> i2)
            {return i2.getValue().compareTo(i1.getValue());}});

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for(Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        List<Album> results = new ArrayList<>();
        for(String name: sortedMap.keySet())
        {
            for(Album a : albums)
            {
                if(a.getAlbumName().equals(name)&& !(k<=0))
                {
                    results.add(a);
                    k--;
                }
            }
        }
        return results;
    }

    /**
     * To return the musician who held the largest number of concerts.
     *
     * @Param k the number of musicians to be returned.*/

    public List<Musician> mostPopularPerformer(int k)
    {
        Collection<Concert> concerts = dao.loadAll(Concert.class);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        if(concerts==null||musicians==null)
        {
            throw new NullPointerException(exceptionMessage);
        }
        Map<String, Integer> musicianMap = new HashMap<String, Integer>();
        for(Musician m:musicians)
        {
            int count = 0;
            for(Concert c:concerts)
            {
                for(Musician m1:c.getMusicians())
                {
                    if(m1.equals(m))
                    {
                        count++;
                    }
                }
            }
            musicianMap.put(m.getName(),count);
       }
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(musicianMap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> i1, Entry<String, Integer> i2)
            {return i2.getValue().compareTo(i1.getValue());}});

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for(Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        List<Musician> results = new ArrayList<>();
        for(String name: sortedMap.keySet())
        {
            for(Musician m : musicians)
            {
                if(m.getName().equals(name)&& !(k<=0))
                {
                    results.add(m);
                    k--;
                }
            }
        }

        return results;
    }
}
