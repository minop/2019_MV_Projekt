package com.example.messagingappmv

import android.content.Context

internal interface DrawerMenuLocker {
    fun setDrawerLocked(shouldLock: Boolean, username: String, context: Context)
}