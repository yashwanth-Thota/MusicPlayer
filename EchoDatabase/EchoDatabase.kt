package com.example.yashwanth.echo.EchoDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.yashwanth.echo.Songs

/**
 * Created by YASHWANTH on 06-04-2018.
 */


class EchoDatabase:SQLiteOpenHelper{




    object Staticated{
        var _songList=ArrayList<Songs>()
        var DB_VERSION=1
        var DB_NAME="FavouriteDAtabase"
        var TABLE_NAME="FavouriteTable"
        var COLUMN_ID="SongID"
        var COLUMN_SONG_TITLE="SongTitle"
        var COLUMN_SONG_ARTIST="SongArtist"
        var COLUMN_SONG_PATH="SongPath"
    }


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("CREATE TABLE "+Staticated.TABLE_NAME+"("+Staticated.COLUMN_ID+" INTEGER,"+Staticated.COLUMN_SONG_ARTIST+" STRING,"+Staticated.COLUMN_SONG_TITLE+" STRING,"+Staticated.COLUMN_SONG_PATH+" STRING)")


    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
    fun storeAsFavourite(id:Int?,artist:String?,title:String?,path:String?){
        val db=this.writableDatabase
        var contentValues=ContentValues()
            if(!checkIfIdExist(id as Int)) {
                        contentValues.put(Staticated.COLUMN_ID, id)
                        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
                        contentValues.put(Staticated.COLUMN_SONG_TITLE, title)
                        contentValues.put(Staticated.COLUMN_SONG_PATH, path)
                        db.insert(Staticated.TABLE_NAME, null, contentValues)
                        db.close()
                    }

    }

    fun queryDBList():ArrayList<Songs>{
        try {
            Staticated._songList?.clear()
            var db=this.readableDatabase
            val query_param="SELECT * FROM "+Staticated.TABLE_NAME
            var cSor=db.rawQuery(query_param,null)

            if(cSor.moveToFirst()){
                do {
                    var _id=cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _path=cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    if(!Staticated._songList?.contains(Songs(_id.toLong(),_title,_artist,_path,0))){
                    Staticated._songList.add(Songs(_id.toLong(),_title,_artist,_path,0))}


                }while (cSor.moveToNext())
            }else{
                return null as ArrayList<Songs>
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return Staticated._songList



    }

    fun checkIfIdExist(_int:Int):Boolean{
        var storeId=-1090
        val db=this.readableDatabase
        val query_params="SELECT * FROM "+Staticated.TABLE_NAME +" WHERE SongID =' $_int'"
        val cSor=db.rawQuery(query_params,null)
        if(cSor.moveToFirst()){
            do {
                if((cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID)))==_int){
                storeId=cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))}

            }while (cSor.moveToNext()
            )
        }else{
            return false
        }
        return storeId!=-1090

    }
    fun deleteFavourite(_int:Int){
        val db=this.writableDatabase
        db.delete(Staticated.TABLE_NAME,Staticated.COLUMN_ID+"="+_int,null)
        db.close()

    }

    fun checkSize():Int{
            var counter =0
        val db=this.readableDatabase
        val query_params="SELECT * FROM "+Staticated.TABLE_NAME
        val cSor=db.rawQuery(query_params,null)
        if(cSor.moveToFirst()){
            do {
                counter=counter + 1
            }while (cSor.moveToNext()
            )
        }else{

        }
            return counter
    }
    constructor(context:Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
}