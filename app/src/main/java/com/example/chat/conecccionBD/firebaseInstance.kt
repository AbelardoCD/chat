package com.example.chat.conecccionBD

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class firebaseInstance {
    lateinit var database:FirebaseDatabase
    lateinit var reference: DatabaseReference


    fun getConection(coleecion:String):DatabaseReference{
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(coleecion)

        return reference
    }

}