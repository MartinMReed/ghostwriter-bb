package com.wss.ghostwriter.bb.widgets;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.NullField;

import org.metova.bb.widgets.util.Timers;

import com.wss.ghostwriter.bb.service.ConnectionService;
import com.wss.ghostwriter.bb.service.PreferenceStore;
import com.wss.ghostwriter.core.model.Message;
import com.wss.ghostwriter.core.model.MouseMovement;
import com.wss.ghostwriter.core.service.CommunicationCode;

public class NavigationConsumer extends NullField {

    private boolean trackballPressed;

    public NavigationConsumer() {

        super( FOCUSABLE );
    }

    protected void onVisibilityChange( boolean visible ) {

        super.onVisibilityChange( visible );

        if (visible) {

            int sensitivity = PreferenceStore.instance().load().getSensitivity();

            Screen screen = getScreen();

            screen.setTrackballFilter( Trackball.FILTER_ACCELERATION );
            screen.setTrackballSensitivityXOffset( sensitivity );
            screen.setTrackballSensitivityYOffset( sensitivity );
        }
    }

    protected boolean keyChar( final char character, final int status, int time ) {

        if (ConnectionService.isAlive()) {

            Timers.runNow( new Runnable() {

                public void run() {

                    String keycode = String.valueOf( Keypad.getKeyCode( character, status ) );
                    ConnectionService.send( new Message( CommunicationCode.KEY_PRESS, keycode ) );
                }
            } );

            return true;
        }

        return super.keyChar( character, status, time );
    }

    protected boolean navigationClick( int status, int time ) {

        if (ConnectionService.isAlive()) {

            Timers.runNow( new Runnable() {

                public void run() {

                    boolean trackballPressed = isTrackballPressed();
                    setTrackballPressed( !trackballPressed );

                    ConnectionService.send( new Message( trackballPressed ? CommunicationCode.MOUSE_PRESS : CommunicationCode.MOUSE_RELEASE ) );
                }
            } );

            return true;
        }

        return super.navigationClick( status, time );
    }

    protected boolean navigationMovement( final int dx, final int dy, int status, int time ) {

        if (ConnectionService.isAlive()) {

            Timers.runNow( new Runnable() {

                public void run() {

                    MouseMovement mouseMovement = new MouseMovement( dx, dy );
                    ConnectionService.send( new Message( CommunicationCode.MOUSE_MOVEMENT, mouseMovement.toString() ) );
                }
            } );

            return true;
        }

        return super.navigationMovement( dx, dy, status, time );
    }

    private boolean isTrackballPressed() {

        return trackballPressed;
    }

    private void setTrackballPressed( boolean trackballPressed ) {

        this.trackballPressed = trackballPressed;
    }
}
