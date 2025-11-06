package com.example.servesphere;

import android.content.Context;

/**
 * Placeholder for future alerts/notifications pipeline.
 * We'll wire scheduling and reminders here later (WorkManager/AlarmManager).
 */
public class AlertScheduler {
    private final Context context;

    public AlertScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleBookingReminder(String bookingId, long triggerAtMillis) {
        // TODO: Implement with WorkManager or AlarmManager
    }
}
