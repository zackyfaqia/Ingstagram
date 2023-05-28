package com.zackyfaqia.ingstagram.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.zackyfaqia.ingstagram.R
import com.zackyfaqia.ingstagram.data.model.UserModel
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.databinding.ActivityLoginBinding
import com.zackyfaqia.ingstagram.ui.auth.register.RegisterActivity
import com.zackyfaqia.ingstagram.ui.main.MainActivity
import com.zackyfaqia.ingstagram.util.ViewModelFactory
import java.lang.ref.WeakReference

lateinit var weakReference: WeakReference<ActivityLoginBinding>
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        weakReference = WeakReference(binding)
        setupView()
        setupViewModel()
        setupAction()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this, { user ->
            this.user = user
        })
    }

    private fun setupAction() {
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.message.observe(this) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.app_name))
                setMessage(it)
                setPositiveButton("OK") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.edtLoginEmail.text.toString()
            val password = binding.edtLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edtLoginEmail.error = getString(R.string.email_hint)
                }
                password.isEmpty() -> {
                    binding.edtLoginPassword.error = getString(R.string.password_hint)
                }

                else -> {
                    loginViewModel.auth(email, password)
                }
            }
        }
        binding.btnGotoRegist.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        fun isErrorPassword(isError: Boolean) {
            val binding = weakReference.get()
            binding?.edtLayoutLoginPassword?.isEndIconVisible = !isError
            binding?.btnLogin?.isEnabled = !isError
        }
    }

}