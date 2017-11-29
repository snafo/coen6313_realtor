package dao;

import entity.FavoriteMetricEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteMetricDao extends CrudRepository<FavoriteMetricEntity, Integer>{
}
