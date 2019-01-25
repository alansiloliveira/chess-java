package chess;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;

public abstract class PecaDeXadrez extends Peca{
	
	private Cor cor;
	private int contagemDeMovimento;

	public PecaDeXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
		contagemDeMovimento = 0;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContagemDeMovimento() {
		return contagemDeMovimento;
	}
	
	public void incrementaMovimento() {
		contagemDeMovimento++;
	}
	
	public void decrementaMovimento() {
		contagemDeMovimento--;
	}
	
	public PosicaoXadrez getPosicaoPeca() {
		return PosicaoXadrez.vaiParaPosicao(posicao);
	}
	
	protected boolean existeUmaPecaAdversaria(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
