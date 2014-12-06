package io.github.jcfandino;

import static com.google.common.collect.Collections2.filter;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.github.jcfandino.spring.Dropwizard;
import io.github.jcfandino.spring.KiviSpringConf;
import io.github.jcfandino.spring.ParentDropwizardConfig;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class KiviApp extends Application<KiviConf> {
    private static final Logger LOG = LoggerFactory.getLogger(KiviApp.class);
    private ApplicationContext springContext;

    public static void main(String[] args) throws Exception {
        String[] completeArgs = args;
        if (args.length < 2) {
            String baseDir = System.getProperty("app.home", "src/main/resources");
            completeArgs = new String[] { "server", baseDir + "/conf/config.yaml" };
        }
        new KiviApp().run(completeArgs);
    }

    @Override
    public void initialize(Bootstrap<KiviConf> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(KiviConf configuration, Environment environment) throws Exception {
        initSpringContext(configuration, environment);

        registerResources(environment);
        registerProviders(environment);
        registerHealthChecks(environment);

        environment.servlets()
                .addFilter("AcceptLanguageServletFilter", new AcceptLanguageServletFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }

    public void initSpringContext(KiviConf configuration, Environment environment) {
        AnnotationConfigApplicationContext parentContext =
                new AnnotationConfigApplicationContext(ParentDropwizardConfig.class);
        Dropwizard dropwizard = parentContext.getBean(Dropwizard.class);
        dropwizard.setConfiguration(configuration);
        dropwizard.setEnvironment(environment);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(parentContext);
        context.register(KiviSpringConf.class);
        context.refresh();
        springContext = context;
    }

    public void registerHealthChecks(Environment environment) {
        Map<String, HealthCheck> healthChecks = springContext.getBeansOfType(HealthCheck.class);
        for (Entry<String, HealthCheck> healthCheck : healthChecks.entrySet()) {
            LOG.info("Registering health check {}", healthCheck.getKey());
            environment.healthChecks().register(healthCheck.getKey(), healthCheck.getValue());
        }
    }

    public void registerProviders(Environment environment) {
        Map<String, Object> providerBeans = springContext.getBeansWithAnnotation(Provider.class);
        for (Entry<String, Object> provider : providerBeans.entrySet()) {
            LOG.info("Registering provider {}", provider.getKey());
            environment.jersey().register(provider.getValue());
        }
    }

    public void registerResources(Environment environment) {
        Map<String, Object> beansWithPath = springContext.getBeansWithAnnotation(Path.class);
        Predicate<Object> predicate = new Predicate<Object>() {
            private Set<String> allowedPackages = Sets.newHashSet("io.github.jcfandino");

            public boolean apply(Object bean) {
                Package pkg = bean.getClass().getPackage();
                return pkg != null && allowedPackages.contains(pkg.getName());
            }
        };
        Collection<Object> resources = filter(beansWithPath.values(), predicate);
        for (Object resource : resources) {
            LOG.info("Registering resource {}", resource.getClass());
            environment.jersey().register(resource);
        }
    }
}
