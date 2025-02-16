/*
 * Copyright 2024 Haulmont.
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

package io.jmix.searchflowui.utils;

import io.jmix.core.Messages;
import io.jmix.search.searching.SearchStrategy;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("search_SearchStrategyUtils")
public class SearchStrategyUtils {

    private static final Set<String> DEPRECATED_SEARCH_STRATEGIES = Set.of("allTermsAnyField", "allTermsSingleField");
    private static final String STRATEGY_LOCALIZATION_KEY_TEMPLATE = "io.jmix.search.searchstrategy.%s.name";

    protected final Messages messages;

    public SearchStrategyUtils(Messages messages) {
        this.messages = messages;
    }

    public boolean isSearchStrategyVisible(SearchStrategy searchStrategy) {
        return !DEPRECATED_SEARCH_STRATEGIES.contains(searchStrategy.getName());
    }

    public String getLocalizedStrategyName(String strategyName) {
        String key = STRATEGY_LOCALIZATION_KEY_TEMPLATE.formatted(strategyName);
        return messages.getMessage(key);
    }
}
