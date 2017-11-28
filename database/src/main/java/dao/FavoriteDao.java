package dao;

import entity.FavoriteEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by chao on 2017-11-24.
 */
@Repository
public interface FavoriteDao extends CrudRepository<FavoriteEntity, Integer> {
    List<FavoriteEntity> findAll();

    List<FavoriteEntity> findByUid(int uid);

    @Query(value = "select * from public.favorite where uid =?1", nativeQuery = true)
    List<FavoriteEntity> findByUidCustom(int uid);
}
