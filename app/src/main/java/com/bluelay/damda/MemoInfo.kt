package com.bluelay.damda

import java.io.Serializable

data class MemoInfo(val id: Int, val type: String?, val wdate: Long, val color: Int, val lock: Int, val bkmr : Int, var check : Boolean, var title : String, var isExpanded: Boolean = false) : Serializable