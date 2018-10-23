package noahzu.io.library.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.xingin.capa.memory.ui.FloatMemoryInfoView;

import noahzu.io.library.R;
import noahzu.io.library.gather.Sampler;

public class FloatingWindowService extends Service {

    public static final String OPERATION = "operation";
    public static final int OPERATION_SHOW = 100;
    public static final int OPERATION_HIDE = 101;

    private static final int HANDLE_SHOW_WINDOW = 200;
    private static final int HANDLE_HIDE_WINDOW = 201;
    private static final int HANDLE_UPDATE_VALUE = 202;

    private boolean isAdded = false;
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams params;
    private FloatMemoryInfoView floatMemoryInfoView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_SHOW_WINDOW:
                    if (!isAdded) {
                        windowManager.addView(floatMemoryInfoView, params);
                        isAdded = true;
                    }
                    break;
                case HANDLE_HIDE_WINDOW:
                    if (isAdded) {
                        windowManager.removeView(floatMemoryInfoView);
                        isAdded = false;
                    }
                    break;
                case HANDLE_UPDATE_VALUE:
                    floatMemoryInfoView.update(msg.getData().getDouble("mem"));
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initView();
        setListener();
        initData();
    }

    private void initData() {

        Sampler.getInstance().start(new Sampler.Callback() {
            @Override
            public void onGet(double cpu, double mem) {
                //on UI thread
                System.out.println("======update:"+mem);
                Message msg = mHandler.obtainMessage();
                msg.what = HANDLE_UPDATE_VALUE;
                Bundle data = msg.getData();
                data.putDouble("cpu",cpu);
                data.putDouble("mem",mem);
                msg.setData(data);
                mHandler.sendMessage(msg);
            }
        });
    }


    private void setListener() {
        floatMemoryInfoView.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        windowManager.updateViewLayout(floatMemoryInfoView, params);
                        break;
                }
                return true;
            }
        });

        ImageButton button  = floatMemoryInfoView.findViewById(R.id.closeMe);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sampler.getInstance().stop();
                mHandler.sendEmptyMessage(HANDLE_HIDE_WINDOW);
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.sendEmptyMessage(HANDLE_HIDE_WINDOW);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int operation = intent.getIntExtra(OPERATION, OPERATION_SHOW);
        switch (operation) {
            case OPERATION_SHOW:
                mHandler.sendEmptyMessage(HANDLE_SHOW_WINDOW);
                break;
            case OPERATION_HIDE:
                mHandler.sendEmptyMessage(HANDLE_HIDE_WINDOW);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     */
    private void initView() {
        floatMemoryInfoView = new FloatMemoryInfoView(getApplicationContext());

        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;//8.0以上用这个
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        windowManager.addView(floatMemoryInfoView, params);
        isAdded = true;
    }

}
