package com.example.chat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.ViewModelStore
import com.bumptech.glide.Glide
import com.example.chat.adapter.adapter_user_solicitud
import com.example.chat.adapter.userAdapter
import com.example.chat.clases.user
import com.example.chat.conecccionBD.firebaseInstance
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_notifications.*
import java.io.IOException

class notifications : AppCompatActivity() {
    /*Variables datos enviados entre ventanas*/

    lateinit var name: String
    lateinit var urlImage: String
    lateinit var idUser: String
    lateinit var email: String

    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference

    /*Variables lista*/
    lateinit var listView: ListView
    lateinit var lista: MutableList<user>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        accionBotonesModulos()

        getDatosEnviadosEntreVentana()



        listView = findViewById(R.id.listViewUsuariosSolicitudesN)
        lista = mutableListOf()








        getUsuariosSolicitudes(idUser)
        onClicItem()
    }

    fun getUsuariosSolicitudes(idUser: String) {


        val firebaseDb: firebaseInstance = firebaseInstance()
        val reference = firebaseDb.getConection("Users")


        var id: String = ""
        try {

            reference.child(idUser).child("solicitudes")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            lista.clear()
                            for (data in snapshot.children) {
                                id = data.key.toString()
                                println("buscar registro   " + id)
                                buscar(id)
                            }
                        }
                    }
                })

        } catch (e: IOException) {
            println(e.message)
        }
    }

    fun buscar(id: String) {
        val firebaseDb: firebaseInstance = firebaseInstance()
        val reference = firebaseDb.getConection("Users")
        lista.clear()
        reference.child(id).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                println(snapshot)

                    val solicitud = snapshot.getValue(user::class.java)
                    lista.add(solicitud!!)


                val adp = adapter_user_solicitud(
                    this@notifications,
                    lista,
                    idUser,"notificaciones"
                )
                listView.adapter = adp
                onClicItem()

            }

        })
    }

    fun onClicItem() {


        listView.setOnItemClickListener { adapterView, view, i, l ->
            println("clic" + lista[i].Nombre)
            val intent = Intent(this, perfilUsuario::class.java).apply {
                putExtra("idUser", lista[i].idUser)
                putExtra("nombre", lista[i].Nombre)
                putExtra("url", lista[i].urlFoto)
                putExtra("idUsuarioLogeado", idUser)
            }
            startActivity(intent)

        }


    }

    private fun getDatosEnviadosEntreVentana() {

        val datosEnviadosEntreVentanas = intent.extras
        name = datosEnviadosEntreVentanas?.getString("name").toString()
        urlImage = datosEnviadosEntreVentanas?.getString("urlImage").toString()
        idUser = datosEnviadosEntreVentanas?.getString("idUser").toString()
        email = datosEnviadosEntreVentanas?.getString("email").toString()

        userNameN.text = name
        Glide.with(this).load(urlImage).circleCrop().into(btnImgN)

    }


    private fun accionBotonesModulos() {



        btnModuloMensajesN.setOnClickListener {
            val intent = Intent(this, amigos::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
                putExtra("urlImage", urlImage)
                putExtra("idUser", idUser)
            }
            startActivity(intent)
            onBackPressed()
        }
        btnModuloHomeN.setOnClickListener {
            val intent = Intent(this, Home::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
                putExtra("urlImage", urlImage)
                putExtra("idUser", idUser)
            }
            startActivity(intent)
            onBackPressed()
        }


    }

    fun getUsuariosIndividual(id:String){
        val firebaseDb: firebaseInstance = firebaseInstance()
        val reference = firebaseDb.getConection("Users")

        var query = reference.orderByChild("idUser").equalTo(id)

        query.addValueEventListener(object :ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                //println("val..." + snapshot)
                if (snapshot.hasChildren()) {
                    println(snapshot)
                    for (dat in snapshot.children) {
                        val usuarioSolicitud = dat.getValue(user::class.java)
                        lista.add(usuarioSolicitud!!)


                        val adp = adapter_user_solicitud(this@notifications, lista, idUser,"notificaciones")
                        listView.adapter = adp
                        onClicItem()
                    }
                }else{
                    lista.clear()
                }
            }
        })
    }
}