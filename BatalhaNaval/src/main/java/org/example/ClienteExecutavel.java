package org.example;

import java.io.IOException;
import java.util.Scanner;

public class ClienteExecutavel {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o endereço IP do servidor:");
        String endereco = scanner.nextLine();

        System.out.println("Digite a porta do servidor:");
        int porta = Integer.parseInt(scanner.nextLine());

        Cliente cliente = new Cliente();
        try {
            cliente.startConnection(endereco, porta);
            System.out.println("Conectado ao servidor em " + endereco + ":" + porta);

            // Permitir que o usuário envie mensagens ao servidor
            String mensagem;
            while (true) {
                System.out.println("Digite uma mensagem para enviar ao servidor (ou 'exit' para sair, 'reiniciar' para reiniciar o jogo):");
                mensagem = scanner.nextLine();
                if (mensagem.equalsIgnoreCase("exit")) {
                    break;
                }
                String resposta = cliente.sendMessage(mensagem);
                System.out.println("Resposta do servidor: " + resposta);
                if (mensagem.equalsIgnoreCase("reiniciar")) {
                    System.out.println("Jogo reiniciado. Os navios foram reposicionados.");
                }
            }

            cliente.stopConnection();
            System.out.println("Conexão encerrada.");
        } catch (IOException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }

        scanner.close();
    }
}
