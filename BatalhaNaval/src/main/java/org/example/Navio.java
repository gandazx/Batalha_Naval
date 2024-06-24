package org.example;

public class Navio {
    private int tamanho;
    private boolean[] partes;

    public Navio(int tamanho) {
        this.tamanho = tamanho;
        this.partes = new boolean[tamanho];
        for (int i = 0; i < tamanho; i++) {
            partes[i] = true; // partes do navio inteiras
        }
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean isAfundado() {
        for (boolean parte : partes) {
            if (parte) return false;
        }
        return true;
    }

    public void atingir(int parte) {
        if (parte >= 0 && parte < tamanho) {
            partes[parte] = false;
        }
    }
}
