package com.example.chat

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast

class userPreferenceManager {


    private val mail = "UserEmail"
    private val password = "UserPassword"
    lateinit var mContext:Context
    lateinit var usuario:Array<String>

    constructor(mcontext:Context){
        this.mContext =mcontext
    }

    public fun guaradarCredenciales(email: String, Password: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(mContext)
        val edito =pref.edit()
        edito.putString(mail,email)
        edito.putString(password,Password)
        edito.apply()
        // Toast.makeText(mContext,"Guardados " + email + "   " + Password,Toast.LENGTH_LONG ).show()

    }

    public fun getEmail():String {

        val pref = PreferenceManager.getDefaultSharedPreferences(mContext)
        val email = pref.getString(mail,"")

        return email.toString()
    }
    public fun getPassword():String {

        val pref = PreferenceManager.getDefaultSharedPreferences(mContext)
        val password = pref.getString(password,"")

        return password.toString()
    }

    public fun deleteUser() {
        val pref = PreferenceManager.getDefaultSharedPreferences(mContext)
        val edito =pref.edit()
        edito.remove(mail)
        edito.remove(password)

        edito.apply()
        Toast.makeText(mContext,"Sesion Cerrada",Toast.LENGTH_LONG ).show()


    }

}