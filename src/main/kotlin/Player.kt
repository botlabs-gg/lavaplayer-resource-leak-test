package gg.botlabs

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState

class Player(val track: AudioTrack, apm: DefaultAudioPlayerManager, val isRepeating: Boolean = true) : AudioEventAdapter() {
    private val player: AudioPlayer = apm.createPlayer()
    val consumer = AudioConsumer(this)

    init {
        player.addListener(this)
        player.playTrack(track)
        consumer.start()
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        println("Track ended: $endReason")
        if (endReason == AudioTrackEndReason.STOPPED) return
        if (isRepeating) player.playTrack(track.makeClone())
    }

    fun tryProvide(): Boolean {
        val state: AudioTrackState
        try {
            state = player.playingTrack.state
        } catch (ex: NullPointerException) {
            return false
        }

        if (state == AudioTrackState.LOADING || state == AudioTrackState.INACTIVE) {
            return true
        }

        return player.provide() != null
    }

    fun destroy() {
        player.destroy()
        consumer.interrupt()
    }
}