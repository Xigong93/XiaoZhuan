package com.xigong.xiaozhuan.channel

sealed class MarketState {

    data object Loading : MarketState()
    data class Success(val info: MarketInfo) : MarketState()
    data class Error(val exception: Throwable) : MarketState()
}