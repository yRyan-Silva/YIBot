package me.ryan.yibotv2.music;

import me.ryan.yibotv2.utils.Registry;

public class PlayerRegistry extends Registry<GuildMusicManager> {

    public GuildMusicManager get(long guildId) {
        return get($ -> $.getGuildId() == guildId);
    }

}
