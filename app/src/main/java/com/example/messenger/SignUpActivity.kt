@file:Suppress("DEPRECATION")

package com.example.messenger

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.messenger.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var pd: ProgressDialog
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        pd = ProgressDialog(this)
        binding.signUpTextToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.signUpBtn.setOnClickListener {

            name = binding.signUpEtName.text.toString()
            email = binding.signUpEmail.text.toString()
            password = binding.signUpPassword.text.toString()
            val emailPattern = Regex(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
            )

            if (binding.signUpEtName.text.isEmpty()) {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (binding.signUpEmail.text.isEmpty()) {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            } else if (!emailPattern.matches(binding.signUpEmail.text.toString())) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            } else if (binding.signUpPassword.text.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must meet the requirements", Toast.LENGTH_SHORT).show()
            } else {
                createAnAccount(name, password, email)
            }
        }
    }

    internal fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    private fun createAnAccount(name: String, password: String, email: String) {

        pd.show()
        pd.setMessage("Registering User")

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = auth.currentUser

                val dataHashMap = hashMapOf(
                    "userid" to user!!.uid,
                    "username" to name,
                    "useremail" to email,
                    "status" to "default",
                    "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"
                )
                firestore.collection("Users").document(user.uid).set(dataHashMap)

                pd.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))

            }
        }
    }
}