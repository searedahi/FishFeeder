package com.geekyind.maritime.fishfeeder

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dji.sdk.sdkmanager.DJISDKManager
import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import dji.sdk.base.BaseProduct
import android.os.Looper
import android.util.Log
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.camera.Camera
import dji.sdk.products.Aircraft
import dji.sdk.products.HandHeld




class DjiFishFeederApp() : Application(){

    companion object {
        var mProduct: BaseProduct? = null
        var mHandler: Handler? = null

        val FLAG_CONNECTION_CHANGE = "fpv_tutorial_connection_change"

        /**
         * This function is used to get the instance of DJIBaseProduct.
         * If no product is connected, it returns null.
         */
        @Synchronized
        fun getProductInstance(): BaseProduct? {
            if (null == mProduct) {
                mProduct = DJISDKManager.getInstance().product
            }
            return mProduct
        }
    }


    private var mDJISDKManagerCallback: DJISDKManager.SDKManagerCallback? = null
    private var mDJIBaseProductListener: BaseProduct.BaseProductListener? = null
    private var mDJIComponentListener: BaseComponent.ComponentListener? = null

    private var instance: Application? = null

    fun setContext(application: Application) {
        instance = application
    }

    override fun getApplicationContext(): Context? {
        return instance
    }

    fun DjiFishFeederApp() {

    }



    @Synchronized
    fun getCameraInstance(): Camera? {

        if (getProductInstance() == null) return null

        var camera: Camera? = null

        if (getProductInstance() is Aircraft) {
            camera = (getProductInstance() as Aircraft).camera

        } else if (getProductInstance() is HandHeld) {
            camera = (getProductInstance() as HandHeld).camera
        }

        return camera
    }

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler(Looper.getMainLooper())
        mDJIComponentListener = BaseComponent.ComponentListener { notifyStatusChange() }
        mDJIBaseProductListener = object : BaseProduct.BaseProductListener {

            override fun onComponentChange(key: BaseProduct.ComponentKey, oldComponent: BaseComponent, newComponent: BaseComponent?) {

                newComponent?.setComponentListener(mDJIComponentListener)
                notifyStatusChange()
            }

            override fun onConnectivityChange(isConnected: Boolean) {

                notifyStatusChange()
            }

        }

        /**
         * When starting SDK services, an instance of interface DJISDKManager.DJISDKManagerCallback will be used to listen to
         * the SDK Registration result and the product changing.
         */
        mDJISDKManagerCallback = object : DJISDKManager.SDKManagerCallback {

            //Listens to the SDK registration result
            override fun onRegister(error: DJIError) {

                if (error === DJISDKError.REGISTRATION_SUCCESS) {

                    val handler = Handler(Looper.getMainLooper())
                    handler.post(Runnable { Toast.makeText(applicationContext, "Register Success", Toast.LENGTH_LONG).show() })

                    DJISDKManager.getInstance().startConnectionToProduct()

                } else {

                    val handler = Handler(Looper.getMainLooper())
                    handler.post(Runnable { Toast.makeText(applicationContext, "Register sdk fails, check network is available", Toast.LENGTH_LONG).show() })

                }
                Log.e("TAG", error.toString())
            }

            //Listens to the connected product changing, including two parts, component changing or product connection changing.
            override fun onProductChange(oldProduct: BaseProduct, newProduct: BaseProduct) {

                mProduct = newProduct
                if (mProduct != null) {
                    mProduct!!.setBaseProductListener(mDJIBaseProductListener)
                }

                notifyStatusChange()
            }
        }
        //Check the permissions before registering the application for android system 6.0 above.
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionCheck2 = ContextCompat.checkSelfPermission(applicationContext!!, android.Manifest.permission.READ_PHONE_STATE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || permissionCheck == 0 && permissionCheck2 == 0) {
            //This is used to start SDK services and initiate SDK.
            DJISDKManager.getInstance().registerApp(applicationContext, mDJISDKManagerCallback)
            Toast.makeText(applicationContext, "registering, pls wait...", Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(applicationContext, "Please check if the permission is granted.", Toast.LENGTH_LONG).show()
        }
    }

    private fun notifyStatusChange() {
        mHandler!!.removeCallbacks(updateRunnable)
        mHandler!!.postDelayed(updateRunnable, 500)
    }

    private val updateRunnable = Runnable {
        val intent = Intent(FLAG_CONNECTION_CHANGE)
        applicationContext!!.sendBroadcast(intent)
    }
}