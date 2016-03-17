package yuown.bulk.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yuown.bulk.entities.Group;
import yuown.bulk.repository.GroupRepository;
import yuown.bulk.service.GroupService;

@RestController
@RequestMapping(value = "/rest/groups", produces = { MediaType.APPLICATION_JSON_VALUE })
public class GroupResourceImpl extends AbstractResourceImpl<Integer, Group, GroupRepository, GroupService> {

    @Autowired
    private GroupService groupService;

    @Override
    public GroupService getService() {
        return groupService;
    }
}
