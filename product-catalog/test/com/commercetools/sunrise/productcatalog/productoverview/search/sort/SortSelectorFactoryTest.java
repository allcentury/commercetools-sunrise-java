package com.commercetools.sunrise.productcatalog.productoverview.search.sort;

import com.commercetools.sunrise.common.contexts.RequestContext;
import com.commercetools.sunrise.common.contexts.UserContext;
import com.commercetools.sunrise.common.contexts.UserContextTestProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.sphere.sdk.search.SearchExpression;
import io.sphere.sdk.search.SortExpression;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

public class SortSelectorFactoryTest {

    private static final String KEY = "key";
    private static final List<SortOption> SORT_OPTIONS = asList(
            SortOption.of("foo-asc", "Foo Asc", singletonList(SortExpression.of("{{locale}}.foo.{{locale}} asc")), false),
            SortOption.of("foo-desc", "Foo Desc", singletonList(SortExpression.of("foo desc")), true),
            SortOption.of("foobar-asc", "Foo Bar Asc", asList(SortExpression.of("foo asc"), SortExpression.of("bar asc")), false));

    @Test
    public void getsSelectedSort() throws Exception {
        final SortConfig config = SortConfig.of(KEY, SORT_OPTIONS);
        final List<String> selectedValues = asList("foobar-asc", "foo-asc");
        test(config, selectedValues, sortSelector ->
                Assertions.assertThat(sortSelector.getSelectedOptions())
                        .extracting(SortOption::getValue)
                        .containsExactly("foobar-asc", "foo-asc"));
    }

    @Test
    public void getsDefaultWhenNoneSelected() throws Exception {
        final SortConfig config = SortConfig.of(KEY, SORT_OPTIONS);
        final List<String> selectedValues = emptyList();
        test(config, selectedValues, sortSelector ->
                Assertions.assertThat(sortSelector.getSelectedOptions())
                        .extracting(SortOption::getValue)
                        .containsExactly("foo-desc"));
    }

    @Test
    public void getsDefaultWhenInvalidSelected() throws Exception {
        final SortConfig config = SortConfig.of(KEY, SORT_OPTIONS);
        final List<String> selectedValues = singletonList("invalid");
        test(config, selectedValues, sortSelector ->
                Assertions.assertThat(sortSelector.getSelectedOptions())
                        .extracting(SortOption::getValue)
                        .containsExactly("foo-desc"));
    }

    @Test
    public void generatesSortExpressions() throws Exception {
        final SortConfig config = SortConfig.of(KEY, SORT_OPTIONS);
        final List<String> selectedValues = asList("foobar-asc", "foo-desc");
        test(config, selectedValues, sortSelector ->
                Assertions.assertThat(sortSelector.getSelectedSortExpressions())
                        .extracting(SearchExpression::expression)
                        .containsExactly("foo asc", "bar asc", "foo desc"));
    }

    @Test
    public void getsSortExprWithLocale() throws Exception {
        final SortConfig config = SortConfig.of(KEY, SORT_OPTIONS);
        final List<String> selectedValues = singletonList("foo-asc");
        test(config, selectedValues, sortSelector ->
                Assertions.assertThat(sortSelector.getSelectedSortExpressions())
                        .extracting(SearchExpression::expression)
                        .containsExactly("en.foo.en asc"));
    }

    private void test(final SortConfig config, final List<String> selectedValues, final Consumer<SortSelector> test) {
        final Map<String, List<String>> queryString = singletonMap(config.getKey(), selectedValues);
        final SortSelectorFactory factory = createSortSelectorFactory(config, queryString);
        test.accept(factory.create());
    }

    private SortSelectorFactory createSortSelectorFactory(final SortConfig config, final Map<String, List<String>> queryString) {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            public void configure() {
                bind(UserContext.class).toProvider(UserContextTestProvider.class);
                bind(RequestContext.class).toInstance(RequestContext.of(queryString, ""));
                bind(SortConfig.class).toInstance(config);
            }
        });
        return injector.getInstance(SortSelectorFactory.class);
    }
}
