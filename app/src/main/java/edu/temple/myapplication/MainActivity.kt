package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import java.util.logging.Handler

class MainActivity : AppCompatActivity() {

    lateinit var timeBinder: TimerService.TimerBinder
    var isConnected = false
    lateinit var playButton: MenuItem

    var timer_val = 100
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            timeBinder = service as TimerService.TimerBinder
            timeBinder.setHandler(timeHandler)
            isConnected = true

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    val timeHandler = android.os.Handler(Looper.getMainLooper()) {
        findViewById<TextView>(R.id.textView).text = it.what.toString()
        timer_val = it.what
        true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
         menu?.run {
            playButton = findItem(R.id.start)
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
        R.id.start ->
            if (isConnected) {
                if (!timeBinder.isRunning && !timeBinder.paused) {
                    timeBinder.start(timer_val)
                    item.setIcon(R.drawable.pause)
                }else if(timeBinder.isRunning && !timeBinder.paused){
                    timeBinder.pause()
                    item.setIcon(android.R.drawable.ic_media_play)
                }else{

                    timeBinder.start(timer_val)

                }
                //timeBinder.start(100)


        }
            R.id.stop ->
                if (isConnected) {
                    timeBinder.stop()
                    timer_val = 100

                    playButton.setIcon(android.R.drawable.ic_media_play)
                }



        }


        return super.onOptionsItemSelected(item)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

//        findViewById<Button>(R.id.startButton).setOnClickListener {
//            if (isConnected) {
//                timeBinder.start(100)
//                findViewById<Button>(R.id.startButton).text = "Pause"
//            }
//        }

//        findViewById<Button>(R.id.stopButton).setOnClickListener {
//            if (isConnected) timeBinder.stop()
//
//        }

    }
    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}