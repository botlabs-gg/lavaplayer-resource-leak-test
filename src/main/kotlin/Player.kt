package gg.botlabs

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import com.sun.org.slf4j.internal.LoggerFactory


class Player(val track: AudioTrack, val apm: DefaultAudioPlayerManager) : AudioEventAdapter() {
    private val player: AudioPlayer = apm.createPlayer()

    init {
        player.addListener(this)
        player.playTrack(track)

        val consumer = AudioConsumer(this)
        consumer.start()
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        player.playTrack(track)
    }

    fun canProvide(): Boolean {
        val state: AudioTrackState
        try {
            state = player.playingTrack.state
        } catch (ex: NullPointerException) {
            player.playTrack(track)
            return false
        }

        if (state == AudioTrackState.LOADING || state == AudioTrackState.INACTIVE) {
            return true
        }

        return player.provide() != null
    }

    companion object {
        private val log = LoggerFactory.getLogger(Player::class.java)
    }
}