import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CheckersGUI extends JPanel {

    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 50;
    private int currentPlayer = 1;
    private final Color lightColor = Color.WHITE;
    private final Color darkColor = Color.BLACK;
    private final Color pieceColor = Color.RED;
    private final Color darkPieceColor = Color.GREEN;

    private final int[][] initialPositions = {
            {0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {2, 0, 2, 0, 2, 0, 2, 0},
            {0, 2, 0, 2, 0, 2, 0, 2},
            {2, 0, 2, 0, 2, 0, 2, 0}
    };

    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean pieceSelected = false;

    public CheckersGUI() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getPoint().y / TILE_SIZE;
                int col = e.getPoint().x / TILE_SIZE;

                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    if (initialPositions[row][col] == currentPlayer) {
                        if (pieceSelected) {
                            if (isValidMove(selectedRow, selectedCol, row, col)) {
                                movePiece(selectedRow, selectedCol, row, col);
                                pieceSelected = false;
                                currentPlayer = (currentPlayer == 1) ? 2 : 1; // Переключаем игрока
                                repaint();
                            } else {
                                selectedRow = -1;
                                selectedCol = -1;
                                pieceSelected = false;
                                repaint();
                            }
                        } else {
                            selectedRow = row;
                            selectedCol = col;
                            pieceSelected = true;
                            repaint();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(lightColor);
                } else {
                    g.setColor(darkColor);
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (initialPositions[row][col] == 1) {
                    g.setColor(pieceColor);
                    g.fillOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                }
                if (initialPositions[row][col] == 2) {
                    g.setColor(darkPieceColor);
                    g.fillOval(col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                }
            }
        }
    }

    private boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        // Проверяем, что конечная клетка находится на доске
        if (endRow < 0 || endRow >= BOARD_SIZE || endCol < 0 || endCol >= BOARD_SIZE) {
            return false;
        }

        // Проверяем, что конечная клетка пуста
        if (initialPositions[endRow][endCol] != 0) {
            return false;
        }

        // Получаем разницу между начальной и конечной позициями
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        // Проверяем, что фишка движется только по диагонали
        if (rowDiff != colDiff) {
            return false;
        }

        // Определяем направление движения фишки (вверх или вниз)
        int rowDirection = (endRow - startRow) / rowDiff;
        int colDirection = (endCol - startCol) / colDiff;

        // Проверяем, что фишка движется в правильном направлении
        if (initialPositions[startRow][startCol] == 1 && rowDirection != -1) {
            return false; // Белые фишки могут двигаться только вверх
        }
        if (initialPositions[startRow][startCol] == 2 && rowDirection != 1) {
            return false; // Черные фишки могут двигаться только вниз
        }

        // Если фишка двигается только на одну клетку вперед по диагонали
        if (rowDiff == 1) {
            return true;
        }

        // Если фишка пытается сделать ход на несколько клеток, проверяем, есть ли фишка, которую можно побить
        int middleRow = startRow + rowDirection;
        int middleCol = startCol + colDirection;

        // Проверяем, что фишка для битья находится на доске и принадлежит другому игроку
        if (middleRow >= 0 && middleRow < BOARD_SIZE && middleCol >= 0 && middleCol < BOARD_SIZE) {
            if (initialPositions[middleRow][middleCol] != 0 && initialPositions[middleRow][middleCol] != initialPositions[startRow][startCol]) {
                // Проверяем, что клетка за фишкой для битья пуста
                int nextRow = middleRow + rowDirection;
                int nextCol = middleCol + colDirection;
                if (nextRow >= 0 && nextRow < BOARD_SIZE && nextCol >= 0 && nextCol < BOARD_SIZE) {
                    if (initialPositions[nextRow][nextCol] == 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void movePiece(int startRow, int startCol, int endRow, int endCol) {
        initialPositions[endRow][endCol] = initialPositions[startRow][startCol];
        initialPositions[startRow][startCol] = 0;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(415, 440);
        frame.getContentPane().add(new CheckersGUI());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}