package com.example.paymentapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.grpcservicelib.user_grpc.SendGetRegisterUser
import com.example.modellib.StaticVariable
import com.example.modellib.networkConfig.NetworkConfig
import kotlinx.android.synthetic.main.activity_resgister.*
import com.example.paymentapp.res.AESCrypt
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import android.annotation.SuppressLint
import android.widget.RadioButton
import android.widget.RadioGroup
import java.util.*


class RegisterActivity : AppCompatActivity(),
    View.OnClickListener,
    SendGetRegisterUser.OnSendGetRegisterUserListener,
    RadioGroup.OnCheckedChangeListener {

    lateinit var context : Context
    var errorsLog = ""
    var gender = ""
    private var datePickerDialog: DatePickerDialog? = null
    private var dateFormatter: SimpleDateFormat? = null
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.paymentapp.R.layout.activity_resgister)
        initiationWidget()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initiationWidget(){
        context = this@RegisterActivity
        dateFormatter = SimpleDateFormat("dd MM yyyy")
        buttonRegister.setOnClickListener(this)
        textViewLogin.setOnClickListener(this)
        imageButtonDate.setOnClickListener(this)
        radioGroupGender.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            imageButtonDate -> {
                showDateDialog()
            }
            buttonRegister -> {
               registerUser()
            }
            textViewLogin -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val radio: RadioButton = findViewById(radioGroupGender.checkedRadioButtonId)
        gender = radio.text.toString()
    }


    private fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != ""){
            Toast.makeText(this,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErrorGetRegisterUser(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetRegisterUser(email : String) {
        val resultIntent = Intent()
        resultIntent.putExtra("Email", email)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun  registerUser(){
        if (editTextPasswordRegister.text.toString() != editTextRepasswordRegister.text.toString()){
            Toast.makeText(this, "Kata Sandi tidak sesuai", Toast.LENGTH_SHORT).show()
        }else{
            SendGetRegisterUser.newBuilder()
                .setUserData(editTextFullNameRegister.text.toString(),
                    AESCrypt.encrypt(editTextPasswordRegister.text.toString()),
                    editTextPhoneNumberRegister.text.toString(), editTextAddressRegister.text.toString(),
                    editTextEmailRegister.text.toString(),
                    800000000f,
                    gender, editTextDateofBirth.text.toString())
                .setNetworkConfig(networkConfig)
                .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
                .setOnSendGetRegisterUserListener(this)
                .send()
        }
    }

    private fun showDateDialog() {
        val newCalendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, dayOfMonth, monthOfYear, year ->

                val newDate = Calendar.getInstance()
                newDate.set(dayOfMonth, monthOfYear, year)
                editTextDateofBirth.setText(dateFormatter!!.format(newDate.time))
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog!!.show()
    }
}
