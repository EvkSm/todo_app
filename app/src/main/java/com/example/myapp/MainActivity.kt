package com.example.myapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomAdapter
    private val todos: MutableList<TodoItem> = mutableListOf()
    private lateinit var binding:ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user_data: EditText = findViewById(R.id.user_data)
        val button: Button = findViewById(R.id.button)
        val list_view: ListView = findViewById(R.id.list_view)

        adapter = CustomAdapter(this, todos)
        list_view.adapter = adapter

        button.setOnClickListener {
            val textFromUser = user_data.text.toString().trim()
            if (textFromUser.isNotEmpty()) {
                todos.add(0, TodoItem(textFromUser, ""))
                adapter.notifyDataSetChanged()
                user_data.text.clear()
            } else {
                Toast.makeText(this, "Please, enter some text :)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showDatePickerDialog(position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.time_picker, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val builder = AlertDialog.Builder(this).setView(dialogView).setTitle("Set Date")
            .setPositiveButton("OK") { _, _ ->
                if (position >= 0 && position < todos.size) {
                    val year = datePicker.year
                    val month = datePicker.month
                    val dayOfMonth = datePicker.dayOfMonth
                    val hour = timePicker.hour
                    val minute = timePicker.minute
                    val formattedDate = "$dayOfMonth/${month + 1}/$year $hour:$minute"

                    todos[position].date = formattedDate
                    adapter.notifyDataSetChanged() // refresh the view of the list bc data has changed
                }
            }
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()
    }

}
data class TodoItem(var text: String, var date: String)
