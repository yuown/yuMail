package yuown.bulk.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import yuown.bulk.entities.Contact;
import yuown.bulk.entities.Group;
import yuown.bulk.model.IdValueModel;
import yuown.bulk.repository.ContactRepository;
import yuown.bulk.repository.GroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                        groupRepository.save(e);
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
                    groupRepository.save(assignedGroup);
                }
            }
        }
    }
    
    public List<Contact> getContactsByGroup(Integer groupId) {
    	List<Contact> byGroup = new ArrayList<Contact>();
    	if(null != groupId) {
    		Group group = groupRepository.findById(groupId);
    		if(null != group) {
    			byGroup.addAll(repository().findAllByGroups(group));
    		}
    	}
    	return byGroup;
    }

    private Group isGroupAssigned(List<Group> groups, Integer groupId) {
        for (Group group : groups) {
            if (group.getId() == groupId) {
                return group;
            }
        }
        return null;
    }
}
