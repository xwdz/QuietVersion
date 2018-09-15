### QuiteVersion

用于Android App检测自动更新

> 如果你觉得这个lib对你有用,随手给个Star,让我知道它是对你有帮助的,我会继续更新和维护它。

### 实现效果
![WeChatSight112.gif](https://upload-images.jianshu.io/upload_images/2651056-8c50665d70685c18.gif?imageMogr2/auto-orient/strip)

### 添加依赖

$lastVersion = [![](https://jitpack.io/v/xwdz/QuiteVersion.svg)](https://jitpack.io/#xwdz/QuiteVersion)

```
    //如已依赖可忽略
    implementation 'com.squareup.okhttp3:okhttp:3.5.0'
    implementation 'com.xwdz:QuietVersion:0.1.0'
```

### 特点

- 任何地方都可以调用
- 支持自定义界面
- 支持强制下载最新Apk
- 自调起安装界面
- 内部使用okHttp进行网络通讯
- 支持OKHttp拦截器
- 适配7.0

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
|setUIActivityClass|自定义activity UI|[DefaultProgressDialogActivity.class](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/ui/DefaultProgressDialogActivity.java)|
|setOnCheckVersionRules|自定义升级规则|[DefaultCheckVersionRules.class](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/core/DefaultCheckVersionRules.java)|


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

### 扩展

#### 自定义升级UI(推荐)

1. 继承`AbstractActivity`实现自己的UI,重写如下三个方法,通过`setUIActivityClass(xxx.class)`注入.

```
//自己定义的UI layout
public abstract int getContentLayoutId();
//数据初始化
public abstract void setUpData();
//当执行下载任务的时候回回调到此方法
public abstract void updateProgress(int percent, long currentLength, long total);
```
参考[`ProgressDialogActivity`](https://github.com/xwdz/QuietVersion/blob/master/lib/src/main/java/com/xwdz/version/ui/DefaultProgressDialogActivity.java)
可通过`getIntent().getStringExtra("note")`拿到`ApkSource`对象


2. ~~实现`OnUINotify`接口(暂不支持)~~

**在不指定`.setNotifyHandler()方法以及`setShowUIActivity`方法时默认实现效果参照文章开头`**
**在自定义容器中实现此接口,在接口方法`show`中调用真正的`show`方法**,[详见simple-code](https://github.com/xwdz/QuietVersion/blob/master/app/src/main/java/com/update/testabc/DialogTest.java)

**注意:自定义容器只能使用一种方式。**


#### 自定义容器中，点击开始下载时,需要调用如下代码

```
VersionHandler.startDownloadApk(getContext());
```


#### 自定义容器中注册接受下载进度条组件

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

#### 配置混淆

```
-keep class com.xwdz.version.** {*;}
```

[@酸菜xwdz](http://huangxingwei.cn)
[Github](https://github.com/xwdz/QuietVersion)

