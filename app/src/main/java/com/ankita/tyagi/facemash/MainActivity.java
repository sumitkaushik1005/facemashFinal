package com.ankita.tyagi.facemash;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String srno;
    public static String name;
    public static String gender;
    public String status;
    TextView t1,t2,t3;
    ImageView img;
    public Bitmap bmp;
    private int PICK_IMAGE_REQUEST = 1;
    private int cameraData = 0;
    public static Bundle b;
    String result;
    NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
    SwipeRefreshLayout swipeRefreshLayout;
    TextView t4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"Swipe right for more options.",Toast.LENGTH_LONG).show();

        t1 = (TextView) findViewById(R.id.main_name);
        t2=(TextView)findViewById(R.id.year);
        t3=(TextView)findViewById(R.id.branch);
        img = (ImageView) findViewById(R.id.imageView2);
        t4=(TextView)findViewById(R.id.Score);
     //   img.setImageResource(R.drawable.malen);
        InputStream is = getResources().openRawResource(R.drawable.malen);
        bmp = BitmapFactory.decodeStream(is);

        b = getIntent().getExtras();
        name = b.getString("name");
        name = name.toUpperCase();
       String ar[] = name.split(",");
        name = ar[0];
        gender=ar[1];
        t1.setText("Hello,Welcome " + name);

        new GetSrno().execute(name,gender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Gender.class);
                i.putExtra("info", srno);
                startActivity(i);


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
       swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new GetImage().execute(srno,gender);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
    }

    private boolean isConnectedToServer() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }





      private   class GetImage extends AsyncTask<String, String, Bitmap> {
            ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=ProgressDialog.show(MainActivity.this,"Downloading Image...","Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                progressDialog.dismiss();
                if(bitmap!=null)
                img.setImageBitmap(bitmap);
                        else {
                    if(gender.equals("MALE")){
                        img.setImageResource(R.drawable.malen);
                    }
                    else
                        img.setImageResource(R.drawable.femalen);
                }
            }



            @Override
            protected Bitmap doInBackground(String... strings) {
                String sr=strings[0].replace("/","");
                String gender=strings[1];
                Bitmap b=null;
                try{
                    URL url=new URL("http://103.235.104.96/~hbtiface/facemash/downloadFromServer.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("srno", "UTF-8") + "=" + URLEncoder.encode(sr, "UTF-8")+"&"+
                            URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.close();
                    b=BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                    httpURLConnection.disconnect();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return b;
                           }



        }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


        } else if (id == R.id.nav_edit_profile) {
            boolean b =isConnectedToServer();
            if (b) {
                Intent intent = new Intent(MainActivity.this, EditPofile.class);
                intent.putExtra("srno", srno);
                startActivity(intent);
            } else
                Toast.makeText(MainActivity.this,"Intenet interruption",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_take_pic) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT,true);
            startActivityForResult(i, cameraData);

        } else if (id == R.id.nav_mail) {

            Intent r = new Intent("android.intent.action.ContactUs");
            String info = srno + "," + name;
            r.putExtra("info", info);
            startActivity(r);
        } else if (id == R.id.nav_players) {
            boolean b=isConnectedToServer();
            Intent r = new Intent(MainActivity.this, Players.class);
            if(b)
            startActivity(r);
            else
                Toast.makeText(MainActivity.this,"Internet Interruption...",Toast.LENGTH_SHORT).show();

        }
            else if(id==R.id.aboutus){
            Intent r=new Intent(MainActivity.this,aboutUs.class);
            startActivity(r);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(MainActivity.this, Cropper.class);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    FileOutputStream fileOutputStream=null;
                    try{
                        fileOutputStream=openFileOutput("Image.txt",Context.MODE_PRIVATE);
                        fileOutputStream.write(byteArrayOutputStream.toByteArray());
                        fileOutputStream.close();
                        Toast.makeText(getBaseContext(),"image written",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(getBaseContext(),"error",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    //   img.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            } else if (requestCode == cameraData) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = openFileOutput("Image.txt", Context.MODE_PRIVATE);
                        fileOutputStream.write(byteArrayOutputStream.toByteArray());
                        fileOutputStream.close();
                        Toast.makeText(getBaseContext(), "image written", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    startActivity(intent);
                 //   intent.putExtra("Bitmap", bitmap);
                   // startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }





    public String getStringImage(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("WANT TO EXIT?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                }
            });
            builder.show();
        }

    }

   private class GetSrno extends AsyncTask<String,String,String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(MainActivity.this,"","Please Wait...",true,true);
        }

        @Override
        protected String doInBackground(String... params) {
            String name=params[0];
            String gender=params[1];
            String response = null;
            //      String u="http://shareyourbook.netau.net/test/OgetStatus.php";
            try {
                URL url = new URL("http://103.235.104.96/~hbtiface/facemash/getScore.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")+ "&" +
                        URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                response=bufferedReader.readLine();

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            srno=s;
            new GetImage().execute(srno,gender);
        }
    }
}
