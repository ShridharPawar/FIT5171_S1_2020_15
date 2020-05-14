package allaboutecm.dataaccess;

import allaboutecm.model.*;

import java.util.Collection;

public interface DAO {
    <T extends Entity> T load(Class<T> clazz, Long id);

    <T extends Entity> T createOrUpdate(T entity);

    <T extends Entity> Collection<T> loadAll(Class<T> clazz);

    <T extends Entity> void delete(T entity);

    Musician findMusicianByName(String name);

    MusicalInstrument findMusicalInstrumentByName(String name);

    Album findAlbumByAlbumName(String albumName);

    Album findAlbumByRecordNumber(String recordNumber);

    Album findAlbumByReleaseYear(int releaseYear);

    Album findAlbumByGenre(String genre);

    Album findAlbumByStyle(String style);

    Track findTrackByName(String name);

    Concert findConcertByName(String name);

    Concert findConcertByCountry(String country);

    Concert findConcertByCity(String city);
}
