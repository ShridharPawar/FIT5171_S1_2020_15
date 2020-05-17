package allaboutecm.dataaccess;

import allaboutecm.model.*;

import java.util.Collection;

public interface DAO {
    <T extends Entity> T load(Class<T> clazz, Long id);

    <T extends Entity> T createOrUpdate(T entity);

    <T extends Entity> Collection<T> loadAll(Class<T> clazz);

    <T extends Entity> void delete(T entity);

    /*
     * Provide database interface for method findMusicianByName
     */
    Musician findMusicianByName(String name);

    /*
     * Provide database interface for method findMusicalInstrumentByName
     */
    MusicalInstrument findMusicalInstrumentByName(String name);

    /*
     * Provide database interface for method AlbumByAlbumName
     */
    Album findAlbumByAlbumName(String albumName);

    /*
     * Provide database interface for method findAlbumByRecordNumber
     */
    Album findAlbumByRecordNumber(String recordNumber);

    /*
     * Provide database interface for method findAlbumByReleaseYear
     */
    Album findAlbumByReleaseYear(int releaseYear);

    /*
     * Provide database interface for method findAlbumByGenre
     */
    Album findAlbumByGenre(String genre);

    /*
     * Provide database interface for method findAlbumByStyle
     */
    Album findAlbumByStyle(String style);

    /*
     * Provide database interface for method findTrackByName
     */
    Track findTrackByName(String name);

    /*
     * Provide database interface for method findConcertByName
     */
    Concert findConcertByName(String name); //

    /*
     * Provide database interface for method findConcertByCountry
     */
    Concert findConcertByCountry(String country); //

    /*
     * Provide database interface for method findConcertByCity
     */
    Concert findConcertByCity(String city);

}
