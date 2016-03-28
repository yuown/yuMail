package yuown.bulk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.RequestEntry;
import yuown.bulk.repository.AttachmentRepository;
import yuown.bulk.repository.RequestEntryRepository;

import java.util.List;
import java.util.Set;

@Service
public class RequestEntryService extends AbstractServiceImpl<Integer, RequestEntry, RequestEntryRepository> {

    @Autowired
    private RequestEntryRepository requestEntryRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public RequestEntryRepository repository() {
        return requestEntryRepository;
    }

    public Set<Integer> getAllRequestIds() {
        return repository().findAllRequestId();
    }

    public List<RequestEntry> getStatusByRequest(Integer id) {
        return repository().findAllByRequestId(id);
    }

    public void deleteStatusByRequest(Integer id) {
        List<RequestEntry> all = getStatusByRequest(id);
        for (RequestEntry requestEntry : all) {
            attachmentRepository.delete(requestEntry.getAttachments());
        }
        repository().deleteAllByRequestId(id);
    }
}
