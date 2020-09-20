package com.example.chat.clases

class user {
    var Nombre:String =""
    var Email:String=""
    var idUser:String=""
    var urlFoto:String=""
    var solicitudes:HashMap<String,Boolean> ?= HashMap()
    constructor(){}

    constructor(idUser:String,nombre:String,email:String,url:String){
        this.idUser = idUser
        this.Nombre = nombre
        this.Email = email
        this.urlFoto=url
        this.solicitudes =   HashMap()
    }

    override fun toString(): String {
        return Nombre +" " + Email
    }
}