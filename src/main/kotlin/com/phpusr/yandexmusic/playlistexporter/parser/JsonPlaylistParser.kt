package com.phpusr.yandexmusic.playlistexporter.parser

import com.google.gson.Gson
import com.phpusr.yandexmusic.playlistexporter.dto.Out
import com.phpusr.yandexmusic.playlistexporter.dto.Playlist
import com.phpusr.yandexmusic.playlistexporter.dto.Track
import java.io.File
import java.net.URL

/**
 * Download playlist from Yandex.Music and parse it into txt file
 */
object JsonPlaylistParser {
    private const val TuneMyMusicUrl = "https://www.tunemymusic.com/ru/"
    private const val PlaylistURL = "https://music.yandex.ru/handlers/playlist.jsx"
    private const val PlaylistsDirName = "playlists"

    fun parse(username: String, playlistId: Int) {
        val playlist = getPlaylist(username, playlistId);

        println("> Found ${playlist.tracks.size} tracks in playlist \"$username - ${playlist.title}\"")

        File(PlaylistsDirName).mkdir()
        writeFile(0, playlist.tracks.size, playlist.tracks, username, playlist.title)

    }

    fun parseByChunk(username: String, playlistId: Int, limit: Int) {
        val playlist = getPlaylist(username, playlistId);

        println("> Found ${playlist.tracks.size} tracks in playlist \"$username - ${playlist.title}\"")

        File(PlaylistsDirName).mkdir()

        val chunkedTracks = playlist.tracks.chunked(limit);
        var from = 0;
        var to = 0;
        for (chunk in chunkedTracks) {
            to += chunk.size
            writeFile(from, to, chunk, username, playlist.title)
            from += chunk.size;
        }
    }

    fun writeFile(from: Int, to: Int, tracks: List<Track>, username: String, playlistTitle: String) {
        val outFileName = "$username - ${playlistTitle} $from-$to.txt"
        val outTxtFile = File("$PlaylistsDirName/$outFileName")
        outTxtFile.delete()
        tracks.forEach { track ->
            outTxtFile.appendText("${track.artists.joinToString{ it.name }} - ${track.title}\n")
        }

        println(" - Go to: $TuneMyMusicUrl, select option \"File\" and import the file: \"${outTxtFile.absoluteFile}\"")
    }

    fun getPlaylist(username: String, playlistId: Int): Playlist {
        val json = URL("$PlaylistURL?owner=$username&kinds=$playlistId").readText()
        val out = Gson().fromJson(json, Out::class.java)
        return out.playlist;
    }
}
