package com.fa.android.firebaseapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    Button Choose,Upload,clear;
    Spinner spin;
    Uri fileUri;
    String tempFile;


     TextView f1;


    FirebaseStorage storage;
    FirebaseDatabase database;

    String a,b,c,d,e;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();

        Choose=(Button) findViewById(R.id.button11);
        Upload=(Button) findViewById(R.id.button13);

        clear=(Button) findViewById(R.id.button12);

        spin=(Spinner) findViewById(R.id.spinner);

        final EditText m1=(EditText) findViewById(R.id.editText9);
        final EditText m2=(EditText) findViewById(R.id.editText10);
        final EditText m3=(EditText) findViewById(R.id.editText11);
        final EditText m4=(EditText) findViewById(R.id.editText12);
        //final EditText m5=(EditText) findViewById(R.id.editText13);

        f1=(TextView) findViewById(R.id.textView23);



        f1.setText("");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.batches));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

                e =arg0.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                e="A";
            }
        });

        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    a=m1.getText().toString();
                    b=m2.getText().toString();
                    c=m3.getText().toString();
                    d=m4.getText().toString();
                    //e=m5.getText().toString();

                    if(a.length()==0)
                        Toast.makeText(getApplicationContext(),"Need atleast 1 Enrollment no!",Toast.LENGTH_SHORT).show();
                    else if(e.length()==0)
                        Toast.makeText(getApplicationContext(),"Enter lab batch",Toast.LENGTH_SHORT).show();
                    else
                        selectfile();
                }
                else
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 9);

            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fileUri!=null) {
                    uploadfile(fileUri);
                    a=m1.getText().toString();
                    b=m2.getText().toString();
                    c=m3.getText().toString();
                    d=m4.getText().toString();
                   // e=m5.getText().toString();

                }
                else
                    Toast.makeText(getApplicationContext(),"Select a file!",Toast.LENGTH_SHORT).show();

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m1.setText("");
                m2.setText("");
                m3.setText("");
                m4.setText("");
               // m5.setText("");
                f1.setText("");
                spin.setSelection(0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectfile();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please Provide Permission",Toast.LENGTH_SHORT).show();
        }
    }

    void uploadfile(Uri fileUri)
    {

        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File....");
        progressDialog.setProgress(0);
        progressDialog.show();


        String filename=" "+a+"_"+b+"_"+c+"_"+d;
        String temp=new String(tempFile);
        int dotFlag = 0;
        String ext=new String("");
        for(int i=0;i<temp.length();i++)
        {
            if(temp.charAt(i)=='.')
            {
                dotFlag=1;
            }
            if(dotFlag ==1)
            {
                ext=ext+String.valueOf(temp.charAt(i));
            }
        }
        filename+=ext;
        String dir=e.toUpperCase();
        final StorageReference storageReference=storage.getReference();
        storageReference.child(dir).child(filename).putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getApplicationContext(),"Succesfully Uploaded !",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Toast.makeText(getApplicationContext(),"Not Uploaded !",Toast.LENGTH_SHORT).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            int currentProgress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
            progressDialog.setProgress(currentProgress);
            if(currentProgress==100)
                progressDialog.dismiss();

            }
        });

    }

    void selectfile()
    {
        Intent intent=new Intent();
        intent.setType("application/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            fileUri=data.getData();
            Toast.makeText(getApplicationContext(),data.getData().toString(),Toast.LENGTH_SHORT).show();
            tempFile=data.getData().getLastPathSegment();
            f1.setText(" "+data.getData().getLastPathSegment());

            //this is point
        }
        else
        {
            Toast.makeText(getApplicationContext(),"PLease Select a file !",Toast.LENGTH_SHORT).show();
        }
    }
}
