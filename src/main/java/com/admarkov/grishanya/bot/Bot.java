package com.admarkov.grishanya.bot;

import com.admarkov.grishanya.radomlunch.Option;
import com.admarkov.grishanya.radomlunch.RandomLunch;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

public class Bot {
    final VkApiClient vk;
    final GroupActor GROUP_ACTOR;
    final RandomLunch randomLunch;

    public Bot() {
        TransportClient transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);
        GROUP_ACTOR = new GroupActor(Integer.parseInt(System.getenv("GROUP_ID")), System.getenv("BOT_TOKEN"));
        randomLunch = new RandomLunch();
    }
    public void handleUpdate(JsonObject update) {
        switch (update.get("type").getAsString()) {
            case "message_new":
                JsonObject message = update.get("object").getAsJsonObject().get("message").getAsJsonObject();
                handleNewMessage(message);
                break;
            default:
                System.out.println("unknown event type");
        }
    }

    private void handleNewMessage(JsonObject message) {
        System.out.println(message);
        String text = message.get("text").getAsString();
        String[] splitted = text.split(" ");
        String command;
        String[] params;
        if (splitted[0] == "grishanya__cat") {
            command = splitted[1];
            params = Arrays.copyOfRange(splitted, 2, splitted.length);
        } else {
            command = splitted[0];
            params = Arrays.copyOfRange(splitted, 1, splitted.length);
        }
        String response = "";
        switch (command) {
            case "голосование":
                randomLunch.clearVotes();
                try {
                    vk.messages().send(GROUP_ACTOR)
                            .attachment("audio43516452_456239854")
                            .peerId(message.get("peer_id").getAsInt())
                            .randomId(Random.from(RandomGenerator.getDefault()).nextInt())
                            .execute();
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {
                    System.out.println(e);
                }
                response = "Счетчики голосов сброшены";
                break;
            case "голосую":
                String vote = params[0];
                for (Option option: randomLunch.getOptions()) {
                    if (option.getName().equals(vote)) {
                        System.out.println(vote);
                        option.addVote();
                    }
                }
            case "места":
                response = "Список мест для обеда:\n";
                for (Option option: randomLunch.getOptions()) {
                    response += "* " + option.getName() + " (" + option.getVotes() + ")" + "\n";
                }
                break;
            case "обед":
                try {
                    vk.messages().send(GROUP_ACTOR)
                            .message("и победитель....")
                            .peerId(message.get("peer_id").getAsInt())
                            .randomId(Random.from(RandomGenerator.getDefault()).nextInt())
                            .execute();
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {
                    System.out.println(e);
                }
                response = randomLunch.selectRandomWeightedWithVotes().getName();
                randomLunch.clearVotes();
                break;
            default:
                response = "Список команд:\n* обед -- случайно выбрать место для обеда взвешенно по количеству голосов\n*места -- список мест для обеда\n*голосование -- начать голосование\n*голос <название места> -- проголосовать";
        }
        // костыльный фикс ошибки апи
        if (!command.equals("голосование")) {
            try {
                vk.messages().send(GROUP_ACTOR)
                        .message(response)
                        .peerId(message.get("peer_id").getAsInt())
                        .randomId(Random.from(RandomGenerator.getDefault()).nextInt())
                        .execute();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
