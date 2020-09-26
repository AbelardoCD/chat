package com.example.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.chat.clases.user
import kotlinx.android.synthetic.main.item_amigo.view.*

class amigo_adapter  (
private val mcontext: Context,
private val listaUsuarios: List<user>
) :
ArrayAdapter<user>(mcontext, 0, listaUsuarios) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout =
            LayoutInflater.from(context).inflate(com.example.chat.R.layout.item_amigo, parent, false)
        val user = listaUsuarios[position]
        println(user)
        layout.nombreContacto.text = user.Nombre
        Glide.with(mcontext).load(user.urlFoto).circleCrop().into(layout.contactoImg)
        return layout
    }
}