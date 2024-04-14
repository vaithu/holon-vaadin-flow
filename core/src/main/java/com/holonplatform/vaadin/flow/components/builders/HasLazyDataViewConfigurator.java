package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider;

public interface HasLazyDataViewConfigurator<T,F, C extends HasLazyDataViewConfigurator<T,F,C> >  {

    C items(BackEndDataProvider<T, F> dataProvider);

    C items(CallbackDataProvider.FetchCallback<T, F> fetchCallback);

    C items(CallbackDataProvider.FetchCallback<T, F> fetchCallback,
            CallbackDataProvider.CountCallback<T, F> countCallback);

    C itemCountEstimate(int itemCountEstimate);

    C itemCountEstimateIncrease(int itemCountEstimateIncrease);

    C itemCountUnknown();
}
