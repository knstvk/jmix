/*
 * Copyright 2025 Haulmont.
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

package io.jmix.messagetemplatesflowui.view.messagetemplateparameter.model;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import io.jmix.messagetemplates.entity.MessageTemplateParameter;
import jakarta.persistence.Id;

import java.util.UUID;

/**
 * The entity is used to display and save localization for {@link MessageTemplateParameter}
 */
@SystemLevel
@JmixEntity(name = "msgtmp_MessageTemplateParameterLocalization")
public class MessageTemplateParameterLocalization {

    @JmixGeneratedValue
    @Id
    protected UUID id;

    @JmixProperty(mandatory = true)
    protected String locale;

    @JmixProperty(mandatory = true)
    protected String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
