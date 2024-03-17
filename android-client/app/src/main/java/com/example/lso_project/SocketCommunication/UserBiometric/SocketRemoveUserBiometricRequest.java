//package com.example.lso_project.SocketCommunication.UserBiometric;
//
//import android.util.Log;
//
//import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
//import com.example.lso_project.StaticInstances.CurrentUser;
//
//import java.io.IOException;
//
//public class SocketRemoveUserBiometricRequest extends BaseSocketCommunication {
//
//    // constructor
//    private SocketRemoveUserBiometricRequest()
//    {
//        super();
//    }
//    // Singleton
//    private static SocketRemoveUserBiometricRequest instance = null;
//
//    public static SocketRemoveUserBiometricRequest getInstance()
//    {
//        if(instance == null)
//        {
//            instance = new SocketRemoveUserBiometricRequest();
//        }
//        return instance;
//    }
//    // used to debug
//    private static final String TAG = "Remove Biometric Request";
//
//    public void removeBiometricRequest()
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
//        currentThread = new Thread(this::threadFunction);
//        currentThread.start();
//    }
//
//    private void threadFunction()
//    {
//        try {
//            // create socket
//            socketOpen();
//            // Create request
//            StringBuilder s = new StringBuilder(String.format("RemoveBio\n%s\n", CurrentUser.getUsername()));
//            // send
//            output.write(writeSocketMessage(s.toString()));
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
