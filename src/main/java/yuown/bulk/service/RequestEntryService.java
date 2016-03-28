package yuown.bulk.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.RequestEntry;
import yuown.bulk.repository.RequestEntryRepository;

@Service
public class RequestEntryService extends AbstractServiceImpl<Integer, RequestEntry, RequestEntryRepository> {

	@Autowired
	private RequestEntryRepository requestEntryRepository;

	@Override
	public RequestEntryRepository repository() {
		return requestEntryRepository;
	}

	public Set<Integer> getAllRequestIds() {
		return repository().findAllRequestId();
	}
}
