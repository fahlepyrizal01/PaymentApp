package com.example.paymentapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.grpcservicelib.user_grpc.SendGetOneUser
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.user.UserDataModel
import com.example.paymentapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_user.*
import  com.example.modellib.StaticVariable
import com.example.paymentapp.activity.LoginActivity
import com.example.paymentapp.res.SharedPrefManager
import java.text.DecimalFormat
import android.app.AlertDialog
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.support.constraint.ConstraintSet
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.dialog_qrcode.view.*
import kotlinx.android.synthetic.main.dialog_top_up.view.*
import java.io.IOException


class ProfileUserFragment : Fragment(),
    View.OnClickListener,
    SendGetOneUser.OnSendGetOneUserListener{

    var errorsLog = ""
    lateinit var dialog: AlertDialog
    lateinit var dialogView: View
    lateinit var inflater: LayoutInflater
    lateinit var ctx : Context
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

    companion object {
        fun newInstance() = ProfileUserFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initiationWidget(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.example.paymentapp.R.layout.fragment_profile_user, container, false)
    }

    private fun initiationWidget(v: View) {
        imageButtonSetting.setOnClickListener(this)
        buttonLogout.setOnClickListener(this)
        imageButtonTopUp.setOnClickListener(this)
        textViewTopUp.setOnClickListener(this)
        imageButtonQRCode.setOnClickListener(this)
        textViewQRCode.setOnClickListener(this)
        textViewChangeProfilePicture.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
        circleImageViewProfileImage.setOnClickListener(this)
        buttonCancelProfile.setOnClickListener(this)
        ctx = activity as Activity
        getOneUser(SharedPrefManager.getIdUser(ctx))
    }

    override fun onClick(v: View?) {
        when (v){
            imageButtonSetting -> {
                showOverFlowMenu()
            }

            buttonLogout -> {
                SharedPrefManager.setLoggedIn(ctx, false)
                startActivity(Intent(context, LoginActivity::class.java))
                activity!!.finish()
            }
            imageButtonTopUp -> {
                showDialogTopUp(ctx)
            }
            textViewTopUp -> {
                showDialogTopUp(ctx)
            }
            imageButtonQRCode -> {
                showDialogQRCode(SharedPrefManager.getIdUser(ctx), ctx)

            }
            textViewQRCode -> {
                showDialogQRCode(SharedPrefManager.getIdUser(ctx), ctx)
            }
            circleImageViewProfileImage -> {
                if (editTextAddressProfile.isEnabled) {
                    requestStoragePermission()
                }
            }
            textViewChangeProfilePicture -> {
                requestStoragePermission()
            }
            buttonSave -> {

            }
            buttonCancelProfile -> {
                textViewFullNameProfile.visibility = View.VISIBLE
                textViewBalanceProfile.visibility = View.VISIBLE
                imageButtonQRCode.visibility = View.VISIBLE
                textViewQRCode.visibility = View.VISIBLE
                imageButtonTopUp.visibility = View.VISIBLE
                textViewTopUp.visibility = View.VISIBLE
                buttonLogout.visibility = View.VISIBLE
                textView69.visibility = View.VISIBLE
                editTextGenderProfile.visibility = View.VISIBLE
                textView22.visibility = View.VISIBLE
                editTextDateofBirthProfile.visibility = View.VISIBLE
                textViewChangeProfilePicture.visibility = View.GONE
                buttonSave.visibility = View.GONE
                buttonCancelProfile.visibility = View.GONE
                editTextEmailProfile.isEnabled = false
                editTextPhoneNumberProfile.isEnabled = false
                editTextAddressProfile.isEnabled = false
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayoutProfile)
                constraintSet.connect(R.id.textView87,ConstraintSet.TOP,R.id.editTextDateofBirthProfile,
                    ConstraintSet.BOTTOM,8)
                constraintSet.applyTo(constraintLayoutProfile)
                getOneUser(SharedPrefManager.getIdUser(ctx))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 147){
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(ctx.contentResolver, contentURI)
                    circleImageViewProfileImage.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(ctx, "Gagal memuat foto.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onErrorGetOneUser(errors: MutableList<String>) {
        showError(errors)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetOneUser(user: UserDataModel) {
        setImageProfile(user.UrlFotoProfil)
        textViewFullNameProfile.text = user.NamaPengguna
        val formatter = DecimalFormat("#,###,###")
        val balance = formatter.format(user.Balance)
        textViewBalanceProfile.text = "Rp. $balance"
        editTextEmailProfile.setText(user.Email)
        editTextPhoneNumberProfile.setText(user.NomorTelepon)
        editTextAddressProfile.setText(user.Alamat)
    }

    private fun getOneUser(idUser : Long){
        SendGetOneUser.newBuilder()
            .setIdPengguna(idUser)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetOneUserListener(this)
            .send()
    }

    private fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != null){
            Toast.makeText(activity,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setImageProfile(path : String) {
        Picasso.get()
            .load(path)
            .error(R.drawable.user)
            .placeholder(R.drawable.user)
            .into(circleImageViewProfileImage)
    }


    private fun showDialogTopUp(context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_top_up, null)
        dialog.setView(dialogView)
        dialogView.buttonCloseTopUp.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDialogQRCode(idUser: Long, context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_qrcode, null)
        dialog.setView(dialogView)
        showQRCode(idUser,dialogView.imageViewQRCode)
        dialogView.textViewIDUser.text = idUser.toString()
        dialogView.buttonCloseQRCode.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showQRCode(idUser: Long, qRCode : ImageView){

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(idUser.toString(),
                BarcodeFormat.QR_CODE,200,200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qRCode.setImageBitmap(bitmap)
        } catch (e : WriterException) {
            e.printStackTrace()
        }
    }

    private fun showOverFlowMenu(){
        val popup = PopupMenu(ctx, imageButtonSetting)
        popup.menuInflater.inflate(R.menu.overflow_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.editProfile -> {
                    textViewFullNameProfile.visibility = View.GONE
                    textViewBalanceProfile.visibility = View.GONE
                    imageButtonQRCode.visibility = View.GONE
                    textViewQRCode.visibility = View.GONE
                    imageButtonTopUp.visibility = View.GONE
                    textViewTopUp.visibility = View.GONE
                    buttonLogout.visibility = View.GONE
                    textView69.visibility = View.GONE
                    editTextGenderProfile.visibility = View.GONE
                    textView22.visibility = View.GONE
                    editTextDateofBirthProfile.visibility = View.GONE
                    textViewChangeProfilePicture.visibility = View.VISIBLE
                    buttonSave.visibility = View.VISIBLE
                    buttonCancelProfile.visibility = View.VISIBLE
                    editTextEmailProfile.isEnabled = true
                    editTextPhoneNumberProfile.isEnabled = true
                    editTextAddressProfile.isEnabled = true
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayoutProfile)
                    constraintSet.connect(R.id.textView87,ConstraintSet.TOP,R.id.textViewChangeProfilePicture,
                        ConstraintSet.BOTTOM,16)
                    constraintSet.applyTo(constraintLayoutProfile)
                    Toast.makeText(ctx, item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.changePassword -> {
                    showDialogChangePassword(ctx)
                }
                R.id.termAndCondition -> {
                    showDialogTemporary()
                }
                R.id.help -> {
                    showDialogTemporary()
                }
            }
            true
        }
        popup.show()
    }

    private fun showDialogTemporary(){
        val builder = AlertDialog.Builder(ctx)
        builder.setMessage("Ini adalah contoh penerapan GRPC pada aplikasi payment berbasis android.")
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun showDialogChangePassword(context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_change_password, null)
        dialog.setView(dialogView)
        dialogView.buttonSaveChangePassword.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, 147)
    }

    private fun requestStoragePermission() {
        Dexter.withActivity(ctx as Activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    choosePhotoFromGallary()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialogAccess()
                    }else {
                        showSettingsDialogWarning()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialogWarning() {
        val builder = AlertDialog.Builder(ctx)
        builder.setMessage("Aplikasi ini membutuhkan akses ke penyimpanan ponsel untuk memilih foto.")
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun showSettingsDialogAccess() {
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle("Butuh Hak Akses")
        builder.setMessage("Aplikasi ini membutuhkan akses ke penyimpanan ponsel untuk memilih foto. Kamu bisa memberikan " +
                "aplikasi ini akses di pengaturan aplikasi.")
        builder.setPositiveButton("PENGATURAN APLIKASI") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, which -> dialog.cancel() }
        builder.show()

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", (ctx as Activity).packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
