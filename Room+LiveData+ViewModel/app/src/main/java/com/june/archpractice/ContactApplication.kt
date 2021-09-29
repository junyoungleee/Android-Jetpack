package com.june.archpractice

import android.app.Application
import com.june.archpractice.model.ContactDatabase
import com.june.archpractice.repository.ContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ContactApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ContactDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ContactRepository(database!!.contactDao()) }

}