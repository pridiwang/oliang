package net.pridi.oliang.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.okhttp.OkHttpStack;
import net.pridi.oliang.Manifest;
import net.pridi.oliang.R;
import net.pridi.oliang.adapter.CatListAdapter;
import net.pridi.oliang.dao.CatItemCollectionDao;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.dao.PostNewDao;
import net.pridi.oliang.manager.HttpManager;
import net.pridi.oliang.manager.http.ApiService;
import net.pridi.oliang.manager.http.FileUploadService;
import net.pridi.oliang.manager.http.ServiceGenerator;
import net.pridi.oliang.utils.FileUtils;
import net.pridi.oliang.utils.ProgressRequestBody;
import net.pridi.oliang.view.AndroidPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

public class PostActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks {
    public static final int PICK_IMAGE = 100;
    public static final int PICK_VDO = 200;
    private static final int REQUEST_VIDEO_CAPTURE = 400;
    private static final int REQUEST_IMAGE_CAPTURE = 300;
    EditText etTitle ;
    EditText etContent;
    private Spinner spCatgeogry;
    private String strImage="";
    private String strVdo="";
    private Toolbar toolbar;
    ProgressBar progressBar;
    ImageView ivThumbImage;
    private CatListAdapter catListAdapter;
    String catname;
    Uri selectedImage;
    private Uri mediaUri;
    String mediaStr;
    private Bitmap imgBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        if(savedInstanceState!=null){
            strImage=savedInstanceState.getString("strImage");
            strVdo=savedInstanceState.getString("strVdo");
            imgBitmap=savedInstanceState.getParcelable("imgBitmap");
            if(imgBitmap!=null){

                Log.d("API"," got bitmap");
            }else{
                Log.d("API"," no bitmap");
            }


        }
        initInstance();

    }
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("strImage",strImage);
        outState.putString("strVdo",strVdo);
        outState.putParcelable("imgBitmap",imgBitmap);


        super.onSaveInstanceState(outState);
    }
    private void initInstance() {

        catListAdapter=new CatListAdapter();
        spCatgeogry= (Spinner) findViewById(R.id.spCategory);

        Call<CatItemCollectionDao> call = HttpManager.getInstance().getService().loadCatList();
        call.enqueue(new Callback<CatItemCollectionDao>() {
            @Override
            public void onResponse(Call<CatItemCollectionDao> call, Response<CatItemCollectionDao> response) {
                if(response.isSuccessful()) {
                    if(response.body()!=null) {
                        catListAdapter.setDao(response.body());
                        spCatgeogry.setAdapter(catListAdapter);
                        spCatgeogry.setSelection(0);
                        //showToast("catname: "+catListAdapter.getDao().getData().get((spCatgeogry.getSelectedItemPosition())).getId());
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<CatItemCollectionDao> call, Throwable t) {

            }
        });

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent= (EditText) findViewById(R.id.etContent);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ivThumbImage= (ImageView) findViewById(R.id.ivThumbImage);
        if(imgBitmap!=null){
            ivThumbImage.setImageBitmap(imgBitmap);
        }
        ivThumbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (intent.resolveActivity(getPackageManager()) != null) {}
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });


        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNew();
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
                //if (intent.resolveActivity(getPackageManager()) != null) {}
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

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

    private void sendPost(int category,String title,String content) {
        Log.d("API"," sending ");
        Call<PostItemDao> call = HttpManager.getInstance().getService().postNewPost(category,title,content,strImage,strVdo);
        Log.d("API"," calling ");
        call.enqueue(new Callback<PostItemDao>() {
            @Override
            public void onResponse(Call<PostItemDao> call, Response<PostItemDao> response) {
                Log.d("API","on Response strImage:"+strImage+" strVdo:"+strVdo);
                finish();
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
        Uri u = data.getData();
        Log.d("API"," before uploadFile resultCode:"+resultCode+" requestCode:"+requestCode);
        if(resultCode==RESULT_OK){
            if(requestCode==PICK_IMAGE) {
                u= data.getData();
                Log.d("API"," uploading Image ");
            }
            if(requestCode==REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                imgBitmap = (Bitmap) extras.get("data");
                u=getImageUri(this,imgBitmap);
                Log.d("API"," uploading from Camera Image ");
                ivThumbImage.setImageBitmap(imgBitmap);
            }
            if(requestCode==REQUEST_VIDEO_CAPTURE){
                u= data.getData();
                Log.d("API"," uploading VDO ");


            }
            uploadMultipart(getApplicationContext(),u,requestCode);
        }

        Log.d("API"," after uploadFile ");

    }
    private void uploadFile(Uri fileUri, final int requestCode) {
        Log.d("API"," start uploadFile fileUri "+fileUri);
        // create upload service client
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri

        File file = FileUtils.getFile(this, fileUri);
        Log.d("API","file:"+file);
        final String fileName=file.getName();
        Log.d("API"," filename:"+fileName);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        ProgressRequestBody fileBody= new ProgressRequestBody(file,this);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("userfile", file.getName(), fileBody);

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
                fileName.replace(" ","_");
                if( requestCode ==PICK_IMAGE){
                    strImage=fileName;
                }
                if( requestCode ==REQUEST_IMAGE_CAPTURE){
                    strImage=fileName;
                    Log.d("API","uploading strImage:"+fileName);
                }
                if( requestCode ==REQUEST_VIDEO_CAPTURE){
                    strVdo=fileName;
                    Log.d("API","uploading strVdo:"+fileName);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressBar.setProgress(percentage);
    }

    @Override
    public void onError() {
        // do something;
    }

    @Override
    public void onFinish() {
        progressBar.setProgress(100);
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
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_post){
            postNew();
            return true;
        }
        if(item.getItemId()== android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void postNew(){
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        int category= catListAdapter.getDao().getData().get((spCatgeogry.getSelectedItemPosition())).getId();
        sendPost(category,title,content);

    }

    private void showToast(String text){
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }
    public void uploadMultipart(final Context context,Uri fileUri, final int requestCode) {
        try {
            Log.d("Upload","start uploadMultipart ");
            Log.d("Upload","filUri:"+fileUri);
            Log.d("Upload","filUri getPath:"+fileUri.getPath());
            Log.d("Upload","filUri toString:"+fileUri.toString());
            Log.d("uplaod","fileUri realpath"+getRealPathFromURIPath(fileUri,this));
            File file = FileUtils.getFile(this, fileUri);
            Log.d("API","file:"+file);
            long fileSize=file.length();

            final String fileName=file.getName();
            Log.d("API"," filename:"+fileName);
            Log.d("API"," filesize:"+fileSize);
            if( requestCode ==PICK_IMAGE){
                strImage=fileName;
            }
            if( requestCode ==REQUEST_IMAGE_CAPTURE){
                strImage=fileName;
                Log.d("API","uploading strImage:"+strImage);

            }
            if( requestCode ==REQUEST_VIDEO_CAPTURE){
                strVdo=fileName;
                Log.d("API","uploading strVdo:"+fileName);
                imgBitmap= ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),MediaStore.Video.Thumbnails.MICRO_KIND);
                ivThumbImage.setImageBitmap(imgBitmap);
            }
            //.setNotificationConfig(new UploadNotificationConfig())
            String uploadId =
                    new MultipartUploadRequest(context, "http://oliang.itban.com/upload")
                            // starting from 3.1+, you can also use content:// URI string instead of absolute file
                            .addFileToUpload(getRealPathFromURIPath(fileUri,this), "userfile")
                            .setMaxRetries(3)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(Context context, UploadInfo uploadInfo) {
                                    progressBar.setProgress(uploadInfo.getProgressPercent());
                                    Log.d("Upload", "progress:"+uploadInfo.getProgressPercent());
                                }

                                @Override
                                public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
                                    showToast(exception.getMessage());
                                    Log.e("Upload",exception.getMessage());

                                }

                                @Override
                                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    progressBar.setProgress(100);
                                    Log.d("Upload","complete uploadMultipart ");
                                }

                                @Override
                                public void onCancelled(Context context, UploadInfo uploadInfo) {
                                    Log.d("Upload","cancel uploadMultipart ");
                                }
                            })
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

}