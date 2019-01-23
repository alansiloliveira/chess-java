package aplicacao;

import java.util.Scanner;

import chess.PartidaDeXadrez;
import chess.PecaDeXadrez;
import chess.PosicaoXadrez;
 
public class Programa {
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		
		while (true) {
		UI.fazerTabuleiro(partidaDeXadrez.getPecas());
		System.out.println();
		System.out.print("Origem: ");
		PosicaoXadrez origem = UI.leiaPosicaoXadrez(sc);
		
		System.out.println();
		System.out.println("Destino: ");
		PosicaoXadrez destino = UI.leiaPosicaoXadrez(sc);
		
		PecaDeXadrez pecaCapturada = partidaDeXadrez.perfomanceMovimentoDaPeca(origem, destino);
		}
	}
	

}
 