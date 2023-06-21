/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package org.nastation.web.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Default implementation of {@link I18NProvider}.
 */
@Component
public class DefaultI18NProvider implements I18NProvider {

    @Override
    public List<Locale> getProvidedLocales() {
        return Collections.unmodifiableList(
                Arrays.asList(Locale.ENGLISH, Locale.KOREAN));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (Locale.ENGLISH.equals(locale)) {
        }

        else if (Locale.KOREAN.equals(locale)) {
        }
        return null;
    }

}
