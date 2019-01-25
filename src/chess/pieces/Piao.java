package chess.pieces;

import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.Cor;
import chess.PartidaDeXadrez;
import chess.PecaDeXadrez;

public class Piao extends PecaDeXadrez {

	private PartidaDeXadrez partidaDeXadrez;

	public Piao(Tabuleiro tabuleiro, Cor cor, PartidaDeXadrez partidaDeXadrez) {
		super(tabuleiro, cor);
		this.partidaDeXadrez = partidaDeXadrez;

	}

	@Override
	public boolean[][] movimentoPossivel() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];

		Posicao p = new Posicao(0, 0);

		if (getCor() == Cor.BRANCO) {
			p.setValues(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValues(posicao.getLinha() - 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p) && getTabuleiro().posicaoExiste(p2)
					&& !getTabuleiro().haUmaPeca(p2) && getContagemDeMovimento() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValues(posicao.getLinha() - 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValues(posicao.getLinha() - 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}

			// #specialmove en passant white
			if (posicao.getLinha() == 3) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if (getTabuleiro().posicaoExiste(esquerda) && existeUmaPecaAdversaria(esquerda)
						&& getTabuleiro().peca(esquerda) == partidaDeXadrez.getEnPassantVulnerable())
					;
				mat[esquerda.getLinha() - 1][esquerda.getColuna()] = true;
			}
			if (posicao.getLinha() == 3) {
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().posicaoExiste(direita) && existeUmaPecaAdversaria(direita)
						&& getTabuleiro().peca(direita) == partidaDeXadrez.getEnPassantVulnerable())
					;
				mat[direita.getLinha() - 1][direita.getColuna()] = true;
			}
		} else {
			p.setValues(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValues(posicao.getLinha() + 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().haUmaPeca(p) && getTabuleiro().posicaoExiste(p2)
					&& !getTabuleiro().haUmaPeca(p2) && getContagemDeMovimento() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValues(posicao.getLinha() + 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValues(posicao.getLinha() + 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExiste(p) && existeUmaPecaAdversaria(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// #specialmove en passant black
			if (posicao.getLinha() == 4) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if (getTabuleiro().posicaoExiste(esquerda) && existeUmaPecaAdversaria(esquerda)
						&& getTabuleiro().peca(esquerda) == partidaDeXadrez.getEnPassantVulnerable())
					;
				mat[esquerda.getLinha() + 1][esquerda.getColuna()] = true;
			}
			if (posicao.getLinha() == 3) {
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().posicaoExiste(direita) && existeUmaPecaAdversaria(direita)
						&& getTabuleiro().peca(direita) == partidaDeXadrez.getEnPassantVulnerable())
					;
				mat[direita.getLinha() + 1][direita.getColuna()] = true;
			}
		}
		return mat;
	}

	@Override
	public String toString() {
		return "P";
	}

}
