package allaboutecm.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
public class MusicalInstrument extends Entity {
    @Property(name="name")
    private String name;

    public MusicalInstrument() {
    }

    public MusicalInstrument(String name) {
        notNull(name);
        notBlank(name);
        if(name.length()>40)
        {
            throw new IllegalArgumentException("Instrument name length cannot be more than 40 characters.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        notNull(name);
        notBlank(name);
        if(name.length()>40)
        {
            throw new IllegalArgumentException("Instrument name length cannot be more than 40 characters.");
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicalInstrument that = (MusicalInstrument) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
