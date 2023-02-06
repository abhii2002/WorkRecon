package com.blissvine.workrecon.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.blissvine.workrecon.Firebase.FirestoreClass
import com.blissvine.workrecon.R
import com.blissvine.workrecon.models.User
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_sign_in.*



class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }

    }


   private fun setUpActionBar(){

       setSupportActionBar(tool_bar_sign_in_activity)

       val actionBar = supportActionBar
       if(actionBar != null){
           actionBar.setDisplayHomeAsUpEnabled(true)
           actionBar.setHomeAsUpIndicator(R.drawable.back_arrow)

       }
       tool_bar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser() {
        val email: String = et_sign_in_email.text.toString().trim { it <= ' ' }
        val password: String = et_sign_in_password.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // TODO (Step 2: Remove the toast message and call the FirestoreClass signInUser function to get the data of user from database. And also move the code of hiding Progress Dialog and Launching MainActivity to Success function.)
                    // Calling the FirestoreClass signInUser function to get the data of user from database.
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                       FirestoreClass().loadUserData(this@SignInActivity)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this, task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        hideProgressDialog()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean{
          return when {
              TextUtils.isEmpty(email) ->{
                  showErrorSnackBar("Please enter an email address")
                   false
              }
              TextUtils.isEmpty(password) ->{
                  showErrorSnackBar("Please enter a password")
                  false
              }
              else -> {
                  true
              }
          }
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}