package yuown.bulk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Template;
import yuown.bulk.repository.TemplateRepository;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TemplateService extends AbstractServiceImpl<Integer, Template, TemplateRepository> {

    @Autowired
    private TemplateRepository templateRepository;

    @Override
    public TemplateRepository repository() {
        return templateRepository;
    }
}
