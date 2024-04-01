package gg.botlabs

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack


@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class PlaylistLoader(val apm: AudioPlayerManager) : AudioLoadResultHandler {
    private var exception: RuntimeException? = null
    private val results: MutableList<AudioTrack> = mutableListOf()

    fun loadTracksSync(identifier: String): List<AudioTrack>? {
        apm.loadItem(identifier, this)

        try {
            synchronized(this) {
                (this as Object).wait()
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        exception?.let { throw it }

        return results
    }

    override fun trackLoaded(track: AudioTrack) {
        results.add(track)
        synchronized(this) {
            (this as Object).notify()
        }
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        results.addAll(playlist.tracks)
        synchronized(this) {
            (this as Object).notify()
        }
    }

    override fun noMatches() {
        exception = RuntimeException("No matches")
        synchronized(this) {
            (this as Object).notify()
        }
    }

    override fun loadFailed(exception: FriendlyException) {
        this.exception = exception
        synchronized(this) {
            (this as Object).notify()
        }
    }
}