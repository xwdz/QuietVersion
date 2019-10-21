### QuietVersion

![logo](./imgs/logo.jpg)

用于Android App检测自动更新

> 如果你觉得这个lib对你有用,随手给个Star,让我知道它是对你有帮助的,我会继续更新和维护它。

### 实现效果
<img src="./imgs/simple.gif" width="200px">  <img src="./imgs/simple2.gif" width="200px">

### 添加依赖

$lastVersion = [![](https://jitpack.io/v/xwdz/QuiteVersion.svg)](https://jitpack.io/#xwdz/QuiteVersion)

```
    //如已依赖可忽略
    implementation 'com.squareup.okhttp3:okhttp:3.5.0'
    implementation 'com.xwdz:QuietVersion:lastVersion'
```

### 特点

- 任何地方都可以调用
- 自定义安装策略校验器(默认校验Apk的MD5，一致则视为通过进行安装)
- 支持自定义界面
- 自定义通知栏
- 支持静默下载最新Apk
- 自调起安装界面
- 内部使用okHttp进行网络通讯
- 适配7.0、8.0、9.0


### 需要权限
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```


### 配置说明


|方法名|说明|默认实现|
|---:|:---|:----|
|setForceDownload|是否强制每次都从服务器下载|true|
|setUIActivityClass|自定义activity UI|[DefaultDialogActivity.class](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/ui/DefaultDialogActivity.java)|
|setUpgradeNetworkStrategy|自定义升级规则|[AppUpgradeStrategy.class](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/strategy/AppUpgradeStrategy.java)|
|addVerifyApkStrategy|安装校验规则|[VerifyApkStrategy.java](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/strategy/VerifyApkStrategy.java)|
|setUpgradeNetworkStrategy|指定网络下进行跟新|[AppNetwork.java](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/entry/AppNetwork.java)|

   ...

#### 注意一定要调用`setResponseNetworkParser`将数据`ApkSource`返回给`QuietVersion`,否则则视为当前没有新版!

### 快速使用

```
 public class TestApp extends Application {
 
     @Override
     public void onCreate() {
         super.onCreate();
 
          AppConfig appConfig = new AppConfig(getApplicationContext());
                 appConfig.setForceDownload(true);
                 appConfig.setUpgradeNetworkStrategy(AppNetwork.ALL);
                 QuietVersion.initialize(appConfig);
     }
 }
 
 
   @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
 
          QuietVersion.get(REQUEST_URL) // or post
                       .addHeaders("","")
                       .addParams("","")
                       .setUpgradeStrategy(AppUpgradeStrategy.NORMAL)
                       .setResponseNetworkParser(new ResponseNetworkParser() {
                         @Override
                         public ApkSource parser(String response) {
                             try {
                                 JSONObject jsonObject        = new JSONObject(response);
                                 String     note              = jsonObject.getString("note");
                                 String     fileSize          = jsonObject.getString("fileSize");
                                 String     url               = jsonObject.getString("url");
                                 String     remoteVersionCode = jsonObject.getString("remoteVersionCode");
                                 String     remoteVersionName = jsonObject.getString("remoteVersionName");
                                 String     md5               = jsonObject.getString("md5");
                                 return new ApkSource(url, note,
                                         Long.parseLong(fileSize),
                                         Integer.parseInt(remoteVersionCode),
                                         remoteVersionName,
                                         md5);
     
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                             return null;
                         }
                       }).check();
     }

```



### 进阶使用

1. 添加多个安装校验规则,实现`VerifyApkStrategy`接口即可

```
  appConfig.addVerifyApkStrategy(new VerifyApkStrategy() {
            @Override
            public boolean verify(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
                String md5 = SignatureUtil.getAppSignatureMD5(context);
                return apkSource.getMd5().toLowerCase().equals(md5.toLowerCase());
            }

            @Override
            public int priority() {
                return PRIORITY_8;
            }

            @Override
            public String getName() {
                return "MD5校验器";
            }
        });

        appConfig.addVerifyApkStrategy(new VerifyApkStrategy() {
            @Override
            public boolean verify(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
                // 其他自定义安装策略
                // true 检验正确 反之错误
                return false;
            }

            @Override
            public int priority() {
                return PRIORITY_10;
            }

            @Override
            public String getName() {
                return "自定义策略校验器";
            }
        });
```

2. 如果使用的第二种升级策略即`setUpgradeStrategy(AppUpgradeStrategy.FORCE_SILENT_DOWNLOAD_NOTIFICATION)`则可以继承[BaseNotification](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/core/BaseNotification.java)来实现自己的通知栏UI。
   [SimpleDefaultNotification](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/core/DefaultNotification.java)


3. 自定义UI


1. **继承[`AbstractActivity`](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/ui/AbstractActivity.java)重写如下三个方法,通过`setUIActivityClass(xxx.class)`注入.**

```
    //自己定义的UI layout
    public abstract int getContentLayoutId();
    //setContentView方法调用以后会调用此方法（通常用来做一些初始化操作）
    public abstract void onViewCreated();
    //当执行下载任务的时候回回调到此方法,需要可重写此方法
    public void onUpdateProgress(int percent, long currentLength, long total){
    
    }
```


参考[`DefaultDialogActivity`](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/ui/DefaultDialogActivity.java)
可通过`getIntent().getParcelableExtra("note")`拿到`ApkSource`对象


2. **自定义容器中，点击开始下载时,一定要调用如下代码**

```
VersionHandler.startDownloadApk(getContext());
```

##### 注意: 当自定义UI进度条界面销毁时候调用`UpgradeHandler.recycle()`;



### 颜色配置
如果`lib`颜色配置不满意可在您项目`colors.xml`文件下重写以下颜色值

|名称|说明|示例|
|:--:|:--:|:--:|
|`quiet_version_button_theme`|`dialog更新按钮颜色`|[quiet_version_button_theme](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|
|`quiet_version_download_file_size`|`进度条下面文件下载进度文字颜色`|[quiet_version_download_file_size](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|
|`quiet_version_progress_background`|`下载进度条背景颜色`|[quiet_version_progress_background](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|
|`quiet_version_progress`|`下载进度条颜色`|[quiet_version_progress](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|


#### 适配7.0

在AndroidManifest.xml添加如下代码

```
<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="您的包名.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
```

在res文件夹下新建xml文件夹新建文件file_paths.xml添加如下代码

```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path path="Android/data/您的包名/" name="files_root" />
    <external-path path="." name="external_storage_root" />
</paths>
```

#### 混淆

```
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#QuietVersion
-keep class com.xwdz.version.**{*;}

```

[@酸菜xwdz](http://huangxingwei.cn)
[Github](https://github.com/xwdz/QuietVersion)

