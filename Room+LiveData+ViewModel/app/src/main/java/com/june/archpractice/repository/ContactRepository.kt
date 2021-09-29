package com.june.archpractice.repository

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.june.archpractice.model.ContactDatabase
import com.june.archpractice.model.dao.ContactDAO
import com.june.archpractice.model.entity.Contact
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

// DAO에 대한 액세스만 필요하기 때문에 DAO만 프로퍼티로 선언
class ContactRepository(private val contactDao: ContactDAO) {

    val allContacts: Flow<List<Contact>> = contactDao.getAll()

    // 기본적으로 Room은 main thread에서 suspend 쿼리를 실행함
    // 따라서 DB 작업을 main thread에서 오래 실행하지 않도록 하는 다른 구현 필요 없음
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(contact: Contact) {
        contactDao.insert(contact)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(contact: Contact) {
        contactDao.delete(contact)
    }
}