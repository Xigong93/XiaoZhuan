package apk.dispatcher.page.version

import apk.dispatcher.RetrofitFactory
import retrofit2.http.GET
import retrofit2.http.Path

fun GithubApi(): GithubApi {
    return RetrofitFactory.create("https://api.github.com/")
}

interface GithubApi {

    @GET("repos/{user}/{repo}/releases/latest")
    fun getLastRelease(
        @Path("user") user: String,
        @Path("repo") repo: String
    ): GithubRelease
}