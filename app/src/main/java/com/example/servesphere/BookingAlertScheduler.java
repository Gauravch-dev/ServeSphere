package com.example.servesphere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.servesphere.room.AppDatabase;
import com.example.servesphere.room.Booking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAlertScheduler {

    // Schedules smart alerts for any booking
    public static void scheduleAlert(Context context, String serviceType, String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date bookingDate = sdf.parse(dateString);
            if (bookingDate == null) return;

            long now = System.currentTimeMillis();
            long diff = bookingDate.getTime() - now;

            long triggerTimeMillis;

            if (diff > 24 * 60 * 60 * 1000L) {
                // More than 24 hours away → alert 1 day before
                triggerTimeMillis = bookingDate.getTime() - 24 * 60 * 60 * 1000L;
            } else if (diff > 60 * 60 * 1000L) {
                // Between 1 hour and 24 hours → alert 1 hour before
                triggerTimeMillis = bookingDate.getTime() - 60 * 60 * 1000L;
            } else {
                // Less than 1 hour → immediate alert
                triggerTimeMillis = now + 10_000L; // in 10 seconds
            }

            Intent intent = new Intent(context, BookingAlertReceiver.class);
            intent.putExtra("serviceType", serviceType);
            intent.putExtra("date", dateString);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Check and fire alerts immediately for bookings within 24 hours
    public static void checkAndNotifyUpcoming(Context context) {
        new Thread(() -> {
            try {
                List<Booking> bookings = AppDatabase.getInstance(context).bookingDao().getAllBookings();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date now = new Date();

                for (Booking booking : bookings) {
                    Date bookingDate = sdf.parse(booking.getDate());
                    if (bookingDate == null) continue;

                    long diff = bookingDate.getTime() - now.getTime();
                    long hours = diff / (1000 * 60 * 60);

                    if (hours <= 24 && hours >= 0) {
                        Intent alertIntent = new Intent(context, BookingAlertReceiver.class);
                        alertIntent.putExtra("serviceType", booking.getServiceType());
                        alertIntent.putExtra("date", booking.getDate());
                        context.sendBroadcast(alertIntent);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
