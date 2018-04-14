package com.example.yashwanth.echo

import android.os.Parcel
import android.os.Parcelable
import com.example.yashwanth.echo.Fragments.SongPlayingFragment

/**
 * Created by YASHWANTH on 04-04-2018.
 */
class Songs( var songId:Long, var songTitle:String,var songArtist:String,var songData:String,var songDate:Long):Parcelable{

    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }



    object Statified{
        var nameComparator:Comparator<Songs> =Comparator<Songs>{song1,song2->
            val songOne=song1.songTitle.toUpperCase()
            val songTwo=song2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)

        }
        var dateComparator:Comparator<Songs> =Comparator<Songs>{song1,song2->
            val songOne=song1.songDate.toDouble()
            val songTwo=song2.songDate.toDouble()
            songOne.compareTo(songTwo)

        }

    }

}