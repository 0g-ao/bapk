
package vn.io.ao.g0

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.GestureDetector
import android.widget.Toast
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import vn.io.ao.g0.databinding.ActivityMainBinding
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import kotlin.math.abs

class MainActivity : AppCompatActivity(), UrlEntryDialogFragment.UrlEntryListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var geckoView: GeckoView
    private lateinit var geckoSession: GeckoSession
    private var geckoRuntime: GeckoRuntime? = null
    private var canGoBack: Boolean = false
    private var currentUrl: String = "https://google.com"

    private lateinit var fabGestureDetector: GestureDetectorCompat

    private var dX: Float = 0f
    private var dY: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved theme mode early to ensure proper theming
        val prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE)
        val savedThemeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        geckoView = binding.geckoView
        geckoSession = GeckoSession()
        geckoRuntime = GeckoRuntime.create(this).also {
            geckoSession.open(it)
        }

        geckoView.setSession(geckoSession)
        
        val sharedPrefs = getSharedPreferences("browser_prefs", MODE_PRIVATE)
        val lastUrl = sharedPrefs.getString("last_url", "https://google.com") ?: "https://google.com"
        geckoSession.loadUri(lastUrl)

        setupDelegates()
        setupFab()
    }



    private fun setupDelegates() {
        geckoSession.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = 0
                currentUrl = url
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                binding.progressBar.progress = progress
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                binding.progressBar.visibility = View.GONE
            }
        }

        geckoSession.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                this@MainActivity.canGoBack = canGoBack
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFab() {
        // Trình phát hiện cử chỉ để phân biệt giữa nhấn nhanh và nhấn giữ
        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // Nhấn nhanh: Hiển thị hộp thoại URL
                UrlEntryDialogFragment().show(supportFragmentManager, "UrlEntryDialog")
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                // Nhấn giữ: Hiển thị menu bật lên
                showPopupMenu()
            }
        }
        fabGestureDetector = GestureDetectorCompat(this, gestureListener)

        // Trình lắng nghe cảm ứng để xử lý kéo
        binding.fab.setOnTouchListener { view, event ->
            // Chuyển sự kiện cảm ứng cho trình phát hiện cử chỉ
            fabGestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    // Giới hạn FAB trong màn hình
                    val parent = view.parent as View
                    view.x = newX.coerceIn(0f, (parent.width - view.width).toFloat())
                    view.y = newY.coerceIn(0f, (parent.height - view.height).toFloat())
                }
            }
            // Trả về true để cho biết chúng ta đã xử lý sự kiện cảm ứng
            true
        }
    }
    
    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.fab)
        popupMenu.menuInflater.inflate(R.menu.fab_menu, popupMenu.menu)

        // Vô hiệu hóa nút 'Back' nếu không thể quay lại
        popupMenu.menu.findItem(R.id.action_back).isEnabled = canGoBack

        // Update theme menu title to show current mode
        val themeItem = popupMenu.menu.findItem(R.id.action_toggle_dark_mode)
        val prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE)
        val currentMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val modeTitleRes = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.string.theme_system
            AppCompatDelegate.MODE_NIGHT_NO -> R.string.theme_light
            AppCompatDelegate.MODE_NIGHT_YES -> R.string.theme_dark
            else -> R.string.theme_system
        }
        themeItem.title = getString(R.string.theme_mode_label, getString(modeTitleRes))

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_back -> {
                    geckoSession.goBack()
                    true
                }
                R.id.action_reload -> {
                    geckoSession.reload()
                    true
                }
                R.id.action_toggle_dark_mode -> {
                    val newMode = toggleDarkMode()
                    val newTitleRes = when (newMode) {
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.string.theme_system
                        AppCompatDelegate.MODE_NIGHT_NO -> R.string.theme_light
                        AppCompatDelegate.MODE_NIGHT_YES -> R.string.theme_dark
                        else -> R.string.theme_system
                    }
                    Toast.makeText(this, getString(R.string.theme_mode_label, getString(newTitleRes)), Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_about -> {
                    AboutDialogFragment().show(supportFragmentManager, "AboutDialog")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun toggleDarkMode(): Int {
        val prefs = getSharedPreferences("browser_prefs", MODE_PRIVATE)
        val currentMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val newMode = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        prefs.edit().putInt("theme_mode", newMode).apply()
        AppCompatDelegate.setDefaultNightMode(newMode)
        return newMode
    }

    override fun onUrlEntered(url: String) {
        var finalUrl = url.trim()
        if (finalUrl.isNotEmpty()) {
            if (!finalUrl.startsWith("https://") && !finalUrl.startsWith("http://")) {
                finalUrl = "https://$finalUrl"
            }
            geckoSession.loadUri(finalUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPrefs = getSharedPreferences("browser_prefs", MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("last_url", currentUrl)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        geckoSession.close()
        geckoRuntime?.shutdown()
    }
}
