package com.june.archpractice.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.june.archpractice.R
import com.june.archpractice.model.entity.Contact
import org.w3c.dom.Text

class ContactAdapter : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById<TextView>(R.id.item_textview_name)
        private val numberTextView: TextView = itemView.findViewById(R.id.item_textview_number)
        private val initialTextView: TextView = itemView.findViewById(R.id.item_textview_initial)

        fun bind(contact: Contact) {
            nameTextView.text = contact.name
            numberTextView.text = contact.number
            initialTextView.text = contact.name[0].uppercase()
        }

        companion object {
            fun create(parent: ViewGroup): ContactViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false)
                return ContactViewHolder(view)
            }
        }
    }

    class ContactComparator : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.number == newItem.number
        }

    }

}