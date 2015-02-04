package com.example.leon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.example.leon.cpuusage.CpuUtil;
import com.example.leon.dao.MyDataBaseHelper;
import com.example.leon.util.LogUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongRunningService extends Service {

    public static Map<String, Map<String, String>> sTopOfOneMin = new HashMap<>();
    public static Map<String, Map<String, String>> sTopOfFiveMin = new HashMap<>();
    public static Map<String, Map<String, String>> sTopOfFifteenMin = new HashMap<>();

    private static MyDataBaseHelper dbHelper;

    public LongRunningService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new MyDataBaseHelper(this, "Process.db", null, 1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /**
         * test of screen recorder
         */
        new Thread(new Runnable(){
            @Override
            public void run(){
                CpuUtil.runCmd(CpuUtil.SCREEN_RECORD);
            }

        }).run();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //这个data作为入库的时间，也是查询的时间依据
                Date date = new Date();

                String result = CpuUtil.runCmd(CpuUtil.TOP);
                String[] results = result.split("\n");

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                /**
                 * start with 0
                 * 3, 4, 6 to 16 is useful
                 */
                for (int i = 0; i < results.length; i++) {
                    //6之后是具体的进程信息
                    if (i >= 7) {

                        String[] processInfo = results[i].trim().split("\\s+");
                        LogUtil.d("the processInfo to add is : " + results[i]);
                        //取出的数据本身已经是根据cpu usage排序的了，如果为0则之后也会为0，直接舍弃即可

                        for (int k = 0; k < processInfo.length; k++) {
                            LogUtil.d("processInfo["+k+"] is :" + processInfo[k]);
                        }

                        LogUtil.d("2 is " + processInfo[2]);
                        processInfo[2] = processInfo[2].substring(0, processInfo[2].length() - 1);
                        LogUtil.d("change 2 to : " + Integer.parseInt(processInfo[2]));

                        LogUtil.d("5 is " + processInfo[5]);
                        processInfo[5] = processInfo[5].substring(0, processInfo[5].length() - 1);
                        LogUtil.d("change 5 to : " + Integer.parseInt(processInfo[5]));

                        LogUtil.d("6 is " + processInfo[6]);
                        processInfo[6] = processInfo[6].substring(0, processInfo[6].length() - 1);
                        LogUtil.d("change 6 to : " + Integer.parseInt(processInfo[6]));

                        LogUtil.d("the cpu usage to judge is : " + Integer.parseInt(processInfo[2]));
                        if (Integer.parseInt(processInfo[2]) == 0) {
                            break;
                        }


                        ContentValues values = new ContentValues();
                        if (processInfo.length == 9) {
                            for (int j = 0; j < 9; j++) {
                                values.put(MyDataBaseHelper.TABLE_PROCESS_COLUMN[j+1], processInfo[j]);
                            }
                        } else if (processInfo.length == 10) {
                            for (int j = 0; j < 7; j++) {
                                values.put(MyDataBaseHelper.TABLE_PROCESS_COLUMN[j+1], processInfo[j]);
                            }
                            values.put(MyDataBaseHelper.TABLE_PROCESS_COLUMN[8], processInfo[8]);//uid
                            values.put(MyDataBaseHelper.TABLE_PROCESS_COLUMN[9], processInfo[9]);//name
                        }
                        values.put(MyDataBaseHelper.TABLE_PROCESS_COLUMN[10], date.getTime());


                        db.beginTransaction();
                        db.insert("Process", null, values);
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    }
                }

                LogUtil.d("end insert");

                if (db.isOpen()) {
                    LogUtil.d("db is still open");
                }

                //update top processes of one minutes
                Cursor cursor = db.rawQuery("select name, cpu, update_time from Process order by update_time desc, cpu desc;", null);
//                Cursor cursor = db.rawQuery("select * from Process", null);
                if (cursor.moveToFirst()) {
                    LogUtil.d("in Cursor");
                    int rank = 1;
                    do {
                        if (Long.parseLong(cursor.getString(2)) == date.getTime()) {
                            String name = cursor.getString(0);
                            int cpu = cursor.getInt(1);
                            String updateTime = cursor.getString(2);

                            Map<String, String> process = new HashMap<String, String>();
                            process.put("name", name);
                            process.put("cpu", cpu + "");
                            process.put("updateTime", updateTime);

                            sTopOfOneMin.put(rank + "", process);
                            rank++;

                            LogUtil.d("name|cpu|update_time is : " + name + "||" + cpu + "||" + updateTime);
                        }
                    } while (cursor.moveToNext());
                    cursor.close();
                }

                Looper.prepare();
                LogUtil.d("out of cursor");
                Message msg = new Message();
                msg.what = MainActivity.UPDATE_TEXT;
                MainActivity.sHandler.sendMessage(msg);

            }
        }).start();

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        int oneMinute = 60 * 1000; //一分钟的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + oneMinute;
        Intent intentOfAlarmReceiver = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentOfAlarmReceiver, 0);

        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }
}
