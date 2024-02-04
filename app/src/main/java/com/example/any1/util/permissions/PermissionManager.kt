package com.example.any1.util.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.example.any1.util.dialogs.OptionDialogBuilder
import com.example.any1.util.dialogs.helper.DialogNoClickListener
import com.example.any1.util.dialogs.helper.DialogYesClickListener
import com.example.any1.util.permissions.PermissionGrantedListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


object PermissionManager : DialogYesClickListener, DialogNoClickListener {

    fun askCameraPermission(activity: Activity, permissionGrantedListener: PermissionGrantedListener){
        Dexter.withContext(activity.applicationContext)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    permissionGrantedListener.onPermissionGranted("camera")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                    if (response != null) {
                        if(response.isPermanentlyDenied){
                            OptionDialogBuilder.createQuickDialog(
                                activity,
                                this@PermissionManager,
                                this@PermissionManager,
                                "Camera Permission Denied",
                                "We require permission to access camera of your device in order to set your profile photo. Enable camera permission? ",
                                yesText = "Enable",
                                noText = "No thanks"
                            )
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun askGalleryPermission(activity: Activity, permissionGrantedListener: PermissionGrantedListener){
        Dexter.withContext(activity.applicationContext)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    permissionGrantedListener.onPermissionGranted("gallery")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response != null) {
                        if (response.isPermanentlyDenied) {
                            OptionDialogBuilder.createQuickDialog(
                                activity,
                                this@PermissionManager,
                                this@PermissionManager,
                                "Files And Media Permission Denied",
                                "We require permission to access files and media on your device in order to set your profile photo. Enable camera permission? ",
                                yesText = "Enable",
                                noText = "No thanks"
                            )
                        }else{
                            Toast.makeText(activity, "denied", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun onNoClick() {
        OptionDialogBuilder.dismissDialog()
    }

    override fun onYesClick(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        OptionDialogBuilder.dismissDialog()
    }
}