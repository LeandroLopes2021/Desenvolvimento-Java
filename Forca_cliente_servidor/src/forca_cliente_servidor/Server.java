package forca_cliente_servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Hashtable;

public class Server {

    public static String palavraJogo;
    private static int acertos = 0;
    private static int portaConexao = 12346;

    public static void main(String[] args) throws IOException {

        try {
            Hashtable letras = new Hashtable(30); //tratamento das letras
            int erro = 0;
            String err;

            ServerSocket server = new ServerSocket(portaConexao); //cria o servidor na porta 12346
            System.out.println("Porta " + portaConexao + " aberta!");
            System.out.println("Esperando Jogador...");
            Socket client = server.accept(); //espera o cliente conectar
            System.out.println("Jogador: " + client.getInetAddress().getHostAddress() + " conectado na porta " + portaConexao);

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in)); //cria um arquivo de leitura
            OutputStream ostream = client.getOutputStream();
            PrintWriter pwrite = new PrintWriter(ostream, true);

            InputStream istream = client.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

            String recebeMensagem, enviaMensagem;

            System.out.println("\n\nDigite o tema: "); //Escolhe o tema 
            enviaMensagem = read.readLine(); //recebe do teclado a mensagem
            pwrite.println(enviaMensagem);   //envia a mensagem
            pwrite.flush(); //garante o envio

            System.out.println("Digite a palavra: "); //escolhe a palavra
            enviaMensagem = read.readLine();//recebe do teclado a mensagem
            pwrite.println(enviaMensagem);//envia a mensagem
            pwrite.flush(); //garante o envio

            enviaMensagem = enviaMensagem.toLowerCase(); //deixa em maiuscula

            //Coloca underlines
            formataString(enviaMensagem.length()); //formata a palavra para ficar em ____

            while (true) {
                while (true) {//Aguarda confirmação
                    if ((recebeMensagem = receiveRead.readLine()) != null) { //recebe mensagem do cliente e verifica se não e nula
                        break;
                    }
                }

                //Envia a quantidade de erros atual
                err = convertErro(erro);
                pwrite.println(err);
                pwrite.flush();

                while (true) {//Aguarda confirmação
                    if ((recebeMensagem = receiveRead.readLine()) != null) { //recebe mensagem do cliente e verifica se não e nula
                        break;
                    }
                }

                //Envia a string correspondente a palavra a ser descoberta
                pwrite.println(palavraJogo);//envia a palavra
                pwrite.flush();

                if ((recebeMensagem = receiveRead.readLine()) != null) { //recebe mensagem do cliente e verifica se não e nula
                    if (recebeMensagem.length() > 1) {//Se jogador optou por digitar palavra completa

                        if (recebeMensagem.equals(enviaMensagem)) {//Caso o jogador tenha acertado a palavra			
                            acertouPalavra(recebeMensagem);

                            pwrite.println("VOCÊ GANHOU");
                            pwrite.flush();
                            //finaliza tudo
                            read.close();
                            client.close();
                            server.close();

                        } else {//Se o jogador perdeu...
                            pwrite.println("PERDEU");
                            pwrite.flush();
                            read.close();
                            client.close();
                            server.close();

                        }
                    } else {//Se jogador optou por digitar letra

                        //Pega char
                        char c = recebeMensagem.charAt(0);
                        boolean letraRepetida = false;

                        if (!letras.contains(c)) { //verifica se não tem a letra
                            letras.put((int) c, (char) c); //se não tiver adiciona

                            //Verifica se existe a letra na string
                            if (!trataRetorno(c, enviaMensagem)) {
                                erro++;
                            }
                        } else {
                            letraRepetida = true;
                        }

                        if (erro == 7) {//Se o jogador perdeu...
                            pwrite.println("PERDEU");
                            pwrite.flush();

                            read.close();
                            client.close();
                            server.close();

                        } else {
                            if (acertos == enviaMensagem.length()) {//Caso o jogador tenha acertado a palavra		
                                pwrite.println("VOCÊ GANHOU");
                                pwrite.flush();
                                read.close();
                                client.close();
                                server.close();

                            } else {//Printa na tela do jogador a string formatada
                                if (letraRepetida) {//Envia letra repetida para ser exibida na tela do jogador
                                    pwrite.println(c);
                                    pwrite.flush();
                                } else {
                                    pwrite.println("Continua");
                                    pwrite.flush();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("SERVIDOR FINALIZADO");
        }

    }

    public static boolean trataRetorno(char c, String sendMessage) {
        boolean logic = false;

        for (int i = 0; i < sendMessage.length(); i++) {
            if (c == sendMessage.charAt(i)) {
                if (palavraJogo.charAt(i * 2) != c) {
                    palavraJogo = palavraJogo.substring(0, i * 2) + c + palavraJogo.substring(i * 2 + 1);
                    acertos++;
                }
                logic = true;
            }
        }
        return logic;
    }

    public static void acertouPalavra(String palavra) {
        palavraJogo = "";

        for (int i = 0; i < palavra.length(); i++) {
            palavraJogo += palavra.charAt(i);
            palavraJogo += " ";
        }
    }

    public static void formataString(int tam) {//recebe o tamanho
        palavraJogo = "";//declara uma string vazia
        for (int i = 0; i < tam - 1; i++) { //adiciona _ ate o tamanho da palavra
            palavraJogo = palavraJogo.concat("_ ");
        }
        palavraJogo = palavraJogo.concat("_");//retorna ______
    }

    public static String convertErro(int erro) {
        switch (erro) {
            case 0:
                return "0";

            case 1:
                return "1";

            case 2:
                return "2";

            case 3:
                return "3";

            case 4:
                return "4";

            case 5:
                return "5";

            case 6:
                return "6";

            case 7:
                return "7";

            default:
                return null;
        }
    }
}
