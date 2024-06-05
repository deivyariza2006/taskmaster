package com.example.myapplication

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TaskActivity : AppCompatActivity() {
    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var addTaskButton: Button
    private var taskList: MutableList<Task> = mutableListOf()
    private var selectedCalendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        taskTitleEditText = findViewById(R.id.edit_task_title)
        taskDescriptionEditText = findViewById(R.id.edit_task_description)
        addTaskButton = findViewById(R.id.add_task)

        taskList = loadTasks()

        addTaskButton.setOnClickListener {
            val title = taskTitleEditText.text.toString()
            val description = taskDescriptionEditText.text.toString()
            if (title.isNotEmpty() && description.isNotEmpty()) {
                showDatePickerDialog(title, description)
            } else {
                Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show()
            }
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_task -> true
                else -> false
            }
        }
    }

    private fun showDatePickerDialog(title: String, description: String) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                showTimePickerDialog(title, description, year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(title: String, description: String, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(year, month, day, hourOfDay, minute, 0)
                selectedCalendar = calendar
                addTask(title, description)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        taskList.add(newTask)
        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
        taskTitleEditText.text.clear()
        taskDescriptionEditText.text.clear()
        saveTasks(taskList)
        selectedCalendar?.let {
            setAlarm(it, title)
        }
    }

    private fun setAlarm(calendar: Calendar, taskTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms()) {
            requestExactAlarmPermission()
        } else {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                putExtra("taskTitle", taskTitle)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Toast.makeText(this, "Alarm set for task: $taskTitle", Toast.LENGTH_SHORT).show()
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivityForResult(intent, REQUEST_CODE_EXACT_ALARM_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EXACT_ALARM_PERMISSION) {
            if (resultCode == RESULT_OK) {
                selectedCalendar?.let {
                    setAlarm(it, "Tu título de tarea")
                }
            } else {
                Toast.makeText(this, "Permiso de alarma exacta denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_EXACT_ALARM_PERMISSION = 1001
    }

    private fun saveTasks(taskList: MutableList<Task>) {
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(taskList)
        editor.putString("task_List", json)
        editor.apply()
    }

    private fun loadTasks(): MutableList<Task> {
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("task_List", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
