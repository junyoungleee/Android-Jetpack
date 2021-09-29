package com.june.archpractice.view.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.june.archpractice.R
import com.june.archpractice.model.entity.Contact

class NewContactActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        nameEditText = findViewById(R.id.new_name_edittext)
        numberEditText = findViewById(R.id.new_number_edittext)

        saveButton = findViewById(R.id.new_save_button)
        saveButton.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(numberEditText.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val name = nameEditText.text.toString()
                val number = numberEditText.text.toString()

                replyIntent.putExtra("name", name)
                replyIntent.putExtra("number", number)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

}


