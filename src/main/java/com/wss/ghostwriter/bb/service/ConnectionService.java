package com.wss.ghostwriter.bb.service;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;

import m.org.apache.log4j.Logger;

import org.metova.mobile.event.EventDispatcher;
import org.metova.mobile.net.Wap2xDetails;
import org.metova.mobile.rt.bb.net.WapDetailsUtility;
import org.metova.mobile.rt.system.MobileDevice;
import org.metova.mobile.rt.system.MobileNetwork;
import org.metova.mobile.util.io.IOUtility;

import com.wss.ghostwriter.bb.event.ErrorEvent;
import com.wss.ghostwriter.bb.model.Preferences;
import com.wss.ghostwriter.core.model.Message;
import com.wss.ghostwriter.core.service.ErrorCode;
import com.wss.ghostwriter.core.service.Ports;

public class ConnectionService {

    private static final Logger log = Logger.getLogger( ConnectionService.class );

    private static ConnectionService connectionService;

    private DatagramConnection datagramConnection;

    private OutgoingThread outgoingThread;
    private IncomingThread incomingThread;
    private ExecutorThread executorThread;

    private long keepAlive;

    private ConnectionService() throws Exception {

        Preferences preferences = PreferenceStore.instance().load();

        String serverDomain = preferences.getServerDomain();
        StringBuffer outgoingAddress = new StringBuffer( "datagram://" + serverDomain + ":" + Ports.SERVER + ";" + Ports.SERVER );

        getConnectParams( outgoingAddress );

        DatagramConnection datagramConnection = (DatagramConnection) Connector.open( outgoingAddress.toString() );
        setDatagramConnection( datagramConnection );

        setOutgoingThread( new OutgoingThread( datagramConnection ) );
        setIncomingThread( new IncomingThread( datagramConnection ) );
        setExecutorThread( new ExecutorThread( datagramConnection ) );

        setKeepAlive( System.currentTimeMillis() );
    }

    public static void getConnectParams( StringBuffer stringBuffer ) {

        if ( MobileDevice.instance().isSimulator() ) {

            stringBuffer.append( ";deviceside=true" );
        }
        else if ( MobileNetwork.instance().isWiFiActive() ) {

            stringBuffer.append( ";interface=wifi" );
        }
        else {

            stringBuffer.append( ";deviceside=true" );

//            Wap1xDetails wap1xDetails = WapDetailsUtility.getWap1xDetails();

            Wap2xDetails wap2xDetails = WapDetailsUtility.getWap2xDetails();
            if ( wap2xDetails != null ) {

                stringBuffer.append( ";ConnectionUID=" + wap2xDetails.getServiceRecordUid() );
            }
        }
    }

    public static void instantiate() {

        try {

            setConnectionService( new ConnectionService() );

            log.info( "Server connection instantiated" );
        }
        catch (Throwable t) {

            log.error( "Unable to instantiate server connection", t );

            ErrorEvent errorEvent = new ErrorEvent();
            errorEvent.setSource( new Integer( ErrorCode.INSTANTIATION ) );
            EventDispatcher.instance().fireEvent( errorEvent );
        }
    }

    public static void reset() {

        dispose();
        instantiate();
    }

    public static void dispose() {

        close();
        setConnectionService( null );

        log.info( "Server connection disposed" );
    }

    public static boolean isAlive() {

        boolean result = false;

        ConnectionService connectionService = getConnectionService();
        if ( connectionService != null ) {

            result |= connectionService.getIncomingThread().isAlive();
            result |= connectionService.getOutgoingThread().isAlive();
            result |= connectionService.getExecutorThread().isAlive();

            return result;
        }

        return result;
    }

    public static boolean start() {

        ConnectionService connectionService = getConnectionService();
        if ( connectionService != null ) {

            connectionService.getIncomingThread().start();
            connectionService.getOutgoingThread().start();
            connectionService.getExecutorThread().start();

            return true;
        }

        return false;
    }

    public static boolean interrupt() {

        ConnectionService connectionService = getConnectionService();
        if ( connectionService != null ) {

            IncomingThread incomingThread = connectionService.getIncomingThread();
            if ( incomingThread.isAlive() ) {

                incomingThread.interrupt();
            }

            OutgoingThread outgoingThread = connectionService.getOutgoingThread();
            if ( outgoingThread.isAlive() ) {

                outgoingThread.interrupt();
            }

            ExecutorThread executorThread = connectionService.getExecutorThread();
            if ( executorThread.isAlive() ) {

                executorThread.interrupt();
            }

            return true;
        }

        return false;
    }

    public static boolean close() {

        ConnectionService connectionService = getConnectionService();
        if ( connectionService != null ) {

            interrupt();

            connectionService.setIncomingThread( null );
            connectionService.setOutgoingThread( null );
            connectionService.setExecutorThread( null );

            IOUtility.safeClose( connectionService.getDatagramConnection() );
            connectionService.setDatagramConnection( null );

            return true;
        }

        return false;
    }

    public static boolean send( Message message ) {

        ConnectionService connectionService = getConnectionService();
        if ( connectionService != null ) {

            connectionService.getOutgoingThread().enqueue( message );
            return true;
        }

        return false;
    }

    public static boolean execute( Message message ) {

        ConnectionService connectionService = getConnectionService();
        if ( connectionService != null ) {

            connectionService.getExecutorThread().enqueue( message );
            return true;
        }

        return false;
    }

    private DatagramConnection getDatagramConnection() {

        return datagramConnection;
    }

    private void setDatagramConnection( DatagramConnection datagramConnection ) {

        this.datagramConnection = datagramConnection;
    }

    public static ConnectionService getConnectionService() {

        return connectionService;
    }

    private static void setConnectionService( ConnectionService connectionService ) {

        ConnectionService.connectionService = connectionService;
    }

    private OutgoingThread getOutgoingThread() {

        return outgoingThread;
    }

    private void setOutgoingThread( OutgoingThread outgoingThread ) {

        this.outgoingThread = outgoingThread;
    }

    private IncomingThread getIncomingThread() {

        return incomingThread;
    }

    private void setIncomingThread( IncomingThread incomingThread ) {

        this.incomingThread = incomingThread;
    }

    public long getKeepAlive() {

        return keepAlive;
    }

    public void setKeepAlive( long keepAlive ) {

        this.keepAlive = keepAlive;
    }

    private ExecutorThread getExecutorThread() {

        return executorThread;
    }

    private void setExecutorThread( ExecutorThread executorThread ) {

        this.executorThread = executorThread;
    }
}
