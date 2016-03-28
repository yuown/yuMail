package yuown.bulk.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yuown.bulk.entities.RequestEntry;

@Repository
public interface RequestEntryRepository extends BaseRepository<RequestEntry, Integer> {

	@Query("select distinct R.requestId from RequestEntry R")
	public Set<Integer> findAllRequestId();

}