package yuown.bulk.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${page.size}")
    private Integer pageSize;

    @Value("${mail.secured}")
    private Boolean isSecured;

    @Value("${mail.smtp.host}")
    private String mailHost;

    @Value("${mail.auth.required}")
    private Boolean mailAuthRequired;

    @Value("${mail.user.name}")
    private String mailUsername;

    @Value("${mail.user.pass}")
    private String mailPassword;

    @Value("${mail.message.from.default}")
    private String messageFromDefault;

    @Value("${mail.smtp.starttls.enable}")
    private Boolean enableTls;

    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @Value("${mail.reply.to}")
    private String mailReplyTo;

    @Value("${mail.debug}")
    private Boolean enableDebug;

    @Value("${proxy.enabled}")
    private Boolean isProxyEnabled;

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private String proxyPort;

    public Configuration getByName(String name) {
        return repository().findByName(name);
    }

    @PostConstruct
    public void init() {
        addConfigItemIfNotFound("page.size");
        addConfigItemIfNotFound("mail.secured");
        addConfigItemIfNotFound("mail.smtp.host");
        addConfigItemIfNotFound("mail.auth.required");
        addConfigItemIfNotFound("mail.user.name");
        addConfigItemIfNotFound("mail.user.pass");
        addConfigItemIfNotFound("mail.message.from.default");
        addConfigItemIfNotFound("mail.smtp.starttls.enable");
        addConfigItemIfNotFound("mail.smtp.port");
        addConfigItemIfNotFound("mail.reply.to");
        addConfigItemIfNotFound("mail.debug");
        addConfigItemIfNotFound("proxy.enabled");
        addConfigItemIfNotFound("proxy.host");
        addConfigItemIfNotFound("proxy.port");

        cacheConfigItems();
    }

    private void addConfigItemIfNotFound(String configName) {
        Configuration item = getByName(configName);
        if (null == item) {
            item = new Configuration();
            item.setName(configName);
            item.setDeletable(false);
            item.setAutoLoad(true);
            if (configName.equals("page.size")) {
                item.setValue(pageSize);
            } else if (configName.equals("mail.smtp.host")) {
                item.setStrValue(mailHost);
            } else if (configName.equals("mail.auth.required")) {
                item.setBoolValue(mailAuthRequired);
            } else if (configName.equals("mail.user.name")) {
                item.setStrValue(mailUsername);
            } else if (configName.equals("mail.user.pass")) {
                item.setStrValue(mailPassword);
            } else if (configName.equals("mail.message.from.default")) {
                item.setStrValue(messageFromDefault);
            } else if (configName.equals("mail.smtp.starttls.enable")) {
                item.setBoolValue(enableTls);
            } else if (configName.equals("mail.smtp.port")) {
                item.setValue(smtpPort);
            } else if (configName.equals("mail.reply.to")) {
                item.setStrValue(mailReplyTo);
            } else if (configName.equals("mail.secured")) {
                item.setBoolValue(isSecured);
            } else if (configName.equals("mail.debug")) {
                item.setBoolValue(enableDebug);
            } else if (configName.equals("proxy.enabled")) {
                item.setBoolValue(isProxyEnabled);
            } else if (configName.equals("proxy.host")) {
                item.setStrValue(proxyHost);
            } else if (configName.equals("proxy.port")) {
                item.setStrValue(proxyPort);
            }
            repository().save(item);
        }
    }

    @Override
    public Configuration save(Configuration resource, HashMap<String, Object> customParams) throws Exception {
        Configuration newC = getByName(resource.getName());
        if (newC != null && resource.getId() == null) {
            throw new Exception("Configuration Item with name '" + resource.getName() + "' already exists");
        }
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
                if (valueModel.getBoolValue() != null) {
                    System.setProperty(name, valueModel.getBoolValue().toString());
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
