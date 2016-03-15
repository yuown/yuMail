package yuown.bulk.repository;

import org.springframework.stereotype.Repository;

import yuown.bulk.entities.Configuration;

import java.util.List;

@Repository
public interface ConfigurationRepository extends BaseRepository<Configuration, Integer> {

    public Configuration findByName(String name);

    public List<Configuration> findByAutoLoad(boolean autoLoad);

}
