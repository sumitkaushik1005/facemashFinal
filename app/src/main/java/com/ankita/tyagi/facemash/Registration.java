package com.ankita.tyagi.facemash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Registration extends AppCompatActivity {

    EditText e12, e14,e15;
    Button b12;
    String username, password, gender,conpass;
    Bitmap bmp;
    int cameraData;
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        e12 = (EditText) findViewById(R.id.editText12);
        e14 = (EditText) findViewById(R.id.editText14);
        b12 = (Button) findViewById(R.id.button12);
        e15=(EditText)findViewById(R.id.editText15);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = (RadioButton) findViewById(i);
                gender = checkedRadioButton.getText().toString();
            }
        });
        b12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = e12.getText().toString();
                password = e14.getText().toString();
                conpass=e15.getText().toString();
                int r = 10;
                View focusView = null;
                boolean cancel = true;
                if (TextUtils.isEmpty(username)) {
                    e12.setError("This field is required");
                    focusView = e12;
                    cancel = false;
                }
                if (TextUtils.isEmpty(password)) {
                    e14.setError("This field is required");
                    focusView = e14;
                    cancel = false;
                }
                if(TextUtils.isEmpty(conpass)){
                    e15.setError("This field is required");
                    focusView=e15;
                    cancel=false;
                }
                if (cancel) {
                    boolean b=isConnectedToServer();
                    if(b){
                        r = validateForm(username, password,conpass);}
                    else
                    {
                        Toast.makeText(Registration.this,"Internet Interruption...",Toast.LENGTH_LONG).show();
                    }
                }
                if (!cancel) {
                    focusView.requestFocus();
                    //  Toast.makeText(getBaseContext(),"Not Empty",Toast.LENGTH_SHORT).show();
                }


                if (r == 2) {
                    Toast.makeText(getBaseContext(), "Password Must Be 6 Characters Long", Toast.LENGTH_SHORT).show();
                }
                if (r == 3) {
                    Toast.makeText(getBaseContext(), "UserName's First Letter Must Be Character", Toast.LENGTH_SHORT).show();
                }
                if (r == 4) {
                    Toast.makeText(getBaseContext(), "Make Sure You Haven't Left Any Field Blank", Toast.LENGTH_SHORT).show();
                }


                if (r == 0) {
                    boolean b=isConnectedToServer();
                    if(b)
                        // new BackgroundActivity1().execute(srno, username);
                        new BackgroundActivity().execute(username, password, gender);

                    else
                        Toast.makeText(Registration.this, "Interent Interruption...", Toast.LENGTH_LONG).show();
                }
                if(r==1){
                    Toast.makeText(Registration.this,"Password doesn't match.",Toast.LENGTH_SHORT).show();
                }

            }


        });
    }

    private boolean isConnectedToServer() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }


    private int validateForm(String u, String p,String conpass) {



        char us[] = u.toCharArray();
        if (!((us[0] >= 'a' && us[0] <= 'z') || (us[0] >= 'A' && us[0] <= 'Z'))) {
            return 3;
        }
        if (p.length() < 6) {
            return 2;
        }
        if(!p.equals(conpass)){
            return 1;
        }
        return 0;

    }

<<<<<<< HEAD
=======
    public class BackgroundActivity1 extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(Registration.this,"Verification...","Please Wait...",true,true);
        }

        @Override
        protected String doInBackground(String... strings) {
            String srno = strings[0];
            String username = strings[1];
            String response = "";
            String url = "http://103.235.104.96/~hbtiface/facemash/user_verification.php";
            try {
                URL add = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) add.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("srno", "UTF-8") + "=" + URLEncoder.encode(srno, "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        public void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equals("Valid")) {
                Toast.makeText(getBaseContext(), "Valid", Toast.LENGTH_SHORT);
                // new BackgroundActivity().execute(username, srno, password, gender);

            } else {
                Toast.makeText(getBaseContext(), "Your Name And S.R.No Doesn't Match", Toast.LENGTH_SHORT).show();
            }

        }
    }
>>>>>>> 16ea1f66d36bde9d77720845455306cc37b6012f

    public class BackgroundActivity extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(Registration.this,"Please Wait...","",true,false);
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("s");
            String url = "http://103.235.104.96/~hbtiface/facemash/facemashRegistration.php";
            String username = params[0];
          //  String srno = params[1];
           // String sr = srno.replace("/", "");
            String password = params[1];
            String gender = params[2];
            String response = "";
            try {
                URL u = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) u.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
                        + "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                is.close();
                httpURLConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        public void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            result=result.replaceAll("\\t+\\t","");
            if (result.equals("user has been registered")) {
                Toast.makeText(getBaseContext(), "Sorry!! You Have Created The Account", Toast.LENGTH_SHORT).show();
            } else if (result.equals("data inserted")) {
<<<<<<< HEAD

=======
               /* AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                builder.setMessage("Help us to verify you easily by uploading your RC or ID. ").setPositiveButton("Upload RC/Id", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(intent, cameraData);
                      }
                }).setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getBaseContext(), "You can log In now.Please give us some time to verify You..", Toast.LENGTH_LONG).show();
                        Intent start = new Intent(Registration.this, LoginActivity.class);
                        startActivity(start);
                        finish();

                    }
                });
                builder.show();*/
>>>>>>> 16ea1f66d36bde9d77720845455306cc37b6012f
                Toast.makeText(getBaseContext(), "Registration Successful!! You can log In now.", Toast.LENGTH_LONG).show();
                Intent start = new Intent(Registration.this, LoginActivity.class);
                startActivity(start);
                finish();


            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cameraData && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            bmp = (Bitmap) b.get("data");
            upload();
        }
    }


    private void upload() {

        class Upload extends AsyncTask<String, String, String> {


            @Override
            protected String doInBackground(String... strings) {
                String img = strings[0];
                String srno = strings[1];
                String password=strings[2];
                String sr = srno.replace("/", "");
                String response = "";
                String url = "http://103.235.104.96/~hbtiface/facemash/uploadId.php";
                try {
                    URL add = new URL(url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) add.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("uploadImage", "UTF-8") + "=" + URLEncoder.encode(img, "UTF-8")
                            + "&" + URLEncoder.encode("srno", "UTF-8") + "=" + URLEncoder.encode(sr, "UTF-8")
                            + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }

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
                // progressDialog.dismiss();
                Toast.makeText(Registration.this, "You can log In now.Please give us some time to varify You.. ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Registration.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
        Upload u = new Upload();
        String img = new MainActivity().getStringImage(bmp);
        String srno=null;
        String sr = srno.replace("/", "");
        if (!sr.equals("")) {
            u.execute(img, sr,password);
        } else {
            Toast.makeText(Registration.this, "Please enter your S.R.No... ", Toast.LENGTH_SHORT).show();
        }
    }


}
