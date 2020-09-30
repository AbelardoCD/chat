package com.example.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.chat.adapter.userAdapter
import com.example.chat.clases.user
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btnImg

import kotlinx.android.synthetic.main.activity_home.progresBarImage
import kotlinx.android.synthetic.main.activity_home.txtNotifications
import kotlinx.android.synthetic.main.activity_home.userName
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.item_user.*
import kotlinx.android.synthetic.main.item_user.view.*
import java.io.IOException

class Home : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var idUsuario: String


    lateinit var filpath: Uri
    private var foto: Int = 1
    lateinit var firebaseStorage: StorageReference
    lateinit var storage: FirebaseStorage


    //variable que nos servira para guardar el url de la foto de perfil actual y en caso de actualizar la foto esta direccion
    // se usara para borrar la foto y posterior a eso subir la otra
    lateinit var urlImagDelete: String

    /*Variables para listar los usuarios*/
    lateinit var lista: MutableList<user>
    lateinit var view: ListView

    /****************VARIABLES ENVIAR MEDIANTE PUT EXTRA ENTRE VENTANAS**********************/
    lateinit var userEmail: String
    lateinit var userId: String
    lateinit var userUrlImage: String
    lateinit var userNameBD: String

    lateinit var arrayAmigos: MutableList<String>
    lateinit var arrayTodosUsuarios: MutableList<user>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        accionBotonesModulos()

        /*Variables firebase*/
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users")

        firebaseStorage = FirebaseStorage.getInstance().getReference()
        storage = Firebase.storage
        urlImagDelete = ""

        /****************************************/
        val valores = intent.extras
        var email = valores?.getString("email")
        userEmail = email.toString()
        idUsuario = ""

        getUserName(email.toString())
        getGaleriatelefono()
        /**********variables lista usuarios**********/
        lista = mutableListOf()
        view = findViewById(R.id.listViewUsuarios)

        arrayAmigos = mutableListOf()
        arrayTodosUsuarios = mutableListOf()


    }

    private fun getUserName(email: String) {
        println("desde getUser")
        val query: Query = reference.orderByChild("Email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                println("desde snap:  " + snapshot)
                for (dato in snapshot.children) {
                    val nombre = dato.getValue(user::class.java)
                    userName.text = nombre?.Nombre
                    userNameBD = nombre?.Nombre.toString()

                    idUsuario = nombre?.idUser.toString()
                    userId = nombre?.idUser.toString()
                    getSolicitudes(idUsuario)
                    getAmigos(idUsuario)

                    println("idUsuario : " + idUsuario)
                    urlImagDelete = nombre?.urlFoto!!
                    //validamos que la url no este basia
                    if (nombre?.urlFoto!!.isNotEmpty()) {
                        userUrlImage = nombre?.urlFoto.toString()
                        Glide.with(this@Home).load(nombre.urlFoto).circleCrop().into(btnImg)

                    }

                }
            }
        })
    }

    private fun getGaleriatelefono() {
        btnImg.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "selecciona una imagen"), foto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == foto && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filpath = data.data!!
            try {
                val bitmapImg: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filpath)
                btnImg.setImageBitmap(bitmapImg)
                guardarObra()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun guardarObra() {
        progresBarImage.visibility = View.VISIBLE
        println("Entramos a guardar foto")
        try {
            val refer = firebaseStorage.child("fotosUsuarios").child(filpath.lastPathSegment!!)
            val uploadTask = refer.putFile(filpath)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                refer.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println("la url ...." + downloadUri + "    " + idUsuario)
                    /*Si existe alguna foto de perfil la borramos y posteriormente asignamos la url nueva al usuario*/
                    println("urlimgborrar " + urlImagDelete)
                    if (urlImagDelete.isNotEmpty()) {
                        deleteImgFromStorage(urlImagDelete)

                    }
                    guardarUrlUsuarioCorrespondiente(downloadUri!!)
                } else {
                    Toast.makeText(
                        this,
                        "Ocurrio un error al cargar la imagen de perfil, intentelo de nuevo mas tarde",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun guardarUrlUsuarioCorrespondiente(url: Uri) {
        userUrlImage = url.toString()


        reference.child(idUsuario.toString()).child("urlFoto").setValue(url.toString())
            .addOnCompleteListener {
                Toast.makeText(this, "Foto actualizada correctamente", Toast.LENGTH_LONG).show()
                getUrlFotoById()
                progresBarImage.visibility = View.INVISIBLE


            }
    }

    fun getUrlFotoById() {
        val query: Query = reference.orderByChild("idUser").equalTo(idUsuario)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato in snapshot.children) {
                    val usuario = dato.getValue(user::class.java)
                    urlImagDelete = usuario?.urlFoto!!
                    //validamos que la url no este basia
                    if (usuario?.urlFoto!!.isEmpty()) {

                    } else {
                        Glide.with(this@Home).load(usuario.urlFoto).circleCrop().into(btnImg)
                    }
                }

            }

        })
    }

    fun deleteImgFromStorage(url: String) {
        val ref = storage.getReferenceFromUrl(url)
        ref.delete().addOnCompleteListener {
            println("Eliminada")
            // Toast.makeText(this,"Eliminada",Toast.LENGTH_LONG).show()
        }
    }

    private fun getAmigos(id: String) {

        reference.child(id).child("amigos").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                   for (data in snapshot.children) {

                        arrayAmigos.add(data.key.toString())
                    }
                getTodosUsers(id)
                }
            }

        })
    }

    fun getTodosUsers(id:String) {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.exists()) {

                    for (data in snapshot.children) {
                        arrayTodosUsuarios.add(data.getValue(user::class.java)!!)
                    }
                    getValidacion(id)
                }
            }
        })
    }

    fun getValidacion(id:String){


        for (dato in arrayTodosUsuarios) {
            if (arrayAmigos.contains(dato.idUser)){

            }else
            {
                if (dato?.idUser.equals(id)) {
                } else {
                    lista.add(dato)
                }

                val adp = userAdapter(this@Home, lista, idUsuario)
                view.adapter = adp

            }
        }


    }
    private fun getSolicitudes(id: String) {
        println("desde get solicitudes" + id)
        var contadorSolicitudes: Int = 0
        reference.child(id).child("solicitudes").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        contadorSolicitudes++
                    }

                    txtNotifications.text = contadorSolicitudes.toString()


                }
            }

        })


    }

    private fun accionBotonesModulos() {

        btnModuloNotificacionH.setOnClickListener {
            val intent = Intent(this, notifications::class.java).apply {
                putExtra("email", userEmail)
                putExtra("name", userNameBD)
                putExtra("urlImage", userUrlImage)
                putExtra("idUser", idUsuario)
            }
            startActivity(intent)
            onBackPressed()
        }


        btnModuloMensajesH.setOnClickListener {
            val intent = Intent(this, amigos::class.java).apply {
                putExtra("email", userEmail)
                putExtra("name", userNameBD)
                putExtra("urlImage", userUrlImage)
                putExtra("idUser", idUsuario)
            }
            startActivity(intent)


        }
    }
}