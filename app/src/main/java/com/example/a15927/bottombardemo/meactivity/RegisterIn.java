package com.example.a15927.bottombardemo.meactivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a15927.bottombardemo.R;
import com.example.a15927.bottombardemo.Utils.AppStr;
import com.example.a15927.bottombardemo.Utils.ImageUtils;
import com.example.a15927.bottombardemo.Utils.MD5Utils;
import com.example.a15927.bottombardemo.Utils.PostPicToYun;
import com.example.a15927.bottombardemo.Utils.TestAndVerify;
import com.example.a15927.bottombardemo.dialog.DialogUIUtils;
import com.example.a15927.bottombardemo.functiontools.PostWith;
import com.example.a15927.bottombardemo.functiontools.UserCO;
import com.example.a15927.bottombardemo.functiontools.UserVO;
import com.google.gson.Gson;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a15927.bottombardemo.dialog.DialogUIUtils.dismiss;


public class RegisterIn extends AppCompatActivity implements View.OnClickListener{
    private EditText input_username;
    private EditText input_password;
    private EditText password_confirm;
    private Button comit_register;
    private ImageView takephoto;
    private ImageView arrow_back;
    private TextView back;

    //拍照功能参数
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private final static int CROP_IMAGE = 3;

    //imageUri照片真实路径
    private Uri imageUri;
    //照片存储
    File filePath;

    //访问的服务器网页
    private int opType = 90001;
    private String url = "http://47.105.185.251:8081/Proj31/register";//http://192.168.2.134:8081/Proj20/register

    //进度条一
    Dialog progressDialog;
    //进度条二
    //private LoadingDialog dialog;

    //    private Handler mHandler = new Handler() {
    //        public void dispatchMessage(android.os.Message msg) {
    //            if (dialog != null && dialog.isShowing()) {
    //                dialog.dismiss();
    //            }
    //        };
    //    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.register_in );

