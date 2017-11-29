package entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user", schema = "public")
public class UserEntity {
    private int id;
    private String name;
    private String password;
    private FavoriteMetricEntity favoriteMetric;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="user_id_seq", sequenceName="user_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator="user_id_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "user")
    public FavoriteMetricEntity getFavoriteMetric() {
        return favoriteMetric;
    }

    public void setFavoriteMetric(FavoriteMetricEntity favoriteMetric) {
        this.favoriteMetric = favoriteMetric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
