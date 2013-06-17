package jp.obanet.gpsreminder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifyService extends Service implements LocationListener{

    private LocationManager locationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(locationManager == null){
            //GPS操作用オブジェクト取得
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
        //現在地情報を取得後、this.onLocationChangedメソッドを実行
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        //緯度経度を取得
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        //データベースに登録されている登録情報を全て取得
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        Map<Long, CheckedPlace> placeMap = dbHelper.getCheckedPlaceMap();
        List<CheckedPlace> checkedPlaceList = new LinkedList<CheckedPlace>(placeMap.values());

        //通知生成用オブジェクト取得
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //登録された場所情報ごとに距離を計測
        for(CheckedPlace place : checkedPlaceList){
            float[] result = new float[1];
            //登録情報と現在地の２点間の距離を計測
            Location.distanceBetween(lat, lng, place.getLat(), place.getLng(), result);

            if(result[0] < place.getDistance()){//指定距離以内の時
                if(place.isNotified()){
                    //すでに通知済みなので通知しない
                    continue;
                }

                //通知をタップしたときに移動する画面情報
                Intent intent = new Intent(this, GoogleMapActivity.class );
                intent.putExtra("checkedPlace", place);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, MainActivity.EDIT_PLACE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent) //通知をタップしたときの処理をセット
                    .setTicker("「" + place.getName() + "」の近くに来ています") //画面上部の通知メッセージ
                    .setSmallIcon(R.drawable.ic_launcher) //アイコン指定
                    .setContentTitle(place.getName()) //通知のタイトル
                    .setContentText(place.getMemo()) //通知の詳細
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS) //音、点灯指定
                    .setWhen(System.currentTimeMillis()) //すぐに通知
                    .build();

                notificationManager.notify((int)place.getId(), notification);

                //通知済みフラグをON
                place.setNotified(1);
                dbHelper.updateCheckedPlace(place);

            }else if(result[0] > place.getDistance() * 1.2){//十分に離れたことを判定するため1.2倍する
                //範囲外に離れたので通知済みフラグをOFF
                place.setNotified(0);
                dbHelper.updateCheckedPlace(place);
            }
        }

        locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
