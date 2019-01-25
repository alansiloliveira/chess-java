package aplicacao;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.PartidaDeXadrez;
import chess.PecaDeXadrez;
import chess.PosicaoXadrez;

public class Programa {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		List<PecaDeXadrez> capturada = new ArrayList<>();

		while (!partidaDeXadrez.getCheckMate()) {
			try {
				UI.limparTela();
				UI.fazerPartida(partidaDeXadrez, capturada);
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.leiaPosicaoXadrez(sc);
				
				boolean[][] movimentoPossivel = partidaDeXadrez.movimentoPossivel(origem);
				UI.limparTela();
				UI.fazerTabuleiro(partidaDeXadrez.getPecas(), movimentoPossivel);
				
				System.out.println();
				System.out.println("Destino: ");
				PosicaoXadrez destino = UI.leiaPosicaoXadrez(sc);

				PecaDeXadrez pecaCapturada = partidaDeXadrez.perfomanceMovimentoDaPeca(origem, destino);
				
				if(pecaCapturada != null) {
					capturada.add(pecaCapturada);
				}
				
				if(partidaDeXadrez.getPromovido() != null) {
					System.out.println("Escolha uma peça para promover (B, T, C, Q): ");
					String type = sc.nextLine();
					partidaDeXadrez.replacePecaPromovida(type);
				}
			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.limparTela();
		UI.fazerPartida(partidaDeXadrez, capturada);
	}
}
