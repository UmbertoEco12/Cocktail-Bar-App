//package com.example.lso_project.SocketCommunication.UserBiometric;
//
//import android.util.Log;
//
//import com.example.lso_project.Helpers.IHandleServerResponse;
//import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
//import com.example.lso_project.StaticInstances.CurrentUser;
//
//import java.io.IOException;
//
//public class SocketInsertUserBiometricRequest extends BaseSocketCommunication {
//
//    // constructor
//    private SocketInsertUserBiometricRequest()
//    {
//        super();
//    }
//    // Singleton
//    private static SocketInsertUserBiometricRequest instance = null;
//
//    public static SocketInsertUserBiometricRequest getInstance()
//    {
//        if(instance == null)
//        {
//            instance = new SocketInsertUserBiometricRequest();
//        }
//        return instance;
//    }
//    // used to debug
//    private static final String TAG = "Insert Biometric Request";
//
//    public void insertBiometricRequest(String hash, IHandleServerResponse response)
//    {
//        if(currentThread != null)
//        {
//            Log.d(TAG, "thread operation still running");
//
//            if(canInterrupt)
//            {
//                // tries to interrupt the thread
//                Log.d(TAG, "interrupting thread");
//                currentThread.interrupt();
//            }
//            else
//            {
//                return;
//            }
//        }
//        // start a timer that sets canInterrupt
//        startTimerThread();
//        currentThread = new Thread(() -> threadFunction(hash, response));
//        currentThread.start();
//    }
//
//    private void threadFunction(String hash, IHandleServerResponse response)
//    {
//        try {
//            // create socket
//            socketOpen();
//            // Create request
//            StringBuilder s = new StringBuilder(String.format("InsertBio\n%s\n%s\n", CurrentUser.getUsername(), hash));
//            // send
//            output.write(writeSocketMessage(s.toString()));
//            // read message
//            String msg = new String(readSocketMessage());
//            // handle response
//            response.HandleResponse(msg);
//            // close
//            socketClose();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG,"Error");
//        }finally {
//            currentThread = null;
//        }
//    }
//
//}
