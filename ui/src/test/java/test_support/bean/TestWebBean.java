/*
 * Copyright 2020 Haulmont.
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

package test_support.bean;

import io.jmix.ui.component.impl.AppWorkAreaImpl;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component(TestWebBean.NAME)
public class TestWebBean {

    public static final String NAME = "test_WebBean";

    public static final ThreadLocal<Boolean> testMethodInvoked = new ThreadLocal<>();

    public static final ThreadLocal<Boolean> workAreaTabChangedEventHandled = new ThreadLocal<>();

    public void testMethod() {
        testMethodInvoked.set(true);
    }

    @EventListener
    public void onWorkAreaTabChangedEvent(AppWorkAreaImpl.WorkAreaTabChangedEvent evt) {
        workAreaTabChangedEventHandled.set(true);
    }
}
