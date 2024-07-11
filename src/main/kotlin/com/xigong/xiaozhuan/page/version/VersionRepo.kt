package com.xigong.xiaozhuan.page.version

interface VersionRepo {

    suspend fun getLastVersion(): AppVersion

    companion object : VersionRepo by GitHubRepo
}

private object GitHubRepo : VersionRepo {
    override suspend fun getLastVersion(): AppVersion {
        val release = GithubApi().getLastRelease("Xigong93", "XiaoZhuan")
        return release.toAppVersion()
    }
}

private object MockRepo : VersionRepo {
    override suspend fun getLastVersion(): AppVersion {
        return AppVersion(Long.MAX_VALUE, "2.0,0", "修复了一大堆bug")
    }

}