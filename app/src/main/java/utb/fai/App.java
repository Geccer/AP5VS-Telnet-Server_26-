package utb.fai;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) {
        int port = 12345;
        int max_threads = 10;    

        if(args.length >= 1){
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Neplatné číslo portu: " + args[0]);
                System.exit(1);
            }
        }
        if (args.length >= 2) {
            try {
                max_threads = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Neplatný počet vláken: " + args[1]);
                System.exit(1);
            }
        }
        if (port < 1 || port > 65535) {
            System.err.println("Port musí být v rozsahu 1-65535.");
            System.exit(1);
        }
        if (max_threads < 1) {
            System.err.println("Počet vláken musí být alespoň 1.");
            System.exit(1);
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(max_threads, max_threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try{
                    executor.execute(new ClientThread(clientSocket));
                }catch(RejectedExecutionException e){

                    try{
                        clientSocket.close();
                    }catch (IOException ignored){

                    }
                }
                
            }
        } catch(IOException e){
            e.printStackTrace();
        }finally{
            executor.shutdownNow();
        }
    }
}
