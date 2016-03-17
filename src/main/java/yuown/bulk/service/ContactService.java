package yuown.bulk.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
            contactGroups = mapper.readValue(customParams.get("customparams").toString(), new TypeReference<List<IdValueModel>>() {
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (null != contactGroups && contactGroups.size() > 0) {
            for (IdValueModel groupId : contactGroups) {
                Group e = groupRepository.findOne(groupId.getId());
                if (e != null) {
                    contact.getGroups().add(e);
                }
            }
        }
        return repository().save(contact);
    }
}