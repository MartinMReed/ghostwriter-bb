package com.wss.ghostwriter.bb.service;

import java.io.InterruptedIOException;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import com.wss.ghostwriter.core.model.Message;

public class IncomingThread extends AbstractWorkerThread {

    public IncomingThread(DatagramConnection datagramConnection) {

        super( datagramConnection );
    }

    protected boolean execute( DatagramConnection datagramConnection, Datagram datagram ) throws Exception {

        try {

            // blocks until data is received
            datagramConnection.receive( datagram );

            // do our execution on another thread so we can keep receiving new messages quickly
            ConnectionService.execute( Message.fromPacket( datagram.getData(), datagram.getOffset(), datagram.getLength() ) );
        }
        catch (InterruptedIOException e) {

            // do nothing
        }

        return true;
    }
}
