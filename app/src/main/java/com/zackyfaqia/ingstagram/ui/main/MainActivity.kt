package com.zackyfaqia.ingstagram.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zackyfaqia.ingstagram.R
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.databinding.ActivityMainBinding
import com.zackyfaqia.ingstagram.ui.add.AddStoryActivity
import com.zackyfaqia.ingstagram.ui.auth.login.LoginActivity
import com.zackyfaqia.ingstagram.ui.maps.MapsActivity
import com.zackyfaqia.ingstagram.util.ViewModelFactory
import com.zackyfaqia.ingstagram.util.ViewModelFactoryPaging

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var listPagingViewModel: ListPagingViewModel
    private lateinit var listStoryPagingAdapter: ListStoriesPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        listStoryPagingAdapter = ListStoriesPagingAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = listStoryPagingAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryPagingAdapter.retry()
            }
        )

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    // custom action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                finish()
                return true
            }
            R.id.action_map -> startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
        return true
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                AddStoryActivity.TOKEN = user.token
                listPagingViewModel = ViewModelProvider(
                    this,
                    ViewModelFactoryPaging(user.token)
                )[ListPagingViewModel::class.java]
                listPagingViewModel.list.observe(this, {
                    listStoryPagingAdapter.submitData(lifecycle, it)
                })
                listStoryPagingAdapter.refresh()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}