package yuown.bulk.repository;

import org.springframework.stereotype.Repository;

import yuown.bulk.entities.Contact;

@Repository
public interface ContactRepository extends BaseRepository<Contact, Integer> {

    public Contact findByName(String name);

}
