package com.uihyun.lib;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.login.DefaultAudience;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.uihyun.lib.image.ImageCacheManager;
import com.uihyun.lib.logger.Logger;
import com.uihyun.lib.util.RequestManager;
import com.uihyun.medic.R;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Uihyun on 2017. 1. 19..
 */
@ReportsCrashes
        (
                // Google Docs 로 전송시에만 사용됨 비워두면 된다.
                // This is required for backward compatibility but not used
                formKey = "",
                // Crash 발생 즉시 Toast 메시지로 알림 : Toast 내용
                resToastText = R.string.crash_toast_text,
                // Dialog 형태로 알림
                mode = ReportingInteractionMode.DIALOG,
                // Dialog 표시 아이콘
                resDialogIcon = android.R.drawable.ic_dialog_info,
                // Dialog Title 표시 문구
                resDialogTitle = R.string.crash_dialog_title,
                // Dialog 본문 표시 문구
                resDialogText = R.string.crash_dialog_text,
                // Dialog OK 선택시 발생 Toast
                resDialogOkToast = R.string.crash_dialog_ok_toast,
                customReportContent = {ReportField.APP_VERSION_CODE,
                        ReportField.APP_VERSION_NAME,
                        ReportField.ANDROID_VERSION,
                        ReportField.PHONE_MODEL,
                        ReportField.CUSTOM_DATA,
                        ReportField.STACK_TRACE,
                        ReportField.LOGCAT,
                        ReportField.USER_APP_START_DATE,
                },
//                formUri = "https://nuums.herokuapp.com/logs"
                mailTo = "uihyunkei@gmail.com"
        )
public class Application extends MultiDexApplication {

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    // png is lossless so quality is ignored but must be provided
    private static int DISK_IMAGECACHE_QUALITY = 100;
    public final String TAG = "Application";
    Gson gson;

    private RequestQueue mRequestQueue;

    // not used
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);

        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;

            if (!topActivity.getPackageName().equals(context.getPackageName()))
                return true;
        }
        return false;
    }

    public static void setBadge(Context context, int count) {
        int badgeCount = count < 0 ? 0 : count;
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        intent.putExtra("badge_count", badgeCount);
        // 메인 메뉴에 나타나는 어플의  패키지 명
        intent.putExtra("badge_count_package_name", "com.uihyun.medic");
        // 메인 메뉴에 나타나는 어플의 클래스 명
        intent.putExtra("badge_count_class_name", "com.uihyun.medic.activity.SplashActivity");
        context.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration.url = getString(R.string.server_url);
        Configuration.isDEBUG = this.getString(R.string.isDebug).equals("1");
        Configuration.isDEBUGREPORT = this.getString(R.string.isDEBUGREPORT).equals("1");
        Configuration.APPNAME = getString(R.string.app_name);

        Logger.initialize(this, Configuration.isDEBUG, Configuration.isDEBUGREPORT);

        // this code is for com.uihyun.lib
        //  Remove for release app ??
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()
//                .build();
        // Initialize ImageLoader with configuration.
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        RequestManager.init(this);

        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGECACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);


        // facebook
        Permission[] permissions = new Permission[]{
                Permission.PUBLIC_PROFILE,
                Permission.EMAIL,
                Permission.USER_BIRTHDAY,
                Permission.USER_FRIENDS,
                Permission.PUBLISH_ACTION};

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getString(R.string.fb_app_id))
                .setNamespace(Configuration.APPNAME)
                .setPermissions(permissions)
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .setAskForAllPermissionsAtOnce(false)
                .build();

        SimpleFacebook.setConfiguration(configuration);

        // kakao
//        Session.initialize(this);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return ImageCacheManager.getInstance().getImageLoader();
    }

    public Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class, new gsonUTCdateAdapter());
//            gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            gson = gsonBuilder.create();
        }

        return gson;
    }

    private class gsonUTCdateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private final DateFormat dateFormat;

        public gsonUTCdateAdapter() {
            // This is the format I need
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
            //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
            dateFormat.setTimeZone(TimeZone.getTimeZone("KST"));
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(dateFormat.format(date));
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            try {
                return dateFormat.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                throw new JsonParseException(e);
            }
        }
    }
}

