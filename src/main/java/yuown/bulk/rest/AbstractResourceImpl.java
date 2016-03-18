package yuown.bulk.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import yuown.bulk.entities.BaseEntity;
import yuown.bulk.repository.BaseRepository;
import yuown.bulk.service.AbstractServiceImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractResourceImpl<ID extends Serializable, E extends BaseEntity<ID>, R extends BaseRepository<E, ID>, S extends AbstractServiceImpl<ID, E, R>> {

    public abstract S getService();

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<E> save(@RequestBody E entity, @RequestHeader(value = "customparams", required = false) HashMap<String, Object> customParams) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus = null;
        try {
            entity = getService().save(entity, customParams);
            responseStatus = HttpStatus.OK;
        } catch (Exception e) {
            responseStatus = HttpStatus.BAD_REQUEST;
            e.printStackTrace();
        }
        return new ResponseEntity<E>(entity, headers, responseStatus);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public E getById(@PathVariable("id") ID id) {
        return getService().findById(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<String> removeById(@PathVariable("id") ID id) {
        E item = getService().findById(id);
        HttpHeaders headers = new HttpHeaders();
        if (null == item) {
            headers.add("errorMessage", "Entity with ID " + id + " Not Found");
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } else {
            try {
                getService().delete(item);
                return new ResponseEntity<String>(headers, HttpStatus.OK);
            } catch (Exception e) {
                headers.add("errorMessage", "Entity with ID " + id + " cannot be Deleted");
                return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<E>> getAll(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "paged", required = false) Boolean paged) {
        HttpHeaders headers = new HttpHeaders();
        Page<E> pagedItems = null;

        List<E> items = null;

        if (paged != null && paged == true) {
            pagedItems = getService().search(name, page, size);
            items = pagedItems.getContent();

            headers.add("pages", pagedItems.getTotalPages() + StringUtils.EMPTY);
            headers.add("totalItems", pagedItems.getTotalElements() + StringUtils.EMPTY);
        } else {
            items = getService().findAll();
        }
        return new ResponseEntity<List<E>>(items, headers, HttpStatus.OK);
    }
}
