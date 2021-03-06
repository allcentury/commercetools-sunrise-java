package com.commercetools.sunrise.shoppingcart.cart.changelineitemquantity;

import com.commercetools.sunrise.common.controllers.WithFormFlow;
import com.commercetools.sunrise.common.controllers.WithTemplateName;
import com.commercetools.sunrise.framework.annotations.IntroducingMultiControllerComponents;
import com.commercetools.sunrise.framework.annotations.SunriseRoute;
import com.commercetools.sunrise.shoppingcart.cart.SunriseCartManagementController;
import com.commercetools.sunrise.shoppingcart.cart.cartdetail.CartDetailPageContent;
import com.commercetools.sunrise.shoppingcart.cart.cartdetail.CartDetailPageContentFactory;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.ChangeLineItemQuantity;
import io.sphere.sdk.client.ClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static java.util.Arrays.asList;

@IntroducingMultiControllerComponents(SunriseChangeLineItemQuantityHeroldComponent.class)
public abstract class SunriseChangeLineItemQuantityController extends SunriseCartManagementController implements WithTemplateName, WithFormFlow<ChangeLineItemQuantityFormData, Cart, Cart> {

    private static final Logger logger = LoggerFactory.getLogger(SunriseChangeLineItemQuantityController.class);

    @Override
    public Set<String> getFrameworkTags() {
        final Set<String> frameworkTags = super.getFrameworkTags();
        frameworkTags.addAll(asList("cart", "manage-cart", "change-line-item-quantity"));
        return frameworkTags;
    }

    @Override
    public String getTemplateName() {
        return "cart";
    }

    @Override
    public Class<? extends ChangeLineItemQuantityFormData> getFormDataClass() {
        return DefaultChangeLineItemQuantityFormData.class;
    }

    @SunriseRoute("processChangeLineItemQuantityForm")
    public CompletionStage<Result> changeLineItemQuantity(final String languageTag) {
        return doRequest(() -> findCart()
                .thenComposeAsync(cartOptional -> cartOptional
                        .map(this::validateForm)
                        .orElseGet(this::redirectToCartDetail), HttpExecution.defaultContext()));
    }

    @Override
    public CompletionStage<? extends Cart> doAction(final ChangeLineItemQuantityFormData formData, final Cart cart) {
        return changeLineItemQuantity(cart, formData.getLineItemId(), formData.getQuantity());
    }

    @Override
    public CompletionStage<Result> handleClientErrorFailedAction(final Form<? extends ChangeLineItemQuantityFormData> form, final Cart cart, final ClientErrorException clientErrorException) {
        saveUnexpectedFormError(form, clientErrorException, logger);
        return asyncBadRequest(renderPage(form, cart, null));
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final ChangeLineItemQuantityFormData formData, final Cart cart, final Cart updatedCart) {
        return redirectToCartDetail();
    }

    @Override
    public CompletionStage<Html> renderPage(final Form<? extends ChangeLineItemQuantityFormData> form, final Cart cart, @Nullable final Cart updatedCart) {
        final Cart cartToRender = Optional.ofNullable(updatedCart).orElse(cart);
        final CartDetailPageContent pageContent = injector().getInstance(CartDetailPageContentFactory.class).create(cartToRender);
        return renderPageWithTemplate(pageContent, getTemplateName());
    }

    @Override
    public void fillFormData(final ChangeLineItemQuantityFormData formData, final Cart cart) {
        // Do nothing
    }

    protected CompletionStage<Cart> changeLineItemQuantity(final Cart cart, final String lineItemId, final long quantity) {
        final ChangeLineItemQuantity changeLineItemQuantity = ChangeLineItemQuantity.of(lineItemId, quantity);
        final CartUpdateCommand cmd = CartUpdateCommand.of(cart, changeLineItemQuantity);
        return executeCartUpdateCommandWithHooks(cmd);
    }
}
