package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
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

import static allaboutecm.model.MusicalInstrument.*;

/**
 * TODO: implement and test the methods in this class.
 * Note that you can extend the Neo4jDAO class to make implementing this class easier.
 */
public class ECMMiner {
    private static Logger logger = LoggerFactory.getLogger(ECMMiner.class);

    private final DAO dao;

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
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        Map<String, Musician> nameMap = Maps.newHashMap();
        for (Musician m : musicians) {
            nameMap.put(m.getName(), m);
        }

        ListMultimap<String, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();

        for (Musician musician : musicians) {
            Set<Album> albums = musician.getAlbums();
            for (Album album : albums) {
                boolean toInclude =
                        !((startYear > 0 && album.getReleaseYear() < startYear) ||
                                (endYear > 0 && album.getReleaseYear() > endYear));

                if (toInclude) {
                    multimap.put(musician.getName(), album);
                }
            }
        }

        Map<String, Collection<Album>> albumMultimap = multimap.asMap();
        for (String name : albumMultimap.keySet()) {
            Collection<Album> albums = albumMultimap.get(name);
            int size = albums.size();
            countMap.put(size, nameMap.get(name));
        }

        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        sortedKeys.sort(Ordering.natural().reverse());
        for (Integer count : sortedKeys) {
            List<Musician> list = countMap.get(count);
            if (list.size() >= k) {
                break;
            }
            if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }

        return result;
    }

    /**
     * Most talented musicians by the number of different musical instruments they play
     *
     * @Param k the number of musicians to be returned.
     * The most talented musician refers to the one who can play the largest number of instruments.
     */
    public List<Musician> mostTalentedMusicians(int k)
    {
        Collection<MusicianInstrument> musicianInstruments = dao.loadAll(MusicianInstrument.class);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        Map<String, Integer> musicianInstrumentmap = new HashMap<String, Integer>();
        for(Musician m: musicians)
        {
            int count = 0;
            for(MusicianInstrument musins: musicianInstruments)
            {
                if(m.equals(musins.getMusician()))
                {
                    count=count+musins.getMusicalInstruments().size();
                }
            }
            musicianInstrumentmap.put(m.getName(),count);
        }
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(musicianInstrumentmap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
            {return o2.getValue().compareTo(o1.getValue());}});
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for(Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        List<Musician> results = new ArrayList<>();
        for(String musicianName:sortedMap.keySet())
        {
            for(Musician m : musicians)
            {
                if (k != 0 && musicianName.equals(m.getName()))
                {
                    results.add(m);
                    k--;
                }
            }
        }
        return results;
    }

    /** 
     * Musicians that collaborate the most widely, by the number of other musicians they work with on albums.
     *
     * @Param k the number of musicians to be returned.
     */

    public List<Musician> mostSocialMusicians(int k)
    {
        int l=k;
        Collection<Album> albums = dao.loadAll(Album.class);
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        ListMultimap<String, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        for (Musician musician : musicians) {
            Set<Album> albums1 = musician.getAlbums();
            for (Album album : albums1) {
                multimap.put(musician.getName(), album);
            }
        }

        Map<String, Collection<Album>> albumMultimap = multimap.asMap();
        Map<String, Integer> countmap = new HashMap<String, Integer>();
        for (String name : albumMultimap.keySet()) {
            Collection<Album> albums1 = albumMultimap.get(name);
            Set<String> musiNames = new HashSet<>();
            for(Album a : albums1)
            {
                for(int i=0;i<a.getFeaturedMusicians().size();i++)
                {
                    musiNames.add(a.getFeaturedMusicians().get(i).getName());
                }
            }
           countmap.put(name, musiNames.size()-1);
        }
        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countmap.values());
        sortedKeys.sort(Ordering.natural().reverse());
        List<Integer> chosenKeys = Lists.newArrayList();
        for(int i=0;i<k;i++){chosenKeys.add(sortedKeys.get(i));}

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(countmap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
            {return o2.getValue().compareTo(o1.getValue());}});
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for(Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        List<String> chosenMusicians = new ArrayList<>();
        for(String name: sortedMap.keySet())
        {
            Integer count = sortedMap.get(name);
            int first = count;
            if(chosenKeys.contains(first) && k!=0){chosenMusicians.add(name);k--;}
        }

        for(Musician m: musicians)
        {
            if(chosenMusicians.contains(m.getName()) && l!=0)
            {
                result.add(m);l--;
            }
        }

          return result;
    }

 /**
     * Busiest year in terms of number of albums released.
     *
     * @Param k the number of years to be returned.
     */

    public List<Integer> busiestYears(int k)
    {
        Collection<Album> albums = dao.loadAll(Album.class);
        Map<Integer, Integer> multimap = new HashMap<Integer, Integer>();
        List<Integer> doneYears = new ArrayList<>();
        for(Album a : albums)
        {
            int count=0;
            int year = a.getReleaseYear();

            for(Album a1:albums)
            {
                if(!doneYears.contains(year))
                {
                    if(year==a1.getReleaseYear())
                    {
                        count++;
                    }
                }
            }
            if(!doneYears.contains(year)){
              multimap.put(a.getReleaseYear(),count);}
            doneYears.add(year);
       }

        List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(multimap.entrySet());
        Collections.sort(list, new Comparator<Entry<Integer, Integer>>()
        {
            public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2)
            {return o2.getValue().compareTo(o1.getValue());}});
        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
        for(Entry<Integer, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        List<Integer> results = new ArrayList<>();
        for(Integer year:sortedMap.keySet())
        {
            if(k!=0)
            {
                results.add(year);
                k--;
            }
        }

        return results;
    }

    /**
     * Most similar albums to a give album. The similarity can be defined in a variety of ways.
     * For example, it can be defined over the musicians in albums, the similarity between names
     * of the albums & tracks, etc.
     *
     * @Param k the number of albums to be returned.
     * @Param album
     */

    public List<Album> mostSimilarAlbums(int k, Album album)
    {
        Collection<Album> albums = dao.loadAll(Album.class);
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
            if(musicalInstruments.equals(givenAlbumInstruments) && a.getGenre().equals(givenAlbumGenre)&&k!=0)
            {
                similarAlbums.add(a);
                k--;
            }
        }
          return similarAlbums;
    }
}
