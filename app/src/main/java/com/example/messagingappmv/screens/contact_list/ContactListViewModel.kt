package com.example.messagingappmv.screens.contact_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messagingappmv.database.UserContactDatabaseDao
import com.example.messagingappmv.database.UserContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.os.AsyncTask
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.responsebodies.ContactListItem
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage


/**
 * ViewModel for SleepTrackerFragment.
 */
class ContactListViewModel(
    dataSource: UserContactDatabaseDao,
    application: Application
) : ViewModel() {

    private val context = application.applicationContext
    /**
     * Hold a reference to SleepDatabase via SleepDatabaseDao.
     */
    val database = dataSource

    /** Coroutine variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var newUserContact = MutableLiveData<UserContact?>()

    val userContactList = database.getAllUserContact()

    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean?>()

    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackbar()`.
     */
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent


    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the user rotates their phone it won't show a duplicate
     * toast.
     */
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }


    /**
     * Navigation for the Chat fragment.
     */
    private val _navigateToChat = MutableLiveData<Long>()
    val navigateToChat
        get() = _navigateToChat

    fun onUserContactClicked(id: Long) {
        _navigateToChat.value = id
    }

    fun onChatNavigated() {
        _navigateToChat.value = null
    }

    init {
        initializeUserContact()
    }

    private fun initializeUserContact() {
        uiScope.launch {
            newUserContact.value = getUserContactFromDatabase()
        }
    }

    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.j
     *
     *  If the start time and end time are not the same, then we do not have an unfinished
     *  recording.
     */
    private suspend fun getUserContactFromDatabase(): UserContact? {
        val userList = mutableListOf<UserContact>()

        Log.d("Uid Login user", TokenStorage.load(context).uid)

        CavojskyWebService.listContacts(context) { contacts ->
            Log.d("ContactListItem", contacts.toString())
            for (item: ContactListItem in contacts) {
                var tmpUserContact = UserContact()
                tmpUserContact = UserContact(item.id.toLong(), item.name)
                userList.add(tmpUserContact)
            }
            AsyncTask.execute { database.insertAll(userList) }

            Log.d("ContactListItem", userList.toString())
        }



        return withContext(Dispatchers.IO) {

            var userContact = database.getUserContact()
            if (userContact?.user_name == userContact?.user_name) {
                userContact = null
            }
            userContact
        }
    }

    private suspend fun insert(userContact: UserContact) {
        withContext(Dispatchers.IO) {
            database.insert(userContact)
        }
    }

    private suspend fun update(userContact: UserContact) {
        withContext(Dispatchers.IO) {
            database.update(userContact)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    /**
     * Executes when the START button is clicked.
     */
    fun onStart() {
        uiScope.launch {
            // Create a new night, which captures the current time,
            // and insert it into the database.
            val newNight = UserContact()
            newNight.user_name = "test"

            insert(newNight)

            newUserContact.value = getUserContactFromDatabase()
        }
    }

    fun onSend(message: String) {
        uiScope.launch {
            val newNight = UserContact()
            newNight.user_name = message
            Log.d("Message", message)
//            CavojskyWebService.sendMessageToContact()
//            insert(newNight)
            newUserContact.value = getUserContactFromDatabase()


        }
    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        uiScope.launch {
            // Clear the database table.
            clear()

            // And clear tonight since it's no longer in the database
            newUserContact.value = null

            // Show a snackbar message, because it's friendly.
            _showSnackbarEvent.value = true
        }
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}