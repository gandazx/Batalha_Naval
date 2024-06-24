package org.example;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Cliente {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Tabuleiro tabuleiro;

    public Cliente() {
        tabuleiro = new Tabuleiro();
        randomizarNavios();
    }

    public void startConnection(String endereco, int porta) throws IOException {
        socket = new Socket(endereco, porta);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void reiniciar() {
        tabuleiro = new Tabuleiro();
        randomizarNavios();
    }

    public int getTotalNavios() {
        return tabuleiro.getTotalNavios();
    }

    public boolean jogar(String posicao) {
        return tabuleiro.atirar(posicao);
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    private void randomizarNavios() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) { // Exemplo de 5 navios
            int x = random.nextInt(8);
            int y = random.nextInt(8);
            while (tabuleiro.getTabuleiro()[x][y] == 'N') { // Garantir que o navio não seja posicionado na mesma célula
                x = random.nextInt(8);
                y = random.nextInt(8);
            }
            tabuleiro.posicionarNavio(x, y);
        }
    }
}

