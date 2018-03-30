package project.aha.doctor_panel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.PathUtil;
import project.aha.R;
import project.aha.ReceiveResult;
import project.aha.SingleUploadBroadcastReceiver;
import project.aha.SpinnerAdapter;
import project.aha.models.Diagnose;


public class CreateExercise extends AppCompatActivity implements ReceiveResult, SingleUploadBroadcastReceiver.Delegate{

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int STORAGE_PERMISSION_CODE = 123;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video
    Spinner specializes_dropdown_list;


    private Button create;
    private EditText subject;
    private EditText description;
    String specialized;
    int specialized_id;

    String imagePath;
    String videoPath;
    private ImageButton btnCapturePicture, btnRecordVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);

        requestStoragePermission();

        subject = (EditText) findViewById(R.id.subject);
        specializes_dropdown_list = (Spinner) findViewById(R.id.specialized);
        Constants.add_to_spinner(this, specializes_dropdown_list);

        specializes_dropdown_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Diagnose di = (Diagnose) parent.getItemAtPosition(position);
                specialized = di.getName();
                specialized_id = di.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                specialized = null;
            }
        });


        description = (EditText) findViewById(R.id.description);

        create = (Button) findViewById(R.id.create_exercise);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExercise();
            }
        });


        btnCapturePicture = (ImageButton) findViewById(R.id.upload_photo_btn);
        btnRecordVideo = (ImageButton) findViewById(R.id.upload_video_btn);

        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), MEDIA_TYPE_IMAGE);
            }
        });

        /**
         * Record video button click event
         */
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), MEDIA_TYPE_VIDEO);
            }
        });
    }


    public void createExercise() {
        String subjectString = subject.getText().toString();
        String descriptionString = description.getText().toString();


        if (subjectString == null || subjectString.length() == 0) {
            subject.requestFocus();
            Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_LONG).show();
        } else if (descriptionString == null || descriptionString.length() == 0) {
            description.requestFocus();
            Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_LONG).show();
        } else if (specialized == null || specialized.length() == 0) {
            specializes_dropdown_list.requestFocus();
            Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences prefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
            final int user_id = prefs.getInt(Constants.PREF_USER_LOGGED_ID, -1);

            if ((imagePath == null || imagePath.length() == 0 )&&
            (videoPath == null || videoPath.length() == 0)) {

                HashMap<String, String> data = new HashMap<>();
                data.put(Constants.CODE, Constants.CREATE_EXERCISE + "");
                data.put("subject", subjectString);
                data.put("description", descriptionString);
                data.put("specialized_id", specialized_id + "");
                data.put("user_id", user_id + "");
                DatabasePostConnection connection = new DatabasePostConnection(this);
                connection.postRequest(data, Constants.DATABASE_URL);
            } else {
                try {

                    String uploadId = UUID.randomUUID().toString();
                    uploadReceiver.setDelegate(this);
                    uploadReceiver.setUploadID(uploadId);


                    MultipartUploadRequest upload = new MultipartUploadRequest(this, uploadId, Constants.DATABASE_URL);

                    if (imagePath != null && imagePath.length() > 0) {
                        upload.addFileToUpload(imagePath, "image");
                    }

                    if (videoPath != null && videoPath.length() > 0) {
                        upload.addFileToUpload(videoPath, "video");
                    }
                    upload.addParameter(Constants.CODE, Constants.CREATE_EXERCISE + "");
                    upload.addParameter("subject", subjectString);
                    upload.addParameter("description", descriptionString);
                    upload.addParameter("specialized_id", specialized_id + "");
                    upload.addParameter("user_id", user_id + "");


                    upload.setNotificationConfig(new UploadNotificationConfig());
                    upload.setMaxRetries(2);

                    upload.startUpload(); //Starting the upload

                } catch (Exception exc) {
                    Toast.makeText(this, exc.getMessage() + "tttt", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    /**
     * Receiving activity result method will be called after pick something from the gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == MEDIA_TYPE_IMAGE && resultCode == RESULT_OK) {

            Uri selectedImg = data.getData();
            try {
                imagePath = PathUtil.getPath(this, selectedImg);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
//            imagePath = getPath(selectedImg);

        } else if (requestCode == MEDIA_TYPE_VIDEO && resultCode == RESULT_OK) {

            Uri selectedVidUri = data.getData();
            try {
                videoPath = PathUtil.getPath(this, selectedVidUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.SCF_CREATE_EXEC: {
                    // set them to visible
                    Toast.makeText(this, getString(R.string.scf_add_exer), Toast.LENGTH_LONG).show();

                    startActivity(new Intent(this , DoctorMainActivity.class));
                    finish();
                    break;
                }

                // if there are records -> fill list view
                case Constants.ERR_CREATE_EXEC: {
                    // set them to visible
                    Toast.makeText(this, getString(R.string.err_add_exer), Toast.LENGTH_LONG).show();
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }
    @Override
    public void onProgress(int progress) {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        try {
            String response = new String(serverResponseBody, "UTF-8");
            onReceiveResult(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCancelled() {

    }
}