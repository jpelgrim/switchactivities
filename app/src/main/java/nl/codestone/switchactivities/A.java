package nl.codestone.switchactivities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class A extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        Button toB = (Button) findViewById(R.id.to_b);
        toB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(A.this, B.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        Button toBWithPendingIntentExtra = (Button) findViewById(R.id.to_b_with_pending_intent_extra);
        toBWithPendingIntentExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent switchBackToMeIntent = getIntent();
                switchBackToMeIntent.setAction(Long.toString(System.currentTimeMillis()));
                switchBackToMeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                PendingIntent pendingIntent = PendingIntent.getActivity(A.this, 0, switchBackToMeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intent = new Intent(A.this, B.class);
                intent.putExtra(B.INTENT_EXTRA_PENDINGINTENT, pendingIntent);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

    }

}