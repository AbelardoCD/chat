package com.example.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_perfil_usuario.*

class perfilUsuario : AppCompatActivity() {


    /*variables firebase*/
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference: DatabaseReference = database.getReference()

    lateinit var idUsuarioSolicitud:String
    lateinit var idUsuarioLogeado:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        val datosUsuario = intent.extras
        val nombre = datosUsuario?.getString("nombre")
        val urlFoto = datosUsuario?.getString("url")
        idUsuarioSolicitud = datosUsuario?.getString("idUser").toString()
        idUsuarioLogeado = datosUsuario?.getString("idUsuarioLogeado").toString()


        txtNombreUser.text = nombre
        Glide.with(this).load(urlFoto).circleCrop().into(imgPerfil)
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users")
        aceptarSolicitud()
    }

    private fun aceptarSolicitud(){

        btnAceptar.setOnClickListener {
            agregarUsuarioAmigoUsuarioReceptor()
        }
    }



    private fun agregarUsuarioAmigoUsuarioReceptor() {
        var data: FirebaseDatabase = FirebaseDatabase.getInstance()
        var ref = data.getReference()


        ref.child("Users").child(idUsuarioLogeado).child("amigos").child(idUsuarioSolicitud).setValue(true)
            .addOnCompleteListener {
                agregarUsuarioAmigoUsuarioEmisor()

            }

    }

    private fun agregarUsuarioAmigoUsuarioEmisor() {
        var data: FirebaseDatabase = FirebaseDatabase.getInstance()
        var ref = data.getReference()

        ref.child("Users").child(idUsuarioSolicitud).child("amigos").child(idUsuarioLogeado).setValue(true)
            .addOnCompleteListener {
                eliminarRegistrodeSolicitudReceptor()

            }
    }
    private fun eliminarRegistrodeSolicitudReceptor() {
        var data: FirebaseDatabase = FirebaseDatabase.getInstance()
        var ref = data.getReference()

        ref.child("Users").child(idUsuarioLogeado).child("solicitudes").child(idUsuarioSolicitud)
            .removeValue().addOnCompleteListener {
                Toast.makeText(this, "Ahora son amigos", Toast.LENGTH_LONG).show()

            }

    }
}