package com.june.archpractice.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.june.archpractice.model.entity.Contact
import com.june.archpractice.repository.ContactRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ContactViewModel(private val repository: ContactRepository): ViewModel() {

    val contacts: LiveData<List<Contact>> = repository.allContacts.asLiveData()

    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }
}

class ContactViewModelFactory(private val repository: ContactRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}