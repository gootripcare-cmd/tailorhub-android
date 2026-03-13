package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val pref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val lastRunVersion = pref.getInt("LAST_RUN_VERSION", -1)
        val currentVersion = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            0
        }

        // 1. Check for First Install
        if (lastRunVersion == -1) {
            pref.edit().putInt("LAST_RUN_VERSION", currentVersion).apply()
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }

        // 2. Check for App Update
        var isUpdate = false
        if (currentVersion > lastRunVersion) {
            pref.edit().putInt("LAST_RUN_VERSION", currentVersion).apply()
            isUpdate = true
            // If update, we show LoginActivity (continue to setContentView)
        }

        // 3. Auto-login check (skip if it's an update)
        if (!isUpdate && pref.contains("USER_ID")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)
        
        // Check for backend version updates as usual
        checkAppVersion()

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val usernameInput = etUsername.text.toString().trim()
            val passwordInput = etPassword.text.toString().trim()

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            
            val loginRequest = LoginRequest(
                username = usernameInput,
                password = passwordInput
            )

            RetrofitClient.instance.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnLogin.isEnabled = true
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.status == "success") {
                            val pref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            pref.edit().apply {
                                putInt("USER_ID", loginResponse.userId ?: 1)
                                putString("USER_NAME", loginResponse.fullName ?: "Tailor Master")
                                putString("USER_MOBILE", loginResponse.mobileNumber ?: "")
                                apply()
                            }

                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, loginResponse?.message ?: "Invalid Credentials", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    btnLogin.isEnabled = true
                    Toast.makeText(this@LoginActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun checkAppVersion() {
        RetrofitClient.instance.getAppVersion().enqueue(object : Callback<AppVersionResponse> {
            override fun onResponse(call: Call<AppVersionResponse>, response: Response<AppVersionResponse>) {
                if (isFinishing || isDestroyed) return
                
                if (response.isSuccessful) {
                    val versionInfo = response.body()
                    val latestVersion = versionInfo?.latestVersion ?: "1.0"
                    val forceUpdate = versionInfo?.forceUpdate ?: false
                    val apkUrl = versionInfo?.apkUrl
                    
                    val currentVersion = try {
                        packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0"
                    } catch (e: Exception) {
                        "1.0"
                    }

                    android.util.Log.d("UPDATE_CHECK", "Current: $currentVersion, Latest: $latestVersion, Force: $forceUpdate")

                    if (forceUpdate && currentVersion != latestVersion) {
                        android.util.Log.d("UPDATE_CHECK", "Showing Update Dialog")
                        showUpdateDialog(apkUrl)
                    }
                }
            }
            override fun onFailure(call: Call<AppVersionResponse>, t: Throwable) {
                // Ignore failure for version check
            }
        })
    }

    private fun showUpdateDialog(apkUrl: String?) {
        if (isFinishing || isDestroyed) return
        
        AlertDialog.Builder(this)
            .setTitle("New Update Available")
            .setMessage("Please update the app to the latest version to continue using all features.")
            .setPositiveButton("Update Now") { _, _ ->
                apkUrl?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    startActivity(intent)
                }
            }
            .setNegativeButton("Later", null)
            .setCancelable(true)
            .show()
    }
}