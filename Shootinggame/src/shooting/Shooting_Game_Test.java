package shooting;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

class GraphicObject2 extends Thread{
	BufferedImage img = null;
	int x = 0, y = 0;
	
	public GraphicObject2(String name) {
		try {
			img = ImageIO.read(new File(name));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
	public void update() {}
	//화면에 이미지
	public void draw(Graphics g) {
		//drawimage (이미지, 좌특 상단 x, 좌측상단 y, 일반적으로 null 값)
		g.drawImage(img, x, y, null);
	}
	public void keyPressed(KeyEvent event) {
		}
}
//GraphicObject상속, 미사일을 나타내는 클래스 정의
class Missile2 extends GraphicObject2 {
	boolean launched = false;
	
	public Missile2(String name) {
		super(name);
		y = -200;
	}
	public void update() {
		//미사일 위치를 변경
		if (launched) //미사일 속도
			y -= 20;
		if (y < -1000)
			launched = false;
	}
	//스페이스키가 눌리면 미사일 발사
	public void keyPressed(KeyEvent event, int x, int y) {
		if (event.getKeyCode() == KeyEvent.VK_SPACE) {
			launched = true;
			this.x = x;
			this.y = y;
		}
	}
}
//필살기 미사일 발사
class strongMissile extends GraphicObject2{
	boolean launched = false;
	
	public strongMissile(String name) {
		super(name);
		y = -200;
	}
	public void update() {
		if(launched)
			y -= 20;
		if (y < -1000)
			launched = false;
	}
	public void keyPressed(KeyEvent event, int x, int y) {
		if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
			launched = true;
			this.x = x;
			this.y = y;
		}
	}
	}
//GraphicObject상속, Enemy클래스 작성
class Enemy2 extends GraphicObject2 {
	int dx = -10;
	public Enemy2(String name, int y) {
		super(name);
		x = 500;
		this.y = y;
	}
	//Enemy 캐릭터의 위치 변경
	public void update() {
		x += dx;
		if (x  < 0)
			dx = +10;
		if (x > 500)
			dx = -10;
	}
}
//GraphicObject 상속, SpaceShip 클래스 작성
class SpaceShip2 extends GraphicObject2 {
	public SpaceShip2(String name) {
		super(name);
		x = 150;
		y = 350;
	}
	//화살표 키에 따라 플레이어 캐릭터 위치 변경
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			x -= 10;
		}
		if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			x += 10;
		}
		if (event.getKeyCode() == KeyEvent.VK_UP) {
			y -= 10;
		}
		if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			y += 10;
		}
	}
	public void keyPressed2(boolean dir[]) {
		if (dir[0]) {
			x -= 10;
		}
		if (dir[2]) {
			x += 10;
		}
		if (dir[1]) {
			y -= 10;
		}
		if (dir[3]) {
			y += 10;
		}
	}
}
class Background2 extends GraphicObject2 {
	public Background2(String name) {
		super(name);
		x = 0;
		y = 0;
	}
}
class MyPanel2 extends JPanel implements KeyListener {
	ArrayList<Enemy2> enemy = new ArrayList<Enemy2>();
	SpaceShip2 spaceship;
	Missile2 missile[] = new Missile2[100];
	strongMissile smissile[] = new strongMissile[20]; //필살기
	boolean direction[] = new boolean[4];
	Background2 background;
	
	
	public MyPanel2() {
		super();
		this.addKeyListener(this);
		this.requestFocus();
		setFocusable(true);
		
		//enemy = new Enemy2("enemy.png");
		spaceship = new SpaceShip2("spaceship.png");
		for (int i = 0; i < 100; i++)
		{
			missile[i] = new Missile2("missile.png");
		}
		for (int i = 0; i < 20; i++)
		{
			smissile[i] = new strongMissile("strong.jpg");
		}
		for (int i = 0; i<4; i++)
			direction[i] = false;
		background = new Background2("background.png");
		
		
		//스레드를 이용하여 게임의 메인 루프를 작성
		//각객체의 위치를 변경하고자 다시 그린다.
		class MyThread extends Thread {
			public void run() {
				while (true) {
					collision();
					for(int i = 0; i < enemy.size(); i++) {
					enemy.get(i).update();
					}
					spaceship.update();
					spaceship.keyPressed2(direction);
					for (int i = 0; i < 100; i++) {
					if (missile[i].launched)
						missile[i].update();
					}
					for (int i = 0; i < 20; i++) {
						if (smissile[i].launched)
							smissile[i].update();
						}
					repaint();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
			}
		}
		Thread t =new MyThread();
		t.start();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (enemy.size() <= 10) {
					Enemy2 e = new Enemy2("enemy.png",(int)(Math.random() * 200));
					enemy.add(e);
				}
				else {
					// enemy가 10 초과시 spaceship 폭파 코딩 부분 (ing)
					SpaceShip2 spaceship = new SpaceShip2("spaceship.png");
					
				}
			}
		},0,1000);
	}
	public void paint(Graphics g) {
		super.paint(g);
		background.draw(g);
		for (int i =0; i< enemy.size(); i++) {
		enemy.get(i).draw(g);
		}
		spaceship.draw(g);
		for (int i =0; i< 100; i++) {
		if (missile[i].launched)
			missile[i].draw(g);
		}
		for (int i =0; i< 20; i++) {
			if (smissile[i].launched)
				smissile[i].draw(g);
			}
	}
	public void collision() {
		for(int i = 0; i < 100; i++) {
			if (missile[i].launched) {
				for (int j = 0; j < enemy.size(); j++) {
					//enemy의 현 위치를 네모 메소드로 구함
					Rectangle rect = new Rectangle(enemy.get(j).x, enemy.get(j).y, 
							enemy.get(j).img.getWidth(),enemy.get(j).img.getHeight());
					if(rect.contains(missile[i].x, missile[i].y)) { 
						// 해당 적의 위치와 미사일의 위치가 같을경우 적 제거
						enemy.remove(j);
						missile[i].launched = false;
					}
				}
			}
		}
		for(int i = 0; i < 20; i++) { //필살기 
			if (smissile[i].launched) {
				for (int j = 0; j < enemy.size(); j++) {
					Rectangle rect = new Rectangle(enemy.get(j).x, enemy.get(j).y, 
					enemy.get(j).img.getWidth(),enemy.get(j).img.getHeight());
					if(rect.contains(smissile[i].x, smissile[i].y)) {
						enemy.removeAll(enemy); //모든 적 제거
						smissile[i].launched = false;
					}
				}
			}
		}
	}
	//키보드 이벤트를 각 객체에 전달
	public void keyPressed(KeyEvent event) {
		//spaceship.keyPressed(event);
		switch (event.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			direction[0] = true;
			break;
		case KeyEvent.VK_UP:
			direction[1] = true;
			break;
		case KeyEvent.VK_RIGHT:
			direction[2] = true;
			break;
		case KeyEvent.VK_DOWN:
			direction[3] = true;
			break;
		}
		int i =0;
		for(i =0; i < 100; i++) { //미사일 발사 위치
			if (!missile[i].launched) {
		missile[i].keyPressed(event, spaceship.x + (spaceship.img.getWidth() /2), spaceship.y);
		break;
			}
		}
		for(i =0; i < 20; i++) { //필살기 발사 위치
			if (!smissile[i].launched) {
		smissile[i].keyPressed(event, spaceship.x-40 + (spaceship.img.getWidth() /2), spaceship.y -20);
		break;
			}
		}
	}
	public void keyReleased(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			direction[0] = false;
			break;
		case KeyEvent.VK_UP:
			direction[1] = false;
			break;
		case KeyEvent.VK_RIGHT:
			direction[2] = false;
			break;
		case KeyEvent.VK_DOWN:
			direction[3] = false;
			break;
		}
	}
	public void keyTyped(KeyEvent arg0) {
	}
}
public class Shooting_Game_Test extends JFrame {
	public Shooting_Game_Test() {
		setTitle("shooting game2");
		add(new MyPanel2());
		setSize(500,500);
		setVisible(true);
	}
	public static void main(String[] args) {
		new Shooting_Game_Test();
	}
}