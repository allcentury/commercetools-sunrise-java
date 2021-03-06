package com.commercetools.sunrise.hooks.requests;

import com.commercetools.sunrise.hooks.HookRunner;
import io.sphere.sdk.customers.queries.CustomerQuery;

public interface CustomerQueryHook extends RequestHook {

    CustomerQuery onCustomerQuery(final CustomerQuery customerQuery);

    static CustomerQuery runHook(final HookRunner hookRunner, final CustomerQuery customerQuery) {
        return hookRunner.runUnaryOperatorHook(CustomerQueryHook.class, CustomerQueryHook::onCustomerQuery, customerQuery);
    }
}
