package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

public class BatalhaNaval extends JFrame {
    private JTextField enderecoField, posicaoField;
    private JTextArea logArea;
    private JButton conectarButton, aguardarButton, jogarButton, reiniciarButton;
    private Cliente cliente;
    private Servidor servidor;
    private JPanel tabuleiroPanel;
    private JLabel[][] celulas;
    private boolean servidorEmExecucao = false;
    private JLabel jogadasLabel, naviosLabel;
    private int jogadas = 0;

    public BatalhaNaval() {
        setTitle("Batalha Naval");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        enderecoField = new JTextField(15);
        posicaoField = new JTextField(5);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        conectarButton = new JButton("Conectar");
        aguardarButton = new JButton("Aguardar jogador");
        jogarButton = new JButton("Jogar");
        reiniciarButton = new JButton("Reiniciar");
        jogadasLabel = new JLabel("Nr Jogadas: 0");
        naviosLabel = new JLabel("Navios restantes: 5");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(new JLabel("Endereço:"));
        controlPanel.add(enderecoField);
        controlPanel.add(conectarButton);
        controlPanel.add(aguardarButton);
        controlPanel.add(new JLabel("Posição:"));
        controlPanel.add(posicaoField);
        controlPanel.add(jogarButton);
        controlPanel.add(reiniciarButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.add(jogadasLabel);
        statusPanel.add(naviosLabel);

        tabuleiroPanel = new JPanel(new GridLayout(9, 9));
        celulas = new JLabel[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                celulas[i][j] = new JLabel("", SwingConstants.CENTER);
                celulas[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                celulas[i][j].setOpaque(true);
                if (i == 0 && j > 0) {
                    celulas[i][j].setText(String.valueOf(j));
                } else if (j == 0 && i > 0) {
                    celulas[i][j].setText(String.valueOf((char) ('a' + i - 1)));
                } else if (i > 0 && j > 0) {
                    celulas[i][j].setBackground(Color.WHITE);
                }
                tabuleiroPanel.add(celulas[i][j]);
            }
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        mainPanel.add(tabuleiroPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.EAST);

        add(mainPanel);

        conectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String endereco = enderecoField.getText();
                try {
                    cliente = new Cliente();
                    new Thread(() -> {
                        try {
                            cliente.startConnection("localhost", Integer.parseInt(endereco));
                            logArea.append("Conectado ao servidor\n");
                        } catch (IOException ex) {
                            logArea.append("Erro ao conectar: " + ex.getMessage() + "\n");
                        }
                    }).start();
                } catch (NumberFormatException ex) {
                    logArea.append("Porta inválida: " + endereco + "\n");
                }
            }
        });

        aguardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!servidorEmExecucao) {
                    new Thread(() -> {
                        try {
                            servidor = new Servidor();
                            servidor.start(12345);
                            logArea.append("Servidor iniciado, aguardando jogador...\n");
                            servidorEmExecucao = true;
                        } catch (IOException ex) {
                            logArea.append("Erro ao iniciar servidor: " + ex.getMessage() + "\n");
                        }
                    }).start();
                } else {
                    logArea.append("Servidor já está em execução\n");
                }
            }
        });

        jogarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String posicao = posicaoField.getText();
                new Thread(() -> {
                    try {
                        String resposta = cliente.sendMessage(posicao);
                        logArea.append("Resposta do servidor: " + resposta + "\n");
                        atualizarTabuleiro(posicao, resposta);
                        jogadas++;
                        jogadasLabel.setText("Nr Jogadas: " + jogadas);
                        if (resposta.contains("Navio afundado!") || resposta.contains("Navio atingido!")) {
                            int naviosRestantes = servidor.getTabuleiro().getNaviosRestantes();
                            SwingUtilities.invokeLater(() -> {
                                naviosLabel.setText("Navios restantes: " + naviosRestantes);
                            });
                        }
                    } catch (IOException ex) {
                        logArea.append("Erro ao enviar jogada: " + ex.getMessage() + "\n");
                    }
                }).start();
            }
        });

        reiniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        cliente.sendMessage("reiniciar");
                        logArea.append("Tabuleiro reiniciado\n");
                        resetarTabuleiro();
                        servidor.getTabuleiro().reiniciar();
                        int naviosRestantes = servidor.getTabuleiro().getNaviosRestantes();
                        SwingUtilities.invokeLater(() -> {
                            naviosLabel.setText("Navios restantes: " + naviosRestantes);
                        });
                        jogadas = 0;
                        jogadasLabel.setText("Nr Jogadas: " + jogadas);
                    } catch (IOException ex) {
                        logArea.append("Erro ao reiniciar tabuleiro: " + ex.getMessage() + "\n");
                    }
                }).start();
            }
        });
    }

    private void atualizarTabuleiro(String posicao, String resposta) {
        SwingUtilities.invokeLater(() -> {
            int linha = posicao.charAt(0) - 'a' + 1;
            int coluna = Character.getNumericValue(posicao.charAt(1));
            if (resposta.contains("Navio atingido") || resposta.contains("Navio afundado")) {
                celulas[linha][coluna].setBackground(Color.RED);
            } else if (resposta.contains("Água")) {
                celulas[linha][coluna].setBackground(Color.BLUE);
            }
        });
    }

    private void resetarTabuleiro() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    celulas[i][j].setBackground(Color.WHITE);
                }
            }
            jogadas = 0;
            jogadasLabel.setText("Nr Jogadas: " + jogadas);
            naviosLabel.setText("Navios restantes: 5");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BatalhaNaval frame = new BatalhaNaval();
            frame.setVisible(true);
        });
    }
}
