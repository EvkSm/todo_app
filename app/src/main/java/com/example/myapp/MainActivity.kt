package com.example.myapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
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

        val todos: MutableList<String> = mutableListOf()
        // design for list view
        val adapter = CustomAdapter(this, todos)
        list_view.adapter = adapter


        button.setOnClickListener {
            val textFromUser = user_data.text.toString().trim()
            if (textFromUser != "") {
                todos.add(0,textFromUser)
                adapter.notifyDataSetChanged() // refresh the view of the list bc data has changed
                user_data.text.clear()
            }
            else{
                Toast.makeText(this, "Please, enter some text :)",Toast.LENGTH_SHORT).show()
            }
        }
    }
}