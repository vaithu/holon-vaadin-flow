/*
 * Copyright 2016-2019 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.components.builders;

import java.time.LocalDateTime;

import com.holonplatform.vaadin.flow.components.Input;

/**
 * {@link LocalDateTime} type {@link Input} components configurator.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.2
 */
@SuppressWarnings("deprecation")
public interface LocalDateTimeInputConfigurator<C extends LocalDateTimeInputConfigurator<C>>
		extends BaseTemporalInputConfigurator<LocalDateTime, C>, HasTimeInputConfigurator<C> {

}
