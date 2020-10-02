package com.example.chat.clases

class mensaje {
    lateinit var mensaje:String
    lateinit var idEmisor:String
    lateinit var hora:String
    constructor(){}
    constructor( mensaje:String, idEmisor:String,hora:String){
        this.mensaje =mensaje
        this.idEmisor =idEmisor
        this.hora =hora
    }

}