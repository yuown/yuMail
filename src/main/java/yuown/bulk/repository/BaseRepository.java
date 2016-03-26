package yuown.bulk.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import yuown.bulk.entities.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity<ID>, ID extends Serializable> extends JpaRepository<E, ID> {

	public E findById(ID id);

	public Page<E> findAllByNameLike(String string, Pageable pageRequest);

	public List<E> findAllByEnabled(Boolean enabled);

}