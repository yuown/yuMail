package yuown.bulk.rest;

import yuown.bulk.entities.Configuration;
import yuown.bulk.repository.ConfigurationRepository;
import yuown.bulk.service.ConfigurationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/settings", produces = { MediaType.APPLICATION_JSON_VALUE })
public class SettingsResourceImpl extends AbstractResourceImpl<Integer, Configuration, ConfigurationRepository, ConfigurationService> {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public ConfigurationService getService() {
        return configurationService;
    }
}
