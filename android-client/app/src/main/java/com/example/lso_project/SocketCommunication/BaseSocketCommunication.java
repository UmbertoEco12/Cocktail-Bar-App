package com.example.lso_project.SocketCommunication;

import android.util.Log;

import com.example.lso_project.Activities.MainActivity;
import com.example.lso_project.Helpers.SSLHelper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
public abstract class BaseSocketCommunication {

    private static final String TAG = "Base Socket Communication";
    public static String SERVER_ADDRESS= "192.168.1.164"; // The server address
    public static int SERVER_PORT = 8888; // The server port
    protected Thread currentThread; // thread running the operation
    protected volatile boolean canInterrupt; // if the thread can be interrupted
    // time before interrupting the thread if still running in seconds
    protected final int timeBeforeInterrupting = 2;

    public static boolean TLS = true;

    private Thread timerThread;

    // socket classes
    protected Socket socket;
    protected DataOutputStream output;
    protected DataInputStream input;

    // starts the timer thread
    protected void startTimerThread()
    {
        if(timerThread != null)
        {
            timerThread.interrupt();
        }
        timerThread = new Thread(this::timerThreadRunnable);
        timerThread.start();
    }
    // waits for x seconds than sets can interrupt true
    private void timerThreadRunnable()
    {
        try
        {
            Log.d(TAG, "timer started");
            canInterrupt = false;
            TimeUnit.SECONDS.sleep(timeBeforeInterrupting);
        }
        catch (InterruptedException ignored)
        {
        }
        finally {
            timerThread = null;
            canInterrupt = true;
            Log.d(TAG, "timer finished");
        }
    }

    // creates the socket connection
    protected void socketOpen() throws IOException
    {
        try{
            if(TLS)
            {
                socket = SSLHelper.createSSLSocketWithTrustAllCertificates(SERVER_ADDRESS, SERVER_PORT);
            }
            else// tcp
            {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getClass().toString());
        }
        // Create a print writer to send data to the server
        output = new DataOutputStream(socket.getOutputStream());
        // Create a buffered reader to receive data from the server
        input = new DataInputStream(socket.getInputStream());
    }
    // closes the socket connection
    protected void socketClose() throws IOException
    {
        // close output
        output.close();
        // close input
        input.close();
        // close socket
        socket.close();
        // finished so can be interrupted
        if(timerThread != null)
        {
            timerThread.interrupt();
            timerThread = null;
        }
        canInterrupt = true;
    }
    // writes a message for socket
    // writes the message length first as a 4 byte unsigned int
    // and then writes the message
    protected byte[] writeSocketMessage(String message)
    {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + bytes.length);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer.array();
    }
    // reads a socket message
    // reads first the length of the message
    // create byte[] from length
    protected byte[] readSocketMessage() throws IOException
    {
        int len = input.readInt();
        // len = Integer.reverseBytes(len);
        byte[] data = new byte[len];
        input.readFully(data);
        return data;
    }
    // read from socket
    protected byte[] readBytes(int len) throws IOException
    {
        byte[] data = new byte[len];
        input.readFully(data);
        return data;
    }
    // read an int and changes it to little endian
    protected int readInt() throws IOException
    {
        int i = input.readInt();
        // i = Integer.reverseBytes(i);
        return i;
    }

}
