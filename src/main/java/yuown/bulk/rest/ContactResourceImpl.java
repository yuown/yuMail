package yuown.bulk.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import yuown.bulk.entities.Contact;
import yuown.bulk.entities.Group;
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

	@RequestMapping(method = RequestMethod.GET, value = "/{id}/groups")
	@ResponseBody
	public List<Group> getContactGroups(@PathVariable(value = "id") Integer contactId) {
		return getService().getContactGroups(contactId);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}/groups/{groupId}")
	@ResponseBody
	public void removeContactFromGroup(@PathVariable(value = "id") Integer contactId, @PathVariable(value = "groupId") Integer groupId) {
		getService().removeContactFromGroup(contactId, groupId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/byGroup/{id}")
	@ResponseBody
	public List<Contact> getContactsByGroup(@PathVariable(value = "id") Integer groupId, @RequestParam(value = "enabled", required = false) Boolean enabled) {
		return getService().getContactsByGroup(groupId, enabled);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/import/{groupId}")
	public void importContacts(@RequestBody ArrayList<Contact> contacts, @PathVariable(value = "groupId") Integer groupId) {
		getService().importContacts(contacts, groupId);
	}
}
