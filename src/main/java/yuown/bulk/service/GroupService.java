package yuown.bulk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Group;
import yuown.bulk.repository.GroupRepository;

import java.util.HashMap;

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
        Group fromDb = new Group();
        if (group.getId() != null) {
            fromDb = repository().findById(group.getId());
        }
        fromDb.setEnabled(group.isEnabled());
        fromDb.setName(group.getName());

        group = repository().save(fromDb);

        return group;
    }
}
