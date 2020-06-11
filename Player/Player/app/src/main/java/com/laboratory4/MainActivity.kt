package com.laboratory4

import android.Manifest
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val AUDIO_SELECT = 1
    private val VIDEO_SELECT = 2
    private var audioPlayer: MediaPlayer? = null
    private var isAudioPrepared = false
    private var videoUri: Uri = Uri.EMPTY
    private var isVideoPrepared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Запрос разрешения на чтение данных
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            0)

        // Выбор аудио/видео файла
        openMusic.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            intent.type = "audio/*"
            startActivityForResult(intent, AUDIO_SELECT)
        }
        openVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, VIDEO_SELECT)
        }

        // Управление музыкой/видео
        startButton.setOnClickListener {
            if (audioPlayer != null) {
                if (!isAudioPrepared) {
                    audioPlayer?.prepare()
                    isAudioPrepared = true
                }
                audioPlayer?.start()
            } else if (!videoView.isPlaying && videoUri != Uri.EMPTY) {
                if (!isVideoPrepared) {
                    playVideo(videoUri)
                    isVideoPrepared = true
                } else
                    videoView.start()
            }
        }
        pauseButton.setOnClickListener {
            if (audioPlayer != null && audioPlayer!!.isPlaying)
                audioPlayer?.pause()
            else if (videoView.isPlaying)
                videoView.pause()
        }
        stopButton.setOnClickListener {
            if (audioPlayer != null) {
                audioPlayer?.stop()
                isAudioPrepared = false
            } else if (videoUri != Uri.EMPTY) {
                videoView.stopPlayback()
                isVideoPrepared = false
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Очистка ресурсов
        stopMusic()
        stopVideo()
        Log.d("DBG", data?.dataString ?: "NULL")
        Log.d("DBG", contentResolver.getType(data?.data ?: Uri.EMPTY) ?: "No type")

        // Запуск аудио/видео
        when (requestCode) {
            AUDIO_SELECT -> if (data?.data != null)
                playMusic(data.data!!)
            else Toast.makeText(applicationContext, "Выбран неправильный медиа файл", Toast.LENGTH_SHORT).show()
            VIDEO_SELECT -> if (data?.data != null) {
                videoUri = data.data!!
                playVideo(data.data!!)
            } else Toast.makeText(applicationContext, "Выбран неправильный медиа файл", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Запуск/остановка музыки

    private fun playMusic(uri: Uri) {
        audioPlayer = MediaPlayer()
        audioPlayer?.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        audioPlayer?.setDataSource(applicationContext, uri)
        audioPlayer?.prepare()
        audioPlayer?.start()
        isAudioPrepared = true
    }

    private fun stopMusic() {
        audioPlayer?.release()
        audioPlayer = null
        isAudioPrepared = false
    }

    // Запуск/остановка видео

    private fun playVideo(uri: Uri) {
        if (uri == Uri.EMPTY) return

        videoView.visibility = View.VISIBLE
        videoView.setVideoURI(uri)
        videoView.start()
        isVideoPrepared = true
    }

    private fun stopVideo() {
        videoView.stopPlayback()
        videoView.visibility = View.INVISIBLE
        videoUri = Uri.EMPTY
        isVideoPrepared = false
    }
}
