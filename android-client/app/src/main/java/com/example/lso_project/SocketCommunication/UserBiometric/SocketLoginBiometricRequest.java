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
//public class SocketLoginBiometricRequest extends BaseSocketCommunication {
//
//    // constructor
//    private SocketLoginBiometricRequest()
//    {
//        super();
//    }
//    // Singleton
//    private static SocketLoginBiometricRequest instance = null;
//
//    public static SocketLoginBiometricRequest getInstance()
//    {
//        if(instance == null)
//        {
//            instance = new SocketLoginBiometricRequest();
//        }
//        return instance;
//    }
//    // used to debug
//    private static final String TAG = "Login Biometric Request";
//
//    public void loginBiometricRequest(String hash, IHandleServerResponse response)
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
//            StringBuilder s = new StringBuilder(String.format("LoginBio\n%s\n", hash));
//            // send
//            output.write(writeSocketMessage(s.toString()));
//            // read first int
//            int len = readInt();
//            String res = "NO";
//            // if 0 than there is no match
//            if(len > 0)
//            {
//                // get username
//                String username = new String(readBytes(len));
//                // get password
//                String password = new String(readSocketMessage());
//
//                CurrentUser.Login(username, password);
//                res = "OK";
//            }
//            response.HandleResponse(res);
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
