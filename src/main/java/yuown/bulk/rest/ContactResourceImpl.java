package yuown.bulk.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yuown.bulk.entities.Contact;
import yuown.bulk.repository.ContactRepository;
import yuown.bulk.service.ContactService;

@RestController
@RequestMapping(value = "/rest/contacts", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ContactResourceImpl extends AbstractResourceImpl<Integer, Contact, ContactRepository, ContactService> {

    @Autowired
    private ContactService contactService;

    @Override
    public ContactService getService() {
        return contactService;
    }
}
