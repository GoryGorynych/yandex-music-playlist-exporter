package com.phpusr.yandexmusic.playlistexporter

import com.phpusr.yandexmusic.playlistexporter.parser.JsonPlaylistParser

fun main(args: Array<String>) {
    if (args.size < 2) {
        System.err.println("> Wrong args number, found: ${args.size}, but must be: 2")
        System.err.println(" - args: ${args.joinToString(separator = ", ")}")
        return
    }

    val username = args[0]
    val playlistId = args[1].toInt()
    var limit = 0;
    if (args.size > 2) {
        limit = args[2].toInt();
    }

    if (limit > 0) {
        JsonPlaylistParser.parseByChunk(username, playlistId, limit)
    } else {
        JsonPlaylistParser.parse(username, playlistId)
    }
}