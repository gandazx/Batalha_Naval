package org.example;

import java.io.*;
import java.net.*;

public class Servidor {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Tabuleiro tabuleiro;

    public Servidor() {
        tabuleiro = new Tabuleiro();
    }

    public void start(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado, aguardando jogador...");
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("reiniciar".equalsIgnoreCase(inputLine)) {
                    reiniciar();
                } else {
                    String resposta = processarJogada(inputLine);
                    out.println(resposta);
                }
            }
        } catch (BindException e) {
            System.err.println("Erro ao iniciar servidor: Address already in use: bind");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private String processarJogada(String posicao) {
        boolean posicaoValida = posicao.length() == 2 &&
                posicao.charAt(0) >= 'a' && posicao.charAt(0) <= 'h' &&
                Character.isDigit(posicao.charAt(1)) &&
                Character.getNumericValue(posicao.charAt(1)) >= 1 &&
                Character.getNumericValue(posicao.charAt(1)) <= 8;

        if (!posicaoValida) {
            return "Posição inválida, tente novamente.";
        }

        boolean navioAtingido = tabuleiro.atirar(posicao);
        if (navioAtingido) {
            if (tabuleiro.getTotalNavios() == 0) {
                return "Navio afundado! Todos os navios foram destruídos!";
            }
            return "Navio atingido!";
        } else {
            return "Água!";
        }
    }


    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public void reiniciar() {
        tabuleiro.reiniciar();
        out.println("Tabuleiro reiniciado");
    }

    public void stop() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
        if (serverSocket != null) serverSocket.close();
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            servidor.start(12345);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
