package yuown.bulk.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import yuown.bulk.entities.RequestEntry;
import yuown.bulk.repository.RequestEntryRepository;
import yuown.bulk.service.RequestEntryService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/rest/status", produces = { MediaType.APPLICATION_JSON_VALUE })
public class RequestEntryResourceImpl extends AbstractResourceImpl<Integer, RequestEntry, RequestEntryRepository, RequestEntryService> {

    @Autowired
    private RequestEntryService requestEntryService;

    @Override
    public RequestEntryService getService() {
        return requestEntryService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ids")
    @ResponseBody
    public Set<Integer> getRequestIds() {
        return requestEntryService.getAllRequestIds();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/request/{id}")
    @ResponseBody
    public List<RequestEntry> getStatusByRequest(@PathVariable("id") Integer id) {
        return requestEntryService.getStatusByRequest(id);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/request/{id}")
    @ResponseBody
    public void deleteStatusByRequest(@PathVariable("id") Integer id) {
        requestEntryService.deleteStatusByRequest(id);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/request/retry/{id}")
    public void retryByRequestId(@PathVariable("id") Integer id) {
        requestEntryService.retryByRequestId(id);
    }
}
