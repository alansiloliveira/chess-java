package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.pieces.Bispo;
import chess.pieces.Cavalo;
import chess.pieces.Piao;
import chess.pieces.Rainha;
import chess.pieces.Rei;
import chess.pieces.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaDeXadrez enPassantVulnerable;
	private PecaDeXadrez promovido;

	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		check = false;
		checkMate = false;
		enPassantVulnerable = null;
		iniciaPartida();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public PecaDeXadrez getPromovido() {
		return promovido;
	}

	public PecaDeXadrez getEnPassantVulnerable() {
		return enPassantVulnerable;
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

	public boolean[][] movimentoPossivel(PosicaoXadrez origem) {
		Posicao posicao = origem.paraPosicao();
		validaPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentoPossivel();
	}

	public PecaDeXadrez perfomanceMovimentoDaPeca(PosicaoXadrez posicaoDeOrigem, PosicaoXadrez posicaoDestino) {
		Posicao origem = posicaoDeOrigem.paraPosicao();
		Posicao destino = posicaoDestino.paraPosicao();
		validaPosicaoOrigem(origem);
		validaPosicaoDestino(origem, destino);
		Peca pecaCapturada = movimentacao(origem, destino);

		if (testeCheck(jogadorAtual)) {
			movimentacaoNegada(origem, destino, pecaCapturada);
			throw new ChessException("Voce não pode se colocar em check");
		}

		PecaDeXadrez pecaMovida = (PecaDeXadrez) tabuleiro.peca(destino);

		// #specialMove promotion
		promovido = null;
		if (pecaMovida instanceof Piao) {
			if ((pecaMovida.getCor() == Cor.BRANCO && destino.getLinha() == 0)
					|| (pecaMovida.getCor() == Cor.PRETO && destino.getLinha() == 7)) {
				promovido = (PecaDeXadrez) tabuleiro.peca(destino);
				promovido = replacePecaPromovida("Q");
			}
		}

		check = (testeCheck(oponente(jogadorAtual))) ? true : false;

		if (testeCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		} else {
			proximoTurno();
		}

		// #specialMove en passant
		if (pecaMovida instanceof Piao
				&& (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			enPassantVulnerable = pecaMovida;
		} else {
			enPassantVulnerable = null;
		}

		return (PecaDeXadrez) pecaCapturada;
	}

	public PecaDeXadrez replacePecaPromovida(String type) {
		if (promovido == null) {
			throw new IllegalStateException("Não há peça a ser promovida");
		}
		if (!type.equals("B") && !type.equals("C") && !type.equals("T") && !type.equals("Q")) {
			throw new InvalidParameterException("Tipo invalido para promoção");
		}

		Posicao pos = promovido.getPosicaoPeca().paraPosicao();
		Peca p = tabuleiro.removePeca(pos);
		pecasNoTabuleiro.remove(p);

		PecaDeXadrez novaPeca = novaPeca(type, promovido.getCor());
		tabuleiro.posicaoDaPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}

	private PecaDeXadrez novaPeca(String type, Cor cor) {
		if (type.equals("B"))
			return new Bispo(tabuleiro, cor);
		if (type.equals("T"))
			return new Torre(tabuleiro, cor);
		if (type.equals("C"))
			return new Cavalo(tabuleiro, cor);
		return new Rainha(tabuleiro, cor);
	}

	private Peca movimentacao(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removePeca(origem);
		p.incrementaMovimento();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.posicaoDaPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		// #specialmove castling kingside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(destino.getLinha(), destino.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(origemT);
			tabuleiro.posicaoDaPeca(torre, destinoT);
			torre.incrementaMovimento();
		}

		// #specialmove castling queenside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(destino.getLinha(), destino.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(origemT);
			tabuleiro.posicaoDaPeca(torre, destinoT);
			torre.incrementaMovimento();
		}

		// #specialmove en passant
		if (p instanceof Piao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao pawnPosicion;
				if (p.getCor() == Cor.BRANCO) {
					pawnPosicion = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} else {
					pawnPosicion = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(pawnPosicion);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}

		return pecaCapturada;
	}

	private void movimentacaoNegada(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez) tabuleiro.removePeca(destino);
		p.decrementaMovimento();
		tabuleiro.posicaoDaPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.posicaoDaPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// #specialmove castling kingside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(destino.getLinha(), destino.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(destinoT);
			tabuleiro.posicaoDaPeca(torre, origemT);
			torre.decrementaMovimento();
		}

		// #specialmove castling queenside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(destino.getLinha(), destino.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez) tabuleiro.removePeca(destinoT);
			tabuleiro.posicaoDaPeca(torre, origemT);
			torre.decrementaMovimento();
		}
		// #specialmove en passant
		if (p instanceof Piao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVulnerable) {
				PecaDeXadrez pawn = (PecaDeXadrez) tabuleiro.removePeca(destino);
				Posicao pawnPosicion;
				if (p.getCor() == Cor.BRANCO) {
					pawnPosicion = new Posicao(3, destino.getColuna());
				} else {
					pawnPosicion = new Posicao(4, destino.getColuna());
				}
				tabuleiro.posicaoDaPeca(pawn, pawnPosicion);
			}
		}
	}

	private void validaPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.haUmaPeca(posicao)) {
			throw new ChessException("Não é peça nessa posição");
		}
		if (jogadorAtual != ((PecaDeXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new ChessException("A peça escolhida não é sua");
		}
		if (!tabuleiro.peca(posicao).existeAlgumMovimentoPossivel()) {
			throw new ChessException("Não existe movimentos possiveis para a peça escolhida");
		}
	}

	private void validaPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new ChessException("A peça escolhida não pode ser mover para posição de destino");
		}
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PecaDeXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaDeXadrez) p;
			}
		}
		throw new IllegalStateException("Não existe o rei da cor " + cor);
	}

	private boolean testeCheck(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoPeca().paraPosicao();
		List<Peca> pecasDoOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecasDoOponente) {
			boolean[][] mat = p.movimentoPossivel();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testeCheckMate(Cor cor) {
		if (!testeCheck(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentoPossivel();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaDeXadrez) p).getPosicaoPeca().paraPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = movimentacao(origem, destino);
						boolean testeCheck = testeCheck(cor);
						movimentacaoNegada(origem, destino, pecaCapturada);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;

	}

	private void posicaoDaNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.posicaoDaPeca(peca, new PosicaoXadrez(coluna, linha).paraPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void iniciaPartida() {
		posicaoDaNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('a', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('b', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('c', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('d', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('e', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('f', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('g', 2, new Piao(tabuleiro, Cor.BRANCO, this));
		posicaoDaNovaPeca('h', 2, new Piao(tabuleiro, Cor.BRANCO, this));

		posicaoDaNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('a', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('b', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('c', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('d', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('e', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('f', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('g', 7, new Piao(tabuleiro, Cor.PRETO, this));
		posicaoDaNovaPeca('h', 7, new Piao(tabuleiro, Cor.PRETO, this));
	}
}
