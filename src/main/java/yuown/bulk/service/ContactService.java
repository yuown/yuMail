package yuown.bulk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Contact;
import yuown.bulk.entities.Group;
import yuown.bulk.model.IdValueModel;
import yuown.bulk.repository.ContactRepository;
import yuown.bulk.repository.GroupRepository;
import yuown.bulk.rest.Constants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContactService extends AbstractServiceImpl<Integer, Contact, ContactRepository> {

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupService groupService;

	public Contact getByName(String name) {
		return repository().findByName(name);
	}

	@Override
	public ContactRepository repository() {
		return contactRepository;
	}

	@Override
	public Contact save(Contact contact, HashMap<String, Object> customParams) {
		ObjectMapper mapper = new ObjectMapper();

		List<IdValueModel> contactGroups = new ArrayList<IdValueModel>();
		try {
			Object params = customParams.get("customparams");
			if (null != params) {
				contactGroups = mapper.readValue(params.toString(), new TypeReference<List<IdValueModel>>() {
				});
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Contact fromDb = new Contact();
		if (contact.getId() != null) {
			fromDb = repository().findById(contact.getId());
		}
		fromDb.setEmail(contact.getEmail());
		fromDb.setEnabled(contact.isEnabled());
		fromDb.setName(contact.getName());

		if (null != contactGroups && contactGroups.size() > 0) {
			for (IdValueModel groupId : contactGroups) {
				Group e = groupRepository.findOne(groupId.getId());
				if (e != null) {
					if (!fromDb.getGroups().contains(e)) {
						fromDb.getGroups().add(e);
						e.getContacts().add(fromDb);
					}
				}
			}
		}
		return repository().save(fromDb);
	}

	public List<Group> getContactGroups(Integer contactId) {
		List<Group> groups = new ArrayList<Group>();
		if (contactId != null) {
			Contact contact = repository().findById(contactId);
			if (contact != null) {
				groups.addAll(contact.getGroups());
			}
		}
		return groups;
	}

	public void removeContactFromGroup(Integer contactId, Integer groupId) {
		List<Group> groups = null;
		if (contactId != null) {
			Contact contact = repository().findById(contactId);
			if (contact != null) {
				groups = contact.getGroups();
				Group assignedGroup = isGroupAssigned(groups, groupId);
				if (assignedGroup != null) {
					groups.remove(assignedGroup);
					assignedGroup.getContacts().remove(contact);
					repository().save(contact);
				}
			}
		}
	}

	public List<Contact> getContactsByGroup(Integer groupId, Boolean enabled) {
		List<Contact> byGroup = new ArrayList<Contact>();
		if (null != groupId) {
			Group group = groupRepository.findById(groupId);
			if (null != group) {
				if(null != enabled) {
					byGroup.addAll(repository().findAllByGroupsAndEnabled(group, enabled));
				} else {
					byGroup.addAll(repository().findAllByGroups(group));
				}
			}
		}
		return byGroup;
	}

	@Override
	public void delete(Contact entity) {
		Contact contact = repository().findById(entity.getId());
		if (null != contact) {
			List<Group> groups = contact.getGroups();
			for (Group group : groups) {
				group.getContacts().remove(contact);
			}
			repository().delete(contact);
		}
	}

	public void importContacts(ArrayList<Contact> contacts, Integer groupId) {
		if (null != groupId && null != contacts && contacts.size() > 0) {
			Group group = groupRepository.findById(groupId);
			if (null != group) {
				List<Contact> toSave = new ArrayList<Contact>();
				for (Contact contact : contacts) {
					if (null != contact) {
						contact.setEnabled(true);
						contact.setGroups(new ArrayList<Group>());
						contact.getGroups().add(group);
						contact.setId(null);
						toSave.add(contact);
						group.getContacts().add(contact);
					}
				}
				repository().save(toSave);
			}
		}
	}

	private Group isGroupAssigned(List<Group> groups, Integer groupId) {
		for (Group group : groups) {
			if (group.getId() == groupId) {
				return group;
			}
		}
		return null;
	}
	
	@Override
	public Page<Contact> search(String name, Integer page, Integer size) {
		if (page == null || page < 0) {
            page = 0;
        }

        Integer fromSystem = 10;
        try {
            fromSystem = Integer.parseInt(System.getProperty(Constants.PAGE_SIZE));
        } catch (Exception e) {
        }
        if (size == null || (size < 0 || size > fromSystem)) {
            size = fromSystem;
        }
        PageRequest pageRequest = new PageRequest(page, size);
        if (StringUtils.isNotBlank(name)) {
            return repository().findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, name, pageRequest);
        } else {
            return repository().findAll(pageRequest);
        }
	}
}
