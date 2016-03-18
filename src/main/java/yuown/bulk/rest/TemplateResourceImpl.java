package yuown.bulk.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yuown.bulk.entities.Template;
import yuown.bulk.repository.TemplateRepository;
import yuown.bulk.service.TemplateService;

@RestController
@RequestMapping(value = "/rest/templates", produces = { MediaType.APPLICATION_JSON_VALUE })
public class TemplateResourceImpl extends AbstractResourceImpl<Integer, Template, TemplateRepository, TemplateService> {

    @Autowired
    private TemplateService templateService;

    @Override
    public TemplateService getService() {
        return templateService;
    }
}
