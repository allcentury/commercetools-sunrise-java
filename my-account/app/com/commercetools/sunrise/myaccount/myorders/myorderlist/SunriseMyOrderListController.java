package com.commercetools.sunrise.myaccount.myorders.myorderlist;

import com.commercetools.sunrise.common.controllers.WithTemplateName;
import com.commercetools.sunrise.common.ctp.ProductDataConfig;
import com.commercetools.sunrise.common.reverserouter.ProductReverseRouter;
import com.commercetools.sunrise.common.template.i18n.I18nResolver;
import com.commercetools.sunrise.framework.annotations.IntroducingMultiControllerComponents;
import com.commercetools.sunrise.framework.annotations.SunriseRoute;
import com.commercetools.sunrise.myaccount.common.SunriseFrameworkMyAccountController;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.queries.PagedQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static java.util.Arrays.asList;

@IntroducingMultiControllerComponents(SunriseMyOrderListHeroldComponent.class)
public abstract class SunriseMyOrderListController extends SunriseFrameworkMyAccountController implements WithTemplateName {

    private static final Logger logger = LoggerFactory.getLogger(SunriseMyOrderListController.class);

    @Inject
    protected ProductDataConfig productDataConfig;
    @Inject
    protected I18nResolver i18nResolver;
    @Inject
    protected ProductReverseRouter productReverseRouter;

    @Override
    public Set<String> getFrameworkTags() {
        final Set<String> frameworkTags = super.getFrameworkTags();
        frameworkTags.addAll(asList("my-orders", "my-order-list", "order"));
        return frameworkTags;
    }

    @Override
    public String getTemplateName() {
        return "my-account-my-orders";
    }

    @SunriseRoute("myOrderListPageCall")
    public CompletionStage<Result> show(final String languageTag) {
        return doRequest(() -> {
            logger.debug("show my orders in locale={}", languageTag);
            return findOrderList()
                    .thenComposeAsync(this::showOrders, HttpExecution.defaultContext());
        });
    }

    protected CompletionStage<Result> showOrders(final PagedQueryResult<Order> orders) {
        return asyncOk(renderPage(orders));
    }

    protected CompletionStage<Html> renderPage(final PagedQueryResult<Order> orderQueryResult) {
        final MyOrderListPageContent pageContent = injector().getInstance(MyOrderListPageContentFactory.class).create(orderQueryResult);
        return renderPageWithTemplate(pageContent, getTemplateName());
    }

    protected CompletionStage<PagedQueryResult<Order>> findOrderList() {
        final String customerId = requireExistingCustomerId();
        return injector().getInstance(OrderListFinderByCustomerId.class).findOrderList(customerId);
    }
}
