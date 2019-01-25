package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.pieces.Piao;
import chess.pieces.Rei;
import chess.pieces.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();


	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		check = false;
		checkMate = false;
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
		
		if (testeCheck(jogadorAtual)) {
			movimentacaoNegada(origem, destino, pecaCapturada);
			throw new ChessException("Voce não pode se colocar em check");
		}
		
		check = (testeCheck(oponente(jogadorAtual))) ? true : false;
		
		if(testeCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		}
		else {
			proximoTurno();
		}
		return (PecaDeXadrez)pecaCapturada;
	}
	
	private void movimentacaoNegada(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(destino);
		p.decrementaMovimento();
		tabuleiro.posicaoDaPeca(p, origem);
		
		if(pecaCapturada != null) {
			tabuleiro.posicaoDaPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
	}
	
	private Peca movimentacao(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(origem);
		p.incrementaMovimento();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.posicaoDaPeca(p, destino);
		
		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
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
	
	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private PecaDeXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : list) {
			if(p instanceof Rei) {
				return (PecaDeXadrez)p;
			}
		}
		throw new IllegalStateException("Não existe o rei da cor " + cor);
	}
	
	private boolean testeCheck(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoPeca().paraPosicao();
		List<Peca> pecasDoOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
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
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentoPossivel();
			for(int i =0; i<tabuleiro.getLinhas(); i++) {
				for(int j=0; j<tabuleiro.getColunas(); j++) {
					if(mat[i][j]) {
						Posicao origem = ((PecaDeXadrez)p).getPosicaoPeca().paraPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = movimentacao(origem, destino);
						boolean testeCheck = testeCheck(cor);
						movimentacaoNegada(origem, destino, pecaCapturada);
						if(!testeCheck) {
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
		posicaoDaNovaPeca('d', 1, new Rei(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('a', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('b', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('c', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('d', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('e', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('f', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('g', 2, new Piao(tabuleiro, Cor.BRANCO));
		posicaoDaNovaPeca('h', 2, new Piao(tabuleiro, Cor.BRANCO));

		posicaoDaNovaPeca('d', 8, new Rei(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('a', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('b', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('c', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('d', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('e', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('f', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('g', 7, new Piao(tabuleiro, Cor.PRETO));
		posicaoDaNovaPeca('h', 7, new Piao(tabuleiro, Cor.PRETO));
	}
}
