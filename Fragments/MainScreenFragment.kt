package com.example.yashwanth.echo.Fragments


import android.app.Activity
import android.app.FragmentTransaction
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
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*

import com.example.yashwanth.echo.Activities.MainActivity
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified._songTitle
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.getSongsList
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.noSongs
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.nowPlayingBottomBar
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.playPauseButton
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.playingBar
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.recylerView
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.sort
import com.example.yashwanth.echo.Fragments.MainScreenFragment.Statified.visibleLayout

import com.example.yashwanth.echo.R
import com.example.yashwanth.echo.Songs
import com.internshala.echo.adapters.MainScreenAdapter
import kotlinx.android.synthetic.main.fragment_main_screen.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {


    object Statified {
        var getSongsList: ArrayList<Songs>? = null
        var nowPlayingBottomBar: RelativeLayout? = null
        var playPauseButton: ImageButton? = null
        var _songTitle: TextView? = null
        var visibleLayout: RelativeLayout? = null
        var noSongs: RelativeLayout? = null
        var playingBar: ImageView? = null
        var recylerView: RecyclerView? = null
        var mediaPlayer: MediaPlayer? = null
        var sort: ImageView? = null
        var mSensorListenor: SensorEventListener? = null
        var mSensorManager: SensorManager? = null
        var MY_PREFS_NAME = "ShakeFeature"
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationPassed: Float = 0f

    var myActivity: Activity? = null


    var trackPosition: Int = 0

    var mainScreenAdapter: MainScreenAdapter? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)

        activity.title = "All Songs"
        setHasOptionsMenu(true)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarMainScreen)
        Statified.playPauseButton = view?.findViewById(R.id.playPauseButton)
        _songTitle = view?.findViewById(R.id.songTitleMain)
        Statified.visibleLayout = view?.findViewById(R.id.visibleLayout)
        Statified.noSongs = view?.findViewById(R.id.noSongs)
        recylerView = view?.findViewById(R.id.contenMain)
        playingBar = view?.findViewById(R.id.deaultMusic)
        //Statified.sort=view?.findViewById(R.id.sort)
        //nowPlayingBottomBar?.visibility=View.VISIBLE

        return view


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList = getSongsFromPhone()
        if (SongPlayingFragment.Statified.currentSongHelper != null && SongPlayingFragment.Statified.currentSongHelper?.isPlaying as Boolean) {
            nowPlayingBottomBar?.visibility = View.VISIBLE
            _songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
        }
        val prefs = activity.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs.getString("action_sort_recent", "false")
        bottomBarSetup()


        if (!getSongsList!!.isEmpty()) {
            SongPlayingFragment.Statified.fetchSongs = getSongsList
            mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(myActivity)
            recylerView?.layoutManager = mLayoutManager
            recylerView?.itemAnimator = DefaultItemAnimator()
            recylerView?.adapter = mainScreenAdapter
        } else {
            nowPlayingBottomBar?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        }
        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
        }

//        sort?.setOnClickListener({
//            var list= getSongsList?.sortedWith(compareBy({it.songArtist}))
//            getSongsList?.clear()
//            getSongsList=list as ArrayList<Songs>
//            if(!getSongsList!!.isEmpty()) {
//                SongPlayingFragment.Statified.fetchSongs= getSongsList
//                mainScreenAdapter= MainScreenAdapter(getSongsList as ArrayList<Songs>,myActivity as Context)
//                val mLayoutManager=LinearLayoutManager(myActivity)
//                recylerView?.layoutManager=mLayoutManager
//                recylerView?.itemAnimator=DefaultItemAnimator()
//                recylerView?.adapter=mainScreenAdapter
//            }else{
//                nowPlayingBottomBar?.visibility=View.INVISIBLE
//                noSongs?.visibility=View.VISIBLE
//            }
//        })


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()

            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "false")
            editor?.putString("action_sort_recent", "true")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
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

    fun bottomBarSetup() {

        try {
            bottomBarClickHandler()
            _songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            if (SongPlayingFragment.Statified.currentSongHelper != null) {
                SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({

                    _songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                    SongPlayingFragment.Staticated.onSongComplete()
                    bottomBarSetup()

                })
            }
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {


        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)

            args.putInt("position", SongPlayingFragment.Statified.currentSongHelper?.currentPosition as Int)
            args.putParcelableArrayList("songData", Statified.getSongsList)
            args.putString("MainBottomBar", "success")

            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                args.putBoolean("isPlaying", true)
            } else {
                args.putBoolean("isPlaying", false)
            }
            songPlayingFragment.arguments = args


            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingMain")
                    .commit()
        })
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition=SongPlayingFragment.Statified.mediaPlayer?.currentPosition
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)

                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition as Int)
                SongPlayingFragment.Statified.mediaPlayer?.start()

                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = true

                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    override fun onPause() {
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
                            SongPlayingFragment.Statified.fetchSongs = Statified.getSongsList
                            if (SongPlayingFragment.Statified.currentSongHelper != null) {
                                if (SongPlayingFragment.Statified.currentSongHelper?.isShuffle as Boolean) {
                                    if (Statified.mediaPlayer?.isPlaying as Boolean) {
                                        SongPlayingFragment.Staticated.playNext("PlayNextLikeNormalShuffle")
                                        bottomBarSetup()
                                    }

                                } else {
                                    if (Statified.mediaPlayer?.isPlaying as Boolean) {
                                        SongPlayingFragment.Staticated.playNext("PlayNextNormal")
                                        bottomBarSetup()
                                    }

                                }
                            }
                        } else {
                            Statified.mediaPlayer?.start()
                            bottomBarSetup()
                        }
                    }
                }
            }
        }
        bottomBarSetup()
    }
}


