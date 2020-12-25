import java.util.Scanner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.*;

public class ChatClient {
    static Scanner sc = new Scanner(System.in);

    static final String HOST = "127.0.0.1";
    static final int PORT = 8080;

    public static void disableWarning() {
        System.err.close();
        System.setErr(System.out);
    }

    public static void main(String[] args) throws Exception {
        disableWarning();

        Scanner sc = new Scanner(System.in);

        //Обработка операций ввода/вывода
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch){
                            ChannelPipeline cp = ch.pipeline();// Обработка событий

                            cp.addLast(new StringEncoder());
                            cp.addLast(new StringDecoder());
                            cp.addLast(new ChatClientHandler());
                        }
                    });

            // Запуск клиента
            ChannelFuture client = b.connect(HOST, PORT).sync();

            //Отправка сообщения на сервер
            while (sc.hasNext()) {
                String input = sc.nextLine();
                Channel channel = client.sync().channel();
                channel.writeAndFlush(input);
                channel.flush();
            }

            client.channel().closeFuture().sync();
        } finally {
            //Закрытие группы
            group.shutdownGracefully();
        }
    }
}