package com.internshala.echo.adapters

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.yashwanth.echo.EchoDatabase.EchoDatabase
import com.example.yashwanth.echo.Fragments.FavouritesFragment
import com.example.yashwanth.echo.Fragments.SettingsFragment
import com.example.yashwanth.echo.Fragments.SongPlayingFragment
import com.example.yashwanth.echo.Songs
import com.example.yashwanth.echo.R


/**
 * Created by yashwanth on 2/13/2018.
 */

/*This adapter class also serves the same function to act as a bridge between the single row view and its data. The implementation is quite similar to the one we did
* for the navigation drawer adapter*/
class MainScreenAdapter(_songDetails: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>() {


    /*Local variables used for storing the data sent from the fragment to be used in the adapter
    * These variables are initially null*/
    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

object Staticated{
    var mediaPlayer=MediaPlayer()


        }
    val songPlayingFragment = SongPlayingFragment()


    /*In the init block we assign the data received from the params to our local variables*/
    init {
        this.songDetails = _songDetails
        this.mContext = _context


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        /*The holder object of our MyViewHolder class has two properties i.e
        * trackTitle for holding the name of the song and
        * trackArtist for holding the name of the artist*/
        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.songArtist

        holder.contentHolder?.setOnClickListener({

            if (SongPlayingFragment.Statified.mediaPlayer != null) {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.stop()


                }
            }


            var args = Bundle()
            args.putString("songArtist", songObject?.songArtist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("songId", songObject?.songId?.toInt() as Int)

            args.putInt("position", position)
            args.putParcelableArrayList("songData", songDetails)
            args.putString("fromMain","yes")
            songPlayingFragment.arguments = args

            try {
                Staticated.mediaPlayer?.setDataSource(mContext, Uri.parse(songObject?.songData))
                Staticated.mediaPlayer?.prepare()


            } catch (e: Exception) {
                e.printStackTrace()
            }

            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,songPlayingFragment,"SongPlayingFragmentMain")
                    .addToBackStack("SongPlayingFragmentMain")
                    .commit()
        })


    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        /*If the array list for the songs is null i.e. there are no songs in your device
        * then we return 0 and no songs are displayed*/
        if (songDetails == null) {
            return 0
        }

        /*Else we return the total size of the song details which will be the total number of song details*/
        else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    /*Every view holder class we create will serve the same purpose as it did when we created it for the navigation drawer*/
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        /*Declaring the widgets and the layout used*/
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var favButton:ImageButton?=null
        var contentHolder: RelativeLayout? = null

        /*Constructor initialisation for the variables*/
        init {

            trackTitle = view.findViewById(R.id.trackTitle)
            trackArtist = view.findViewById(R.id.trackArtist)
            contentHolder = view.findViewById(R.id.contentRow)
        }
    }
}