package com.bluelay.damda

import java.io.Serializable

data class MemoInfo(val id: Int, val type: String?, val wdate: String, val color: Int, val lock: Int) : Serializable {
}