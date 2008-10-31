package com.wss.ghostwriter.bb.service;

public class UID extends org.metova.mobile.persistence.UID {

    public static final UID QUEUE_MANAGER_ID = new UID( UID.class.getName() + ".QueueManager" );
    public static final UID PREFERENCES_STORE = new UID( UID.class.getName() + ".store.preferences" );

    protected UID(String description) {

        super( description );
    }
}
