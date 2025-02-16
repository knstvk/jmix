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

package io.jmix.flowui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import io.jmix.core.common.util.Preconditions;
import io.jmix.flowui.event.notification.NotificationClosedEvent;
import io.jmix.flowui.event.notification.NotificationOpenedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

/**
 * Configures and displays popup notifications.
 */
@org.springframework.stereotype.Component("flowui_Notifications")
public class Notifications {
    private static final Logger log = LoggerFactory.getLogger(Notifications.class);

    protected UiComponentProperties uiComponentProperties;
    protected ApplicationContext applicationContext;

    @Autowired
    public Notifications(UiComponentProperties uiComponentProperties) {
        this.uiComponentProperties = uiComponentProperties;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Shows a simple text notification.
     *
     * @param text notification text
     */
    public void show(String text) {
        Preconditions.checkNotNullArgument(text);

        create(text).show();
    }

    /**
     * Shows a notification with a title and message.
     *
     * @param title   notification title
     * @param message notification message
     */
    public void show(String title, String message) {
        Preconditions.checkNotNullArgument(title);
        Preconditions.checkNotNullArgument(message);

        create(title, message).show();
    }

    /**
     * Shows a component in the notification popup.
     *
     * @param component component to show in notification
     */
    public void show(Component component) {
        Preconditions.checkNotNullArgument(component);

        create(component).show();
    }

    /**
     * Returns a builder for configuring and displaying the notification.
     *
     * @param text notification text
     */
    public NotificationBuilder create(String text) {
        Preconditions.checkNotNullArgument(text);

        return new NotificationBuilder(text)
                .applyDefaults(uiComponentProperties)
                .applyApplicationContext(applicationContext);
    }

    /**
     * Returns a builder for configuring and displaying the notification.
     *
     * @param title   notification title
     * @param message notification message
     */
    public NotificationBuilder create(String title, String message) {
        Preconditions.checkNotNullArgument(title);
        Preconditions.checkNotNullArgument(message);

        return new NotificationBuilder(title, message)
                .applyDefaults(uiComponentProperties)
                .applyApplicationContext(applicationContext);
    }

    /**
     * Returns a builder for configuring and displaying the notification.
     *
     * @param component component to show in notification
     */
    public NotificationBuilder create(Component component) {
        Preconditions.checkNotNullArgument(component);

        return new NotificationBuilder(component)
                .applyDefaults(uiComponentProperties)
                .applyApplicationContext(applicationContext);
    }

    /**
     * Notification type.
     */
    public enum Type {
        DEFAULT,
        SUCCESS,
        ERROR,
        SYSTEM,
        WARNING
    }

    /**
     * A builder that configures and displays notifications.
     */
    public static class NotificationBuilder {

        protected static final String CLOSEABLE_LAYOUT_CLASS_NAME = "jmix-closeable-layout";
        protected static final String CLOSE_BUTTON_CLASS_NAME = "close-button";

        protected static final String TEXT_LAYOUT_CLASS_NAME = "jmix-text-layout";
        protected static final String CLOSEABLE_TEXT_LAYOUT_CLASS_NAME = "jmix-text-layout-closeable";
        protected static final String TITLE_CLASS_NAME = "title";
        protected static final String MESSAGE_CLASS_NAME = "message";

        protected static final String COMPONENT_CONTENT_CLASS_NAME = "component-content";

        protected Notification notification;
        protected ApplicationContext applicationContext;

        protected String text;
        protected String title;
        protected String message;
        protected Component component;

        protected Notification.Position position;
        protected int duration;
        protected boolean fireOpenedChangeEvents;
        protected Type type = Type.DEFAULT;
        protected Boolean closeable = null;
        protected NotificationVariant themeVariant;
        protected String className;
        protected boolean assertive;

        /**
         * @param text notification text
         */
        public NotificationBuilder(String text) {
            this.text = text;
        }

        /**
         * @param title   notification title
         * @param message notification message
         */
        public NotificationBuilder(String title, String message) {
            this.title = title;
            this.message = message;
        }

        /**
         * @param component component to show in notification
         */
        public NotificationBuilder(Component component) {
            this.component = component;
        }

        /**
         * Sets notification position.
         * <p>
         * Default position is specified in the {@code jmix.flowui.component.default-notification-position} property
         * which is MIDDLE by default, see {@link UiComponentProperties#getDefaultNotificationPosition()}.
         */
        public NotificationBuilder withPosition(Notification.Position position) {
            Preconditions.checkNotNullArgument(position);

            this.position = position;
            return this;
        }

        /**
         * Sets notification duration in milliseconds to show the notification.
         * Set to 0 or a negative number to disable the notification auto-closing.
         * <p>
         * Default duration is specified in the {@code jmix.flowui.component.default-notification-duration} property
         * which is 3000 by default, see {@link UiComponentProperties#getDefaultNotificationDuration()}.
         */
        public NotificationBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Sets notification type.
         */
        public NotificationBuilder withType(Type type) {
            Preconditions.checkNotNullArgument(type);

            this.type = type;
            return this;
        }

        /**
         * Sets whether to show the button for closing the notification.
         */
        public NotificationBuilder withCloseable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        /**
         * Sets a theme variant to the notification.
         */
        public NotificationBuilder withThemeVariant(NotificationVariant themeVariant) {
            Preconditions.checkNotNullArgument(themeVariant);

            this.themeVariant = themeVariant;
            return this;
        }

        /**
         * Sets a CSS class name to the notification.
         */
        public NotificationBuilder withClassName(String className) {
            Preconditions.checkNotNullArgument(className);

            this.className = className;
            return this;
        }

        /**
         * Sets {@code aria-live} attribute of the notification card web-component to {@code assertive} instead
         * of {@code polite}. This makes screen readers announce the notification content immediately when it appears.
         *
         * @param assertive whether to set {@code aria-live} attribute to {@code assertive}
         * @return {@code this}
         */
        public NotificationBuilder withAssertive(boolean assertive) {
            this.assertive = assertive;
            return this;
        }

        public Notification.Position getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }

        public Type getType() {
            return type;
        }

        public boolean isCloseable() {
            return isInternalCloseable();
        }

        @Nullable
        public String getText() {
            return text;
        }

        @Nullable
        public String getTitle() {
            return title;
        }

        @Nullable
        public String getMessage() {
            return message;
        }

        @Nullable
        public NotificationVariant getThemeVariant() {
            return themeVariant;
        }

        @Nullable
        public String getClassName() {
            return className;
        }

        /**
         * @return {@code true} if screen readers announce the notification content immediately when it appears,
         * {@code false} otherwise
         */
        public boolean isAssertive() {
            return assertive;
        }

        @Nullable
        public Component getComponent() {
            return component;
        }

        /**
         * Creates the notification.
         * <p>
         * Use {@link Notification#open()} to show created notification.
         */
        public Notification build() {
            notification = createNotification();
            notification.setDuration(duration);
            notification.addThemeName(convertTypeToThemeName(type));

            // force setting assertive if type error
            notification.setAssertive(assertive || type == Type.ERROR);

            if (themeVariant != null) {
                notification.addThemeVariants(themeVariant);
            }

            if (className != null) {
                notification.addClassName(className);
            }

            if (position != null) {
                position = type == Type.SYSTEM ? Notification.Position.TOP_STRETCH : position;
                notification.setPosition(position);
            }

            if (isInternalCloseable()) {
                log.debug("Notification is closeable so duration property will be ignored");
                notification.setDuration(0);
            }

            Component content = buildNotificationContent();
            if (content != null) {
                notification.add(content);
            }

            if (fireOpenedChangeEvents) {
                notification.addOpenedChangeListener(this::fireNotificationOpenedChangeEvent);
            }

            return notification;
        }

        /**
         * Creates and shows the notification.
         */
        public void show() {
            build().open();
        }

        protected NotificationBuilder applyDefaults(UiComponentProperties properties) {
            this.duration = properties.getDefaultNotificationDuration();
            this.position = properties.getDefaultNotificationPosition();
            this.fireOpenedChangeEvents = properties.isNotificationsOpenedChangeEventsEnabled();
            return this;
        }

        protected NotificationBuilder applyApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
            return this;
        }

