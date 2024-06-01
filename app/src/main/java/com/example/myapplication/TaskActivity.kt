package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.SharedPreferences

class TaskActivity : AppCompatActivity() {
    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var addTaskButton: Button
    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        taskTitleEditText = findViewById(R.id.edit_task_title)
        taskDescriptionEditText = findViewById(R.id.edit_task_description)
        addTaskButton = findViewById(R.id.add_task)

        // Recuperar la lista de tareas guardadas
        taskList = loadTasks()

        addTaskButton.setOnClickListener {
            val title = taskTitleEditText.text.toString()
            val description = taskDescriptionEditText.text.toString()
            if (title.isNotEmpty() && description.isNotEmpty()) {
                val newTask = Task(title, description)
                taskList.add(newTask)
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                taskTitleEditText.text.clear()
                taskDescriptionEditText.text.clear()
                // Guardar la lista de tareas actualizada
                saveTasks(taskList)
                // Navegar a DashboardActivity
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
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

    private fun saveTasks(taskList: MutableList<Task>) {
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
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
