package project.aha.doctor_panel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
//import project.aha.SingleUploadBroadcastReceiver;
import project.aha.SingleUploadBroadcastReceiver;
import project.aha.SpinnerAdapter;
import project.aha.models.Diagnose;


public class CreateExercise extends AppCompatActivity implements ReceiveResult, SingleUploadBroadcastReceiver.Delegate {

    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();

    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int MEDIA_TYPE_IMAGE = 1;

    private Spinner specializes_dropdown_list;

    private Button create;
    private EditText subject;
    private EditText description, youtube_link;
    private String specialized;
    private int specialized_id;

    private String imagePath;
    private String videoPath;
    private ImageButton btnCapturePicture;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);
        Constants.showLogo(this);

        requestStoragePermission();

        subject = (EditText) findViewById(R.id.subject);

        // get the dropdown of specializes
        specializes_dropdown_list = (Spinner) findViewById(R.id.specialized);
        if(Constants.diagnoses != null && !Constants.diagnoses.isEmpty()){
            ArrayList<Diagnose> diagnoses = new ArrayList<>(Constants.diagnoses.values());
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_style, diagnoses );
            specializes_dropdown_list.setAdapter(spinnerAdapter);

            // set action when user clicks on specialize
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
        }



        description = (EditText) findViewById(R.id.description);
        youtube_link = (EditText) findViewById(R.id.youtube_link);
        create = (Button) findViewById(R.id.create_exercise);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExercise();
            }
        });


        btnCapturePicture = (ImageButton) findViewById(R.id.upload_photo_btn);

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
    }


    public void createExercise() {
        String subjectString = subject.getText().toString();
        String descriptionString = description.getText().toString();
        videoPath = youtube_link.getText().toString();


        if (subjectString == null || subjectString.length() == 0) {
            subject.requestFocus();
            Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_LONG).show();
        } else if (descriptionString == null || descriptionString.length() == 0) {
            description.requestFocus();
            Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_LONG).show();
        } else if (specialized == null || specialized.length() == 0) {
            specializes_dropdown_list.requestFocus();
            Toast.makeText(this, getString(R.string.field_required), Toast.LENGTH_LONG).show();
        } else if (videoPath != null && videoPath.length() > 0 && !videoPath.toLowerCase().contains("youtube")) {
            youtube_link.requestFocus();
            Toast.makeText(this, getString(R.string.must_be_youtube_link), Toast.LENGTH_LONG).show();
        } else {


            final int user_id = Constants.get_current_user_id(this);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(true);
            progressDialog.show();

            if ((imagePath == null || imagePath.length() == 0)) {

                HashMap<String, String> data = new HashMap<>();
                data.put(Constants.CODE, Constants.CREATE_EXERCISE + "");
                data.put("subject", subjectString);
                data.put("description", descriptionString);
                data.put("specialized_id", specialized_id + "");
                data.put("user_id", user_id + "");


                if (videoPath != null && videoPath.length() > 0) {
                    data.put("youtube_link", videoPath);

                }
                DatabasePostConnection connection = new DatabasePostConnection(this);
                connection.postRequest(data, Constants.DATABASE_URL);
            } else {
                try {

                    String uploadId = UUID.randomUUID().toString();
                    uploadReceiver.setDelegate(this);
                    uploadReceiver.setUploadID(uploadId);


                    MultipartUploadRequest upload = new MultipartUploadRequest(this, uploadId, Constants.DATABASE_URL).setUtf8Charset();

                    if (imagePath != null && imagePath.length() > 0) {
                        upload.addFileToUpload(imagePath, "image");
                        upload.setMaxRetries(2);
                    }

                    if (videoPath != null && videoPath.length() > 0) {
                        upload.addParameter("youtube_link", videoPath);

                    }
                    upload.addParameter(Constants.CODE, Constants.CREATE_EXERCISE + "");
                    upload.addParameter("subject", subjectString);
                    upload.addParameter("description", descriptionString);
                    upload.addParameter("specialized_id", specialized_id + "");
                    upload.addParameter("user_id", user_id + "");

                    upload.startUpload(); //Starting the upload

                } catch (Exception exc) {
                    Log.d("Error in multipart", exc.getMessage());
                    Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
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

        }
    }


    //Requesting permission -> from internet
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

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, getString(R.string.denied_storage_access), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onReceiveResult(String resultJson) {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }

        try {
            Log.d("RESULT", resultJson);
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.SCF_CREATE_EXEC: {
                    // set them to visible
                    Toast.makeText(this, getString(R.string.scf_add_exer), Toast.LENGTH_LONG).show();

                    startActivity(new Intent(this, DoctorMainActivity.class));
                    finish();
                    break;
                }


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
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
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
    public void onCancelled() {

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

    private static class PathUtil { // from internet
        /*
         * Gets the file path of the given Uri.
         */
        @SuppressLint("NewApi")
        public static String getPath(Context context, Uri uri) throws URISyntaxException {
            final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
            String selection = null;
            String[] selectionArgs = null;
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            // deal with different Uris.
            if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{split[1]};
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        if(Constants.get_current_user_type(this) == Constants.ADMIN_TYPE){
            menu.findItem(R.id.chat_activity).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Constants.handleItemChoosed(this, super.onOptionsItemSelected(item), item);
    }
}