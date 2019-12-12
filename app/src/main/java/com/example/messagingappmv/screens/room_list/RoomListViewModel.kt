package com.example.messagingappmv.screens.room_list

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messagingappmv.database.RoomContactDatabaseDao
import com.example.messagingappmv.database.RoomContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.room.util.StringUtil
import android.net.wifi.WifiInfo
import android.content.Context.WIFI_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.os.Build
import android.net.NetworkCapabilities
import android.net.Network
import android.os.AsyncTask
import androidx.core.content.ContextCompat.getSystemService
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RoomListItem
import java.time.LocalDateTime


/**
 * ViewModel for SleepTrackerFragment.
 */
class RoomListViewModel(
    dataSource: RoomContactDatabaseDao,
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

    private var newRoomContact = MutableLiveData<RoomContact?>()

    val roomContactList = database.getAllRoomContact()

    
    /**
     * Navigation for the Room fragment.
     */
    private val _navigateToRoom = MutableLiveData<String>()
    val navigateToRoom
        get() = _navigateToRoom

    fun onRoomContactClicked(ssid: String) {
        _navigateToRoom.value = ssid
    }

    fun onRoomNavigated() {
        _navigateToRoom.value = null
    }

    init {
        initializeRoomContact()
    }

    private fun initializeRoomContact() {
        uiScope.launch {
            if(TokenStorage.containsToken(context))
                newRoomContact.value = getRoomContactFromDatabase()
        }
    }

    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.j
     *
     *  If the start time and end time are not the same, then we do not have an unfinished
     *  recording.
     */
    private suspend fun getRoomContactFromDatabase(): RoomContact? {
        val userList = mutableListOf<RoomContact>()

        Log.d("Uid Login user", TokenStorage.load(context).uid)

        CavojskyWebService.listRooms(context, { rooms ->
            Log.d("RoomListItem", rooms.toString())
            for (item: RoomListItem in rooms) {
                var tmpRoomContaxt = RoomContact()
                tmpRoomContaxt = RoomContact(item.roomid)
                userList.add(tmpRoomContaxt)
            }
            AsyncTask.execute { database.insertAll(userList) }

            Log.d("RoomListItem", userList.toString())
        })

        return withContext(Dispatchers.IO) {

            var userContact = database.getRoomContact()
            if (userContact?.ssid == userContact?.ssid) {
                userContact = null
            }
            userContact
        }
    }

    private suspend fun insert(roomContact: RoomContact) {
        withContext(Dispatchers.IO) {
            database.insert(roomContact)
        }
    }

    private suspend fun get(ssid: String): RoomContact? {
        return withContext(Dispatchers.IO) {
            var roomContact = database.get(ssid)
            roomContact
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
            val newRoom = RoomContact()
            newRoom.ssid = "test"

            insert(newRoom)

            newRoomContact.value = getRoomContactFromDatabase()
        }
    }

    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo

                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI)
                }
            } else {
                val n = cm.activeNetwork

                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)

                    return nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            }
        }

        return false
    }

    fun getCurrentSsid(context: Context): String? {
        var ssid: String? = null
        if (isWifiConnected(context)) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = wifiManager.connectionInfo
            if (connectionInfo != null && !connectionInfo.ssid.isBlank()) {
                ssid = connectionInfo.ssid
            }
        }
        return ssid
    }
    fun getCurrentBssid(context: Context): String? {
        var bssid: String? = null
        if (isWifiConnected(context)) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = wifiManager.connectionInfo
            if (connectionInfo != null && !connectionInfo.bssid.isBlank()) {
                bssid = connectionInfo.bssid
            }
        }
        return bssid
    }

    //checks if the wifi the user is currently on (if he is on any) is in the database. if it's not, add it.
    fun attemptAddCurrentWifi(ssid: String, bssid: String, context: Context) {
        uiScope.launch {
            val localRoom = get(ssid)
            if(localRoom == null && TokenStorage.containsToken(context)) {
                //add the room to the database
                if(ssid == ""){
                    insert(RoomContact(bssid))
                }
                else{
                    insert(RoomContact(ssid))
                }
                newRoomContact.value = getRoomContactFromDatabase()
            }
//            if(localRoom == null) {
//                //v lokalnej databaze sa room nenachadza. ak sa nenachadza ani na webservice tak
//                CavojskyWebService.listRooms(context) {
//                    if(it.isNotEmpty()){
//                        val containsMyRoom = false
//                        for (roomItem: RoomListItem in it) {
//                            if((roomItem.roomid == ssid) || (roomItem.roomid == bssid)) {
//                                //ID roomu je bud ssid alebo bssid mojej siete (btw to ze to nemame ako rozoznat, ci ide o SSID alebo BSSID znamena, ze to nie je unique identifier)
//                                //
//                            }
//                            newRoomContact.value = getRoomContactFromDatabase()
//                        }
//                    }
//                }
//            }
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
            newRoomContact.value = null
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
    fun onSend(message: String) {
        uiScope.launch {
            val newRoom = RoomContact()
            newRoom.ssid = message
            insert(newRoom)
            newRoomContact.value = getRoomContactFromDatabase()
        }
    }
}