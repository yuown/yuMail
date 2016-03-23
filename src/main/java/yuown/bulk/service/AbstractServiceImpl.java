package yuown.bulk.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import yuown.bulk.entities.BaseEntity;
import yuown.bulk.repository.BaseRepository;
import yuown.bulk.rest.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Transactional
public abstract class AbstractServiceImpl<ID extends Serializable, E extends BaseEntity<ID>, R extends BaseRepository<E, ID>> {

    public abstract R repository();

    public E save(E entity, HashMap<String, Object> customParams) throws Exception {
        return repository().save(entity);
    }

    public List<E> findAll() {
        return repository().findAll();
    }

    public void delete(E entity) {
        repository().delete(entity);
    }

    public List<E> saveAll(List<E> entities) {
        return repository().save(entities);
    }

    public E findById(ID id) {
        return repository().findOne(id);
    }

    public Page<E> search(String name, Integer page, Integer size) {
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
            return repository().findAllByNameLike(name.toUpperCase(), pageRequest);
        } else {
            return repository().findAll(pageRequest);
        }
    }
}