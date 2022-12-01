public final class Viewport
{
    private int row;
    private int col;
    private int numRows;
    private int numCols;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public void shift(Viewport viewport, int col, int row) {
        viewport.col = col;
        viewport.row = row;
    }

    public Point viewportToWorld(Viewport viewport, int col, int row) {
        return new Point(col + viewport.col, row + viewport.row);
    }

    public Point worldToViewport(Viewport viewport, int col, int row) {
        return new Point(col - viewport.col, row - viewport.row);
    }

    public boolean contains(Viewport viewport, Point p) {
        return p.getY() >= viewport.row && p.getY() < viewport.row + viewport.numRows
                && p.getX() >= viewport.col && p.getX() < viewport.col + viewport.numCols;
    }
}
