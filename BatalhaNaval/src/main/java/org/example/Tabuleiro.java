package org.example;

import java.util.Random;

public class Tabuleiro {
    private char[][] tabuleiro;
    private int totalNavios;

    public Tabuleiro() {
        tabuleiro = new char[8][8];
        totalNavios = 5; // Exemplo de 5 navios
        inicializarTabuleiro();
    }

    private void inicializarTabuleiro() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tabuleiro[i][j] = '-';
            }
        }
        randomizarNavios();
    }

    public void posicionarNavio(int x, int y) {
        if (tabuleiro[x][y] == '-') {
            tabuleiro[x][y] = 'N';
        }
    }

    public boolean atirar(String posicao) {
        if (posicao.length() < 2) return false;
        int x = posicao.charAt(0) - 'a';
        int y = Character.getNumericValue(posicao.charAt(1)) - 1;

        if (x < 0 || x >= 8 || y < 0 || y >= 8) {
            return false; // Posição inválida
        }

        if (tabuleiro[x][y] == 'N') {
            tabuleiro[x][y] = 'X';
            totalNavios--;
            return true;
        }
        return false;
    }

    public void reiniciar() {
        inicializarTabuleiro();
        totalNavios = 5; // Reposiciona o número de navios inicial
    }


    public int getTotalNavios() {
        return totalNavios;
    }

    public int getNaviosRestantes() {
        return totalNavios;
    }

    public char[][] getTabuleiro() {
        return tabuleiro;
    }

    public void randomizarNavios() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) { // Exemplo de 5 navios
            int x = random.nextInt(8);
            int y = random.nextInt(8);
            while (tabuleiro[x][y] == 'N') { // Garantir que o navio não seja posicionado na mesma célula
                x = random.nextInt(8);
                y = random.nextInt(8);
            }
            posicionarNavio(x, y);
        }
    }

}

