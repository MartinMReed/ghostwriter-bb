package com.wss.ghostwriter.bb.service;

import org.metova.mobile.persistence.AbstractStore;

import com.wss.ghostwriter.bb.model.Preferences;

public class PreferenceStore extends AbstractStore {

    private static PreferenceStore myself;

    public static PreferenceStore instance() {

        if (myself == null) {

            myself = new PreferenceStore();
        }

        return myself;
    }

    private PreferenceStore() {

        super( Preferences.class, UID.PREFERENCES_STORE );
    }

    public Preferences load() {

        return (Preferences) super.loadObject();
    }

    public void persist( Preferences preferences ) {

        super.persistObject( preferences );
    }
}
