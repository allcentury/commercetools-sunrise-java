package common.controllers;

import play.mvc.Call;

public interface ReverseRouter {

    Call home(final String languageTag);

    Call category(final String languageTag, final String categorySlug);

    Call search(final String languageTag);

    Call product(final String locale, final String productSlug, final String sku);

    Call productToCartForm(final String languageTag);

    Call showCheckoutAddressesForm(final String language);

    Call processCheckoutAddressesForm(final String language);

    Call showCheckoutShippingForm(final String language);

    Call processCheckoutShippingForm(final String language);

    Call showCheckoutPaymentForm(String language);

    Call processCheckoutPaymentForm(String language);

    Call showCheckoutConfirmationForm(final String language);

    Call processCheckoutConfirmationForm(final String language);

    Call designAssets(final String file);

    Call showCart(final String language);

    Call showCheckoutThankyou(final String language);

    Call processDeleteLineItem(String language);

    Call processChangeLineItemQuantity(String language);
}