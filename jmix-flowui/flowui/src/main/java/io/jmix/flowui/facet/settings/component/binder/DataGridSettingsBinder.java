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

package io.jmix.flowui.facet.settings.component.binder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import io.jmix.core.JmixOrder;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.data.ContainerDataUnit;
import io.jmix.flowui.data.grid.DataGridItems;
import io.jmix.flowui.facet.settings.Settings;
import io.jmix.flowui.facet.settings.component.DataGridSettings;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.HasLoader;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Order(JmixOrder.LOWEST_PRECEDENCE)
@org.springframework.stereotype.Component("flowui_DataGridSettingsBinder")
public class DataGridSettingsBinder implements DataLoadingSettingsBinder<DataGrid<?>, DataGridSettings> {

    @Override
    public Class<? extends Component> getComponentClass() {
        return DataGrid.class;
    }

    @Override
    public Class<? extends Settings> getSettingsClass() {
        return DataGridSettings.class;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void applySettings(DataGrid<?> component, DataGridSettings settings) {
        if (isEmpty(settings.getColumns())) {
            return;
        }

        List<? extends Grid.Column<?>> componentColumns = getOrderedColumns(component);

        List<String> componentColumnKeys = componentColumns.stream().map(Grid.Column::getKey).toList();
        List<String> settingsColumnKeys = settings.getColumns().stream().map(DataGridSettings.Column::getKey).toList();

        // Checks only size of collections and same elements. It does not consider the order in collections.
        // So settings won't be applied if DataGrid contains columns that are missed in settings.
        if (CollectionUtils.isEqualCollection(componentColumnKeys, settingsColumnKeys)) {
            List<Grid.Column<?>> newColumnsOrder = new ArrayList<>(componentColumnKeys.size());

            for (DataGridSettings.Column sColumn : settings.getColumns()) {
                Grid.Column<?> column = component.getColumnByKey(sColumn.getKey());
                Objects.requireNonNull(column);

                if (sColumn.getWidth() != null) {
                    column.setWidth(sColumn.getWidth());
                }
                newColumnsOrder.add(column);
            }
            component.setColumnOrder((List) newColumnsOrder);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void applyDataLoadingSettings(DataGrid<?> component, DataGridSettings settings) {
        if (!isDataLoadingSettingsEnabled(component)
                || isEmpty(settings.getSortOrder())) {
            return;
        }

        List sortOrder = settings.getSortOrder().stream()
                .map(sSortOrder -> new GridSortOrder<>(
                        component.getColumnByKey(sSortOrder.getKey()),
                        SortDirection.valueOf(sSortOrder.getSortDirection())))
                .toList();

        if (component.getItems() instanceof DataGridItems.Sortable) {
            ((DataGridItems.Sortable<?>) component.getItems()).suppressSorting();
        }
        try {
            component.sort(sortOrder);
        } finally {
            if (component.getItems() instanceof DataGridItems.Sortable) {
                ((DataGridItems.Sortable<?>) component.getItems()).enableSorting();
            }
        }
    }

    @Override
    public boolean saveSettings(DataGrid<?> component, DataGridSettings settings) {
        boolean changed = false;

        List<? extends GridSortOrder<?>> sortOrder = component.getSortOrder();
        if (isColumnSortOrderChanged(sortOrder, settings.getSortOrder())) {
            setSortOrderToSettings(sortOrder, settings);
            changed = true;
        }
        List<? extends Grid.Column<?>> componentColumns = getOrderedColumns(component);
        if (isColumnSettingsChanged(componentColumns, settings.getColumns())) {
            setColumnsToSettings(componentColumns, settings);
            changed = true;
        }

        return changed;
    }

    @Override
    public DataGridSettings getSettings(DataGrid<?> component) {
        DataGridSettings settings = createSettings();
        settings.setId(component.getId().orElse(null));

        setSortOrderToSettings(component.getSortOrder(), settings);
        setColumnsToSettings(component.getVisibleColumns(), settings);

        return settings;
    }

    protected boolean isDataLoadingSettingsEnabled(DataGrid<?> dataGrid) {
        DataGridItems<?> items = dataGrid.getItems();
        if (items instanceof ContainerDataUnit) {
            CollectionContainer<?> container = ((ContainerDataUnit<?>) items).getContainer();
            return container instanceof HasLoader
                    && ((HasLoader) container).getLoader() instanceof CollectionLoader;
        }
        return false;
    }

    protected boolean isColumnSortOrderChanged(@Nullable List<? extends GridSortOrder<?>> componentSortOrder,
                                               @Nullable List<DataGridSettings.SortOrder> settingsSortOrder) {
        if (isEmpty(componentSortOrder) && isEmpty(settingsSortOrder)) {
            return false;
        }
        if (isEmpty(componentSortOrder) || isEmpty(settingsSortOrder)) {
            return true;
        }

        if (componentSortOrder.size() != settingsSortOrder.size()) {
            return true;
        }
        for (int i = 0; i < componentSortOrder.size(); i++) {
            GridSortOrder<?> sortOrder = componentSortOrder.get(i);
            String key = sortOrder.getSorted().getKey();

            DataGridSettings.SortOrder sSortOrder = settingsSortOrder.get(i);

            if (!key.equals(sSortOrder.getKey())) {
                return true;
            }
            if (!sortOrder.getDirection().name().equals(sSortOrder.getSortDirection())) {
                return true;
            }
        }
        return false;
    }

    protected void setSortOrderToSettings(List<? extends GridSortOrder<?>> sortOrder, DataGridSettings settings) {
        if (isEmpty(sortOrder)) {
            settings.setSortOrder(null);
            return;
        }

        List<DataGridSettings.SortOrder> settingsSortOrder = sortOrder.stream()
                .map(cSortOrder -> {
                    DataGridSettings.SortOrder sSortOrder = new DataGridSettings.SortOrder();
                    sSortOrder.setKey(cSortOrder.getSorted().getKey());
                    sSortOrder.setSortDirection(cSortOrder.getDirection().name());
                    return sSortOrder;
                }).toList();

        settings.setSortOrder(settingsSortOrder);
    }

    protected boolean isColumnSettingsChanged(@Nullable List<? extends Grid.Column<?>> componentColumns,
                                              @Nullable List<DataGridSettings.Column> settingsColumns) {
        if (isEmpty(componentColumns) && isEmpty(settingsColumns)) {
            return false;
        }
        if (isEmpty(componentColumns) || isEmpty(settingsColumns)) {
            return true;
        }

        if (componentColumns.size() != settingsColumns.size()) {
            return true;
        }
        for (int i = 0; i < componentColumns.size(); i++) {
            Grid.Column<?> column = componentColumns.get(i);
            DataGridSettings.Column sColumn = settingsColumns.get(i);

            // Check columns order
            if (!Objects.equals(column.getKey(), sColumn.getKey())) {
                return true;
            }
            if (!Objects.equals(column.getWidth(), sColumn.getWidth())) {
                return true;
            }
        }
        return false;
    }

    protected void setColumnsToSettings(@Nullable List<? extends Grid.Column<?>> componentColumns,
                                        DataGridSettings settings) {
        if (isEmpty(componentColumns)) {
            settings.setColumns(null);
            return;
        }

        List<DataGridSettings.Column> settingsColumns = componentColumns.stream().
                map(column -> {
                    DataGridSettings.Column sColumn = new DataGridSettings.Column();
                    sColumn.setKey(column.getKey());
                    sColumn.setWidth(column.getWidth());
                    return sColumn;
                }).toList();

        settings.setColumns(settingsColumns);
    }

    protected DataGridSettings createSettings() {
        return new DataGridSettings();
    }

    protected List<? extends Grid.Column<?>> getOrderedColumns(DataGrid<?> dataGrid) {
        // Gets all (with hidden by security) columns list that has correct order.
        List<? extends Grid.Column<?>> allColumns = dataGrid.getAllColumns();

        // Gets columns that are added to DataGrid, even with visible=false.
        List<? extends Grid.Column<?>> columns = dataGrid.getColumns();

        // We need to save the correct user's order and
        // exclude hidden by security columns.
        return allColumns.stream()
                .filter(columns::contains)
                .toList();
    }
}
