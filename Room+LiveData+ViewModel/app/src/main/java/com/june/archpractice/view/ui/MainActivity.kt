package com.june.archpractice.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.june.archpractice.ContactApplication
import com.june.archpractice.R
import com.june.archpractice.model.entity.Contact
import com.june.archpractice.view.adapter.ContactAdapter
import com.june.archpractice.viewmodel.ContactViewModel
import com.june.archpractice.viewmodel.ContactViewModelFactory

class MainActivity : AppCompatActivity() {

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((application as ContactApplication).repository)
    }

    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.contact_recyclerview)
        val adapter = ContactAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        contactViewModel.contacts.observe(this, Observer { contacts ->
            contacts.let { adapter.submitList(it) }
        })

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val name: String = it.data?.getStringExtra("name").toString()
                val number: String = it.data?.getStringExtra("number").toString()

                val contact = Contact(name, number)
                contactViewModel.insert(contact)
            } else {
                Toast.makeText(applicationContext, "empty not saved", Toast.LENGTH_SHORT).show()
            }
        }

        val addButton = findViewById<FloatingActionButton>(R.id.add_button)
        addButton.setOnClickListener {
            val intent = Intent(this@MainActivity, NewContactActivity::class.java)
            getResult.launch(intent)
        }
    }
}