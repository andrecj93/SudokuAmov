package com.example.sudokuamov.sockets;

import android.os.Handler;
import android.util.Log;

import com.example.sudokuamov.game.GameCell;
import com.example.sudokuamov.game.GameEngine;
import com.example.sudokuamov.game.Profile;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class SocketConnector {

    private static final Object mutex = new Object();
    private static final Object mutexSend = new Object();
    public static int SERVER = 0;
    public static int CLIENT = 1;
    static int AmIserver;
    private static SocketConnector instance;
    final int PORT = 8899;
    ServerSocket serverSocket;
    Socket socketGame;

    List<ClientPlayers> clientSocketList;

    MutableLiveData<Boolean> ConnectionEstablished = new MutableLiveData<>();


    Handler procMsg = new Handler();

    BufferedReader input;
    String baseDir = null;

    SocketConnector() {
        clientSocketList = new ArrayList<>();
    }

    public static SocketConnector getInstance() {
        SocketConnector result = instance;

        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new SocketConnector();
            }
        }

        return result;
    }

    public static void resetInstance() {
        instance = null;
    }

    public static int getAmIserver() {
        return AmIserver;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public Thread ConnectToServer(final String ipServer) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketGame = new Socket();
                    socketGame.connect(new InetSocketAddress(ipServer, PORT));

                    AmIserver = CLIENT;

                    //SendFileImage(socketGame,baseDir + "/userPhoto_thumb.jpg");

                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            ConnectionEstablished.postValue(true);
                        }
                    });
                } catch (Exception e) {
                    Log.e("CONNECTTOSERVERServer", "ERRO NO CICLO DE ConnectToServer");
                }

            }
        });
    }

    public Thread WaitforClients() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AmIserver = SERVER;
                    serverSocket = new ServerSocket(PORT);

                    while (serverSocket != null) {
                        final Socket socket = serverSocket.accept();

                        //GetPlayerImage(socket,baseDir+socket.getInetAddress().toString().replace('.', '-')+".jpg");

                        clientSocketList.add(new ClientPlayers(socket));

                        procMsg.post(new Runnable() {
                            @Override
                            public void run() {
                                ConnectionEstablished.postValue(true);
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e("WAITFORCLIENTS", "ERRO NO CICLO DE WAITFORCLIENTS");
                }
            }
        });
    }

    public void closeServerSocket() {
        try {
            if (!serverSocket.isClosed())
                serverSocket.close();
            serverSocket = null;
        } catch (Exception e) {

        }
    }

    public void startReceivingData() {
        if (AmIserver == SERVER) {
            for (ClientPlayers client : clientSocketList) {
                ReceiveInfoThread(client.socket);
            }
        } else {
            ReceiveInfoThread(socketGame);
        }
    }

    public void actualizaPlayerList(GameEngine gameEngine) {

        if (AmIserver == SERVER) {
            for (ClientPlayers client : clientSocketList) {
                gameEngine.addPlayerToList(new Profile(client.Ip, client.Ip + ".jpg", baseDir + "/" + client.Ip + ".jpg", client.Ip));
            }

            DataExchange dataExchange = new DataExchange(DataExchange.Operation.setPlayerList, null, null, gameEngine.getAllPlayers(), gameEngine.getProfileActive(), null, 0);

            this.sendInfoToAllClients(dataExchange.getJSON());
        }
    }



    public void sendInfoToAllClients(final String message) {
        synchronized (mutexSend) {
            for (ClientPlayers clientPlayer : clientSocketList) {
                this.sendInfo(clientPlayer.socket, message);
            }
        }
    }

    public void sendClientInfo(final String message) {
        synchronized (mutexSend) {
            this.sendInfo(socketGame, message);
        }
    }

    private void trataMensagens(final DataExchange exchange, final Socket socket) {

    }

    private void ReceiveInfoThread(final Socket socket) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (Exception e) {
                }


                while (true) {
                    try {
                        String read = input.readLine();

                        Log.d("Recebi", read);

                        final DataExchange exchange = new Gson().fromJson(read, DataExchange.class);

                        switch (exchange.operation) {
                            case setPlayerList:
                                setPlayerList(exchange.profileList, exchange.profileActive);
                                break;
                            case setPlayerName:
                                setPlayerName(exchange.playerName, getSocketIp(socket));
                                break;
                            case SetGameBoar:
                                setGameBoard(exchange.gameBoard);
                                break;
                            case SetTime:
                                setTime(exchange.newTime);
                                break;
                            case CheckNewGamePlay:
                                checkGamePlay(exchange.gamePlay, getSocketIp(socket));
                                break;
                            case SetCellRed:
                                setCellRed(exchange.gamePlay);
                                break;
                            case SetCellValue:
                                setGameBoard(exchange.gameBoard);
                                setPlayerList(exchange.profileList, exchange.profileActive);
                                break;
                        }
                    } catch (Exception e) {
                        Log.d("ERRO A RECEBER", e.getMessage());
                    }
                }
            }

        }).start();
    }

    private void setCellRed(GamePlay gamePlay) {
        GameEngine.getInstance().getGrid().setCellRed(gamePlay.poisitionX, gamePlay.positionY, gamePlay.newNum);
    }

    private void checkGamePlay(GamePlay gamePlay, String ip) {
        GameEngine gameEngine = GameEngine.getInstance();

        if (ip.equals(gameEngine.getActivePalyer().getIp()))
            gameEngine.setRemoteNumber(gamePlay.poisitionX, gamePlay.positionY, gamePlay.newNum);
    }

    private void setPlayerList(List<Profile> clientSocketList, int activePlayer) {
        if (AmIserver == SERVER)
            return;

        GameEngine gameEngine = GameEngine.getInstance();

        gameEngine.removeAllPlayers();

        String IpString = socketGame.getInetAddress().toString().replace('.', '-');

        for (Profile profile : clientSocketList) {

            if (IpString.equals(profile.getIp()))
                gameEngine.setMyProfile(gameEngine.getMyProfile());
            else
                gameEngine.addPlayerToList(profile);
        }

        gameEngine.setProfileActive(activePlayer);
    }

    private void setPlayerName(String name, String ip) {
        Profile p = GameEngine.getInstance().getThisPlayers(ip);

        if (p != null) {
            p.setUsername(name);
            GameEngine gameEngine = GameEngine.getInstance();

            DataExchange dataExchange = new DataExchange(DataExchange.Operation.setPlayerList, null, null, gameEngine.getAllPlayers(), 0, null, 0);

            this.sendInfoToAllClients(dataExchange.getJSON());
        }
    }


    private void setGameBoard(List<GameCell> gameBoard) {
        GameEngine.getInstance().setGameBoard(gameBoard);
    }

    private void setTime(int time) {
        GameEngine.getInstance().setTimer(time);
    }

    private String getSocketIp(Socket socket) {
        return socket.getInetAddress().toString().replace('.', '-').replace("/", "");
    }


    public void sendInfo(final Socket socket, final String message) {

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PrintWriter output = new PrintWriter(socket.getOutputStream());

                        Log.d("Enviei", message);

                        output.println(message);

                        output.flush();

                    } catch (Exception e) {
                        Log.d("ERRO A ENVIAR", e.getMessage());
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.d("ERRO A ENVIAR", e.getMessage());
        }
    }

    /*public String getMySocketIpAddress(Socket socket) {
        try {
            return socket.getInetAddress().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }*/

    private void SendFileImage(Socket socket, String filepath) {
        OutputStream out = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;


        try {
            File myFile = new File(filepath);
            byte[] mybytearray = new byte[(int) myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);

            bis.read(mybytearray, 0, mybytearray.length);

            out = socket.getOutputStream();

            out.write(mybytearray, 0, mybytearray.length);

            out.flush();

        } catch (Exception e) {
            Log.e("ERRO A ENVIAR MENSAGEM", e.getMessage());
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
            }
        }
    }

    private void GetPlayerImage(final String filepath) {
        InputStream ins = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int bytesRead;


        Socket socket = new Socket();
        try {

            File f = new File(filepath);
            f.deleteOnExit();

            byte[] mybytearray = new byte[1024];
            ins = socket.getInputStream();
            fos = new FileOutputStream(filepath);
            bos = new BufferedOutputStream(fos);

            while ((bytesRead = ins.read(mybytearray)) > 0) {
                bos.write(mybytearray, 0, bytesRead);
                bos.flush();
            }

        } catch (Exception e) {
            Log.e("ERRO A RECEBER MENSAGEM", e.getMessage());
        } finally {
            try {
                ins = null;
                if (fos != null) fos.close();
                if (bos != null) bos.close();
            } catch (Exception e) {
            }
        }
    }

    public void setObserver(LifecycleOwner lifecycleOwner, Observer<Boolean> observer) {
        ConnectionEstablished.observe(lifecycleOwner, observer);
    }

    public void removeObserver(LifecycleOwner lifecycleOwner) {
        ConnectionEstablished.removeObservers(lifecycleOwner);
    }
}