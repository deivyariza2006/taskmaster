package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val taskList = loadTasks()

        val taskCountTextView: TextView = findViewById(R.id.task_count)
        taskCountTextView.text = formatTaskCountText(taskList.size)

        // Configura el BottomNavigationView
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_task -> {
                    val intent = Intent(this, TaskActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        val newsIcon: ImageView = findViewById(R.id.news_icon)
        newsIcon.setOnClickListener {
            val url = "https://micampusresidencias.com/como-organizar-tu-tiempo-de-estudio-en-la-universidad/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
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

    private fun formatTaskCountText(taskCount: Int): CharSequence {
        val spannable = SpannableString("Tienes $taskCount tareas")
        val start = spannable.indexOf(taskCount.toString())
        val end = start + taskCount.toString().length
        spannable.setSpan(ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(RelativeSizeSpan(1.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}
