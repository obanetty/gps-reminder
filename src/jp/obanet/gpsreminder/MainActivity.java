package jp.obanet.gpsreminder;

import java.util.LinkedList;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

    private SQLiteHelper dbHelper;
    private Map<Long, CheckedPlace> placeMap;

    public static final int NONE = -1;
    public static final int NEW_PLACE = 0;
    public static final int EDIT_PLACE = 1;
    public static final int DELETE_PLACE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //画面設定
        setContentView(R.layout.activity_main);

        //保存している登録情報の読込
        dbHelper = new SQLiteHelper(this);
        placeMap = dbHelper.getCheckedPlaceMap();

        //場所情報のリスト表示
        updatePlaceList();

        /*
         * リストがタップされたときの処理を登録
         */
        ListView placeListView = (ListView) findViewById(R.id.listview);
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                // タップされたアイテムを取得します
                CheckedPlace checkedPlace = (CheckedPlace) listView.getItemAtPosition(position);

                //マップ画面に遷移（タップされたアイテムも引き渡す）
                Intent intent = new Intent( MainActivity.this, GoogleMapActivity.class );
                intent.putExtra("checkedPlace", checkedPlace);
                startActivityForResult( intent, EDIT_PLACE );
            }
        });

        /*
         * リストが長押しされた時の処理を登録
         */
        placeListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView)parent;
                // 長押しされたアイテムを取得します
                final CheckedPlace checkedPlace = (CheckedPlace) listView.getItemAtPosition(position);

                //削除するかどうかダイアログを開いて確認
                deleteCheckedPlaceWithDialog(checkedPlace);
                return false;
            }
        });
    }

    /**
     * 登録情報の表示を更新する
     */
    private void updatePlaceList(){
        ListView placeListView = (ListView) findViewById(R.id.listview);
        ArrayAdapter<CheckedPlace> placeListAdopter = new ArrayAdapter<CheckedPlace>(this, android.R.layout.simple_list_item_1, new LinkedList<CheckedPlace>(placeMap.values()));
        placeListView.setAdapter(placeListAdopter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == NEW_PLACE && resultCode == RESULT_OK){
            //マップ画面での入力情報取得
            CheckedPlace checkedPlace = (CheckedPlace)intent.getSerializableExtra("checkedPlace");

            //データベースへ登録
            Long id = dbHelper.insertCheckedPlace(checkedPlace);
            checkedPlace.setId(id);

            //画面のリスト更新
            placeMap.put(Long.valueOf(id), checkedPlace);
            updatePlaceList();

            //GPS定期チェック処理開始
            startSchedule();

        }else if(requestCode == EDIT_PLACE && resultCode == RESULT_OK){
            CheckedPlace checkedPlace = (CheckedPlace)intent.getSerializableExtra("checkedPlace");
            int mode = intent.getIntExtra("mode", NONE);
            if(mode == EDIT_PLACE){
                //更新の場合の処理
                updateCheckedPlace(checkedPlace);
            }else if(mode == DELETE_PLACE){
                //削除の場合の処理
                deleteCheckedPlace(checkedPlace);
            }
        }
    }

    public void updateCheckedPlace(CheckedPlace checkedPlace){
        dbHelper.updateCheckedPlace(checkedPlace);

        //画面更新
        placeMap.put(checkedPlace.getId(), checkedPlace);
        updatePlaceList();

        //GPS定期チェック処理開始
        startSchedule();
    }

    public void deleteCheckedPlaceWithDialog(final CheckedPlace checkedPlace){
        MessageDialogFragment messageDialog = new MessageDialogFragment();
        messageDialog
            .setMessage(getString(R.string.delete_confirm))
            .setPositiveButton(getString(R.string.YES), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteCheckedPlace(checkedPlace);
                }
            })
            .setNegativeButton(getString(R.string.NO), null);
        messageDialog.show(getSupportFragmentManager(), "deleteConfirm");
    }

    private void deleteCheckedPlace(CheckedPlace checkedPlace){
        dbHelper.deleteCheckedPlace(checkedPlace);
        placeMap.remove(checkedPlace.getId());
        updatePlaceList();

        if(placeMap.size() == 0){
            cancelSchedule();
        }
    }

    private void startSchedule(){
        AlarmManager alermManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = System.currentTimeMillis();//すぐに起動するため現在時間を取得

        //定期的に起動する処理の登録
        Intent intent = new Intent(this, NotifyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if(pendingIntent != null) {
            long delay = 30 * 60 * 1000; //30分間隔で定期的に処理を行う
            //定期処理開始
            alermManager.setRepeating(AlarmManager.RTC, time, delay, pendingIntent);
        }
    }

    private void cancelSchedule(){
        AlarmManager alermManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotifyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, -1, intent, PendingIntent.FLAG_NO_CREATE);
        alermManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onClick(View v){
        Intent intent = new Intent(this, GoogleMapActivity.class );
        startActivityForResult( intent, NEW_PLACE );
    }
}
