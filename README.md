### QuietVersion

![logo](./imgs/logo.jpg)

用于Android App检测自动更新

> 如果你觉得这个lib对你有用,随手给个Star,让我知道它是对你有帮助的,我会继续更新和维护它。

### 实现效果
![simple.gif](./imgs/simple.gif)

### 添加依赖

$lastVersion = [![](https://jitpack.io/v/xwdz/QuiteVersion.svg)](https://jitpack.io/#xwdz/QuiteVersion)

```
    //如已依赖可忽略
    implementation 'com.squareup.okhttp3:okhttp:3.5.0'
    implementation 'com.xwdz:QuietVersion:lastVersion'
```

### 特点

- 任何地方都可以调用
- 支持自定义界面
- 支持静默下载最新Apk
- 自调起安装界面
- 内部使用okHttp进行网络通讯
- 支持OKHttp拦截器
- 适配7.0、8.0、9.0


### 需要权限
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

### 配置

```
VersionConfigs.getImpl()
            .setForceDownload(true)
            .setApkPath(String apkPath)
            .setApkName(String apkName)
            // 是否强制每次都从服务器下载
            .setForceDownload(boolean force)
            // 自定义activity 
            .setUIActivityClass(xxx.class)
            // 
            .setOnCheckVersionRules(new DefaultCheckVersionRules());
```

### 配置说明


|方法名|说明|默认实现|
|---:|:---|:----|
|setForceDownload|是否强制每次都从服务器下载|false|
|setApkName|apk名称|apk名称为Url最后一个`/`至`.`为止之间内容为名称|
|setApkPath|apk文件存储路径|context.getExternalFilesDir("apk").getAbsolutePath() + / + apkFilename|
|setUIActivityClass|自定义activity UI|[DefaultDialogActivity.class](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/ui/DefaultDialogActivity.java)|
|setOnCheckVersionRules|自定义升级规则|[DefaultCheckVersionRules.class](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/core/DefaultCheckVersionRules.java)|


### 颜色配置
如果`lib`颜色配置不满意可在您项目`colors.xml`文件下重写以下颜色值

|名称|说明|示例|
|:--:|:--:|:--:|
|`quiet_version_button_theme`|`dialog更新按钮颜色`|[colors.xml](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|
|`quiet_version_download_file_size`|`进度条下面文件下载进度文字颜色`|[colors.xml](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|
|`quiet_version_progress_background`|`下载进度条背景颜色`|[colors.xml](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|
|`quiet_version_progress`|`下载进度条颜色`|[colors.xml](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/res/values/colors.xml)|


### 简单使用

```
        QuietVersion.getInstance(this)
                //or POST
                .GET("http://www.baidu.com")
                .addHeader()
                .addParams()
                .addInterceptor()
                .addNetworkInterceptor()
                // 开发者必须实现此接口,返回QuiteVersion需要的Apk信息,如果返回null,则视为没有新版本更新
                .setOnNetworkParserListener(new OnNetworkParserListener() {
                    @Override
                    public ApkSource parser(String response) {
                        return new ApkSource(
                                kugou,
                                "更新内容如下\n1.你好\n2.我不好",
                                123123123,
                                123,
                                9999
                        );
                    }
                })
                .apply();

//界面销毁时注意释放资源
Quite.getInstance(this).recycle()
```

### 自定义UI


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


3. **自定义容器中注册接受下载进度条组件。如果继承了`AbstractActivity`可忽略(可选)**

```
private final VersionHandler.ProgressReceiver mProgressReceiver = new VersionHandler.ProgressReceiver() {
        @Override
        public void onUpdateProgress(long total, long currentLength, int percent) {
            Utils.LOG.i("tag", "current = " + currentLength);
        }
    };


//在容器创建等合适的时候调用注册代码
VersionHandler.registerProgressbarReceiver(getContext(), mProgressReceiver);

//容器销毁的时候调用注销代码
VersionHandler.unregisterProgressbarReceiver(getContext(), mProgressReceiver);
```


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

