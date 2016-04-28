package cwh.utils.socket;

/**
 * Created by cwh on 16-4-26
 */

import cwh.utils.log.VSLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private static String TAG = "SocketServer";
    // 定义常数
    private final static int DEFAULT_MAX_BACKLOG = 5;// 默认等待队列长度

    public static String listenPort(int port)// 线程的主办法
    {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            int backlog = DEFAULT_MAX_BACKLOG;
            serverSocket = new ServerSocket(port, backlog);// 创建ServerSocket
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (serverSocket != null) {
                clientSocket = serverSocket.accept();// 监听客户端，等待连接请求
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;
        String content = "";
        try {
            if (clientSocket != null) {
                in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                String input;
                while ((input = in.readLine()) != null) {
                    content += input;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {// 关闭所有流
            try {
                if (in != null)
                    in.close();
                if (clientSocket != null)
                    clientSocket.close();
                if (serverSocket != null)
                    serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }


    public static void main(String[] args) {
        VSLog.d(TAG, listenPort(8887));
    }
}
