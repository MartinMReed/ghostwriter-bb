package com.wss.ghostwriter.bb.service;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import m.org.apache.log4j.Logger;

import org.metova.mobile.util.io.IOUtility;

public abstract class AbstractWorkerThread extends Thread {

    private static final Logger log = Logger.getLogger( OutgoingThread.class );

    private DatagramConnection datagramConnection;
    private boolean running;

    public AbstractWorkerThread(DatagramConnection datagramConnection) {

        setDatagramConnection( datagramConnection );
    }

    public void interrupt() {

        setRunning( false );

        super.interrupt();
    }

    protected abstract boolean execute( DatagramConnection datagramConnection, Datagram datagram ) throws Exception;

    public final void run() {

        setRunning( true );

        DatagramConnection datagramConnection = getDatagramConnection();

        try {

            Datagram datagram = datagramConnection.newDatagram( datagramConnection.getMaximumLength() );

            while (isRunning()) {

                boolean yield = true;

                try {

                    yield = execute( datagramConnection, datagram );
                }
                catch (Throwable t) {

                    log.error( "Lost packet", t );
                }

                if (yield) {

                    Thread.yield();
                }
            }
        }
        catch (Throwable t) {

            log.error( t );
        }
        finally {

            IOUtility.safeClose( datagramConnection );
        }
    }

    private DatagramConnection getDatagramConnection() {

        return datagramConnection;
    }

    private void setDatagramConnection( DatagramConnection datagramConnection ) {

        this.datagramConnection = datagramConnection;
    }

    private boolean isRunning() {

        return running;
    }

    private void setRunning( boolean running ) {

        this.running = running;
    }
}
