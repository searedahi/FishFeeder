package com.geekyind.maritime.fishfeeder

import android.content.Intent
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class RangeRingActivity : AppCompatActivity(), TextureView.SurfaceTextureListener, View.OnClickListener {

    //DJI Video Variables
    private var mVideoSurface: TextureView? = null

    //    private val mCaptureBtn: Button? = null
    //    private val mShootPhotoModeBtn: Button? = null
    //    private val mRecordVideoModeBtn: Button? = null
    //    private val mRecordBtn: ToggleButton? = null
    //    private val recordingTime: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_ring)
        var settingsButton= findViewById<Button>(R.id.btnSettings)
        settingsButton.setOnClickListener(this)


//        val videoView = findViewById<VideoView>(R.id.liveVideoView)
//        val mediaController = MediaController(this)
//        mediaController.setAnchorView(videoView)
//        videoView.setMediaController(mediaController)
//        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.fake_drone_feed))
//        videoView.start()

        val np = findViewById<NumberPicker>(R.id.altitudeDesired)
        np.minValue = 0
        np.maxValue = 200

        np.setOnValueChangedListener(onValueChangeListener)

//        //Initialize DJI SDK Manager
//        mHandler = Handler(Looper.getMainLooper())

        //Get the live feed from DJI
        initUI()

    }












    private var onValueChangeListener: NumberPicker.OnValueChangeListener = NumberPicker.OnValueChangeListener { numberPicker, i, i1 ->
        Toast.makeText(this@RangeRingActivity,
                "selected number " + numberPicker.value, Toast.LENGTH_SHORT)
    }





    //DJI

//    private val mDJIBaseProductListener = object : BaseProduct.BaseProductListener {
//        override fun onComponentChange(key: BaseProduct.ComponentKey, oldComponent: BaseComponent, newComponent: BaseComponent?) {
//            newComponent?.setComponentListener(mDJIComponentListener)
//            notifyStatusChange()
//        }
//
//        override fun onConnectivityChange(isConnected: Boolean) {
//            notifyStatusChange()
//        }
//    }
//    private val mDJIComponentListener = BaseComponent.ComponentListener { notifyStatusChange() }
//    private fun notifyStatusChange() {
//        mHandler?.removeCallbacks(updateRunnable)
//        mHandler?.postDelayed(updateRunnable, 500)
//    }
//
//    private val updateRunnable = Runnable {
//        val intent = Intent(flagConnectionRequest)
//        sendBroadcast(intent)
//    }

    private fun showToast(toastMsg: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_LONG).show() }
    }




    //DJI Video Feed

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
    }

//    fun onReturn(view: View) {
//        this.finish()
//    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    private fun initUI() {
        // init mVideoSurface
        mVideoSurface = findViewById<TextureView>(R.id.liveVideoView)
//        recordingTime = findViewById<TextView>(R.id.timer)
//        mCaptureBtn = findViewById<Button>(R.id.btn_capture)
//        mRecordBtn = findViewById<ToggleButton>(R.id.btn_record)
//        mShootPhotoModeBtn = findViewById<Button>(R.id.btn_shoot_photo_mode)
//        mRecordVideoModeBtn = findViewById<Button>(R.id.btn_record_video_mode)

        mVideoSurface!!.surfaceTextureListener = this

//        mCaptureBtn.setOnClickListener(this)
//        mRecordBtn.setOnClickListener(this)
//        mShootPhotoModeBtn.setOnClickListener(this)
//        mRecordVideoModeBtn.setOnClickListener(this)
//        recordingTime.setVisibility(View.INVISIBLE)
//        mRecordBtn.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSettings -> {
                var myIntent = Intent(this, DjiDeviceConnectionActivity::class.java)
                startActivity(myIntent)
            }
            else -> {
                return
            }
        }
    }

}
