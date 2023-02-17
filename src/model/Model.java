package model;

import actions.Move;
import actions.MoveEfficiency;
import entity.Tile;

import java.util.*;


public class Model {
    private static final int FIELD_WIDTH = 4;//ширина игрового поля
    private Tile[][] gameTiles;
    public int score = 0;
    public int maxTile = 2;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int randomTileIndex = (int) (Math.random() * emptyTiles.size());
            emptyTiles.get(randomTileIndex).value = (Math.random() < 0.9) ? 2 : 4;
        }
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(new Move() {
            @Override
            public void move() {
                left();
            }
        }));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        priorityQueue.poll().getMove().move();
    }

    //будет возвращать true, если вес плиток в массиве gameTiles отличается от веса плиток в верхнем массиве стека previousStates
    public boolean hasBoardChanged() {
        Tile[][] clone = previousStates.peek();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                if (gameTiles[i][j].value != clone[i][j].value)
                    return true;
            }
        }
        return false;
    }

    //возвращает объект типа MoveEfficiency описывающий эффективность переданного хода
    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency moveEfficiency = null;
        if (!hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        } else {
            int EmptyTiles = 0;
            for (int i = 0; i < gameTiles.length; i++) {
                for (int j = 0; j < gameTiles.length; j++) {
                    if (gameTiles[i][j].isEmpty())
                        EmptyTiles++;
                }
            }
            moveEfficiency = new MoveEfficiency(EmptyTiles, score, move);
        }
        rollback();
        return moveEfficiency;
    }

    //игре возможность самостоятельно выбирать следующий ход
    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                up();
                break;
            case 1:
                right();
                break;
            case 2:
                down();
                break;
            case 3:
                left();
                break;
            default:
                break;
        }
    }

    public void saveState(Tile[][] tiles) {
        Tile[][] clone = new Tile[4][4];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                Tile a = new Tile();
                a.value = tiles[i][j].value;
                clone[i][j] = a;
            }
        }
        previousStates.push(clone);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty())
                    emptyTiles.add(gameTiles[i][j]);
            }
        }
        return emptyTiles;
    }

    public void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) return true;

        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 1; j < gameTiles.length; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j - 1].value)
                    return true;
            }
        }

        for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 1; i < gameTiles.length; i++) {
                if (gameTiles[i][j].value == gameTiles[i - 1][j].value)
                    return true;
            }
        }
        return false;
    }

    private boolean compressTiles(Tile[] tiles) {
        Tile[] clone = tiles.clone();
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value == 0 && i < tiles.length - 1 && tiles[i + 1].value != 0) {
                Tile temp = tiles[i];
                tiles[i] = tiles[i + 1];
                tiles[i + 1] = temp;
                i = -1;
            }
        }
        for (int i = 0; i < clone.length; i++) {
            if (clone[i].value != tiles[i].value) return true;
        }
        return false;
    }

    private boolean mergeTiles(Tile[] tiles) {
        Tile[] clone = tiles.clone();
        for (int i = 1; i < tiles.length; i++) {
            if ((tiles[i - 1].value == tiles[i].value) && !tiles[i - 1].isEmpty() && !tiles[i].isEmpty()) {

                tiles[i - 1].value *= 2;
                if (tiles[i - 1].value > maxTile) {
                    maxTile = tiles[i - 1].value;
                }
                score += tiles[i - 1].value;
                tiles[i] = new Tile();

                compressTiles(tiles);
            }
        }
        for (int i = 0; i < clone.length; i++) {
            if (clone[i].value != tiles[i].value) return true;
        }
        return false;
    }

    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }
        if (isChanged) addTile();
        isSaveNeeded = true;
    }

    public void right() {
        saveState(gameTiles);
        rotateleft();
        rotateleft();
        left();
        rotateleft();
        rotateleft();
    }

    private void rotateleft() {
        // rotate
        for (int k = 0; k < FIELD_WIDTH / 2; k++) // border -> center
        {
            for (int j = k; j < FIELD_WIDTH - 1 - k; j++) // left -> right
            {
                // меняем местами 4 угла
                Tile tmp = gameTiles[k][j];
                gameTiles[k][j] = gameTiles[j][FIELD_WIDTH - 1 - k];
                gameTiles[j][FIELD_WIDTH - 1 - k] = gameTiles[FIELD_WIDTH - 1 - k][FIELD_WIDTH - 1 - j];
                gameTiles[FIELD_WIDTH - 1 - k][FIELD_WIDTH - 1 - j] = gameTiles[FIELD_WIDTH - 1 - j][k];
                gameTiles[FIELD_WIDTH - 1 - j][k] = tmp;
            }
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public void up() {
        saveState(gameTiles);
        rotateleft();
        left();
        rotateleft();
        rotateleft();
        rotateleft();
    }

    public void down() {
        saveState(gameTiles);
        rotateleft();
        rotateleft();
        rotateleft();
        left();
        rotateleft();
    }

}
