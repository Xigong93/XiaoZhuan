package apk.dispatcher.page.version

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppVersionVM : ViewModel() {

    fun getLastVersion() {
        viewModelScope.launch {
            try {
                val release = GithubApi().getLastRelease("Xigong93", "ApkDispatcher")
                release.toAppVersion()
            } catch (e: Exception) {

            }
        }
    }
}