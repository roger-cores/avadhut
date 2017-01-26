package com.frostox.calculoII.activities;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.frostox.calculoII.R;
import com.frostox.calculoII.apis.FilesSync;
import com.frostox.calculoII.entities.wrappers.FileListWrapper;
import com.frostox.calculoII.pulled_sourses.Util;
import com.frostox.calculoII.pulled_sourses.wifidirect.WiFiDirectActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {


    LinearLayout buttonContainer;
    Button downloadNow, receiveLater;

    NotificationManager mNotifyManager;

    final Integer NOTIF_ID = 1;

    //SQLiteDatabase db;
    private boolean check = false;
    SharedPreferences sharedPreferences;

    ProgressBar progressBar;


    Button startButton;

    TextView progressMessage;

    BroadcastReceiver internetState;

    //NotificationManager mNotifyManager;
    android.support.v4.app.NotificationCompat.Builder mBuilder;


    PowerManager.WakeLock wakeLock;

    FirebaseAuth mAuth;

    final String wakeTag = "MainActivityWakeTag";

    SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm:ss");

    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        //checkLogin();
        if (mAuth.getCurrentUser()!=null) {
            Intent i = new Intent(this, MenuActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }


        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Downloading study material")
                .setContentText("Download in progress")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);


        mBuilder.setProgress(100, 0, false);


        setContentView(R.layout.activity_main);




        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(R.drawable.start_image).into(imageView);
        startButton = (Button) findViewById(R.id.button);
        progressMessage = (TextView) findViewById(R.id.textView6);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        buttonContainer = (LinearLayout) findViewById(R.id.button_container);
        downloadNow = (Button) findViewById(R.id.download_now);
        receiveLater = (Button) findViewById(R.id.receive_later);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().hide();

        progressBar.setProgress(0);

        if(getIntent().getBooleanExtra("unzip", false)){
            new UnzipTask().execute();
        } else init();





    }

    public void init(){
        internetState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                final ConnectivityManager connMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                final android.net.NetworkInfo wifi = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                final android.net.NetworkInfo mobile = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (isOnline(MainActivity.this)) {
                    // Do something
                    if(progressMessage.getVisibility() == View.VISIBLE && progressMessage.getText().toString().contains("You need internet")){
                        startButton.setVisibility(View.INVISIBLE);
                        progressMessage.setText("Please wait, downloading study material!");
                        progressMessage.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        new ListFileAsyncTask().execute();
                    }
                }
            }
        };






        File extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");
        extraClassFolder.mkdirs();
        File contents[] = extraClassFolder.listFiles();
        boolean firstTime = sharedPreferences.getBoolean("firstTime", true);

        if(contents == null || contents.length == 0 || firstTime){
            startButton.setVisibility(View.GONE);
            progressMessage.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            buttonContainer.setVisibility(View.VISIBLE);
            //progressBar.setProgress(1);

            //progressMessage.setText("Please wait, downloading study material");

            //new ListFileAsyncTask().execute();


        }
    }

    public void downloadNow(View view){
        progressBar.setProgress(1);
        startButton.setVisibility(View.INVISIBLE);
        progressMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.INVISIBLE);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        this.registerReceiver(internetState, intentFilter);

        progressMessage.setText("Please wait, downloading study material (0%)");

        new ListFileAsyncTask().execute();
    }

    public void receiveLater(View view){
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        intent.putExtra("goHome", true);
        intent.putExtra("send", false);
        this.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if(internetState!=null)
                this.unregisterReceiver(this.internetState);
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onnresume", "reached here");

        if(internetState!=null){
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

            this.registerReceiver(internetState, intentFilter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    public void goLogin(View v) {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    public boolean checkLogin() {
        check = sharedPreferences.getBoolean("check", false);
        return check;
    }


    class FileType {
        private String type;
        private String name;
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class FileDownloaderAsyncTask extends AsyncTask<FileListWrapper, Integer, Integer> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;



        private boolean interrupted = false;

        public FileDownloaderAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            wakeLock.release();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(NOTIF_ID, mBuilder.build());
        }

        @Override
        protected Integer doInBackground(FileListWrapper... wrappers) {




            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);


            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeTag);
            wakeLock.acquire();

            float i = -1;
            FileListWrapper wrapper = wrappers[0];
            List<com.frostox.calculoII.entities.File> files = wrapper.getFiles();
            Long totalSize = wrapper.getTotalSize();
            Long downloadedSize = new Long(0);

            ArrayList<DownloadManager.Request> downloadRequests = new ArrayList<>();

            for(com.frostox.calculoII.entities.File file : files){

                File rootDir = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");



                File destination = new File(rootDir, file.getPath());

                if(interrupted){
                    return 0;
                }

                if(file.getAction() != null && file.getAction().equals("d")){
                    destination.delete();

                    continue;
                }

                i++;
                System.out.print("Data:: i:" + i);
                int count;
                try {

                    //**** new Download Part ****//

//                    URL url = new URL(getString(R.string.base_url_extraclass) + "uploads/" + file.getPath().replaceAll(" ", "%20"));
//                    String destinationPath = destination.getAbsolutePath();
//                    new File(destinationPath.substring(0, destinationPath.lastIndexOf('/'))).mkdirs();
//
//                    DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(getString(R.string.base_url_extraclass) + "uploads/" + file.getPath()));
//                    downloadRequest.setDescription("Downloading Study Material Data")
//                            .setTitle("ExtraCLASS file")
//                            .setVisibleInDownloadsUi(false)
//                            .setDestinationUri(Uri.fromFile(destination))
//                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//                    downloadRequests.add(downloadRequest);


                    //**** new Download Part ****//

                    URL url = new URL(getString(R.string.base_url_extraclass) + "uploads/" + file.getPath().replaceAll(" ", "%20"));
                    URLConnection conection = url.openConnection();
                    conection.setConnectTimeout(30000);
                    conection.setReadTimeout(30000);
                    conection.connect();



                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(
                            url.openStream(), 8192);


                    String destinationPath = destination.getAbsolutePath();
                    new File(destinationPath.substring(0, destinationPath.lastIndexOf('/'))).mkdirs();

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(destination);
                    System.out.println("Data::" + destination.getAbsolutePath());

                    byte data[] = new byte[1024];

                    long total = 0;

                    double progress = ((downloadedSize/((float) totalSize)) * 95.0d);

                    while ((count = input.read(data)) != -1) {
                        System.out.println("Data::" + count);
                        total += count;
                        downloadedSize += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            double temp = ((downloadedSize/((float) totalSize)) * 95.0d);
                            if(Math.abs(temp - progress) > 5){
                                publishProgress((int) (temp));
                            }
                        }



                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    //input.close();
                    ((HttpURLConnection) conection).disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    
                }




            }

            for(DownloadManager.Request downloadRequest:downloadRequests){
                long myDownloadReference = downloadManager.enqueue(downloadRequest);

            }

            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            progressBar.setProgress(progress[0]);

            mBuilder.setProgress(100, progress[0], false);
            mNotifyManager.notify(NOTIF_ID, mBuilder.build());
            progressMessage.setText("Please wait, downloading study material (" + progress[0] + "%)");

            if(!isOnline(MainActivity.this)){
                progressBar.setVisibility(View.INVISIBLE);
                progressMessage.setText("You need internet connection to download study material. Please check your connection and try again.");
                interrupted = true;
                mBuilder.setContentText("Download Failed");
                mBuilder.setProgress(100, 0, false);
                mNotifyManager.notify(NOTIF_ID, mBuilder.build());

            }
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);

            if(s == 0 || !isOnline(MainActivity.this)){
                mBuilder.setContentText("Failed to download. Check network");
                mNotifyManager.notify(NOTIF_ID, mBuilder.build());
                progressBar.setVisibility(View.INVISIBLE);
                progressMessage.setText("You need internet connection to download study material. Please check your connection and try again.");
                wakeLock.release();
                return;
            }
            progressBar.setVisibility(View.INVISIBLE);
            progressMessage.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("firstTime", false);
            editor.commit();

            wakeLock.release();
            mNotifyManager.cancel(NOTIF_ID);

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class ListFileAsyncTask extends AsyncTask<String, Integer, FileListWrapper>{

        File extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");
        List<com.frostox.calculoII.entities.File> files = new ArrayList<>();

        public void walk(File root) {


            File[] list = root.listFiles();

            for (File f : list) {
                if (f.isDirectory()) {
                    walk(f);
                }
                else {
                    Log.d("", "File: " + f.getAbsoluteFile());
                    com.frostox.calculoII.entities.File file = new com.frostox.calculoII.entities.File();
                    file.setSize(f.length());
                    file.setModified(sdf.format(f.lastModified()));
                    file.setPath(extraClassFolder.toURI().relativize(f.toURI()).getPath());
                    files.add(file);
                }
            }
        }

        @Override
        protected FileListWrapper doInBackground(String... params) {
            files.clear();
            this.walk(extraClassFolder);
            FileListWrapper wrapper = new FileListWrapper();
            wrapper.setFiles(files);
            return wrapper;
        }

        @Override
        protected void onPostExecute(FileListWrapper wrapper) {
            super.onPostExecute(wrapper);

            progressBar.setProgress(3);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url_extraclass))
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            FilesSync syncService = retrofit.create(FilesSync.class);

            Call<FileListWrapper> callSync = syncService.getDownloadableFiles(wrapper);

            callSync.enqueue(new Callback<FileListWrapper>() {
                @Override
                public void onResponse(Call<FileListWrapper> call, Response<FileListWrapper> response) {
                    FileListWrapper wrapper =  response.body();
                    progressBar.setProgress(5);
                    System.out.println(files.size() + " <- size");
                    new FileDownloaderAsyncTask(MainActivity.this).execute(wrapper);
                }

                @Override
                public void onFailure(Call<FileListWrapper> call, Throwable t) {
                    t.printStackTrace();
                    progressBar.setVisibility(View.INVISIBLE);
                    progressMessage.setText("You need internet connection to download study material. Please check your connection and try again.");
                    return;
                }
            });

        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());

    }


    class UnzipTask extends AsyncTask<Void, Void, Void>{

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "Saving files", "Please wait... It may take a few minutes!", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final File recvFile = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass.zip");
            try {
                Util.unzip(recvFile, new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            init();
        }
    }



}
