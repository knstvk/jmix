/*
 * Copyright 2022 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.flowui.entity.filter;

import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.JmixEntity;

@JmixEntity(name = "flowui_AbstractSingleFilterCondition")
@SystemLevel
public abstract class AbstractSingleFilterCondition extends FilterCondition {

    protected FilterValueComponent valueComponent;

    protected Boolean required = false;

    public FilterValueComponent getValueComponent() {
        return valueComponent;
    }

    public void setValueComponent(FilterValueComponent valueComponent) {
        this.valueComponent = valueComponent;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
