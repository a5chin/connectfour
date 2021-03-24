import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.applet.*;


public class Sample extends JPanel {
    static final int WIDTH = 1500;
    static final int HEIGHT = 1000;
    static final int SquareSize = 150;
    static final int VERTICAL_MARGIN = 50;
    static final int HORIZONTAL_MARGIN = 70;
    static final int BLUE = -1;
    static final int EMPTY = 0;
    static final int RED = 1;
    static final int MODE_TITLE = 0;
    static final int MODE_GAME = 1;
    static final int MODE_OVER = 2;
    static final int CONTINUE = 0;
    private AudioClip ac;

    int mode = MODE_GAME;
    int turn = RED;
    int winner = CONTINUE;

    int[][] board = {
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
    };

    public Sample() {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        addMouseListener(new MouseProc());
        ac = Applet.newAudioClip(getClass().getResource("fanfare.wav"));
    }

    public void initGame() {
        turn = RED;
        winner = CONTINUE;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = 0;
            }
        }
    }

    public void paintComponent(Graphics g) {
        Font font1 = new Font("‚l‚r ‚oƒSƒVƒbƒN",Font.PLAIN,50);
        Font font2 = new Font("‚l‚r ‚oƒSƒVƒbƒN",Font.PLAIN,100);

        g.setColor(new Color(200, 200, 200, 161));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < 6; i++) {
            int y = VERTICAL_MARGIN + SquareSize * i;
            for (int j = 0; j < 7; j++) {
                int x = HORIZONTAL_MARGIN + SquareSize * j;
                g.setColor(new Color(249, 199, 112));
                g.fillRect(x, y, SquareSize, SquareSize);
                g.setColor(Color.black);
                g.drawRect(x, y, SquareSize, SquareSize);
                if (board[i][j] != EMPTY) {
                    if (board[i][j] == RED) {
                        g.setColor(Color.red);
                    } else if (board[i][j] == BLUE) {
                        g.setColor(Color.blue);
                    }
                } else {
                    g.setColor(Color.gray);
                }
                g.fillOval(x + SquareSize / 10, y + SquareSize / 10, SquareSize * 8 / 10, SquareSize * 8 / 10);
            }
        }

        g.setFont(font1);

        if (winner == CONTINUE) {
            if (turn == RED) {
                g.setColor(Color.red);
                g.drawString("Ô‚Ì”Ô‚Å‚·", 1150, 500);
            }
            else if (turn == BLUE) {
                g.setColor(Color.blue);
                g.drawString("Â‚Ì”Ô‚Å‚·", 1150, 500);
            }
        } else if (winner == RED) {
            g.setFont(font2);
            g.setColor(Color.MAGENTA);
            ac.play();
            g.drawString("Ô‚ÌŸ‚¿!", 400, 500);
        } else if (winner == BLUE) {
            g.setFont(font2);
            g.setColor(Color.CYAN);
            ac.play();
            g.drawString("Â‚ÌŸ‚¿!", 400, 500);
        } else {
            g.setFont(font2);
            g.drawString("ˆø‚«•ª‚¯‚Å‚·", 400, 500);
        }
    }

    class MouseProc extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (mode == MODE_TITLE) {
                mode = MODE_GAME;
                initGame();
            } else if (mode == MODE_GAME) {
                int x = e.getX();
                int y = e.getY();
                int col = (x - HORIZONTAL_MARGIN) / SquareSize;
                int row = (y - VERTICAL_MARGIN) / SquareSize;

                for (int i = 5; 0 <= i; i--) {
                    if (board[i][col] == EMPTY && winner == CONTINUE) {
                        board[i][col] = turn;
                        break;
                    }
                }
                if (judge(turn, col, row) == CONTINUE) {
                    mode = MODE_GAME;
                } else {
                    winner = judge(turn, col, row);
                    mode = MODE_OVER;
                }

                if (mode == MODE_OVER) {
                    mode = MODE_TITLE;
                }

                turn = opponent(turn);
            } else {
                mode = MODE_TITLE;
            }
            repaint();
        }
    }

    public int opponent(int turn) {
        return -turn;
    }

    public int countDisc(int turn, int row, int col, int dx, int dy) {
        int count = 0;
        int x = col + dx;
        int y = row + dy;

        while (0 <= x && x < 7 && 0 <= y && y < 6) {
            if (board[y][x] == turn){
                count++;
            } else if (board[y][x] == opponent(turn) || board[y][x] == EMPTY) {
                return count;
            }
            x += dx;
            y += dy;
        }
        return count;
    }

    public int judge(int turn, int col, int row) {
        int[] dx = {-1, 0, 1,-1, 1,-1, 0, 1};
        int[] dy = {-1,-1,-1, 0, 0, 1, 1, 1};
        int count = 1;

        for (int i = 0; i < 4; i++) {
            count = 1;
            count += countDisc(turn, row, col, dx[i], dy[i]);
            count += countDisc(turn, row, col, dx[7 - i], dy[7 - i]);
            if (count >= 4) {
                break;
            }
        }
        
        if (count >= 4) {
            return turn;
        }
        else {
            return CONTINUE;
        }
    }

    public static void main(String[] args) {
            JFrame f = new JFrame();
            ImageIcon icon = new ImageIcon("./icon.png");
            f.setIconImage(icon.getImage());
            f.getContentPane().setLayout(new FlowLayout());
            f.getContentPane().add(new Sample());
            f.pack();
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
    }
}
