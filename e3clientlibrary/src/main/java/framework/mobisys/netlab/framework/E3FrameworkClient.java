package framework.mobisys.netlab.framework;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import framework.mobisys.netlab.framework.E3RemoteService;
import framework.mobisys.netlab.framework.ICallback;

/**
 * Created by LZQ on 11/25/2015.
 */
public class E3FrameworkClient extends Thread {


    private PriorityBlockingQueue<Request> mQueue = new PriorityBlockingQueue<Request>();
    String TAG = "E3FrameworkClient";

    /**
     * 用于启动Service的Context
     */
    Context context = null;

    /**
     * 处理回调事件的Handler
     */
    private Handler handler = new Handler();


    /**
     * 让整个Framework在用户看来只有一个实例
     */
    static E3FrameworkClient instance = null;


    E3RemoteService e3remote;

    /**
     * 判断是否绑定成功的boolean变量
     */
    public boolean isBind = false;

    //IByteArray iba=new IByteArray();

    /**
     * bind到Service的连接类
     * 这里被bindService调用，执行后续的绑定Service操作
     * 绑定结束之后，bindService会异步的调用onServiceConnected和disconnected函数
     * 所以可以在这里定义绑定成功之后的一系列操作
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            Log.d(TAG, "connected to remote service.");
            e3remote = E3RemoteService.Stub.asInterface(service);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Called when the connection with the service disconnects unexpectedly
            Log.e(TAG, "Service has unexpectedly disconnected");
            e3remote = null;
        }
    };


    /**
     * 在run函数里面绑定到Service中，而run函数会在构造函数中被调用
     */
    public void run() {
        final Intent intent = new Intent();
        intent.setAction("framework.mobisys.netlab.framework.E3Service");
        final Intent eintent = new Intent(createExplicitFromImplicitIntent(context, intent));
        context.bindService(eintent, mConnection, Context.BIND_AUTO_CREATE);


        /**
         * 确保连接上Service之后再执行后续操作
         */
        while (!isBind) ;

        ICallback.Stub mCallback = new ICallback.Stub() {

            /**
             * Demonstrates some basic types that you can use as parameters
             * and return values in AIDL.
             *
             * @param anInt
             * @param aLong
             * @param aBoolean
             * @param aFloat
             * @param aDouble
             * @param aString
             */
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

            }

            @Override
            public void showResult(int result) throws RemoteException {
                Log.e(TAG, "showResult is called.");
            }

            @Override
            public void CallbackString(String s) {
                Log.e(TAG, "Not Successfully download string");

            }

            public void CallbackObject(IByteArray b) {
                if (b.getByte() == null) {
                    Log.e(TAG, "b.getByte()==null");
                } else {
                    Log.e(TAG, "received byte, size= " + b.getByte().length);
                }
            }

        };

//        try {
//            e3remote.registerCallback(mCallback);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        //  while(true)
        {
            try {
                Request request = mQueue.take();

                Log.d(TAG, "call remote service after checking binding state: " + isBind);

                //if(request.instanceof(StringRequest))
                e3remote.putObjectRequest(request.url, request.delay, request.tag, mCallback);


            } catch (InterruptedException e) {
                e.printStackTrace();
                //  continue;
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * 构造函数，保证只有一个实例
     *
     * @param context
     */
    private E3FrameworkClient(Context context) {
        this.context = context;
//        this.mQueue=queue;
        this.start();
    }

    public static E3FrameworkClient getInstant(Context context) {
        if (instance == null) {
            instance = new E3FrameworkClient(context);
        }
        return instance;
    }


    /**
     * E3 API的client版本。这些API的Client版本本质上都是将一些基础参数通过AIDL接口传递给E3 Serivce
     *
     * @param url
     * @param delay
     * @param tag
     * @return
     */
    public StringRequest createStringRequest(String url, int delay, String tag) {
        return new StringRequest(url, delay, tag);
    }

    public ObjectRequest createObjectRequest(String url, int delay, String tag) {
        return new ObjectRequest(url, delay, tag);
    }

    public String perfromStringRequest(StringRequest sr) {
        return null;
    }

    public byte[] performObjectRequest(ObjectRequest or) {
        return null;
    }

    public void putStringRequest(StringRequest sr, ResponseListener rl) {
        String c_url = sr.url;
        int c_delay = sr.delay;
        String c_tag = sr.tag;

        mQueue.add(sr);

//        while(!isBind);

        // Log.d(TAG, "call putStringRequest after service is connected.");
    }

    public void putObjectRequest(ObjectRequest or, ResponseListener rl, ProgressListener pl) {
        mQueue.add(or);
    }

    public void deleteRequest(String tag) {

    }


    /**
     * 回调接口
     */
    interface ResponseListener {
        void onResponse();
    }

    interface ProgressListener {
        void onProgress();
    }


    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     * <p/>
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     * <p/>
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     *
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

}
