package chess;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.pieces.Rei;
import chess.pieces.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor jogadorAtual;
	

	private Tabuleiro tabuleiro;

	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		iniciaPartida();
	}
	
	public int getTurno() {
		return turno;
	}
	
	public Cor getJogadorAtual() {
		return jogadorAtual;
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
	
	public boolean[][] movimentoPossivel(PosicaoXadrez origem){
		Posicao posicao = origem.paraPosicao();
		validaPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentoPossivel(); 
	}
	
	public PecaDeXadrez perfomanceMovimentoDaPeca(PosicaoXadrez posicaoDeOrigem, PosicaoXadrez posicaoDestino){
		Posicao origem = posicaoDeOrigem.paraPosicao();
		Posicao destino = posicaoDestino.paraPosicao();
		validaPosicaoOrigem(origem);
		validaPosicaoDestino(origem, destino);
		Peca pecaCapturada = movimentacao(origem, destino);
		proximoTurno();
		return (PecaDeXadrez)pecaCapturada;
	}
	
	private Peca movimentacao(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removePeca(origem);
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.posicaoDaPeca(p, destino);
		return pecaCapturada;
	}
	
	private void validaPosicaoOrigem(Posicao posicao) {
		if(!tabuleiro.haUmaPeca(posicao)) {
			throw new ChessException("Não é peça nessa posição"); 
		}
		if(jogadorAtual != ((PecaDeXadrez)tabuleiro.peca(posicao)).getCor()) {
			throw new ChessException("A peça escolhida não é sua");
		}
		if (!tabuleiro.peca(posicao).existeAlgumMovimentoPossivel()) {
			throw new ChessException("Não existe movimentos possiveis para a peça escolhida"); 
		}
	}
	
	private void validaPosicaoDestino(Posicao origem, Posicao destino) {
		if(!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new ChessException("A peça escolhida não pode ser mover para posição de destino");
		}
	}
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
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
