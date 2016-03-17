package yuown.bulk.repository;

import org.springframework.stereotype.Repository;

import yuown.bulk.entities.Group;

@Repository
public interface GroupRepository extends BaseRepository<Group, Integer> {

    public Group findByName(String name);

}
