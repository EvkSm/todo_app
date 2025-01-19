package com.example.myapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class CustomAdapter(private val context: Context, private val items: MutableList<TodoItem>) :
    ArrayAdapter<TodoItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        listItemView?.apply {
            val currentItem = getItem(position)

            val itemTextView = findViewById<TextView>(R.id.itemTextView)
            val dateTextView = findViewById<TextView>(R.id.dateTextView)

            itemTextView.text = currentItem?.text ?: ""
            dateTextView.text = currentItem?.date ?: ""

            val deleteButton = findViewById<Button>(R.id.deleteButton)
            deleteButton.setOnClickListener {
                if (position >= 0 && position < items.size) {
                    (context as? MainActivity)?.deleteTask(position)
                } else {
                    Toast.makeText(context, "Error: Unable to delete task :(", Toast.LENGTH_SHORT).show()
                }
            }

            val setTimeButton = findViewById<Button>(R.id.setTimeButton)
            setTimeButton.setOnClickListener {
                (context as? MainActivity)?.showDatePickerDialog(position)
            }
        }

        return listItemView!!
    }
}