        //绑定控件并设置监听
        input_username = (EditText) findViewById( R.id.user_input );
        input_password = (EditText) findViewById( R.id.password_input );
        password_confirm = (EditText) findViewById( R.id.password_confirm );
        takephoto = (ImageView) findViewById( R.id.takephoto );
        takephoto.setOnClickListener( this );
        arrow_back = (ImageView) findViewById( R.id.arrow_back );
        arrow_back.setOnClickListener( this );
        back = (TextView) findViewById( R.id.back );
        back.setOnClickListener( this );
        comit_register = (Button) findViewById( R.id.register );
        comit_register.setOnClickListener( this );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takephoto:
                //底部弹框Dialog
                //获取拍照和手机相册的权利
                List<String> stringList = new ArrayList<>();
                stringList.add( "拍照" );
                stringList.add( "从相册选择" );

                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog( RegisterIn.this, stringList );
                optionBottomDialog.setItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //取消底部弹框
                        //optionBottomDialog.dismiss();
                        switch (position) {
                            case 0:
                                //测试使用，验证是否为position= 0
                                //Toast.makeText(RegisterIn.this,"position 0", Toast.LENGTH_SHORT ).show();

                                //启动相机程序
                                //隐式Intent
                                Intent intent_photo = new Intent( "android.media.action.IMAGE_CAPTURE" );
                                //putExtra()指定图片的输出地址，填入之前获得的Uri对象
                                imageUri = ImageUtils.getImageUri( RegisterIn.this );
                                intent_photo.putExtra( MediaStore.EXTRA_OUTPUT, imageUri );
                                startActivityForResult( intent_photo, TAKE_PHOTO );
                                //底部弹框消失
                                optionBottomDialog.dismiss();
                                break;
                            case 1:
                                //测试使用，验证是否为position= 1
                                //Toast.makeText(RegisterIn.this,"position 1", Toast.LENGTH_SHORT ).show();
                                //打开相册
                                openAlbum();
                                //底部弹框消失
                                optionBottomDialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                } );
                break;
            case R.id.register:
                //application全局变量
                AppStr appStr = (AppStr)getApplication();
                if(appStr.isState() == true){
                    String yunFlag = PostPicToYun.getYunFlag();
                    Log.i( "Test", "onClick: "+yunFlag );
                    String picUrl = PostPicToYun.getPicUrl();
                    Log.i( "Test", "onClick: "+picUrl );
                    if(yunFlag != null && yunFlag.equals( "OK" )){
                        if(picUrl != null || picUrl.length() > 20){
                            allCommit(picUrl);
                        }
                    }else{
                        Toast.makeText( this, "头像上传失败！", Toast.LENGTH_SHORT ).show();
                        Log.i( "Test", "onClick: 上传失败！");
                    }
                }else{
                    Toast.makeText( RegisterIn.this, "头像图片上传尚未成功，请稍作等待！", Toast.LENGTH_SHORT ).show();
                }
                break;
            case R.id.arrow_back:
                finish();
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    //打开相册
    private void openAlbum() {
        Intent intent_album = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
        //需要返回给此活动一个消息，如果打开相册成功，则需要显示图片到活动中
        startActivityForResult( intent_album, CHOOSE_PHOTO );
    }

    //剪切图片
    private void startImageCrop(File saveToFile,Uri uri) {
        if(uri == null){
            return ;
        }
        Intent intent = new Intent( "com.android.camera.action.CROP" );
        Log.i( "Test", "startImageCrop: " + "执行到压缩图片了" + "uri is " + uri );
        intent.setDataAndType( uri, "image/*" );//设置Uri及类型
        //uri权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra( "crop", "true" );
        intent.putExtra( "aspectX", 1 );//X方向上的比例
        intent.putExtra( "aspectY", 1 );//Y方向上的比例
        intent.putExtra( "outputX", 500 );//裁剪区的X方向宽
        intent.putExtra( "outputY", 500 );//裁剪区的Y方向宽
        intent.putExtra( "scale", true );//是否保留比例
        intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString() );
        intent.putExtra( "return-data", false );//是否将数据保留在Bitmap中返回dataParcelable相应的Bitmap数据，防止造成OOM
        //判断文件是否存在
        if (!saveToFile.getParentFile().exists()) {
            saveToFile.getParentFile().mkdirs();
        }
        //将剪切后的图片存储到此文件
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveToFile));
        startActivityForResult( intent, CROP_IMAGE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //设置照片存储文件及剪切图片
                    File saveFile = ImageUtils.getTempFile();
                    filePath = ImageUtils.getTempFile();
                    startImageCrop( saveFile,imageUri );
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //选中相册照片显示
                    try {
                        imageUri = data.getData(); //获取系统返回的照片的Uri
                        Log.i( "Test", "onActivityResult: uriImage is " +imageUri );
                        //设置照片存储文件及剪切图片
                        File saveFile = ImageUtils.setTempFile( RegisterIn.this );
                        filePath = ImageUtils.getTempFile();
                        startImageCrop( saveFile,imageUri );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_IMAGE:
                if(resultCode == RESULT_OK){
                    // 通过图片URI拿到剪切图片
                    //bitmap = BitmapFactory.decodeStream( getContentResolver().openInputStream( imageUri ) );
                    //通过FileName拿到裁剪图片
                    Bitmap bitmap = BitmapFactory.decodeFile( filePath.toString() );
                    //把裁剪后的图片展示出来
                    takephoto.setImageBitmap( bitmap );
                    //application全局变量
                    AppStr appStr = (AppStr)getApplication();
                    appStr.setState( false );
                    Toast.makeText( RegisterIn.this, "即将上传头像图片至腾讯云！", Toast.LENGTH_SHORT ).show();
                    //图片上传腾讯云
                    PostPicToYun.PostPic( RegisterIn.this,filePath ,"reg");
                }
                break;
            default:
                break;
        }
    }

    //注册
    public void allCommit(String picUrl) {
        Log.i( "Test", "allcomit: " +picUrl);
        String inputUsername = input_username.getText().toString().trim();
        String inputpassword = input_password.getText().toString().trim();
        String confirmpassword = password_confirm.getText().toString().trim();

        //数据库写入，再弹出注册成功过的消息，跳入登录活动页面

        if (TextUtils.isEmpty( inputUsername )) {
            Toast.makeText( RegisterIn.this, "用户名不能为空", Toast.LENGTH_SHORT ).show();
        } else {
            if (inputpassword.equals( "" )) {
                Toast.makeText( RegisterIn.this, "密码不能为空", Toast.LENGTH_SHORT ).show();
            } else {
                if (confirmpassword.equals( "" )) {
                    Toast.makeText( RegisterIn.this, "第二次输入密码不能为空", Toast.LENGTH_SHORT ).show();
                } else {
                    if (inputpassword.equals( confirmpassword )) {
                        //进度框显示方法一
                        progressDialog = DialogUIUtils.showLoadingDialog( RegisterIn.this, "正在注册" );
                        progressDialog.show();
                        //                        //进度条显示方法二
                        //                        dialog = new LoadingDialog(RegisterIn.this,R.layout.tips_load);
                        //                        //点击物理返回键是否可取消dialog
                        //                        dialog.setCancelable(true);
                        //                        //点击dialog之外 是否可取消
                        //                        dialog.setCanceledOnTouchOutside(true);
                        //                        //显示
                        //                        dialog.show();
                        register( inputUsername, inputpassword ,picUrl);
                    } else
                        Toast.makeText( RegisterIn.this, "两次密码不一致", Toast.LENGTH_SHORT ).show();
                }
            }
        }
    }

    public void register(String uname, String upassword ,String picUrl) {
        //生成部分属性
        UserVO userVO = new UserVO();
        String uuid = UUID.randomUUID().toString().replaceAll( "-","" );
        userVO.setOpType( opType );
        userVO.setUid( uuid );
        userVO.setUname( uname );
        userVO.setUpassword( MD5Utils.getMD5( upassword ) );
        userVO.setPicDir( picUrl );
        userVO.setUphone( "15927305629" );
        userVO.setSex( 1 );
        userVO.setQq( "1574367559" );
        userVO.setWeixin( "hui18727067996" );
        //String转json
        Gson gson = new Gson();
        String JsonStr = gson.toJson( userVO, UserVO.class );

        //Post请求
        PostWith.sendPostWithOkhttp( url, JsonStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //异常情况的逻辑
                Log.d( "Test", "获取数据失败了" + e.toString() );
                //切换为主线程
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        //取消进度框一
                        dismiss( progressDialog );
                        //取消进度条二
                        //mHandler.sendEmptyMessage(1);
                        String errorData = TestAndVerify.judgeError( RegisterIn.this );
                        Toast.makeText( RegisterIn.this, errorData, Toast.LENGTH_SHORT ).show();
                    }
                } );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d( "Test", "获取数据成功了" );
                    //获取后台返回结果
                    final String responseData = response.body().string();
                    //json转String
                    Gson g = new Gson();
                    UserCO userCO = g.fromJson( responseData, UserCO.class );
                    Log.i( "Test", userCO.toString() );
                    int flag = userCO.getFlag();
                    Log.i( "Test", String.valueOf( flag ) );
                    //                    String message = userCO.getMessage();
                    //                    Log.i( "Test", message );
                    //                    String token = userCO.getToken();
                    //当token无返回值时，为null,但是Log打印时message不可为空，故而出现此步崩溃
                    //Log.i( "Test", token );

                    if (flag == 200) {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                //取消进度框一
                                dismiss( progressDialog );
                                //取消进度条二
                                //mHandler.sendEmptyMessage(1);
                                Toast.makeText( RegisterIn.this, "注册成功", Toast.LENGTH_SHORT ).show();
                                Intent intentTras = new Intent( RegisterIn.this, MeLogin.class );
                                startActivity( intentTras );
                            }
                        } );
                    } else if (flag == 10002) {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                //取消进度框一
                                dismiss( progressDialog );
                                //取消进度条二
                                //mHandler.sendEmptyMessage(1);
                                Toast.makeText( RegisterIn.this, "该用户名已存在，注册失败！", Toast.LENGTH_SHORT ).show();
                            }
                        } );

                    } else {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                //取消进度框一
                                dismiss( progressDialog );
                                //取消进度条二
                                //mHandler.sendEmptyMessage(1);
                                Toast.makeText( RegisterIn.this, "注册失败", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                }
            }
        } );
    }
}
