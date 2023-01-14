package com.admarkov.grishanya;

import com.admarkov.grishanya.bot.Bot;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;

import java.util.concurrent.TimeUnit;

public class Main {

    private final static int GROUP_ID = Integer.parseInt(System.getenv("GROUP_ID"));
    private final static String ACCESS_TOKEN = System.getenv("BOT_TOKEN");

    public static void main(String[] args) {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);

        GetLongPollServerResponse longPollServer;
        try {
            longPollServer = vk.groups().getLongPollServer(new GroupActor(GROUP_ID, ACCESS_TOKEN), GROUP_ID).execute();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        Bot bot = new Bot();
        String ts = longPollServer.getTs();
        while (true) {
            GetLongPollEventsResponse longpollEvents;
            try {
                longpollEvents = vk.longPoll().getEvents(
                        longPollServer.getServer(),
                        longPollServer.getKey(),
                        ts
                ).execute();
                if (!longpollEvents.getUpdates().isEmpty()) {
                    ts = longpollEvents.getTs();
                    for (JsonObject update: longpollEvents.getUpdates()) {
                        bot.handleUpdate(update);
                    }
                }
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
