package com.example.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chat.adapter.adapter_mensaje
import com.example.chat.clases.mensaje
import com.example.chat.clases.user
import com.example.chat.conecccionBD.firebaseInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mensajes.*
import kotlinx.android.synthetic.main.activity_notifications.*

class mensajes : AppCompatActivity() {

    lateinit var name: String
    lateinit var urlImage: String
    lateinit var idContacto: String
    lateinit var email: String
    lateinit var userLogado: String
    lateinit var nombreUsuarioLogeado:String

    lateinit var coneccion: firebaseInstance
    lateinit var referencia: DatabaseReference

    lateinit var listaMensajes:MutableList<mensaje>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajes)
        getDatosEnviadosEntreVentana()

        coneccion = firebaseInstance()
        referencia = coneccion.getConection("Mensajeria")
    listaMensajes = mutableListOf()
        enviarMensaje()
        getMensajes()
    }

private fun getMensajes(){

    referencia.child(userLogado).child(idContacto).addValueEventListener(object:ValueEventListener{
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                listaMensajes.clear()
                for (data in snapshot.children){
                    val mensaje = data.getValue(mensaje::class.java)
                    listaMensajes.add(mensaje!!)

                    val adp = adapter_mensaje(this@mensajes,listaMensajes,idContacto,name,nombreUsuarioLogeado)
                    listViewMensajes.adapter =adp
                }
            }
        }

    })
}
    private fun enviarMensaje() {
        btnEnviarMensaje.setOnClickListener {

            val nuevoMensaje = mensaje(txtMensaje.text.toString(), userLogado)
            val mensajeReceptor =   referencia.child(userLogado).child(idContacto)
            val mensajeEmisor =   referencia.child(idContacto).child(userLogado)

            mensajeReceptor.push().setValue(nuevoMensaje)
            mensajeEmisor.push().setValue(nuevoMensaje).addOnCompleteListener {

            }


        }
    }

    private fun getDatosEnviadosEntreVentana() {

        val datosEnviadosEntreVentanas = intent.extras
        name = datosEnviadosEntreVentanas?.getString("nombre").toString()
        urlImage = datosEnviadosEntreVentanas?.getString("url").toString()
        idContacto = datosEnviadosEntreVentanas?.getString("idUser").toString()
        userLogado = datosEnviadosEntreVentanas?.getString("idUsuarioLogeado").toString()
        nombreUsuarioLogeado = datosEnviadosEntreVentanas?.getString("nombreUsuarioLogeado").toString()

        textNombreContacto.text = name
        Glide.with(this).load(urlImage).circleCrop().into(imgContacto)

    }


}