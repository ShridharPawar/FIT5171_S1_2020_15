package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by maskfox on 4/16/20
 *
 * @author maskfox
 */

public class GroupUnitTest {
    private Group group;
    private String groupName = "JAN GARBAREK QUARTET";
    private Long id = 9L;
    private Album album = new Album(1971, "ECM 1007", "AFRIC PEPPERBIRD");
    private Set<Album> albums = Sets.newLinkedHashSet();
    private int hash = 77106402;

    @BeforeEach
    public void setUp()
    {
        group = new Group(groupName);
        group.setAlbums(albums);
    }

    //getGroupName
    @DisplayName("GroupName should be same as assigned")
    @Test
    public void groupNameShouldBeSameAsAssigned(){
        assertEquals(groupName, group.getGroupName());
    }

    //setGroupName
    @DisplayName("GroupName Should Be Correctly Set")
    @Test
    public void groupNameShouldBeCorrectlySet(){
        String newGroupName = "JAN GARBAREK QUARTET";
        group.setGroupName(newGroupName);
        assertEquals(groupName, group.getGroupName());
    }

    @DisplayName("Group name cannot be null")
    @Test
    public void groupNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> group.setGroupName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Group name cannot be empty or blank")
    void groupNameCannotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> group.setGroupName(arg));
    }

    //getAlbums()
    @DisplayName("AlbumSet should be same as assigned")
    @Test
    public void albumSetShouldBeSameAsAssigned(){
        albums.add(album);
        assertEquals(albums, group.getAlbums());
    }

    //setAlbums()
    @DisplayName("AlbumSet Should Be Correctly Set")
    @Test
    public void setAlbumSetNeedToBeCorrectlySet() {
        Album album1 = new Album(1971, "ECM 1007", "AFRIC PEPPERBIRD");
        albums.add(album1);
        group.setAlbums(albums);
        assertEquals(albums, group.getAlbums());
    }

    @Test
    @DisplayName("Check if any object within the set is null.")
    public void nullObjectInAlbum(){
        Set<Album> albums = Sets.newHashSet();
        albums.add(null);
        assertThrows(NullPointerException.class,()->group.setAlbums(albums));
    }

    // equals()
    @DisplayName("Same name means same Group")
    @Test
    public void sameNameMeansSameGroup() {
        Group newGroup = new Group(groupName);
        assertEquals(group, newGroup);
    }
}
