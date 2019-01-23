package chess.pieces;

import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.Cor;
import chess.PecaDeXadrez;

public class Torre extends PecaDeXadrez{

	public Torre(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro, cor);
	}
	
	@Override
	public String toString() {
		return "T";
	}
	
	@Override
	public boolean[][] movimentoPossivel() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0, 0);
		
		// em cima
		p.setValues(posicao.getLinha() - 1,	posicao.getColuna());
		while(getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setLinha(p.getLinha() - 1);
		}
		if(getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// esquerda
		p.setValues(posicao.getLinha(),	posicao.getColuna() - 1);
		while(getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setColuna(p.getColuna() - 1);
		}
		if(getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// direita
		
		p.setValues(posicao.getLinha(),	posicao.getColuna() + 1);
		while(getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setColuna(p.getColuna() + 1);
		}
		if(getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// baixo
		
		p.setValues(posicao.getLinha() + 1,	posicao.getColuna());
		while(getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setLinha(p.getLinha() + 1);
		}
		if(getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		return mat;
	}
}
