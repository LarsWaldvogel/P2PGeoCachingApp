package com.example.p2pgeocaching.data

import java.io.Serializable

@kotlinx.serialization.Serializable
data class CacheListData(val dataList: List<CacheData>) : Serializable