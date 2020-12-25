import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public final class ChatServer {

    static final int PORT = 8080;

    public static void disableWarning() {
        System.err.close();
        System.setErr(System.out);
    }

    public static void main(String[] args) throws Exception {
        disableWarning();

        //Обработка операций ввода/вывода
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)// Использование nio для новых соединений
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel sh) {
                            ChannelPipeline cp = sh.pipeline(); // Обработка событий

                            cp.addLast(new StringDecoder());
                            cp.addLast(new StringEncoder());
                            cp.addLast(new ChatServerHandler());
                        }
                    });

            // Запуск сервера
            ChannelFuture server = sb.bind(PORT).sync();
            System.out.println("Сервер запущен!");

            //Ожидание закрытия сервера
            server.channel().closeFuture().sync();
        } finally {
            // Закрытие групп
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}