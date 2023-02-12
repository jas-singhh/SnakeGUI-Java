package game.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

	private final int UNIT = 25;
	
	private Timer timer;	
	private Random random;
	
	private int appleX, appleY;
	
	private int w = GameFrame.WIDTH;
	private int h = GameFrame.HEIGHT;
	private int xCentre, yCentre;
	
	private boolean isRunning;
	
	// Apple
	private Rectangle apple;
	
	// Snake
	private Rectangle head, part2;
	private ArrayList<Rectangle> snakeBody;
	private boolean right, left, up, down;
	
	// Score
	private int score;
	
	GamePanel() {
		setup();
		
		timer = new Timer(70, this);
		random = new Random();
		
		isRunning = false;
		
		appleX = (random.nextInt(w) / UNIT) * UNIT;
		appleY = (random.nextInt(h) / UNIT) * UNIT;
		
		xCentre = w / 2;
		yCentre = h / 2;
		
		timer.start();
		
		apple = new Rectangle(appleX, appleY, UNIT, UNIT);
		
		snakeBody = new ArrayList<>();
		right = true;
		left = false;
		up = false;
		down = false;
		
		head = new Rectangle(xCentre, yCentre, UNIT, UNIT);
		snakeBody.add(head);
		part2 = new Rectangle(snakeBody.get(snakeBody.size() - 1).x - UNIT, yCentre, UNIT, UNIT);
		snakeBody.add(part2);
		part2 = new Rectangle(snakeBody.get(snakeBody.size() - 1).x - UNIT, yCentre, UNIT, UNIT);
		snakeBody.add(part2);	
		
		score = 0;
	}
	
	private void setup() {
		this.setPreferredSize(new Dimension(GameFrame.WIDTH, GameFrame.HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(true);
		addKeyListener(this);
	}  
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Start text
		if (!isRunning) {// initial screen
			g.setColor(Color.yellow.darker());
			g.setFont(new Font("Arial", Font.ITALIC, 20));
			g.drawString("Welcome to the snake game", xCentre - 120, yCentre - 50);
			g.drawString("Press space to start", xCentre - 90, yCentre - 20);
		} else {
		
		drawApple(g);
	
		// Snake
		for (int i = 0; i < snakeBody.size(); i++) {
			if (i == 0) {
				g.setColor(Color.green.darker());
				g.fillRect(snakeBody.get(i).x, snakeBody.get(i).y, snakeBody.get(i).width, snakeBody.get(i).height);
			} else {
				g.setColor(Color.GREEN);
				g.fillRect(snakeBody.get(i).x, snakeBody.get(i).y, snakeBody.get(i).width, snakeBody.get(i).height);
			}
		}
		
		// Score
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 25));
		g.drawString("Score: " + score, w - 120, UNIT);
		}
	}
	
	private void drawApple(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(apple.x, apple.y, apple.width, apple.height);
	}
	
	private void moveSnake() {
		
		for (int i = snakeBody.size() - 1; i > 0; i--) {
			snakeBody.get(i).x = snakeBody.get(i - 1).x;
			snakeBody.get(i).y = snakeBody.get(i - 1).y;
		}
		
		Rectangle head = snakeBody.get(0);

		if (right) head.x += UNIT;
		else if (down) head.y += UNIT;
		else if (left) head.x -= UNIT;
		else if (up) head.y -= UNIT;
		
		if (head.x >= w) head.x = 0;
		else if (head.x < 0) head.x = w;
		
		if (head.y >= h) head.y = 0;
		else if (head.y < 0) head.y = h;
	}
	
	private boolean isOnApple(Rectangle snake) {
		if (apple.intersects(snake)) {
			return true;
		}
		return false;
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_SPACE) isRunning = true;
		
		if (key == KeyEvent.VK_RIGHT && !left) {
			right = true;
			up = false;
			left = false;
			down = false;
		} else if (key == KeyEvent.VK_DOWN && !up) {
			down = true;
			up = false;
			right = false;
			left = false;
		} else if (key == KeyEvent.VK_LEFT && !right) {
			left = true;
			up = false;
			right = false;
			down = false;
		} else if (key == KeyEvent.VK_UP && !down) {
			up = true;
			right = false;
			left = false;
			down = false;
		}
		
		
		repaint();
	}


	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isRunning) {
			moveSnake();
			checkCollision();
			checkSelfCollision();
		}
		repaint();
	}
	
	private void checkCollision() {
		Rectangle head = snakeBody.get(0);
		Rectangle last = snakeBody.get(snakeBody.size() - 1);
		
		Rectangle newPart = null;
		
		if (right) newPart = new Rectangle(last.x - UNIT, last.y, UNIT, UNIT);
		else if (down) newPart = new Rectangle(last.x, last.y - UNIT, UNIT, UNIT);
		else if (left) newPart = new Rectangle(last.x + UNIT, last.y, UNIT, UNIT);
		else if (up) newPart = new Rectangle(last.x, last.y + UNIT, UNIT, UNIT);
		
		if (isOnApple(head)) {
			score++;
			spawnApple();
			snakeBody.add(newPart);
		}
	}
	
	private void spawnApple() {
		appleX = (random.nextInt(w) / UNIT) * UNIT;
		appleY = (random.nextInt(h) / UNIT) * UNIT;
		
		apple.x = appleX;
		apple.y = appleY;
	}
	
	private void checkSelfCollision() {
		Rectangle head = snakeBody.get(0);
		for (int i = 1; i < snakeBody.size(); i++) {
			if (snakeBody.get(i).intersects(head)) {
				isRunning = false;
			}
		}
	}

	
	@Override
	public void keyTyped(KeyEvent e) {}
	
}