        @Nullable
        protected Component buildNotificationContent() {
            HasComponents closeableLayout = isInternalCloseable()
                    ? createCloseableLayout()
                    : null;

            Component content;
            if (text != null) {
                content = createTextComponent(text);
            } else if (title != null && message != null) {
                HasComponents textLayout = createTextLayout();
                textLayout.add(createTitleComponent(title));
                textLayout.add(createMessageComponent(message));
                content = (Component) textLayout;
            } else if (component != null) {
                content = component;
            } else {
                log.debug("No text message or component provided to the notification");
                return null;
            }

            if (closeableLayout != null) {
                closeableLayout.addComponentAsFirst(content);
                closeableLayout.getElement().getClassList().add(COMPONENT_CONTENT_CLASS_NAME);
                return (Component) closeableLayout;
            }

            return content;
        }

        protected HasComponents createTextLayout() {
            Div div = new Div();

            if (isInternalCloseable()) {
                div.setClassName(CLOSEABLE_TEXT_LAYOUT_CLASS_NAME);
            } else {
                div.setClassName(TEXT_LAYOUT_CLASS_NAME);
            }
            return div;
        }

        protected Component createTextComponent(String text) {
            return new Text(text);
        }

        protected Component createTitleComponent(String title) {
            H4 titleElement = new H4();
            titleElement.setText(title);
            titleElement.setClassName(TITLE_CLASS_NAME);
            return titleElement;
        }

