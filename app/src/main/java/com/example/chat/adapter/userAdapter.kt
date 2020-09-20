package com.example.chat.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chat.Home
import com.example.chat.clases.user
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_user.view.*

class userAdapter(private val mcontext:Context,private val listaUsuarios:List<user>,private val idUsuarioLogeado:String):
    ArrayAdapter<user>(mcontext,0,listaUsuarios){



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layout = LayoutInflater.from(context).inflate(com.example.chat.R.layout.item_user,parent,false)
        val user = listaUsuarios[position]
        layout.userNameView.text = user.Nombre
        Glide.with(mcontext).load(user.urlFoto).circleCrop().into(layout.userImage)

        layout.btnAgregar.setOnClickListener {
         var data:FirebaseDatabase = FirebaseDatabase.getInstance()
         var ref = data.getReference()


             ref.child("Users").child(user.idUser).child("solicitudes").child(idUsuarioLogeado).setValue(true).addOnCompleteListener {
                  println("Solicitud enviada")
                 //Toast.makeText(mcontext,"Solicitud enviada", Toast.LENGTH_LONG).show()
                 layout.btnAgregar.setEnabled(false)
                 layout.solicitudEnviadaTxt.text ="Solicitud Enviada"



              }
        }

        return layout
        
    }
}

