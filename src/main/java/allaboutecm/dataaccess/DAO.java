package allaboutecm.dataaccess;

import allaboutecm.model.Album;
import allaboutecm.model.Entity;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.Review;
import allaboutecm.model.Track;
import allaboutecm.model.Webpage;

import java.util.Collection;

public interface DAO {
    <T extends Entity> T load(Class<T> clazz, Long id);

    <T extends Entity> T createOrUpdate(T entity);

    <T extends Entity> Collection<T> loadAll(Class<T> clazz);

    <T extends Entity> void delete(T entity);

    Musician findMusicianByName(String name);

    MusicalInstrument findMusicalInstrumentByName(String name);

    void createOrUpdate(String review);

    void delete(String review);

    Album findAlbumByAlbumName(String albumName);

    Album findAlbumByReleaseYear(int releaseYear);

    Album findAlbumByGenre(String genre);

    Album findAlbumByStyle(String style);

    Track findTrackByName(String name);
}
