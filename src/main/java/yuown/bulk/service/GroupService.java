package yuown.bulk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Contact;
import yuown.bulk.entities.Group;
import yuown.bulk.repository.GroupRepository;

import java.util.HashMap;
import java.util.List;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GroupService extends AbstractServiceImpl<Integer, Group, GroupRepository> {

    @Autowired
    private GroupRepository groupRepository;

    public Group getByName(String name) {
        return repository().findByName(name);
    }

    @Override
    public GroupRepository repository() {
        return groupRepository;
    }

    @Override
    public Group save(Group group, HashMap<String, Object> customParams) {
//        if (null != group.getId()) {
//            Group fromDb = repository().findOne(group.getId());
//            List<Contact> existing = fromDb.getContacts();
//            group.getContacts().addAll(existing);
//        }
        return repository().save(group);
    }
}
