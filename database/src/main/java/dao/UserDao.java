package dao;

import entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by qinyu on 2017-11-24.
 */
@Repository
public interface UserDao extends CrudRepository<UserEntity, Integer> {
    List<UserEntity> findAll();

    List<UserEntity> findByName(String name);

    UserEntity findById(Integer id);

    @Query(value = "select * from public.user where name =?1", nativeQuery = true)
    List<UserEntity> findByNameCustom(String name);
}
