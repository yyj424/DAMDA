package com.bluelay.damda

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.*

class MovieXmlParser {
    enum class TagType {
        NONE, TITLE, PUB_DATE, IMAGE
    }

    fun parse(xml: String?): ArrayList<Movie> {
        val resultList = arrayListOf<Movie>()
        var dto: Movie? = null
        var tagType = TagType.NONE
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG ->  if (parser.name == "item") {
                        dto = Movie()
                    } else if (parser.name == "title") {
                        if (dto != null) tagType = TagType.TITLE
                    } else if (parser.name == "pubDate") {
                        if (dto != null) tagType = TagType.PUB_DATE
                    } else if (parser.name == "image") {
                        if (dto != null) tagType = TagType.IMAGE
                    }
                    XmlPullParser.END_TAG -> if (parser.name == "item") {
                        resultList.add(dto!!)
                        dto = null
                    }
                    XmlPullParser.TEXT -> {
                        when (tagType) {
                            TagType.TITLE -> dto!!.title = parser.text
                            TagType.PUB_DATE -> dto!!.date = parser.text
                            TagType.IMAGE -> dto!!.image = parser.text
                        }
                        tagType = TagType.NONE
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultList
    }
}