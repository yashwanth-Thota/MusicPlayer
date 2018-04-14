package com.example.yashwanth.echo.Fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.speech.SpeechRecognizer
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.yashwanth.echo.CurrentSongHelper
import com.example.yashwanth.echo.EchoDatabase.EchoDatabase
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Staticated.onSongComplete
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Staticated.playNext
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Staticated.processInformaation
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Staticated.updateTextViews
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.audioVisiulisation
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.currentPosition
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.currentSongHelper

import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.endTimeText
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.favouriteContent
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.favouriteImageButton
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.fetchSongs
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.glview
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.loopImageButton
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.mediaPlayer
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.myActivity
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.nextImageButton
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.previousImageButton
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.seekbar
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.songArtistView
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.songTitleView
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.startTimeText
import com.example.yashwanth.echo.Fragments.SongPlayingFragment.Statified.updateSongTime

import com.example.yashwanth.echo.R
import com.example.yashwanth.echo.Songs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_song_playing.*
import org.w3c.dom.Text
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {


    object Statified {
        var currentPosition: Int = 0
        var myActivity: Activity? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var mediaPlayer = MediaPlayer()
        var currentSongHelper: CurrentSongHelper? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var favSongList = ArrayList<Songs>()
        var audioVisiulisation: AudioVisualization? = null
        var favouriteImageButton: ImageButton? = null
        var speechVisualization: SpeechRecognizer? = null
        var glview: GLAudioVisualizationView? = null
        var mSensorManager: SensorManager? = null
        var fetchSongs: ArrayList<Songs>? = null
        var mSensorListenor: SensorEventListener? = null
        var favouriteContent: EchoDatabase? = null
        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                                (TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))

                                        )))
                seekbar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }

        }
    }

    var t:Toast?=null

    object Staticated {
        var my_shuffle = "shuffle feature"
        var my_loop = "Loop feature"
        fun onSongComplete() {
            if(Statified.currentSongHelper!=null){
            if (currentSongHelper?.isShuffle as Boolean) {

                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else {
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(Statified.currentPosition)
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.songArtist
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songId = nextSong?.songId as Long
                    updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
                    mediaPlayer?.reset()
                    try {
                        mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformaation(mediaPlayer as MediaPlayer)


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true

                }
            }
            }
            if ((favouriteContent?.checkIfIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
                Statified.favouriteImageButton?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
            } else {
                Statified.favouriteImageButton?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            Statified.songTitleView?.setText(songTitle)
            Statified.songArtistView?.setText(songArtist)
        }

        fun processInformaation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer?.duration
            val startTime = mediaPlayer?.currentPosition
            seekbar?.max = finalTime
            startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long) -
                            (TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long)))))
            endTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long) -
                            (TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long)))))
            seekbar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }


        fun playNext(check: String) {

            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.minus(1) as Int)
                currentPosition = randomPosition


            }
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (currentPosition >= fetchSongs?.size as Int) {
                currentPosition = 0
            }
            if(!fetchSongs?.isEmpty()!! as Boolean) {
                var nextSong = fetchSongs?.get(currentPosition)
                currentSongHelper?.isLoop = false

                currentSongHelper?.songTitle = nextSong?.songTitle
                currentSongHelper?.songArtist = nextSong?.songArtist
                currentSongHelper?.songPath = nextSong?.songData
                currentSongHelper?.currentPosition = currentPosition
                currentSongHelper?.songId = nextSong?.songId as Long
                Staticated.updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
                mediaPlayer?.reset()
                try {
                    mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    Staticated.processInformaation(mediaPlayer as MediaPlayer)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if ((favouriteContent?.checkIfIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
                Statified.favouriteImageButton?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
            } else {
                Statified.favouriteImageButton?.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
            }
        }


    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationPassed: Float = 0f
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        activity.title = "NowPlaying"
        setHasOptionsMenu(true)
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playPauseImageButton = view?.findViewById(R.id.playPauseButtonPlaying)
        previousImageButton = view?.findViewById(R.id.previousButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        seekbar = view?.findViewById(R.id.seekBar)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        glview = view?.findViewById(R.id.visualizer_view)
        favouriteImageButton = view?.findViewById(R.id.favouriteButton)
        favouriteImageButton?.alpha = 0.8f



        return view
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisiulisation = glview as AudioVisualization


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onPause() {
        super.onPause()
        audioVisiulisation?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListenor)
    }

    override fun onResume() {
        super.onResume()
        audioVisiulisation?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListenor, Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioVisiulisation?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationPassed = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.favouriteContent = EchoDatabase(Statified.myActivity)
        fetchSongs = ArrayList<Songs>()
        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isShuffle = false
        currentSongHelper?.isLoop = false
        super.onCreate(savedInstanceState)

        var _path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var _songId: Long = 0
        try {

            _path = arguments.getString("path");
            _songArtist = arguments.getString("songArtist")
            _songTitle = arguments.getString("songTitle")
            _songId = arguments.getInt("songId").toLong()
            Statified.currentPosition = arguments.getInt("position")
            fetchSongs = arguments.getParcelableArrayList("songData")
            currentSongHelper?.songPath = _path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = _songId
            currentSongHelper?.currentPosition = Statified.currentPosition
            Staticated.updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        var isPlaying:Boolean=true
        var fromFavFragment = arguments.get("FavBottomBar") as? String
        var fromMainFragment = arguments.get("MainBottomBar") as? String
        if (fromFavFragment != null) {
            Statified.mediaPlayer = FavouritesFragment.Statified.mediaPlayer as MediaPlayer
            Statified.fetchSongs = FavouritesFragment.Statified.refreshList
            isPlaying=arguments.getBoolean("isPlaying")

        } else if (fromMainFragment != null) {
            Statified.fetchSongs = getSongsFromPhone()
            Statified.mediaPlayer = MainScreenFragment.Statified.mediaPlayer as MediaPlayer
            isPlaying=arguments.getBoolean("isPlaying")

        } else {
            Statified.mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(_path))
                mediaPlayer?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer?.start()
        }
        currentSongHelper?.isPlaying=isPlaying
        processInformaation(mediaPlayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {

            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaPlayer?.setOnCompletionListener {
            onSongComplete()

        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisiulisation?.linkTo(visualizationHandler)
        var prefShuffle = myActivity?.getSharedPreferences(Staticated.my_shuffle, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefLoop = myActivity?.getSharedPreferences(Staticated.my_loop, Context.MODE_PRIVATE)
        var isLoopAllowed = prefLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            currentSongHelper?.isLoop = true
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isLoop = false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if ((favouriteContent?.checkIfIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
            favouriteButton.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
        } else {
            favouriteButton.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
        }


    }

    fun clickHandler() {
        t?.setGravity(Gravity.BOTTOM,0,0)
        favouriteImageButton?.setOnClickListener({

            if ((favouriteContent?.checkIfIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
                favouriteButton.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))

                favouriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as Int)
                t=Toast.makeText(myActivity, "Deleted from favourites", Toast.LENGTH_SHORT)
                t?.show()
                FavouritesFragment.Statified.refreshList=favouriteContent?.queryDBList()
                Statified.favSongList = favouriteContent?.queryDBList() as ArrayList<Songs>

            } else {
                favouriteButton.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))

                favouriteContent?.storeAsFavourite(currentSongHelper?.songId?.toInt(), currentSongHelper?.songArtist,
                        currentSongHelper?.songTitle, currentSongHelper?.songPath)
                FavouritesFragment.Statified.refreshList=favouriteContent?.queryDBList()
                t=Toast.makeText(myActivity, "Added to  favourites", Toast.LENGTH_SHORT)
                t?.show()


            }
        })
        shuffleImageButton?.setOnClickListener({
            var editorShuffle = myActivity?.getSharedPreferences(Staticated.my_shuffle, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.my_loop, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isShuffle as Boolean) {
                currentSongHelper?.isShuffle = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isLoop = false
                currentSongHelper?.isShuffle = true
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }

        })
        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }

        })
        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()

        })
        loopImageButton?.setOnClickListener({
            var editorShuffle = myActivity?.getSharedPreferences(Staticated.my_shuffle, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.my_loop, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }

        })
        playPauseImageButton?.setOnClickListener({
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer?.start()
                processInformaation(mediaPlayer as MediaPlayer)
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                seekbar?.progress = p0?.progress as Int
                mediaPlayer?.seekTo(seekbar?.progress as Int)
            }
        })
    }


    fun playPrevious() {
        currentPosition = currentPosition - 1
        if (currentPosition == -1) {
            currentPosition = 0
        }

        playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

        var nextSong = fetchSongs?.get(currentPosition)
        currentSongHelper?.isLoop = false

        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.songArtist
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.currentPosition = currentPosition
        currentSongHelper?.songId = nextSong?.songId as Long
        updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        mediaPlayer?.reset()
        try {
            mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            processInformaation(mediaPlayer as MediaPlayer)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        if ((favouriteContent?.checkIfIdExist(currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
            favouriteButton.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_on))
        } else {
            favouriteButton.setImageDrawable(ContextCompat.getDrawable(myActivity, R.drawable.favorite_off))
        }

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2:MenuItem?=menu?.findItem(R.id.action_sort)
        item2?.isVisible=false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect->{
                if(!Statified.mediaPlayer?.isPlaying){
                    MainScreenFragment.Statified.mediaPlayer?.pause()
                }
                else{
                    Statified.mediaPlayer?.start()
                }
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
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
                if (mAcceleration > 8 && currentSongHelper!=null) {
                    val prefs = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        if(currentSongHelper?.isPlaying as Boolean){
                        if (currentSongHelper?.isShuffle as Boolean) {
                            playNext("PlayNextLikeNormalShuffle")
                        } else {
                            playNext("PlayNextNormal")
                        }
                    } else {
                        mediaPlayer?.start()

                        playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                    }
                }
                    }
                }

            }
        }


} // Required empty public constructo
