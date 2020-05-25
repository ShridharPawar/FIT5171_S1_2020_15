package allaboutecm.model;


import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Created by maskfox on 4/16/20
 *
 * @author maskfox
 */
@NodeEntity
public class Group extends Entity {
    @Property
    private String groupName;

    @Relationship(type = "albums")
    private Set<Album> albums;

    public Group(String groupName) {
        this.groupName = groupName;
        albums = new LinkedHashSet<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        notBlank(groupName);
        notNull(groupName);
        this.groupName = groupName;
    }
    
    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        notNull(albums);
        for(Album a:albums)
        {
            if(a.equals(null))
            {
                throw new NullPointerException("Object within the Review set should not be null.");
            }
        }
        this.albums = albums;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group that = (Group) o;
        return groupName.equals(that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName);
    }
}
