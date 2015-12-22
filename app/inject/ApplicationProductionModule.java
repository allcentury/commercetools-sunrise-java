package inject;

import com.google.inject.AbstractModule;
import common.cms.CmsService;
import common.controllers.ReverseRouter;
import common.templates.TemplateService;
import pages.ReverseRouterImpl;

import javax.inject.Singleton;

/**
 * Configuration for the Guice {@link com.google.inject.Injector} which
 * shall be used in production and integration tests.
 */
public class ApplicationProductionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TemplateService.class).toProvider(TemplateServiceProvider.class).in(Singleton.class);
        bind(CmsService.class).toProvider(CmsServiceProvider.class).in(Singleton.class);
        bind(ReverseRouter.class).toInstance(new ReverseRouterImpl());
    }
}