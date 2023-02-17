package actions;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;//кол-во пуст. клеток
    private int score;
    private Move move;

    public Move getMove() {
        return move;
    }

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {

        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        if (numberOfEmptyTiles != o.numberOfEmptyTiles) {
            return Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles);
        } else {
            return Integer.compare(score, o.score);
        }
    }
}
