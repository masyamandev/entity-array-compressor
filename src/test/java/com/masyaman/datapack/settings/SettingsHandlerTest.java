package com.masyaman.datapack.settings;

import org.junit.Test;

import java.nio.charset.Charset;

import static com.masyaman.datapack.settings.SettingsKeys.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsHandlerTest {

    @Test
    public void assertDefaults() throws Exception {
        assertThat(SettingsHandler.DEFAULTS.get(CHARSET)).isEqualTo(Charset.forName("UTF8"));
        assertThat(SettingsHandler.DEFAULTS.get(COLLECTION_REORDERING_CACHE_SIZE)).isEqualTo(COLLECTION_REORDERING_CACHE_SIZE.getDefaultValue());
        assertThat(SettingsHandler.DEFAULTS.get(ENABLE_REORDERING_FIELDS)).isEqualTo(true);
    }

    @Test
    public void assertDefaultsOnNewSettings() throws Exception {
        SettingsHandler settings = new SettingsHandler();
        assertThat(settings.get(CHARSET)).isEqualTo(Charset.forName("UTF8"));
        assertThat(settings.get(COLLECTION_REORDERING_CACHE_SIZE)).isEqualTo(COLLECTION_REORDERING_CACHE_SIZE.getDefaultValue());
        assertThat(settings.get(ENABLE_REORDERING_FIELDS)).isEqualTo(true);
    }

    @Test
    public void assertChangingSettings() throws Exception {
        SettingsHandler settings = new SettingsHandler()
                .set(COLLECTION_REORDERING_CACHE_SIZE, 10)
                .set(CHARSET, Charset.forName("UTF16"));
        assertThat(settings.get(CHARSET)).isEqualTo(Charset.forName("UTF16"));
        assertThat(settings.get(COLLECTION_REORDERING_CACHE_SIZE)).isEqualTo(10);
        assertThat(settings.get(ENABLE_REORDERING_FIELDS)).isEqualTo(true);

        assertThat(SettingsHandler.DEFAULTS.get(CHARSET)).isEqualTo(Charset.forName("UTF8"));
        assertThat(SettingsHandler.DEFAULTS.get(COLLECTION_REORDERING_CACHE_SIZE)).isEqualTo(COLLECTION_REORDERING_CACHE_SIZE.getDefaultValue());
        assertThat(SettingsHandler.DEFAULTS.get(ENABLE_REORDERING_FIELDS)).isEqualTo(true);
    }
}