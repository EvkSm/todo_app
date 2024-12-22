package com.example.myapp
// displays the list of todos
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class CustomAdapter(context: Context, private val items: MutableList<String>) :
    ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // for each item in list
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val currentItem = getItem(position)

        val itemTextView = listItemView!!.findViewById<TextView>(R.id.itemTextView)
        itemTextView.text = currentItem

        val deleteButton = listItemView.findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            items.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context, "the note is deleted", Toast.LENGTH_SHORT).show()
        }

        return listItemView
    }
}