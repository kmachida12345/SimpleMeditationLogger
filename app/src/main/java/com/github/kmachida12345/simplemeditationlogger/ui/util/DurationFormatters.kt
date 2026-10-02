package com.github.kmachida12345.simplemeditationlogger.ui.util

/** "3:05" 形式(分:秒2桁)で表示する */
fun formatMinutesSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

/** 秒の端数がなければ "3"、あれば "3:05" 形式で表示する */
fun formatMinutesOrMinutesSeconds(totalSeconds: Int): String {
    return if (totalSeconds % 60 == 0) {
        "${totalSeconds / 60}"
    } else {
        formatMinutesSeconds(totalSeconds)
    }
}

/** 秒の端数がなければ "3 min"、あれば "3:05" 形式で表示する */
fun formatMinutesWithSuffixOrMinutesSeconds(totalSeconds: Int): String {
    return if (totalSeconds % 60 == 0) {
        "${totalSeconds / 60} min"
    } else {
        formatMinutesSeconds(totalSeconds)
    }
}
