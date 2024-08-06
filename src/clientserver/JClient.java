package clientserver;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class JClient extends Thread {
    private static final int SERVER_PORT = 8189;// номер канала для связи с сервером
    private static final String LOCAL_HOST = "127.0.0.1";// IP адрес компьютера

    public static void main(String[] args) {
        Socket socket = null;
        try {
            try {
                System.out.println("Добро пожаловать на клиентскую сторону\n" +
                        ">> Подключение к серверу\n\t(IP адрес " + LOCAL_HOST + ")" +
                        " порт" + SERVER_PORT + ")");
                InetAddress ipAddress = InetAddress.getByName(LOCAL_HOST);
                socket = new Socket(ipAddress, SERVER_PORT);
                System.out.println(">> Ready");
                System.out.println("\tАдрес хоста = " + socket.getInetAddress().getHostAddress() +
                        "\tРазмер буфера = " + socket.getReceiveBufferSize());
                
                // создаём потоки для чтения и записи данных
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                
                // поток для чтения вводимых данных с клавиатуры
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader keyBoard = new BufferedReader(isr);
                
                String line;
                while (true) {
                    System.out.println("Enter query:");
                    line = keyBoard.readLine();// читаем с клавиатуры
                    dos.writeUTF(line);// пишем в поток вывода
                    dos.flush();
                    line = dis.readUTF();// читаем из потока ввода
                    if(line.isBlank()) {
                        // если строка пустая или содержит только пробелы
                        continue;// продолжаем цикл
                    }
                    if(line.endsWith("quit")) {
                        // если строка содержит QUIT, прекращаем цикл
                        break;
                    } else {
                        // печатаем строку
                        System.out.println("\nEcho:\n\t" + line);
                        System.out.println();
                    }
                }
                System.exit(0);// завершение работы
            } catch (IOException e) {
                System.out.println("error=" + e.getMessage());
            }
        } finally {

            try {
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("error=" + e.getMessage());
            }
        }

    }
}
