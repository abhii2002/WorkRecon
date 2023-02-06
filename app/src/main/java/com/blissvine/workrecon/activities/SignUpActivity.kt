package com.blissvine.workrecon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.blissvine.workrecon.Firebase.FirestoreClass
import com.blissvine.workrecon.R
import com.blissvine.workrecon.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.regex.Pattern

class SignUpActivity : BaseActivity() {
    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sign_up)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_sign_up.setOnClickListener {
            registerUser()
        }

    }

     fun userRegisteredSuccess(){
          Toast.makeText(this, "You have successfully registered ", Toast.LENGTH_LONG).show()
         hideProgressDialog()

       // FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow)
        }

                  toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // TODO (Step 9: A function to register a new user to the app.)
    // START
    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser(){
        val name: String = et_name.text.toString().trim{it <= ' '}
        val email: String = et_email.text.toString().trim{it <= ' '}
        val password: String = et_password.text.toString().trim{it <= ' '}

        if(validateForm(name, email, password) && validateEmail(email) && validatePassword(password)){
            showProgressDialog(resources.getString(R.string.please_wait))
           FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!! // getting the registered email
                    val user = User(firebaseUser.uid, name, registeredEmail)

                    FirestoreClass().registerUser(this, user)
                } else {
                    hideProgressDialog()
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // END

    // TODO (Step 10: A function to validate the entries of a new user.)
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String,
                             password: String): Boolean{
        return when{
            TextUtils.isEmpty(name)->{
               showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email adress")
                false
            }
            TextUtils.isEmpty(password)-> {
                 showErrorSnackBar("Please enter a password")
                 false
            }else ->{
                true
            }
        }
    }
    // END

    private fun validateEmail(email:String): Boolean {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorSnackBar("Invalid Email")
            return false
        }else{
            return  true
        }
    }

    private fun validatePassword(password: String): Boolean{
      if(password.length < 8 ){
           showErrorSnackBar("Minimum 8 Character Password")
       return false
    }
    if(!password.matches(".*[A-Z].*".toRegex())) {
        showErrorSnackBar("Password Must Contain 1 Upper-case Character")
      return  false
    }
        if(!password.matches(".*[a-z].*".toRegex())){
            showErrorSnackBar("Password Must Contain 1 Lower-case Character")
           return false
        }
        if(!password.matches(".*[@#/$%^&+=].*".toRegex())){
            showErrorSnackBar("Password Must Contain 1  Special Character (@#/\$%^&+=)")
            return false
        }
        else{
            return  true
        }

    }
}