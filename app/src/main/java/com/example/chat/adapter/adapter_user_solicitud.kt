package com.example.chat.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chat.clases.user
import com.example.chat.notifications
import com.example.chat.perfilUsuario
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_user.view.*


class adapter_user_solicitud(
    private val mcontext: Context,
    private val listaUsuarios: List<user>,
    private val idUserLogeado: String,
    private val ventana: String


) :

    ArrayAdapter<user>(mcontext, 0, listaUsuarios) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(context)
            .inflate(com.example.chat.R.layout.user_solicitud, parent, false)


        if (ventana == "notificaciones") {
            val user = listaUsuarios[position]
            println("user desde snap.. " + user.Nombre)
            layout.userNameView.text = user.Nombre
            Glide.with(mcontext).load(user.urlFoto).circleCrop().into(layout.userImage)
            layout.btnAgregar.text = "Aceptar"
            layout.btnAgregar.setOnClickListener {
                agregarUsuarioAmigoUsuarioReceptor(user)

            }

            layout.userNameView.setOnClickListener {
                val intent = Intent(mcontext, perfilUsuario::class.java)
                ContextCompat.startActivity(mcontext, intent, Bundle())
            }


        }

        if (ventana=="amigos"){
            val user = listaUsuarios[position]
            println("user desde snap.. " + user.Nombre)
            layout.userNameView.text = user.Nombre
            Glide.with(mcontext).load(user.urlFoto).circleCrop().into(layout.userImage)


        }
        return layout
    }


    private fun agregarUsuarioAmigoUsuarioReceptor(user: user) {
        var data: FirebaseDatabase = FirebaseDatabase.getInstance()
        var ref = data.getReference()


        ref.child("Users").child(idUserLogeado).child("amigos").child(user.idUser).setValue(true)
            .addOnCompleteListener {
                agregarUsuarioAmigoUsuarioEmisor(user)

            }

    }

    private fun agregarUsuarioAmigoUsuarioEmisor(user: user) {
        var data: FirebaseDatabase = FirebaseDatabase.getInstance()
        var ref = data.getReference()

        ref.child("Users").child(user.idUser).child("amigos").child(idUserLogeado).setValue(true)
            .addOnCompleteListener {
                elimainarSolicitud(user)

            }
    }

    fun elimainarSolicitud(user: user) {
        var data: FirebaseDatabase = FirebaseDatabase.getInstance()
        var ref = data.getReference()

        ref.child("Users").child(idUserLogeado).child("solicitudes").child(user.idUser)
            .removeValue().addOnCompleteListener {
                Toast.makeText(mcontext, "Ahora son amigos", Toast.LENGTH_LONG).show()
                /* val intent = Intent(mcontext,notifications::class.java).apply {
                     putExtra("nombre",name)
                     putExtra("email",email)
                     putExtra("idUser",idUserLogeado)
                     putExtra("urlImage",urlImage)
                 }
                 ContextCompat.startActivity(mcontext,intent,Bundle())
     */
            }


    }


}
