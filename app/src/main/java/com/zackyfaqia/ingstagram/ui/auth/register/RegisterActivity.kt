package com.zackyfaqia.ingstagram.ui.auth.register

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
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.databinding.ActivityRegisterBinding
import com.zackyfaqia.ingstagram.ui.auth.login.LoginActivity
import com.zackyfaqia.ingstagram.util.ViewModelFactory
import java.lang.ref.WeakReference

var weakReference: WeakReference<ActivityRegisterBinding>? = null
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        registerViewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerViewModel.isError.observe(this) {
            isError = it
        }

        registerViewModel.message.observe(this) {
            AlertDialog.Builder(this@RegisterActivity).apply {
                setTitle(getString(R.string.app_name))
                setMessage(R.string.regist_failed)
                setPositiveButton("OK") { _, _ -> if (!isError) finish() }
                create()
                show()
            }
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.edtRegistUsername.text.toString()
            val email = binding.edtRegistEmail.text.toString()
            val password = binding.edtRegistPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edtRegistUsername.error = getString(R.string.username_hint)
                }
                email.isEmpty() -> {
                    binding.edtRegistEmail.error = getString(R.string.email_hint)
                }
                password.isEmpty() -> {
                    binding.edtRegistPassword.error = getString(R.string.password_hint)
                }
                else -> {
                    registerViewModel.register(name, email, password)
                    registerViewModel.isError.observe(this) {
                        if (!it) successAlert()
                    }
                }
            }
        }

        binding.btnGotoLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    fun successAlert() {
        AlertDialog.Builder(this@RegisterActivity).apply {
            setTitle(getString(R.string.app_name))
            setMessage(R.string.regist_success)
            setPositiveButton("OK") { _, _ -> finish() }
            create()
            show()
        }
    }

    companion object {
        var isError = false
        fun isErrorPassword(isError: Boolean) {
            val binding = weakReference?.get()
            binding?.edtLayoutRegistPassword?.isEndIconVisible = !isError
            binding?.btnRegister?.isEnabled = !isError
        }

    }
}