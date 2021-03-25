package com.example.onpressrecording

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnVoiceClicked {
    var AudioSavePathInDevice: String? = null
    var mediaRecorder: MediaRecorder? = null
    private var random: Random? = null
    var folderPath: String = ""
    private var RandomAudioFileName = "ABCDEFGHIJKLMNOP"
    private val RequestPermissionCode = 1
    var mediaPlayer: MediaPlayer? = null
    private val userVoice: ArrayList<UsersData> = ArrayList()
    private lateinit var AdapterForRecordings: AdapterForRecordings
    private lateinit var RecentrecyclerView: RecyclerView
    //................................................................
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       
        random = Random(123)
        btnRecord.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action === MotionEvent.ACTION_DOWN) {

                    if (checkPermission()) {
                        folderPath = getExternalFilesDir(null)?.getAbsolutePath() + "/userID"
                        val myfie = File(folderPath)
                        if (!myfie.exists()) {
                            myfie.mkdir()
                        }
                        AudioSavePathInDevice = (folderPath + "/DDDD" + CreateRandomAudioFileName(5) + "AudioRecording.opus")
                        MediaRecorderReady()
                        try {
                            mediaRecorder?.prepare()
                            mediaRecorder?.start()
                        } catch (e: IllegalStateException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        } catch (e: IOException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }

                        Toast.makeText(this@MainActivity, "Recording started", Toast.LENGTH_LONG).show()
                    } else {
                        requestPermission()
                    }

                } else if (event.getAction() === MotionEvent.ACTION_UP) {
                    stopRecording()

                    if (mediaPlayer != null) {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.release()
                        MediaRecorderReady()
                    }
                    // Released
                    Toast.makeText(this@MainActivity, "Send", Toast.LENGTH_SHORT).show()
                    userVoice.add(UsersData(AudioSavePathInDevice.toString()))

                    setRecyclerView()
                }
                return true
            }
        })
    }

    private fun setRecyclerView() {

        RecentrecyclerView = findViewById(R.id.Rc_VoiceRecording)
        AdapterForRecordings = AdapterForRecordings(userVoice, this)
        RecentrecyclerView.setHasFixedSize(true)
        RecentrecyclerView.layoutManager = LinearLayoutManager(this)
        RecentrecyclerView.adapter = AdapterForRecordings

    }

    fun MediaRecorderReady() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder!!.setOutputFile(AudioSavePathInDevice)
    }

    fun CreateRandomAudioFileName(string: Int): String {
        val stringBuilder = StringBuilder(string)
        var i = 0
        while (i < string) {
            stringBuilder.append(RandomAudioFileName[random!!.nextInt(RandomAudioFileName.length)])
            Log.i("rendom", random.toString())
            i++
        }
        return stringBuilder.toString()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf<String>(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO),
            RequestPermissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RequestPermissionCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else
                    return
            }
        }
    }

    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun stopRecording() {
        try {
            mediaRecorder!!.stop()
        }
        catch (stopException: RuntimeException) {
            Log.e("Tag", stopException.cause.toString())
        }
    }

    override fun onVoiceClick(voicePath: String) {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(voicePath)
            mediaPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaPlayer!!.start()
        Toast.makeText(this, "Recording Playing", Toast.LENGTH_SHORT).show()
    }
}