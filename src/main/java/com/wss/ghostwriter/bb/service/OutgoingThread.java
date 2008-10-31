package com.wss.ghostwriter.bb.service;

import java.util.Stack;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import com.wss.ghostwriter.bb.model.Preferences;
import com.wss.ghostwriter.core.model.Message;

public class OutgoingThread extends AbstractWorkerThread {

    private final Stack messageStack = new Stack();

    public OutgoingThread(DatagramConnection datagramConnection) {

        super( datagramConnection );
    }

    public synchronized void enqueue( Message message ) {

        getMessageStack().push( message );
    }

    protected boolean execute( DatagramConnection datagramConnection, Datagram datagram ) throws Exception {

        long currentTimeMillis = System.currentTimeMillis();

        Stack messageStack = getMessageStack();
        if (messageStack.isEmpty() == false) {

            ConnectionService.getConnectionService().setKeepAlive( currentTimeMillis );

            Message message = (Message) messageStack.elementAt( 0 );
            messageStack.removeElementAt( 0 );

            byte[] packet = message.toPacket();

            datagram.setData( packet, 0, packet.length );
            datagramConnection.send( datagram );
        }
        else {

            Preferences preferences = PreferenceStore.instance().load();
            if (preferences.isKeepAlive() == false) {

                long keepAlive = ConnectionService.getConnectionService().getKeepAlive();
                long keepAliveDelay = preferences.getKeepAliveDelay() * 1000 * 60;
                if (currentTimeMillis - keepAliveDelay >= keepAlive) {

                    ConnectionService.reset();
                }
            }
        }

        // do not yield if we still have messages to send
        return messageStack.isEmpty();
    }

    private Stack getMessageStack() {

        return messageStack;
    }
}
