package yuown.bulk.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import yuown.bulk.entities.Contact;
import yuown.bulk.entities.Group;

@Repository
public interface ContactRepository extends BaseRepository<Contact, Integer> {

	public Contact findByName(String name);

	public List<Contact> findAllByGroupsAndEnabled(Group group, Boolean enabled);

	public List<Contact> findAllByGroups(Group group);

	public Page<Contact> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageRequest);
}
