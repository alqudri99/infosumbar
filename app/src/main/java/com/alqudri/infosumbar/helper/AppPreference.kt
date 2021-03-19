package com.alqudri.infosumbar.helper


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by communitydevpadang in 2020.
 */
class AppPreferences(context: Context) {
    private val mContext: Context
    private val mSharedPreferences: SharedPreferences

    var uid: String?
        get() {
            val key: String ="uid"
            return mSharedPreferences.getString(key, null)
        }
        set(token) {
            val editor = mSharedPreferences.edit()
            val key: String = "uid"
            editor.putString(key, token)
            editor.apply()
        }

    var nohp: String?
        get() {
            val key: String ="nohp"
            return mSharedPreferences.getString(key, null)
        }
        set(token) {
            val editor = mSharedPreferences.edit()
            val key: String = "nohp"
            editor.putString(key, token)
            editor.apply()
        }

    var nama: String?
        get() {
            val key: String ="nama"
            return mSharedPreferences.getString(key, null)
        }
        set(token) {
            val editor = mSharedPreferences.edit()
            val key: String = "nama"
            editor.putString(key, token)
            editor.apply()
        }

    var email: String?
        get() {
            val key: String ="email"
            return mSharedPreferences.getString(key, null)
        }
        set(token) {
            val editor = mSharedPreferences.edit()
            val key: String = "email"
            editor.putString(key, token)
            editor.apply()
        }

    var jobs: String?
        get() {
            val key: String ="jobs"
            return mSharedPreferences.getString(key, null)
        }
        set(token) {
            val editor = mSharedPreferences.edit()
            val key: String = "jobs"
            editor.putString(key, token)
            editor.apply()
        }

    var avatar: String?
        get() {
            val key: String ="avatar"
            return mSharedPreferences.getString(key, null)
        }
        set(token) {
            val editor = mSharedPreferences.edit()
            val key: String = "avatar"
            editor.putString(key, token)
            editor.apply()
        }

    init {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mContext = context
    }
}