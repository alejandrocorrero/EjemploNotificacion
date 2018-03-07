package com.alejandrocorrero.ejemplonotificacion;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Observable;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private NotificationManager mGestor;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGestor =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Se crea el constructor de notificaciones.
        mBuilder = new NotificationCompat.Builder(this, "test")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("test")
                .setContentText("texto")
                .setTicker("ticker")
                // accion al pulsar la notificacion
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this,
                        MainActivity.class), PendingIntent.FLAG_ONE_SHOT))

                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

       /* NotificationCompat.BigPictureStyle estyle = new NotificationCompat.BigPictureStyle()
                .setBigContentTitle("Contenido grande")
                .setSummaryText("Texto summary");
        mBuilder.setStyle(estyle);*/

        mBuilder.setProgress(100, 0, false);
        assert mGestor != null;
        mGestor.notify(1, mBuilder.build());


        io.reactivex.Observable<Integer> result = io.reactivex.Observable.create(emitter -> {
            for (int a = 1; a != 100; a++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                emitter.onNext(a);
            }
            emitter.onComplete();
        });
         result.subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        test(integer);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mBuilder.setContentText("Final").
                                setProgress(0, 0, false).setAutoCancel(true);
                        mGestor.notify(1, mBuilder.build());
                        dispose();
                    }
                });

    }


    private void test(Integer t) {
        mBuilder.setProgress(100, t, false);
        mGestor.notify(1, mBuilder.build());
    }


}
