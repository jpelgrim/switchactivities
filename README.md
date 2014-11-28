## Switch activities by PendingIntent

This is a small project to demonstrate switching between activities with the [`Intent.FLAG_ACTIVITY_REORDER_TO_FRONT`](http://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_REORDER_TO_FRONT) via a normal call to startActivity is working fine, but when I send an intent with that flag in a PendingIntent and then call `mPendingIntent.send()` the flag seems to be ignored :-(.

### Happy flow with startActivity

When you start this small app press the `START ACTIVITY A` button on the MainActivity. Then press the `NORMAL SWITCH -> B` button on activity A. The activity stack then looks like this:

      TaskRecord{217a0c5a #1150 A=nl.codestone.switchactivities U=0 sz=3}
        Run #9: ActivityRecord{12e8571b u0 nl.codestone.switchactivities/.B t1150}
        Run #8: ActivityRecord{36a1485d u0 nl.codestone.switchactivities/.A t1150}
        Run #7: ActivityRecord{1d53b477 u0 nl.codestone.switchactivities/.MainActivity t1150}

If I then press `SWITCH TO ACTIVITY A` the following code will be executed:

                Intent intent = new Intent(B.this, A.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

And the activity stack looks like this:

      TaskRecord{217a0c5a #1150 A=nl.codestone.switchactivities U=0 sz=3}
        Run #9: ActivityRecord{36a1485d u0 nl.codestone.switchactivities/.A t1150}
        Run #8: ActivityRecord{12e8571b u0 nl.codestone.switchactivities/.B t1150}
        Run #7: ActivityRecord{1d53b477 u0 nl.codestone.switchactivities/.MainActivity t1150}

Exactly what I want. You can press the `NORMAL SWITCH -> A` and `NORMAL SWITCH -> B` buttons indefinitely and the stack will remain to exist of three activities, with either A on top or B on top.

### Not so happy flow with a PendingIntent `send()`

Okay, here's the catch. When you click the `SWITCH -> B WITH PENDING INTENT` button I create a pending intent like this and add it as an intent extra:

                Intent switchBackToMeIntent = getIntent();
                switchBackToMeIntent.setAction(Long.toString(System.currentTimeMillis()));
                switchBackToMeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                PendingIntent pendingIntent = PendingIntent.getActivity(A.this, 0, switchBackToMeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intent = new Intent(A.this, B.class);
                intent.putExtra(B.INTENT_EXTRA_PENDINGINTENT, pendingIntent);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

All's fine here, the stack still looks like this:

      TaskRecord{217a0c5a #1150 A=nl.codestone.switchactivities U=0 sz=3}
        Run #9: ActivityRecord{12e8571b u0 nl.codestone.switchactivities/.B t1150}
        Run #8: ActivityRecord{36a1485d u0 nl.codestone.switchactivities/.A t1150}
        Run #7: ActivityRecord{1d53b477 u0 nl.codestone.switchactivities/.MainActivity t1150}

But now, when you click on the `SWITCH -> A BY PENDINGINTENT.SEND()` button in activity B I use the PendingIntent extra like this:

                if (mPendingIntent != null) {
                    try {
                        mPendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        Log.e("B", "Pending intent was cancelled", e);
                    }
                }

Then the mess starts... It looks like the `Intent.FLAG_ACTIVITY_REORDER_TO_FRONT` flag is ignored and I get this stack:

      TaskRecord{217a0c5a #1150 A=nl.codestone.switchactivities U=0 sz=4}
        Run #10: ActivityRecord{29351389 u0 nl.codestone.switchactivities/.A t1150}
        Run #9: ActivityRecord{339095db u0 nl.codestone.switchactivities/.B t1150}
        Run #8: ActivityRecord{3fe1e11d u0 nl.codestone.switchactivities/.A t1150}
        Run #7: ActivityRecord{1d53b477 u0 nl.codestone.switchactivities/.MainActivity t1150}

When I again press the `SWITCH -> B WITH PENDING INTENT` button in activity A and then the `SWITCH -> A BY PENDINGINTENT.SEND()` button in activity B the stack starts to grow like so:

      TaskRecord{217a0c5a #1150 A=nl.codestone.switchactivities U=0 sz=5}
        Run #11: ActivityRecord{31b517ed u0 nl.codestone.switchactivities/.A t1150}
        Run #10: ActivityRecord{339095db u0 nl.codestone.switchactivities/.B t1150}
        Run #9: ActivityRecord{29351389 u0 nl.codestone.switchactivities/.A t1150}
        Run #8: ActivityRecord{3fe1e11d u0 nl.codestone.switchactivities/.A t1150}
        Run #7: ActivityRecord{1d53b477 u0 nl.codestone.switchactivities/.MainActivity t1150}

Meh... not what I want.

**P.S.:** I'm using `clear; adb shell dumpsys activity | grep -B 1 -A 0 "Run #"` to monitor the activity stack. The -B argument specifies the 'lines before' and -A the 'lines after' the matched lines to grep. And thus gives a nice single screen overview of running activities.

