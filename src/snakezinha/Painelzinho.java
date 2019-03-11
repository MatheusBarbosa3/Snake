/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakezinha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import static java.lang.Byte.SIZE;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.omg.CORBA.ORB;

/**
 *
 * @author USER
 */
public class Painelzinho extends JPanel implements Runnable, KeyListener {

    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;

    //loop do gamezin
    private Thread thread;
    private boolean direcao;
    private long tempoDoAlvo;

    //renderização
    private Graphics2D g2d;
    private BufferedImage image;

    //objetos do jogo
    private final int SIZE = 10;
    Entity head, apple;
    ArrayList<Entity> cobrinha;
    private int pontuacao;
    private int level;
    private boolean gameOver;

    //movimento
    private int dx, dy;

    //botoes de entrada
    private boolean up, down, right, left, start;

    public Painelzinho() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        requestFocus();
    }

    public void addNotify() {
        super.addNotify();
        thread = new Thread(this);
        thread.start();
    }

    private void setFPS(int fps) {
        tempoDoAlvo = 1000 / fps;
    }

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        if (k == KeyEvent.VK_UP) {
            up = true;
        }
        if (k == KeyEvent.VK_DOWN) {
            down = true;
        }
        if (k == KeyEvent.VK_LEFT) {
            left = true;
        }
        if (k == KeyEvent.VK_RIGHT) {
            right = true;
        }
        if (k == KeyEvent.VK_ENTER) {
            start = true;
        }

    }

    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();

        if (k == KeyEvent.VK_UP) {
            up = false;
        }
        if (k == KeyEvent.VK_DOWN) {
            down = false;
        }
        if (k == KeyEvent.VK_LEFT) {
            left = false;
        }
        if (k == KeyEvent.VK_RIGHT) {
            right = false;
        }
        if (k == KeyEvent.VK_ENTER) {
            start = false;
        }

    }

    @Override
    public void run() {
        if (direcao) {
            return;
        }
        init();
        long iniciarTempo;
        long decorrido;
        long esperar;
        while (direcao) {
            iniciarTempo = System.nanoTime();
            decorrido = System.nanoTime() - iniciarTempo;
            esperar = tempoDoAlvo - decorrido / 1000000;

            update();
            requestRender();

            if (esperar > 0) {
                try {
                    Thread.sleep(esperar);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    private void init() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        direcao = true;
        SetUpLevel();

    }

    private void SetUpLevel() {
        cobrinha = new ArrayList<Entity>();
        head = new Entity(SIZE);
        head.setPosition(WIDTH / 2, HEIGHT / 2);
        cobrinha.add(head);

        for (int i = 1; i < 3; i++) {
            Entity e = new Entity(SIZE);
            e.setPosition(head.getX() + (i * SIZE), head.getY());
            cobrinha.add(e);
        }
        apple = new Entity(SIZE);
        setApple();
        pontuacao = 0;
        level = 1;
        gameOver = false;
        dx = dy = 0;
        setFPS(level * 20);
    }

    public void setApple() {
        int x = (int) (Math.random() * (WIDTH - SIZE));
        int y = (int) (Math.random() * (HEIGHT - SIZE));
        x = x - (x % SIZE);
        y = y - (y % SIZE);
        apple.setPosition(x, y);

    }

    private void update() {
        if (gameOver) {
            if (start) {
                SetUpLevel();
            }
            return;
        }

        if (up && dy == 0) {
            dy = -SIZE;
            dx = 0;
        }
        if (down && dy == 0) {
            dy = SIZE;
            dx = 0;
        }
        if (left && dx == 0) {
            dy = 0;
            dx = -SIZE;
        }
        if (right && dx == 0) {
            dy = 0;
            dx = SIZE;
        }

        if (dx != 0 || dy != 0) {
            for (int i = cobrinha.size() - 1; i > 0; i--) {
                cobrinha.get(i).setPosition(
                        cobrinha.get(i - 1).getX(),
                        cobrinha.get(i - 1).getY());
            }
            head.move(dx, dy);
        }
        for (Entity e : cobrinha) {
            if (e.isCollision(head)) {
                gameOver = true;
                break;

            }
        }
        if (apple.isCollision(head)) {
            pontuacao++;
            setApple();

            Entity e = new Entity(SIZE);
            e.setPosition(-100, -100);
            cobrinha.add(e);

            if (pontuacao % 10 == 0) {
                level++;
                if (level > 10) {
                    level = 10;
                    setFPS(level * 10);
                }
            }
        }

        if (head.getX() < 0) {
            head.setX(WIDTH);
        }
        if (head.getY() < 0) {
            head.setY(HEIGHT);
        }
        if (head.getX() > WIDTH) {
            head.setX(0);
        }
        if (head.getY() > HEIGHT) {
            head.setY(0);
        }

    }

    private void requestRender() {
        render(g2d);
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    public void render(Graphics2D g2d) {
        g2d.clearRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.GREEN);
        for (Entity e : cobrinha) {
            e.render(g2d);

        }
        g2d.setColor(Color.RED);
        apple.render(g2d);
        if (gameOver) {
            g2d.drawString(" Se fodeu! ", 160, 250);
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString(" Pontuação: " + pontuacao + "  " + " Nível: " + level, 10, 10);
        if (dx == 0 && dy == 0) {
            g2d.drawString(" Só vai Porran! ", 160, 150);
        }
        for (Entity e : cobrinha) {
            head.render(g2d);
            g2d.setColor(Color.BLUE);

        }
    }

}
