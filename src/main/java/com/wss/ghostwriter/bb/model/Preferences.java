package com.wss.ghostwriter.bb.model;

import net.rim.device.api.ui.Trackball;

import org.metova.mobile.rt.persistence.Persistable;

public class Preferences implements Persistable {

    private String serverDomain = "192.168.1.1";
    private int sensitivity;
    private boolean keepAlive;
    private long keepAliveDelay = 5;

    public Preferences() {

        int sensitivityX = Trackball.getSensitivityXForSystem();
        int sensitivityY = Trackball.getSensitivityYForSystem();
        setSensitivity( Math.max( sensitivityX, sensitivityY ) );
    }

    public String getServerDomain() {

        return serverDomain;
    }

    public void setServerDomain( String serverDomain ) {

        this.serverDomain = serverDomain;
    }

    public int getSensitivity() {

        return sensitivity;
    }

    public void setSensitivity( int sensitivity ) {

        this.sensitivity = sensitivity;
    }

    public boolean isKeepAlive() {

        return keepAlive;
    }

    public void setKeepAlive( boolean keepAlive ) {

        this.keepAlive = keepAlive;
    }

    public long getKeepAliveDelay() {

        return keepAliveDelay;
    }

    public void setKeepAliveDelay( long keepAliveDelay ) {

        this.keepAliveDelay = keepAliveDelay;
    }
}
