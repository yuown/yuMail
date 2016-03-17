package yuown.bulk.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Configuration;
import yuown.bulk.repository.ConfigurationRepository;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ConfigurationService extends AbstractServiceImpl<Integer, Configuration, ConfigurationRepository> {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public Configuration getByName(String name) {
        return repository().findByName(name);
    }

    @PostConstruct
    public void init() {
        cacheConfigItems();
    }

    @Override
    public Configuration save(Configuration resource, HashMap<String, Object> customParams) {
        Configuration saved = super.save(resource, customParams);
        if (null != saved.getAutoLoad() && saved.getAutoLoad()) {
            cacheConfigItem(saved.getName(), saved);
        } else {
            clearFromCache(saved);
        }
        return saved;
    }

    public void cacheConfigItem(String name, Configuration valueModel) {
        int value = 0;
        if (valueModel != null) {
            try {
                value = valueModel.getValue() != null ? valueModel.getValue() : 0;
                System.setProperty(name, Integer.toString(value));

                if (StringUtils.isNotBlank(valueModel.getStrValue())) {
                    System.setProperty(name, valueModel.getStrValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cacheConfigItems() {
        List<Configuration> startupItems = repository().findByAutoLoad(true);
        for (Configuration configurationModel : startupItems) {
            cacheConfigItem(configurationModel.getName(), configurationModel);
        }
    }

    public int getIntPropertyFromCache(String name) {
        int returnValue = 0;
        try {
            Configuration fromDb = getByName(name);
            if (fromDb != null) {
                returnValue = fromDb.getValue().intValue();
                fromDb.setAutoLoad(true);
                save(fromDb, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public void remove(Configuration item) {
        clearFromCache(item);
        repository().delete(item);
    }

    private void clearFromCache(Configuration saved) {
        System.clearProperty(saved.getName());
    }

    @Override
    public ConfigurationRepository repository() {
        return configurationRepository;
    }
}
