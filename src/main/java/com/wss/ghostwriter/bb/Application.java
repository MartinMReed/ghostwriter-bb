package com.wss.ghostwriter.bb;

import java.io.InputStream;

import org.metova.bb.widgets.Screens;
import org.metova.mobile.net.queue.QueueManager;

import com.metova.bb.MetovaManagedUiApplication;
import com.wss.ghostwriter.bb.service.ConnectionService;
import com.wss.ghostwriter.bb.service.UID;
import com.wss.ghostwriter.bb.ui.HomeContainer;

public final class Application extends MetovaManagedUiApplication {

    private QueueManager queueManager = new QueueManager( Application.class.getName(), UID.QUEUE_MANAGER_ID );

    public static void main( String[] args ) {

        run( Application.class, args );
    }

    protected void lifecycleSetup() {

        ConnectionService.instantiate();
    }

    protected void lifecycleStartup() {

        Screens.pushScreen( new HomeContainer() );
    }

    public InputStream getResourceAsStream( String resource ) {

        return getClass().getResourceAsStream( resource );
    }

    public QueueManager getQueueManager() {

        return queueManager;
    }

    public void onExit() {

        ConnectionService.dispose();
    }

    public void resetApplication() {

        getQueueManager().deleteAll();

        super.resetApplication();
    }

    protected void initializeThemeManager() throws Exception {

        super.initializeThemeManager();
        getThemeManager().addThemeDescriptor( "ghostwriter", true );
    }
}
