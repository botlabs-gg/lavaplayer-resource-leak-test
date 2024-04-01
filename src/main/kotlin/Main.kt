package gg.botlabs

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers

object Main

fun main() {
    println("Press enter to start")
    System.`in`.read()

    val identifier = "https://soundcloud.com/zimmermusic/zimmer-summer-2023-tape"
    val apm = DefaultAudioPlayerManager()
    AudioSourceManagers.registerRemoteSources(apm)
    val track = PlaylistLoader(apm).loadTracksSync(identifier).first()

    val players = mutableListOf<Player>()
    repeat(200) {
        Thread.sleep(10)
        println(it)
        players.add(Player(track.makeClone(), apm, false))
    }

    Thread.sleep(5000)
    players.forEach {
        it.destroy()
    }

    println("Players destroyed")
    Thread.sleep(Long.MAX_VALUE)
}