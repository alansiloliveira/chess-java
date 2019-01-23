package aplicacao;

import java.util.InputMismatchException;
import java.util.Scanner;

import chess.ChessException;
import chess.PartidaDeXadrez;
import chess.PecaDeXadrez;
import chess.PosicaoXadrez;

public class Programa {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();

		while (true) {
			try {
				UI.limparTela();
				UI.fazerTabuleiro(partidaDeXadrez.getPecas());
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.leiaPosicaoXadrez(sc);

				System.out.println();
				System.out.println("Destino: ");
				PosicaoXadrez destino = UI.leiaPosicaoXadrez(sc);

				PecaDeXadrez pecaCapturada = partidaDeXadrez.perfomanceMovimentoDaPeca(origem, destino);
			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
	}
}
