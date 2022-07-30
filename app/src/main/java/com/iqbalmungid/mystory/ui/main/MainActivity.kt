package com.iqbalmungid.mystory.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.iqbalmungid.mystory.R
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.data.remote.response.Stories
import com.iqbalmungid.mystory.databinding.ActivityMainBinding
import com.iqbalmungid.mystory.ui.detailstory.DetailStoryActivity
import com.iqbalmungid.mystory.ui.maps.StoryMaps
import com.iqbalmungid.mystory.ui.poststory.PostStoryActivity
import com.iqbalmungid.mystory.ui.poststory.PostViewModel
import com.iqbalmungid.mystory.ui.signin.SignInActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewMainModel: MainViewModel
    private lateinit var adapter: ListStoriesAdapter
    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ListStoriesAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Stories) {
                Intent(this@MainActivity, DetailStoryActivity::class.java).also {
                    it.putExtra(DetailStoryActivity.EX_STORY, data)
                    startActivity(it)
                }
                Toast.makeText(applicationContext, data.name, Toast.LENGTH_SHORT).show()
            }
        })

        binding.apply {
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                rvStories.layoutManager = GridLayoutManager(this@MainActivity, 2)
            } else {
                rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
            }
            rvStories.setHasFixedSize(true)
            rvStories.adapter = adapter
            rvStories.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )

            fabPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostStoryActivity::class.java))
            }
        }

        val pref = AccountPreferences.getInstance(dataStore)
        viewMainModel = ViewModelProvider(this, StoryViewModelFactory.getInstance(this))[MainViewModel::class.java]
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[PostViewModel::class.java]
        viewModel.getUser().observe(this){ account ->
            if (account != null) {
                viewMainModel.getStories("Bearer ${account.token}").observe(this) { stories ->
                    adapter.submitData(lifecycle, stories)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_signout -> {
                viewModel.signout()
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.info))
                    setMessage(getString(R.string.logout_success))
                    setPositiveButton(getString(R.string.next)) { _, _ ->
                        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                        finish()
                    }
                    create()
                    setCancelable(false)
                    show()
                }
            }

            R.id.menu_settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.menu_maps -> {
                startActivity(Intent(this, StoryMaps::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}