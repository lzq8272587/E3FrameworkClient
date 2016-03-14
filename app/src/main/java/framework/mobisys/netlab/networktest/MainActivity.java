package framework.mobisys.netlab.networktest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.ERequest;
import com.android.volley.Response;

import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import framework.mobisys.netlab.framework.ByteRequest;
import framework.mobisys.netlab.framework.E3FrameworkClient;
import framework.mobisys.netlab.framework.ICallback;
import framework.mobisys.netlab.framework.ObjectRequest;
import framework.mobisys.netlab.framework.StringRequest;
import framework.mobisys.netlab.framework.E3RemoteService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String TAG="MainActivity";
    E3FrameworkClient e3client;
    E3RemoteService e3remote;
    private String callbackString;
    private TextView textView;
    private ImageView imageView1;
    private Bitmap callbackBitmap;
    boolean isBind=false;
    long t1,t2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "" + SystemClock.elapsedRealtime(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        Intent intent =new Intent("framework.mobisys.netlab.framework.E3Service");
//        intent.setPackage("framework.mobisys.netlab.framework");
//        intent.setAction("framework.mobisys.netlab.framework.E3Service");
//        final Intent intent = new Intent();
//        intent.setAction("framework.mobisys.netlab.framework.E3Service");
//        final Intent eintent = new Intent(createExplicitFromImplicitIntent(this,intent));
//        bindService(eintent, mConnection, Context.BIND_AUTO_CREATE);
//
//        while(!isBind);
//
//        try {
//            Log.d(TAG,"Call remote service: "+e3remote.getPid());
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        textView = (TextView) findViewById(R.id.textView);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        e3client=E3FrameworkClient.getInstant(this);
        String url;


        /**
         * 向framework请求文字下载
         */
//        url = "http://121.42.158.232/json_test.txt";
//        e3client.putByteRequest(new ByteRequest(url, ERequest.ACTIVE, "New Text Request"), new Response.Listener<byte[]>() {
//            @Override
//            public void onResponse(byte[] response) {
//
//                Message msg = new Message();
//                callbackString = new String(response);
//                msg.what = 0;
//                msg.obj = callbackString;
//                mHandler.sendMessage(msg);
//            }
//        });

        /**
         * 向framework请求图片下载
         */
        url = "http://121.42.158.232/mountain.jpg";

        t1 = System.currentTimeMillis();
        e3client.putByteRequest(new ByteRequest(url, ERequest.ACTIVE, "New Image Request"), new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                t2 = System.currentTimeMillis();
                Message msg = new Message();
                callbackBitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                msg.what = 1;
                msg.obj = callbackBitmap;
                mHandler.sendMessage(msg);
                Log.e("time","cost time: "+ (t2-t1));
            }
        });



    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    textView.setText(msg.obj.toString());
                    break;
                case 1:
                    imageView1.setImageBitmap((Bitmap) msg.obj);
                    break;

            }
        }

    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
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
