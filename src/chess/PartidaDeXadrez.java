package chess;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.pieces.Rei;
import chess.pieces.Torre;

public class PartidaDeXadrez {

	private Tabuleiro tabuleiro;

	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		iniciaPartida();
	}

	public PecaDeXadrez[][] getPecas() {
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	public PecaDeXadrez perfomanceMovimentoDaPeca(PosicaoXadrez posicaoInicial, PosicaoXadrez posicaoDestino){
		Posicao inicial = posicaoInicial.paraPosicao();
		Posicao destino = posicaoDestino.paraPosicao();
		validaPosicaoInicial(inicial);
		Peca pecaCapturada = movimentacao(inicial, destino);
		return (PecaDeXadrez)pecaCapturada;
	}
	
	private Peca movimentacao(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removePeca(origem);
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.posicaoDaPeca(p, destino);
		return pecaCapturada;
	}
	
	private void validaPosicaoInicial(Posicao posicao) {
		if(!tabuleiro.haUmaPeca(posicao)) {
			throw new ChessException("N�o � pe�a nessa posi��o"); 
		}
	}

	private void posicaoDaNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.posicaoDaPeca(peca, new PosicaoXadrez(coluna, linha).paraPosicao());
	}

	private void iniciaPartida() {
		posicaoDaNovaPeca('c', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('c', 2, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('d', 2, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('e', 2, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('e', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('d', 1, new Rei(tabuleiro, Cor.BRANCO));

		posicaoDaNovaPeca('c', 7, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('c', 8, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('d', 7, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('e', 7, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('e', 8, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('d', 8, new Rei(tabuleiro, Cor.PRETO));
	}
}
