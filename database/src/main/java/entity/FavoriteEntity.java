package entity;

import com.sun.javafx.beans.IDProperty;

import javax.persistence.*;

@Entity
@Table(name = "favorite", schema = "public")
public class FavoriteEntity {
    private int uid;
    private String propertyId;

    @Id
    @Column(name = "uid")
    @SequenceGenerator(name="user_id_seq", sequenceName="user_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator="user_id_seq")
    public int getUid() {
        return uid;
    }

    @Id
    @Column(name = "propertyId")
    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public void setUid(int uid) { this.uid = uid; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FavoriteEntity that = (FavoriteEntity) o;

        if (uid != that.uid) return false;
        if (propertyId != null ? !propertyId.equals(that.propertyId) : that.propertyId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + (propertyId != null ? propertyId.hashCode() : 0);
        return result;
    }

}
