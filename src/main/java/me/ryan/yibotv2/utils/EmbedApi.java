package me.ryan.yibotv2.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

@AllArgsConstructor
public enum EmbedApi {

    SUCCESS(Color.blue), WARNING(Color.yellow), ERROR(Color.red);

    @Getter
    private final Color color;

    public MessageEmbed getMessage(String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(description);
        switch (this) {
            case SUCCESS:
                embedBuilder.setColor(SUCCESS.getColor());
                break;
            case WARNING:
                embedBuilder.setColor(WARNING.getColor());
                break;
            case ERROR:
                embedBuilder.setColor(ERROR.getColor());
                break;
        }
        return embedBuilder.build();
    }

    public MessageEmbed getMessage(String title, String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!title.isEmpty())
            embedBuilder.setTitle(title);
        if (!description.isEmpty())
            embedBuilder.setDescription(description);
        switch (this) {
            case SUCCESS:
                embedBuilder.setColor(SUCCESS.getColor());
                break;
            case WARNING:
                embedBuilder.setColor(WARNING.getColor());
                break;
            case ERROR:
                embedBuilder.setColor(ERROR.getColor());
                break;
        }
        return embedBuilder.build();
    }


    public EmbedBuilder getEmbed(String title, String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!title.isEmpty())
            embedBuilder.setTitle(title);
        if (!description.isEmpty())
            embedBuilder.setDescription(description);
        switch (this) {
            case SUCCESS:
                embedBuilder.setColor(SUCCESS.getColor());
                break;
            case WARNING:
                embedBuilder.setColor(WARNING.getColor());
                break;
            case ERROR:
                embedBuilder.setColor(ERROR.getColor());
                break;
        }
        return embedBuilder;
    }

}