        protected Component createMessageComponent(String message) {
            Paragraph messageElement = new Paragraph();
            messageElement.setClassName(MESSAGE_CLASS_NAME);
            messageElement.setText(message);
            return messageElement;
        }

        protected HasComponents createCloseableLayout() {
            Div div = new Div();
            div.setClassName(CLOSEABLE_LAYOUT_CLASS_NAME);
            div.add(createCloseButton());
            return div;
        }

        protected Button createCloseButton() {
            Button button = new Button();
            button.setClassName(CLOSE_BUTTON_CLASS_NAME);
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
            button.setIcon(new Icon(VaadinIcon.CLOSE_SMALL));
            button.addClickListener(this::onCloseButtonClick);
            return button;
        }

        protected void onCloseButtonClick(ClickEvent<Button> event) {
            notification.close();
        }

        protected boolean isInternalCloseable() {
            boolean internalCloseable = type == Type.ERROR || type == Type.WARNING || type == Type.SYSTEM
                    || duration == 0;
            return closeable != null ? closeable : internalCloseable;
        }

        protected Notification createNotification() {
            return new Notification();
        }

        protected String convertTypeToThemeName(Type type) {
            switch (type) {
                case DEFAULT:
                    return "";
                case SUCCESS:
                    return NotificationVariant.LUMO_SUCCESS.getVariantName();
                case ERROR:
                    return NotificationVariant.LUMO_ERROR.getVariantName();
                case SYSTEM:
                    return NotificationVariant.LUMO_CONTRAST.getVariantName();
                case WARNING:
                    return NotificationVariant.LUMO_WARNING.getVariantName();
                default:
                    throw new IllegalStateException("Unsupported type " + type);
            }
        }

        protected void fireNotificationOpenedChangeEvent(Notification.OpenedChangeEvent openedChangeEvent) {
            if (openedChangeEvent.isOpened()) {
                NotificationOpenedEvent notificationOpenedEvent = new NotificationOpenedEvent(
                        notification, text,
                        text == null ? title : null,
                        text == null ? message : null,
                        text == null && (title == null || message == null) ? component : null,
                        type);

                applicationContext.publishEvent(notificationOpenedEvent);
            } else {
                applicationContext.publishEvent(new NotificationClosedEvent(notification));
            }
        }
    }
}
