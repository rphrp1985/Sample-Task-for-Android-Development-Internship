package com.prianshuprasad.videoplayer

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class dataBase {
     private val db = FirebaseFirestore.getInstance().collection("videos")
     private val array:ArrayList<videoData> = ArrayList()
     private val searchhlper= searchHelper()

     fun uploadData(url:String,videoTitle:String,tags:ArrayList<String>){
        val docid= genDocID()
          GlobalScope.launch {
          db.document(docid).set(videoData(url,videoTitle,tags))
          }
          searchhlper.addData(videoTitle,tags,docid)

     }

     fun genDocID():String{
          return "${System.currentTimeMillis()}${System.nanoTime()}"
     }

     fun getSearchHelper()= searchhlper


     fun getDB(): CollectionReference {

     return db;
     }


}