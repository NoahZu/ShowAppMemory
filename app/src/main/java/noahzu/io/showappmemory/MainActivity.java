package noahzu.io.showappmemory;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import noahzu.io.library.permission.FloatPermissionManager;
import noahzu.io.library.service.FloatingWindowService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(MainActivity.this);
//                if (isPermission) {
////                    Intent show = new Intent(MainActivity.this, FloatingWindowService.class);
////                    show.putExtra(FloatingWindowService.OPERATION, FloatingWindowService.OPERATION_SHOW);
////                    startService(show);
////                }

                Intent show = new Intent(MainActivity.this, FloatingWindowService.class);
                show.putExtra(FloatingWindowService.OPERATION, FloatingWindowService.OPERATION_SHOW);
                startService(show);
            }
        });
    }
}
