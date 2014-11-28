package nl.codestone.switchactivities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class B extends ActionBarActivity {

    public static final String INTENT_EXTRA_PENDINGINTENT = "pending_intent_extra";

    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);

        if (savedInstanceState == null) {
            if (getIntent().hasExtra(INTENT_EXTRA_PENDINGINTENT)) {
                mPendingIntent = getIntent().getParcelableExtra(INTENT_EXTRA_PENDINGINTENT);
            }
        } else {
            if (savedInstanceState.containsKey(INTENT_EXTRA_PENDINGINTENT)) {
                mPendingIntent = savedInstanceState.getParcelable(INTENT_EXTRA_PENDINGINTENT);
            }
        }

        Button toA = (Button) findViewById(R.id.to_a);
        toA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(B.this, A.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        Button toAByPendingIntent = (Button) findViewById(R.id.to_a_by_pending_intent);
        toAByPendingIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPendingIntent != null) {
                    try {
                        mPendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        Log.e("B", "Pending intent was cancelled", e);
                    }
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(INTENT_EXTRA_PENDINGINTENT)) {
            mPendingIntent = getIntent().getParcelableExtra(INTENT_EXTRA_PENDINGINTENT);
        }
        setIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPendingIntent != null) outState.putParcelable(INTENT_EXTRA_PENDINGINTENT, mPendingIntent);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(INTENT_EXTRA_PENDINGINTENT)) {
            mPendingIntent = savedInstanceState.getParcelable(INTENT_EXTRA_PENDINGINTENT);
        }
    }
}