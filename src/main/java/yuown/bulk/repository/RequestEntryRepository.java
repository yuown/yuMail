package yuown.bulk.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yuown.bulk.entities.RequestEntry;

import java.util.List;
import java.util.Set;

@Repository
public interface RequestEntryRepository extends BaseRepository<RequestEntry, Integer> {

    @Query("select distinct R.requestId from RequestEntry R")
    public Set<Integer> findAllRequestId();

    public List<RequestEntry> findAllByRequestId(Integer id);

    public void deleteAllByRequestId(Integer id);

}