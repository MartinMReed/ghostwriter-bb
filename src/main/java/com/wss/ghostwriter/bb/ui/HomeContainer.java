package com.wss.ghostwriter.bb.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;

import org.metova.bb.theme.precision.Container;
import org.metova.bb.widgets.Menus;
import org.metova.bb.widgets.field.label.WrappedTextLabelField;
import org.metova.mobile.event.Event;
import org.metova.mobile.event.EventDispatcher;
import org.metova.mobile.event.EventListener;
import org.metova.mobile.util.text.Text;

import com.wss.ghostwriter.bb.event.ErrorEvent;
import com.wss.ghostwriter.bb.service.ConnectionService;
import com.wss.ghostwriter.bb.widgets.ErrorManager;
import com.wss.ghostwriter.bb.widgets.NavigationConsumer;
import com.wss.ghostwriter.core.model.Message;
import com.wss.ghostwriter.core.model.ScreenCapture;
import com.wss.ghostwriter.core.service.CommunicationCode;
import com.wss.ghostwriter.core.service.ErrorCode;

public class HomeContainer extends Container {

    private NavigationConsumer navigationConsumer;
    private WrappedTextLabelField greeting;

    private ErrorManager errorManager;
    private ErrorEventListener errorEventListener;

    public HomeContainer() {

        setBackgroundOnClose( true );
    }

    public boolean onClose() {

        ConnectionService.dispose();

        return super.onClose();
    }

    protected void onInitializeContent() {

        setNavigationConsumer( new NavigationConsumer() );

        WrappedTextLabelField greeting = new WrappedTextLabelField( "Welcome to the Ghost Network", USE_ALL_WIDTH );
        greeting.getStyleManager().setStyleClass( "h3-special-margin" );
        setGreeting( greeting );

        setErrorManager( new ErrorManager() );
        setErrorEventListener( new ErrorEventListener() );
    }

    protected void addContentFields() {

        add( getNavigationConsumer() );
        add( getGreeting() );
        add( getErrorManager() );

        EventDispatcher.instance().registerListener( getErrorEventListener(), ErrorEvent.class );
    }

    protected void onUnloadingContent() {

        EventDispatcher.instance().removeListener( getErrorEventListener(), ErrorEvent.class );

        super.onUnloadingContent();
    }

    private void resetError() {

        resetError( null );
    }

    private void resetError( final String text ) {

        UiApplication.getUiApplication().invokeLater( new Runnable() {

            public void run() {

                getErrorManager().setText( text );
            }
        } );
    }

    protected void makeMenu( Menu menu, int instance ) {

        if ( ConnectionService.isAlive() ) {

            menu.add( Menus.newMenuItemRunNow( "View", new Runnable() {

                public void run() {

                    ScreenCapture screenCapture = new ScreenCapture( Display.getWidth(), Display.getHeight() );
                    ConnectionService.send( new Message( CommunicationCode.SCREEN_CAPTURE_REQUEST, screenCapture.toString() ) );
                }
            } ) );

            menu.add( Menus.newMenuItemRunNow( "Ping", new Runnable() {

                public void run() {

                    ConnectionService.send( new Message( CommunicationCode.PING, String.valueOf( System.currentTimeMillis() ) ) );
                }
            } ) );

            menu.add( Menus.newMenuItemRunNow( "Disconnect", new Runnable() {

                public void run() {

                    resetError();
                    ConnectionService.reset();
                }
            } ) );
        }
        else {

            menu.add( Menus.newMenuItemRunNow( "Connect", new Runnable() {

                public void run() {

                    resetError();
                    ConnectionService.reset();
                    ConnectionService.start();
                }
            } ) );
        }

        menu.add( Menus.newMenuItemPushScreen( "Preferences", new PreferencesContainer() ) );

        super.makeMenu( menu, instance );
    }

    private class ErrorEventListener implements EventListener {

        public void onEvent( Event event ) {

            Integer errorCode = (Integer) event.getSource();

            String text = ErrorCode.getText( errorCode.intValue() );
            if ( Text.isNull( text ) == false ) {

                resetError( text );
            }
        }
    }

    private NavigationConsumer getNavigationConsumer() {

        return navigationConsumer;
    }

    private void setNavigationConsumer( NavigationConsumer navigationConsumer ) {

        this.navigationConsumer = navigationConsumer;
    }

    private WrappedTextLabelField getGreeting() {

        return greeting;
    }

    private void setGreeting( WrappedTextLabelField greeting ) {

        this.greeting = greeting;
    }

    private ErrorManager getErrorManager() {

        return errorManager;
    }

    private void setErrorManager( ErrorManager errorManager ) {

        this.errorManager = errorManager;
    }

    private ErrorEventListener getErrorEventListener() {

        return errorEventListener;
    }

    private void setErrorEventListener( ErrorEventListener errorEventListener ) {

        this.errorEventListener = errorEventListener;
    }
}
