package jp.obanet.gpsreminder;

import jp.obanet.gpsreminder.InputPlaceDialogFragment.InputPlaceDialogListener;
import jp.obanet.gpsreminder.R.id;
import jp.obanet.gpsreminder.ReplaceAlertDialogFragment.RepalaceAlertDialogLister;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends FragmentActivity
    implements InputPlaceDialogListener, RepalaceAlertDialogLister, OnMarkerClickListener{

    private CheckedPlace checkedPlace;
    private GoogleMap map;
    private LatLng tempLatLng;

    private static final double INITIAL_LAT = 38;
    private static final double INITIAL_LNG = 138;
    private static final float INITIAL_ZOOM = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LatLng position;
        checkedPlace = (CheckedPlace)getIntent().getSerializableExtra("checkedPlace");

        if(checkedPlace == null){
            //バラメータがない場合は新規登録画面
            setContentView(R.layout.activity_map);

            //Mapオブジェクト取得
            SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            map = fragment.getMap();

            //初期表示位置、初期ズーム設定（アプリケーションのデフォルト値）
            position = new LatLng(INITIAL_LAT, INITIAL_LNG);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, INITIAL_ZOOM));
        }else{
            //パラメータがある場合は登録情報表示、変更画面
            setContentView(R.layout.activity_map_edit);

            //Mapオブジェクト取得
            SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            map = fragment.getMap();

            //初期表示位置、初期ズーム設定（登録情報より）
            position = new LatLng(checkedPlace.getLat(), checkedPlace.getLng());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, checkedPlace.getZoom()));

            /*
             * 設定位置にマーカーを表示
             */
            MarkerOptions options = new MarkerOptions();
            options.position(position);//マーカー表示の緯度経度をセット
            options.draggable(false);//マーカーのドラッグ移動不可
            map.addMarker(options);//マーカーを追加
            map.setOnMarkerClickListener(this);//マーカーをタップしたとき呼び出す処理を指定

            //画面上部に登録情報のタイトル、詳細表示
            ((TextView)findViewById(id.placeNameViewLabel)).setText(checkedPlace.getName());
            ((TextView)findViewById(id.placeMemoViewLabel)).setText(checkedPlace.getMemo());
        }

        //現在地の情報を表示
        map.setMyLocationEnabled(true);
        // 設定の取得
        UiSettings settings = map.getUiSettings();
        //コンパスを表示
        settings.setCompassEnabled(true);
        //現在地に移動するボタンを表示
        settings.setMyLocationButtonEnabled(true);
        //ズームイン・アウトボタンを表示
        settings.setZoomControlsEnabled(true);
        //すべてのジェスチャーを有効
        settings.setAllGesturesEnabled(true);

        //地図上の任意の場所をタップしたときの処理を追加
        map.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                tempLatLng = point;
                if(GoogleMapActivity.this.checkedPlace == null){
                    //新規登録時は新しい登録情報の入力ダイアログ表示
                    InputPlaceDialogFragment dialog = new InputPlaceDialogFragment();
                    dialog.show(getSupportFragmentManager(), "newPlaceDialog");
                }else{
                    //変更時は確認ダイアログ表示
                    ReplaceAlertDialogFragment dialog = new ReplaceAlertDialogFragment();
                    dialog.show(getSupportFragmentManager(), "replaceAlertDialog");
                }
            }
        });
    }

    public void onDeleteButtonClick(View v){
        MessageDialogFragment messageDialog = new MessageDialogFragment();
        messageDialog
            .setMessage(getString(R.string.delete_confirm))
            .setPositiveButton(getString(R.string.YES), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("mode", MainActivity.DELETE_PLACE);
                    intent.putExtra("checkedPlace", checkedPlace);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            })
            .setNegativeButton(getString(R.string.NO), null);
        messageDialog.show(getSupportFragmentManager(), "deleteConfirm");
    }

    @Override
    public void onInputPlaceDialogPositiveClick(Dialog dialog) {
        //情報取得
        String name = ((EditText)dialog.findViewById(id.placeName)).getText().toString();
        String memo = ((EditText)dialog.findViewById(id.placeMemo)).getText().toString();
        int distance = ((SeekBar)dialog.findViewById(id.distance)).getProgress() * InputPlaceDialogFragment.SEEK_BAR_RATIO + InputPlaceDialogFragment.SEEK_BAR_ADDITIONAL;

        Intent intent = new Intent();

        if(checkedPlace != null){
            if(tempLatLng != null){
                //場所変更の場合
                intent.putExtra("mode", MainActivity.EDIT_PLACE);
                checkedPlace = new CheckedPlace(checkedPlace.getId(), name, tempLatLng.latitude, tempLatLng.longitude, distance, map.getCameraPosition().zoom, memo, 0);
            }else{
                //既存の場所の付属情報のみ変更の場合
                intent.putExtra("mode", MainActivity.EDIT_PLACE);
                checkedPlace = new CheckedPlace(checkedPlace.getId(), name, checkedPlace.getLat(), checkedPlace.getLng(), distance, map.getCameraPosition().zoom, memo, checkedPlace.getNotified());
            }
        }else{
            //新規作成の場合
            intent.putExtra("mode", MainActivity.NEW_PLACE);
            checkedPlace = new CheckedPlace(name, tempLatLng.latitude, tempLatLng.longitude, distance, map.getCameraPosition().zoom, memo);
        }

        intent.putExtra("checkedPlace", checkedPlace);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onInputPlaceDialogNegativeClick(Dialog dialog) {
    }

    @Override
    public void onReplaceAlertDialogPositiveClick(Dialog dialog) {
        InputPlaceDialogFragment newPlaceDialog = new InputPlaceDialogFragment();
        newPlaceDialog.show(getSupportFragmentManager(), "newPlaceDialog");
    }

    @Override
    public void onReplaceAlertDialogNegativeClick(Dialog dialog) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        InputPlaceDialogFragment inputPlaceDialog = new InputPlaceDialogFragment();
        inputPlaceDialog.setCheckedPlace(checkedPlace);
        inputPlaceDialog.show(getSupportFragmentManager(), "EditPlaceDialog");

        return false;
    }
}
