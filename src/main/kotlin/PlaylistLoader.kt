package gg.botlabs

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.concurrent.CountDownLatch

class PlaylistLoader(val apm: AudioPlayerManager) : AudioLoadResultHandler {
    private var exception: RuntimeException? = null
    private val results: MutableList<AudioTrack> = mutableListOf()
    private val latch = CountDownLatch(1)

    fun loadTracksSync(identifier: String): List<AudioTrack>? {
        apm.loadItem(identifier, this)
        latch.await()
        exception?.let { throw it }
        return results
    }

    override fun trackLoaded(track: AudioTrack) {
        results.add(track)
        latch.countDown()
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        results.addAll(playlist.tracks)
        latch.countDown()
    }

    override fun noMatches() {
        exception = RuntimeException("No matches")
        latch.countDown()
    }

    override fun loadFailed(exception: FriendlyException) {
        this.exception = exception
        latch.countDown()
    }
}