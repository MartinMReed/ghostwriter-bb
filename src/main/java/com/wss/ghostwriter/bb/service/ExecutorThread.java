package com.wss.ghostwriter.bb.service;

import java.util.Stack;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import m.org.apache.log4j.Logger;

import org.metova.bb.widgets.Screens;
import org.metova.mobile.rt.alert.MobileAlert;
import org.metova.mobile.util.math.MathUtils;

import com.wss.ghostwriter.bb.ui.ScreenViewer;
import com.wss.ghostwriter.core.model.Message;
import com.wss.ghostwriter.core.service.CommunicationCode;

public class ExecutorThread extends AbstractWorkerThread {

    private static final Logger log = Logger.getLogger( ExecutorThread.class );

    private int screenCaptureExpected;
    private int screenCaptureReceived;
    private String screenCaptureString;

    private final Stack messageStack = new Stack();

    public ExecutorThread(DatagramConnection datagramConnection) {

        super( datagramConnection );

        screenCaptureReset();
    }

    public synchronized void enqueue( Message message ) {

        getMessageStack().push( message );
    }

    protected boolean execute( DatagramConnection datagramConnection, Datagram datagram ) throws Exception {

        Stack messageStack = getMessageStack();
        if (messageStack.isEmpty() == false) {

            Message message = (Message) messageStack.elementAt( 0 );
            messageStack.removeElementAt( 0 );

            switch (message.getCode()) {

                case CommunicationCode.PONG: {

                    pong( message );
                    break;
                }

                case CommunicationCode.SCREEN_CAPTURE_RESPONSE_A: {

                    screenCaptureA( message );
                    break;
                }

                case CommunicationCode.SCREEN_CAPTURE_RESPONSE_B: {

                    screenCaptureB( message );
                    break;
                }
            }
        }

        // do not yield if we still have messages to send
        return messageStack.isEmpty();
    }

    private void pong( Message message ) throws Exception {

        String value = message.getValue();
        int size = value.getBytes().length;
        long ping = Long.parseLong( value );
        double delay = MathUtils.doubleDivision( System.currentTimeMillis() - ping, 1000 );
        MobileAlert.instance().show( "Pong!\nRound trip: " + delay + " seconds\nSize: " + size + " bytes" );
    }

    private void screenCaptureReset() {

        setScreenCaptureExpected( 0 );
        setScreenCaptureReceived( 0 );
        setScreenCaptureString( "" );
    }

    private void screenCaptureA( Message message ) throws Exception {

        screenCaptureReset();

        setScreenCaptureExpected( Integer.parseInt( message.getValue() ) );
    }

    private void screenCaptureB( Message message ) throws Exception {

        int screenCaptureExpected = getScreenCaptureExpected();
        if (screenCaptureExpected > 0) {

            String screenCaptureString = getScreenCaptureString() + message.getValue();
            int screenCaptureReceived = getScreenCaptureReceived() + 1;

            log.fatal( "screenCaptureReceived[" + screenCaptureReceived + "], screenCaptureExpected[" + screenCaptureExpected + "]" );

            if (screenCaptureReceived == screenCaptureExpected) {

                screenCaptureReset();

                byte[] rgbBytes = screenCaptureString.getBytes();

                int[] rgb = new int[rgbBytes.length];
                for (int i = 0; i < rgbBytes.length; i++) {

                    rgb[i] = (int) rgbBytes[i];
                }

                Screens.pushScreenLater( new ScreenViewer( rgb ) );
            }
            else {

                setScreenCaptureString( screenCaptureString );
                setScreenCaptureReceived( screenCaptureReceived );
            }
        }
    }

    private String getScreenCaptureString() {

        return screenCaptureString;
    }

    private void setScreenCaptureString( String screenCaptureString ) {

        this.screenCaptureString = screenCaptureString;
    }

    private int getScreenCaptureExpected() {

        return screenCaptureExpected;
    }

    private void setScreenCaptureExpected( int screenCaptureExpected ) {

        this.screenCaptureExpected = screenCaptureExpected;
    }

    private int getScreenCaptureReceived() {

        return screenCaptureReceived;
    }

    private void setScreenCaptureReceived( int screenCaptureReceived ) {

        this.screenCaptureReceived = screenCaptureReceived;
    }

    private Stack getMessageStack() {

        return messageStack;
    }
}
