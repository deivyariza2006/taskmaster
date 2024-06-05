package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val serviceIntent = Intent(context, NotificationService::class.java)
        serviceIntent.putExtra("taskTitle", intent?.getStringExtra("taskTitle"))
        context.startService(serviceIntent)
    }
}
