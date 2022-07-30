package com.iqbalmungid.mystory.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.iqbalmungid.mystory.R
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.databinding.ActivitySignInBinding
import com.iqbalmungid.mystory.helper.ApiCallbackString
import com.iqbalmungid.mystory.helper.isEmailValid
import com.iqbalmungid.mystory.ui.customview.CustomEditText
import com.iqbalmungid.mystory.ui.main.MainActivity
import com.iqbalmungid.mystory.ui.main.ViewModelFactory
import com.iqbalmungid.mystory.ui.signup.SignUpActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
class SignInActivity : AppCompatActivity() {
    private lateinit var viewModel: SignInViewModel
    private lateinit var binding: ActivitySignInBinding
    private lateinit var customEditText: CustomEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AccountPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[SignInViewModel::class.java]

        customEditText = binding.txtEmail
        binding.apply {
            btnSignup.setOnClickListener {
                signup()
            }

            txtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setMyButtonEnable()
                }
                override fun afterTextChanged(s: Editable) {
                    if (!customEditText.isEmailValid(s)){
                        customEditText.showError()
                    }
                }
            })

            txtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setMyButtonEnable()
                }
                override fun afterTextChanged(s: Editable) {
                }
            })

            btnSignin.setOnClickListener {
                signin()
            }
        }
        setMyButtonEnable()
        playAnimation()
    }

    private fun setMyButtonEnable() {
        binding.apply {
            val resultEmail = txtEmail.text
            val resultPass = txtPassword.text

            btnSignin.isEnabled = resultPass != null && resultEmail != null &&
                    txtPassword.text.toString().length >=6 &&
                    isEmailValid(txtEmail.text.toString())
            if (btnSignin.isEnabled) {
                btnSignin.text = getString(R.string.signin)
            }
        }
    }

    private fun signin() {
        binding.apply {
            val email = txtEmail.text.toString()
            val pass = txtPassword.text.toString()
            load(true)

            viewModel.signin(email, pass, object : ApiCallbackString {
                override fun onResponse(state: Boolean, message: String) {
                    process(state, message)
                }
            })
        }
    }

    private fun load(state: Boolean){
        if (state){
            binding.progress.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else {
            binding.progress.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun signup() {
        Intent(this, SignUpActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun process(state: Boolean, message: String) {
        if (state) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.info))
                setMessage(getString(R.string.login_success))
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                load(false)
                setCancelable(false)
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.info))
                setMessage("${getString(R.string.login_failed)}, $message")
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    binding.progress.visibility = View.GONE
                }
                load(false)
                setCancelable(false)
                create()
                show()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome = ObjectAnimator.ofFloat(binding.txtWelcome, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.txtMsg, View.ALPHA, 1f).setDuration(500)
        val txtEmail = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val txtPass = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)
        val btnSignin = ObjectAnimator.ofFloat(binding.btnSignin, View.ALPHA, 1f).setDuration(500)
        val txtOr = ObjectAnimator.ofFloat(binding.rlOr, View.ALPHA, 1f).setDuration(500)
        val btnSignup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(welcome, message, txtEmail, txtPass, btnSignin, txtOr, btnSignup)
            startDelay = 500
        }.start()
    }
}