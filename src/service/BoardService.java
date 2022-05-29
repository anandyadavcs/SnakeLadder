package service;

import model.Board;
import model.Ladder;
import model.Player;
import model.Snake;

import java.util.*;

public class BoardService {
    private Board board;
    private Queue<Player> players;
    private boolean isGameCompleted;

    private static final int DEFAULT_BOARD_SIZE = 100;

    public BoardService(int boardSize) {
        this.board = new Board(boardSize);
        this.players = new LinkedList<>();
    }

    public BoardService() {
        this.board = new Board(DEFAULT_BOARD_SIZE);
    }

    public void setPlayers(List<Player> players) {
        this.players = new LinkedList<>();
        Map<String, Integer> playerPositions = new HashMap<>();
        for (Player player : players) {
            this.players.add(player);
            playerPositions.put(player.getId(), 0);
        }
        board.setPlayerListAndPosition(playerPositions);
    }

    public void setSnakes(List<Snake> snakes) {
        board.setSnakes(snakes);
    }

    public void setLadders(List<Ladder> ladders) {
        board.setLadders(ladders);
    }

    private int getNewPostionAfterDiceRoll() {
        return DiceService.roll();
    }

    private void movePlayer(Player player, int position) {
        int oldPosition = board.getPlayerListAndPosition().get(player.getId());
        int newPostion = oldPosition + position;

        int boardSize = board.getSize();

        if (newPostion > boardSize) {
            newPostion = oldPosition;
        } else {
            newPostion = getNewPostionAfterGoingThroughSnakesAndLadders(newPostion);
        }
        board.getPlayerListAndPosition().put(player.getId(), newPostion);
        System.out.println("Player " + player.getName() + " moved from " + oldPosition + " to " + newPostion);
    }

    private int getNewPostionAfterGoingThroughSnakesAndLadders(int newPostion) {
        int previousPostion;
        do {
            previousPostion = newPostion;
            for (Snake snake : board.getSnakes()) {
                if (snake.getStart() == newPostion) {
                    newPostion = snake.getEnd();
                }
            }
            for (Ladder ladder : board.getLadders()) {
                if (ladder.getStart() == newPostion) {
                    newPostion = ladder.getEnd();
                }
            }
        } while (newPostion != previousPostion);
        return newPostion;
    }

    private boolean hasPlayerWon(Player player) {
        int playerPostion = board.getPlayerListAndPosition().get(player.getId());
        return board.getSize() == playerPostion;
    }

    private boolean isGameOver() {
        return isGameCompleted;
    }

    public void startGame() {
        while (!isGameOver()) {
            int diceValue = getNewPostionAfterDiceRoll();
            Player currentPlayer = players.poll();
            movePlayer(currentPlayer, diceValue);
            if (hasPlayerWon(currentPlayer)) {
                System.out.println(currentPlayer.getName() + " has won the game");
                isGameCompleted = true;
            } else {
                players.add(currentPlayer);
            }
        }
    }
}
