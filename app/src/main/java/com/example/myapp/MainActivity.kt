/* plans:
1. create an edit button
2. change the background photo
3. add an icon
*/
package com.example.myapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp.databinding.ActivityMainBinding
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomAdapter
    private val todos: MutableList<TodoItem> = mutableListOf()
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    companion object {
        const val CHANNEL_ID = "todo_notification_channel"
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("NoteData",Context.MODE_PRIVATE)
        binding.button.setOnClickListener{
            val note = binding.userData.text.toString()
            val sharedEdit = sharedPreferences.edit()
            sharedEdit.putString("note",note)
            sharedEdit.apply()
            Toast.makeText(this, "Note stored successsfully :) ", Toast.LENGTH_SHORT).show()
            binding.userData.text.clear()
        }

        checkExactAlarmPermission()
        createNotificationChannel()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = CustomAdapter(this, todos)
        binding.listView.adapter = adapter

        binding.button.setOnClickListener {
            val textFromUser = binding.userData.text.toString().trim()
            if (textFromUser.isNotEmpty()) {
                todos.add(0, TodoItem(textFromUser, ""))
                adapter.notifyDataSetChanged()
                binding.userData.text.clear()
            } else {
                Toast.makeText(this, "Please, enter some text :)", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(todoText: String, year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) {
        if (todoText.isEmpty()) {
            Toast.makeText(this, "Reminder text cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(applicationContext, NotificationReceiver::class.java).apply {
            putExtra("todoText", todoText)
        }

        //exception -- ?
        val pendingIntent = try {
            PendingIntent.getBroadcast(
                applicationContext,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (pendingIntent == null) {
            Toast.makeText(this, "Failed to create PendingIntent", Toast.LENGTH_SHORT).show()
            return
        }


        val calendar = Calendar.getInstance().apply {
            set(year, month, dayOfMonth, hour, minute, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Please select a future time!", Toast.LENGTH_SHORT).show()
            return
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            showAlert(calendar.timeInMillis, todoText)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showAlert(time: Long, todoText: String) {
        val date = Date(time)
        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
        val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage("\nTitle: $todoText \nAt: ${dateFormat.format(date)} ${timeFormat.format(date)}")
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Todo Notifications"
            val descriptionText = "Channel for todo reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
                    adapter.notifyDataSetChanged() // refresh the view of the list
                    scheduleNotification(todos[position].text, year, month, dayOfMonth, hour, minute)
                }
            }
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()
    }
}

data class TodoItem(var text: String, var date: String)
