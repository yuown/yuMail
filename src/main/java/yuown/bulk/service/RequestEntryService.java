package yuown.bulk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.RequestEntry;
import yuown.bulk.model.MailStatus;
import yuown.bulk.repository.AttachmentRepository;
import yuown.bulk.repository.RequestEntryRepository;

@Service
public class RequestEntryService extends AbstractServiceImpl<Integer, RequestEntry, RequestEntryRepository> {

	@Autowired
	private RequestEntryRepository requestEntryRepository;

	@Autowired
	private AttachmentRepository attachmentRepository;

	@Autowired
	private MailSenderHelper mailSenderHelper;

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

	public void retryByRequestId(Integer id) {
		RequestEntry entry = requestEntryRepository.findById(id);
		if (null != entry) {
			if (StringUtils.equals(entry.getStatus(), MailStatus.FAILED.name())) {
				List<RequestEntry> entries = new ArrayList<RequestEntry>();
				entries.add(entry);
				mailSenderHelper.submitAsync(entries);
			}
		}
	}
}
