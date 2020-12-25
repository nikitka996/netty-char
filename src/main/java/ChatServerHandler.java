import java.util.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.*;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    String username;
    boolean registered = false;

    static final Set<String> users = new TreeSet<>();
    static final List<String> messages = new ArrayList<>();
    static final List<Channel> channels = new ArrayList<>();
    private Map<Channel, String> usersMap = new HashMap<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ChannelFuture cf = ctx.writeAndFlush("Enter username: ");
        if (!cf.isSuccess()) {
            System.out.println("failed: " + cf.cause());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        users.remove(username);
        channels.remove(ctx.channel());
        sendToAllClients("User " + username
                + " has left the chat");
    }

    private void sendToAllClients(String message) {
        messages.add(message);
        for (Channel c : channels) {
            c.writeAndFlush(message);
        }
    }

    private void sendToAllClientsExceptCurrent(String message, String name) {
        messages.add(message);
        for (Channel c : channels) {
            if (usersMap.get(c) != name) {
                c.writeAndFlush(message);
            }
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext chc, String s) {
        s = s.trim();
        System.out.println("received: " + s);
        if (!registered) {
            if (users.contains(s)) {
                chc.writeAndFlush("Failed. Try input another name: ");
            } else {
                Channel channel = chc.channel();
                channels.add(channel);
                usersMap.put(channel, s);
                users.add(s);
                registered = true;
                username = s;
                for (String m : messages) {
                    chc.writeAndFlush(m);
                }
                sendToAllClients(s + " is in the chat!/n");
            }
        } else {
            String m = username + ": " + s;
            sendToAllClientsExceptCurrent(m, username);
        }
    }
}
