package net.pridi.oliang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;
import net.pridi.oliang.Manifest;
import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.dao.PostNewDao;
import net.pridi.oliang.manager.HttpManager;
import net.pridi.oliang.manager.http.ApiService;
import net.pridi.oliang.manager.http.FileUploadService;
import net.pridi.oliang.manager.http.ServiceGenerator;
import net.pridi.oliang.utils.FileUtils;
import net.pridi.oliang.view.AndroidPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class PostActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 100;
    public static final int PICK_VDO = 200;
    private static final int REQUEST_VIDEO_CAPTURE = 300;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String pathToStoredVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        final EditText etTitle = (EditText) findViewById(R.id.etTitle);
        final EditText etContent= (EditText) findViewById(R.id.etContent);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();
                PostNewDao dao= new PostNewDao();
                dao.setTitle(title);
                dao.setContent(content);
                sendPost(title,content);

            }
        });

        Button btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

            }
        });

        Button btnImgCam = (Button) findViewById(R.id.btnImgCam);
        btnImgCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        Button btnVdo = (Button) findViewById(R.id.btnVdo);
        btnVdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
                }

            }
        });


    }

    private void sendPost(String title,String content) {
        Log.d("API"," sending ");
        Call<PostItemDao> call = HttpManager.getInstance().getService().postNewPost(title,content);
        Log.d("API"," calling ");
        call.enqueue(new Callback<PostItemDao>() {
            @Override
            public void onResponse(Call<PostItemDao> call, Response<PostItemDao> response) {
                Log.d("API","on Response");
            }

            @Override
            public void onFailure(Call<PostItemDao> call, Throwable t) {
                Log.d("API","on Fail "+t.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("API"," on Result "+resultCode+ " RESULT_OK "+RESULT_OK);
        Uri u= data.getData();
        Log.d("API"," before uploadFile resultCode:"+resultCode+" requestCode:"+requestCode);
        if(resultCode==RESULT_OK){
            if(requestCode==PICK_IMAGE) {
                Log.d("API"," uploading Image ");

            }
            if(requestCode==REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imgBitmap = (Bitmap) extras.get("data");
                u=getImageUri(this,imgBitmap);
                Log.d("API"," uploading from Camera Image ");

            }
            if(requestCode==REQUEST_VIDEO_CAPTURE){
                //pathToStoredVideo = getRealPathFromURIPath(u, PostActivity.this);
                //uploadVideoToServer(pathToStoredVideo);

                Log.d("API"," uploading VDO ");

            }
            uploadFile(u);
        }

        Log.d("API"," after uploadFile ");

    }
    private void uploadVideoToServer(String pathToVideoFile){
        Log.d("API"," start uploadFile pathtovdofile "+pathToVideoFile.toString());
        File videoFile = new File(pathToVideoFile);
        Log.d("API"," start uploadFile vdofile "+videoFile.toString());
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userfile", videoFile.getName(), requestFile);
        Log.d("API"," body "+body.toString());
        String descriptionString = "userfile";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);
        Log.d("API"," description "+description.toString());
        Call<ResponseBody> call = HttpManager.getInstance().getService().upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("API"," on Response response"+response.toString());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("API"," on Failure"+t.getMessage());
            }
        });

    }
    private void uploadFile(Uri fileUri) {
        Log.d("API"," start uploadFile fileUri "+fileUri);
        // create upload service client
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri

        File file = FileUtils.getFile(this, fileUri);
        Log.d("API","file "+file);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("userfile", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "userfile";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Log.d("API","body "+body.toString() );
        // finally, execute the request

        Call<ResponseBody> call = HttpManager.getInstance().getService().upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success "+response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
