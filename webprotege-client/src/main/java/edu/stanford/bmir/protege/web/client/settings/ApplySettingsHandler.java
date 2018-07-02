package edu.stanford.bmir.protege.web.client.settings;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2 Jul 2018
 */
public interface ApplySettingsHandler {

    void handleApplySettings(@Nonnull ApplySettingsCallback callback);
}
