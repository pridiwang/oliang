package net.pridi.oliang.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

import static android.view.View.GONE;

public class PostActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks {
    private static final String FILE_PROVIDER_AUTHORITY = "net.pridi.oliang.fileprovider";
    private  static final String TAG ="Post";
    public static final int PICK_IMAGE = 100;
    public static final int PICK_VDO = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 300;
    private static final int REQUEST_VIDEO_CAPTURE = 400;
    private static final int PROGRESS_BAR_MAX = 1000;
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
    private Uri clipUri;
    private Uri imgUri;
    String mediaPath;
    private Uri mHihgQualityImageUri;
    String mediaStr;
    private Bitmap imgBitmap;
    File finalFile;
    String filename = Environment.getExternalStorageDirectory().getPath() + "/test/testfile.jpg";
    Uri imageUri = Uri.fromFile(new File(filename));
    ContentResolver resolver;
    private Future<Void> mFuture;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        if(savedInstanceState!=null){
            strImage=savedInstanceState.getString("strImage");
            strVdo=savedInstanceState.getString("strVdo");
            imgBitmap=savedInstanceState.getParcelable("imgBitmap");
            if (savedInstanceState.containsKey("mediaUri")) {
                mediaUri=Uri.parse(savedInstanceState.getString("mediaUri"));
                //ivThumbImage.setImageURI(mediaUri);

            }
            if(savedInstanceState.containsKey("imgBitmap")){
                imgBitmap=savedInstanceState.getParcelable("imgBitmap");
                if(imgBitmap!=null)
                    ivThumbImage.setImageBitmap(imgBitmap);
            }


        }
        initInstance();

    }
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("strImage",strImage);
        outState.putString("strVdo",strVdo);
        outState.putParcelable("imgBitmap",imgBitmap);
        if(mediaUri!=null){
            outState.putString("mediaUri",mediaUri.toString());
        }
        if(imgBitmap!=null){
            outState.putParcelable("imgBitmap",imgBitmap);
        }
        Log.d("API"," saveInstance mediaUri "+mediaUri+" mHi: "+mHihgQualityImageUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("mediaUri")) {
            mediaUri=Uri.parse(savedInstanceState.getString("mediaUri"));
            Log.d("API"," restore mediaUri:"+mediaUri);
            //ivThumbImage.setImageURI(mediaUri);

        }
        Log.d("API"," restoreInstance mediaUri "+mediaUri+" mHi: "+mHihgQualityImageUri);
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
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        View.OnClickListener submitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNew();
            }
        };
        btnSubmit.setOnClickListener(submitListener);

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
        View.OnClickListener onCamClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ContentValues values = new ContentValues();
                //values.put(MediaStore.Images.Media.TITLE, "oliang");
                //values.put(MediaStore.Images.Media.DESCRIPTION, "picture");
                //imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                File file=getOutputMediaFile(1);
                imgUri=Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);


            }
        };
        btnImgCam.setOnClickListener(onCamClickListener);
        ivThumbImage.setOnClickListener(onCamClickListener);


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
        Button btnUpVdo = (Button) findViewById(R.id.btnUpVdo);
        btnUpVdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select VDO"), PICK_VDO);

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

        Log.d("API"," on result: mediaUri:"+mediaUri);
        Log.d("API"," on Result "+resultCode+ " RESULT_OK "+RESULT_OK+" request_code "+requestCode+" data:");
        if(data==null){
            Log.d("API"," data null");
            //if(requestCode== REQUEST_IMAGE_CAPTURE) data.setData(imgUri);
            //return;
        }else {
            try {
                mediaUri = data.getData();
                String mediaPath = getRealPathFromURIPath(mediaUri);

            } catch (Exception e) {
                Log.e("API", "get data error " + e.getMessage());
            }
        }

        Log.d("API"," before uploadFile resultCode:"+resultCode+" requestCode:"+requestCode+" mediaUri:"+mediaUri);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case PICK_IMAGE:
                    mediaUri= data.getData();
                    ivThumbImage.setImageURI(mediaUri);
                    imgBitmap =((BitmapDrawable) ivThumbImage.getDrawable()).getBitmap();
                    Log.d("API"," uploading Image ");
                    uploadMultipart(getBaseContext(),mediaUri,requestCode);
                    break;

                case REQUEST_IMAGE_CAPTURE:
                    //mediaUri= data.getData();
                    Log.d("API","uploading from Camera Image imgUri:"+imgUri);


                    ivThumbImage.setImageURI(imgUri);
                    //imgBitmap =((BitmapDrawable) ivThumbImage.getDrawable()).getBitmap();
                    uploadMultipart(getBaseContext(),imgUri,requestCode);
                    break;

                case REQUEST_VIDEO_CAPTURE:
                    mediaUri= data.getData();
                    Log.d("API"," uploading VDO ");
                    if(vdoResize(data,requestCode)) {
                        Log.d(TAG," resized");
                        //uploadMultipart(getBaseContext(), mediaUri, requestCode);
                    }
                    break;

                case PICK_VDO:
                    mediaUri= data.getData();
                    if(vdoResize(data,requestCode)) {
                        Log.d(TAG," resized");
                    }
                    break;

                default:
                    Log.d("API","no request code");
                    break;
            }

        }else{
            Log.d("API"," result not ok "+resultCode);
        }
    }
    private boolean vdoResize(Intent data, final int requestCode){
        final File file;
        final long startTime = SystemClock.uptimeMillis();
        ContentResolver resolver = getContentResolver();
        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            File outputDir = new File(getExternalFilesDir(null), "outputs");
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdir();
            file = File.createTempFile("clip_", ".mp4", outputDir);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create temporary file.", e);
            Toast.makeText(this, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            parcelFileDescriptor = resolver.openFileDescriptor(data.getData(), "r");
        } catch (FileNotFoundException e) {
            Log.w("Could not open '" + data.getDataString() + "'", e);
            Toast.makeText(PostActivity.this, "File not found.", Toast.LENGTH_LONG).show();
            return false;
        }
        final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
                if (progress < 0) {
                    //progressBar.setIndeterminate(true);
                } else {
                    //progressBar.setIndeterminate(false);
                    progressBar.setProgress((int) Math.round(progress * 100)/2);
                    Log.d(TAG," convert progress : "+Math.round(progress*100));
                }
            }

            @Override
            public void onTranscodeCompleted() {
                Log.d(TAG, "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                onTranscodeFinished(true, "transcoded file placed on file:" + file, parcelFileDescriptor);
                //Uri uri = FileProvider.getUriForFile(PostActivity.this, FILE_PROVIDER_AUTHORITY, file);
                clipUri= Uri.fromFile(file);
                Log.d(TAG,"new file:"+file+ " fileUri:"+clipUri);
                uploadMultipart(getBaseContext(), clipUri, requestCode);


/*                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(uri, "video/mp4")
                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
                        */
            }

            @Override
            public void onTranscodeCanceled() {
                onTranscodeFinished(false, "Transcoder canceled.", parcelFileDescriptor);
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                onTranscodeFinished(false, "Transcoder error occurred.", parcelFileDescriptor);

            }
        };
        Log.d(TAG, "transcoding into " + file);
        mFuture = MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, file.getAbsolutePath(),
                MediaFormatStrategyPresets.createAndroid720pStrategy(8000 * 1000, 128 * 1000, 1), listener);
        return true;
    }
    private void onTranscodeFinished(boolean isSuccess, String toastMessage, ParcelFileDescriptor parcelFileDescriptor) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        //progressBar.setIndeterminate(false);
        //progressBar.setProgress(isSuccess ? PROGRESS_BAR_MAX : 0);
        Toast.makeText(PostActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG," onTranscodeFinished "+toastMessage);
        try {
            parcelFileDescriptor.close();
        } catch (IOException e) {
            Log.w("Error while closing", e);
        }
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

    private String getRealPathFromURIPath(Uri contentURI) {
        try {
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                return contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }

        }catch (Exception e){
            return e.getMessage();
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
        btnSubmit.setVisibility(View.INVISIBLE);
        if(fileUri==null) return;
        Log.d("Upload","start uploadMultipart ");
        try {
            if(( requestCode ==REQUEST_VIDEO_CAPTURE)&&(clipUri!=null)){
                Log.d("Upload","change from fileUri:"+fileUri+" to clipUri:"+clipUri);
                fileUri=clipUri;
            }
            //fileUri=mHihgQualityImageUri;
            Log.d("Upload","fileUri: "+fileUri);
            String filePath = fileUri.getPath();
            Log.d("Upload","fileUri getPath: "+filePath);
            String fileString=fileUri.toString();
            Log.d("Upload","fileUri toString: "+fileString);
            String fileRealPath=getRealPathFromURIPath(fileUri);
            Log.d("upload","fileUri realPath: "+fileRealPath);

            File file = FileUtils.getFile(this, fileUri);
            Log.d("upload","file :"+file);
            Log.d("upload","file toString :"+file.toString());
            //file=finalFile;


            long fileSize=file.length();
            final String fileName=file.getName();
            Log.d("API"," filename:"+fileName);
            Log.d("API"," filesize:"+fileSize);
            if( requestCode ==PICK_IMAGE){
                strImage=fileName;
            }
            if( requestCode ==REQUEST_IMAGE_CAPTURE){
                strImage=fileName;
                fileRealPath=file.toString();
                Log.d("API","uploading multipart strImage:"+strImage+" filerealpath:"+fileRealPath);

            }
            if( requestCode ==REQUEST_VIDEO_CAPTURE){
                strVdo=fileName;
                Log.d("API","uploading strVdo:"+fileName);
                imgBitmap= ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                ivThumbImage.setImageBitmap(imgBitmap);
            }
            if( requestCode ==PICK_VDO){
                strVdo=fileName;
                Log.d("API","uploading strVdo:"+fileName);
                imgBitmap= ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                ivThumbImage.setImageBitmap(imgBitmap);
                fileRealPath=file.toString();
            }

            //getTempFile(this).getPath();

            //.setNotificationConfig(new UploadNotificationConfig())
            //filePath=getRealPathFromURIPath(fileUri);
            String uploadId =
                    new MultipartUploadRequest(context, "http://oliang.itban.com/upload")
                            // starting from 3.1+, you can also use content:// URI string instead of absolute file
                            .addFileToUpload(fileRealPath, "userfile")
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
                                    btnSubmit.setVisibility(View.VISIBLE);
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
    private Uri generateTimeStampPhotoFileUri() {

        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if (outputDir != null) {
            Time t = new Time(System.currentTimeMillis());
            File photoFile = new File(outputDir, System.currentTimeMillis()
                    + ".jpg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }
    private File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageStagte = Environment.getExternalStorageState();
        if (externalStorageStagte.equals(Environment.MEDIA_MOUNTED)) {
            File photoDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(photoDir, getString(R.string.app_name));
            if (!outputDir.exists())
                if (!outputDir.mkdirs()) {
                    Toast.makeText(
                            this,
                            "Failed to create directory "
                                    + outputDir.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                    outputDir = null;
                }
        }
        return outputDir;
    }
    private  File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApplication");

        /* Create the storage directory if it does not exist */
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        /*Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }
}