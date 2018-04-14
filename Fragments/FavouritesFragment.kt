package com.example.yashwanth.echo.Fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.yashwanth.echo.Activities.MainActivity
import com.example.yashwanth.echo.Adapters.FragmentAdapter
import com.example.yashwanth.echo.EchoDatabase.EchoDatabase
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified._playPauseImageButton
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified._songTitle
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.favouriteContent
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.mediaPlayer
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.noFavourites
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.nowPlayingBottomBar
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.playingBar
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.recylerView
import com.example.yashwanth.echo.Fragments.FavouritesFragment.Statified.refreshList

import com.example.yashwanth.echo.R
import com.example.yashwanth.echo.Songs
import com.internshala.echo.adapters.MainScreenAdapter
import kotlinx.android.synthetic.main.fragment_main_screen.*


/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {
    var myActivity: Activity? = null
    var trackPosition:Int?=0
    object Statified {
        var noFavourites: TextView? = null
        var nowPlayingBottomBar: RelativeLayout? = null
        var _playPauseImageButton: ImageButton? = null
        var _songTitle: TextView? = null
        var recylerView: RecyclerView? = null
        var favouriteContent: EchoDatabase? = null
        var playingBar: ImageView? = null
        var refreshList: ArrayList<Songs>? = null
        var mediaPlayer: MediaPlayer? = null
        var SrefreshList: Set<Songs>? = null
        var listFromDB = ArrayList<Songs>()
        var mSensorListenor: SensorEventListener? = null
        var mSensorManager: SensorManager? = null
        var MY_PREFS_NAME = "ShakeFeature"
    }
    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationPassed: Float = 0f


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_favourites, container, false)
        activity.title = "Favourites"
        setHasOptionsMenu(false)
        noFavourites = view?.findViewById(R.id.nofavourites)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        _playPauseImageButton = view?.findViewById(R.id.playpauseButton)
        _songTitle = view?.findViewById(R.id.songTitleFavScreen)
        recylerView = view?.findViewById(R.id.favouritesRecyler)
        playingBar = view?.findViewById(R.id.deaultMusic)

        bottomBarSetup()
        return view

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favouriteContent = EchoDatabase(myActivity)

        bottomBarSetup()
        bottomBarClickHandler()
        display_favouritesBySearching()





    }



    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songDate = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentData = songCursor.getString(songData)
                val currentArtist = songCursor.getString(songArtist)
                val currentDate = songCursor.getLong(songDate)
                arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))

            }

        }
        return arrayList as ArrayList<Songs>

    }

    fun display_favouritesBySearching() {
        var c = favouriteContent?.checkSize()
        Toast.makeText(myActivity, "$c  Favourites", Toast.LENGTH_SHORT).show()
        if (favouriteContent?.checkSize() as Int > 0) {

            Statified.refreshList = ArrayList<Songs>()

            Statified.listFromDB = favouriteContent?.queryDBList() as ArrayList<Songs>
            var fetchListFromDevice = getSongsFromPhone()
            if (fetchListFromDevice != null) {
                for (j in 0..Statified.listFromDB?.size as Int - 1) {
                    for (i in 0..fetchListFromDevice?.size - 1) {
                        if (((Statified.listFromDB?.get(j)?.songId) == (fetchListFromDevice?.get(i)?.songId))) {
                            if (!(Statified.refreshList?.contains(Statified.listFromDB?.get(j)) as Boolean)) {
                                Statified.refreshList?.add((Statified.listFromDB as ArrayList<Songs>)[j])
                            }


                        }


                    }
                }
            } else {


            }
        }

            if(Statified.refreshList?.size!=null){
                if (Statified.refreshList?.distinct()?.size == 1) {
                    var list = Statified.refreshList?.distinct()
                    Statified.refreshList?.clear()
                    Statified.refreshList?.add(list?.get(0) as Songs)
                } else if(Statified.refreshList?.distinct()?.size!! > 1){
                    Statified.refreshList = Statified.refreshList?.distinct() as ArrayList<Songs>
                }
                noFavourites?.visibility = View.INVISIBLE
                recylerView?.visibility = View.VISIBLE


                    SongPlayingFragment.Statified.fetchSongs = Statified.refreshList
                    var favouriteAdapter = FragmentAdapter(Statified.refreshList as ArrayList<Songs>, myActivity as Context)
                    val mLayoutManager = LinearLayoutManager(myActivity)

                    recylerView?.layoutManager = mLayoutManager
                    recylerView?.adapter = favouriteAdapter
                    recylerView?.itemAnimator = DefaultItemAnimator()
                    recylerView?.setHasFixedSize(true)


            }else{
                noFavourites?.visibility = View.VISIBLE
                recylerView?.visibility = View.INVISIBLE
            }

    }
    fun returnList():ArrayList<Songs>{
        display_favouritesBySearching()
        return Statified.refreshList as ArrayList<Songs>
    }
    fun bottomBarSetup(){

        try{
            if(Statified.refreshList?.size!=0) {
                recylerView?.visibility=View.VISIBLE
                noFavourites?.visibility=View.INVISIBLE
                if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    nowPlayingBottomBar?.visibility=View.VISIBLE
                    _songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
                    SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                        _songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                        SongPlayingFragment.Staticated.onSongComplete()
                        bottomBarSetup()

                    })
                }
                    else{
                    nowPlayingBottomBar?.visibility=View.INVISIBLE

                    }
                }
            else{
                recylerView?.visibility=View.INVISIBLE
                nowPlayingBottomBar?.visibility=View.INVISIBLE

                noFavourites?.visibility=View.VISIBLE

            }
        }catch(e:Exception){
            e.printStackTrace()
        }
    }
    fun bottomBarClickHandler(){

        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer=SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)

            args.putInt("position", SongPlayingFragment.Statified.currentSongHelper?.currentPosition as Int)
            args.putParcelableArrayList("songData",Statified.refreshList)
            args.putString("FavBottomBar","success")
            if(Statified.mediaPlayer?.isPlaying as Boolean){
                args.putBoolean("isPlaying",true)
            }
            else{
                args.putBoolean("isPlaying",false)
            }
            songPlayingFragment.arguments=args

            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment,songPlayingFragment)
                    .addToBackStack("SongPlaying")
                    .commit()
        })
        _playPauseImageButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition=SongPlayingFragment.Statified.mediaPlayer?.currentPosition
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)

                _playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition as Int)
                SongPlayingFragment.Statified.mediaPlayer?.start()

                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = true

                _playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }
    override  fun onPause() {
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListenor)
    }

    override fun onResume() {
        super.onResume()

        Statified.mSensorManager?.registerListener(Statified.mSensorListenor, Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationPassed = SensorManager.GRAVITY_EARTH
        bindShakeListener()
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
    }
    fun bindShakeListener() {
        Statified.mSensorListenor = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {
                var x = p0.values[0]
                var y = p0.values[1]
                var z = p0.values[2]
                mAccelerationPassed = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x * x) + (y * y) + (z * z)).toDouble()).toFloat()
                val delta = mAccelerationCurrent - mAccelerationPassed
                mAcceleration = mAcceleration * 0.9f + delta
                if (mAcceleration > 8) {
                    val prefs = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Statified.mediaPlayer=SongPlayingFragment.Statified.mediaPlayer
                        if (Statified.mediaPlayer?.isPlaying as Boolean) {

                            if(SongPlayingFragment.Statified.currentSongHelper!=null||(Statified.refreshList?.size!! >0 )) {
                                SongPlayingFragment.Statified.fetchSongs=Statified.refreshList
                                SongPlayingFragment.Statified.currentSongHelper?.currentPosition=Statified.mediaPlayer?.currentPosition as Int
                                if (SongPlayingFragment.Statified.currentSongHelper!=null&&SongPlayingFragment.Statified.currentSongHelper?.isShuffle as Boolean) {
                                   SongPlayingFragment.Staticated.playNext("PlayNextLikeNormalShuffle")
                                   bottomBarSetup()
                                } else {
                                    SongPlayingFragment.Staticated.playNext("PlayNextNormal")
                                   bottomBarSetup()
                                }
                            }else{
                                FavouritesFragment.Statified.mediaPlayer?.start()

                                bottomBarSetup()

                            }
                        }
                    }
                }

            }
        }
    }

}// Required empty public constructor
