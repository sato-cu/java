import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.Random;
import javax.swing.JLabel;
import java.util.Date;
import java.util.TimerTask;
import java.util.Calendar;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

//Java Sound API clip ���g�p���邽�߂�import
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class Ex_15_ShabonGame extends JFrame {
	final int windowWidth = 1000;
	final int windowHeight = 600;
	
	// main ���\�b�h�́A�v���O���������s���鎞�ɍŏ��ɌĂяo����郁�\�b�h
	// ���̃��\�b�h���ɑ��̃N���X�̃I�u�W�F�N�g���쐬����L�q�����Ă������ƂŃv���O�����͓��삷��
	public static void main(String[] args) throws MalformedURLException, InterruptedException, Exception{
		new Ex_15_ShabonGame();
	}
	
	public Ex_15_ShabonGame() {

		int yesno = JOptionPane.showConfirmDialog(null, "�Q�[���N��", "�Z���V���{���ʃQ�[���Z��", JOptionPane.YES_NO_OPTION);
        print(yesno);
		
		Dimension dimOfScreen = Toolkit.getDefaultToolkit().getScreenSize();

		// ������ʂ�\������
		GameWindow gw = new GameWindow("�Q�[���̐���",600, 400);
		gw.setVisible(true);
		gw.change(new MyJPanel2());
		
		try {
			Thread.sleep(6000); // 6�b�������X���[�v
		} catch (InterruptedException e) {
		}
		
		// ������ʂ��\���ɂ���
		gw.setVisible(false);

		/* setBounds �E�B���h�E�̕��ƍ�����\���������CwindowWidth�CwindowHeight �Ƃ��C���l�̎g�p���Ȃ��� */
		setBounds(dimOfScreen.width/2 - windowWidth/2, dimOfScreen.height/2 - windowHeight/2, windowWidth, windowHeight); //after
		setResizable(false);
		setTitle("Software Development Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		MyJPanel panel= new MyJPanel();
		
		//���C�A�E�g�}�l�[�W���𖳌��ɂ���isetBounds�����f����Ȃ����Ƃ̉�����j
		panel.setLayout(null);
		
		//���C����ʂ��ĕ\��������
		Container c = getContentPane();
		c.add(panel);
		setVisible(true);
		
		/*** ���ʉ��̐ݒ� ***/
		Clip clip = createClip(new File("./wav/tw043.wav"));
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		/*** ���ʉ��̐ݒ肱���܂� ***/
		
		/*** �ʃE�B���h�E�\�� ***/
		//new TestAnim();

	}
	
	// �V���[�e�B���O�Q�[���J�n �I���{�^���̏���
	private static void print(int result) {
		switch(result) {
		case JOptionPane.YES_OPTION:                        //JOptionPane. YES_OPTION
			break;
		case JOptionPane.NO_OPTION:                         //JOptionPane. No_OPTION (exit����)
			System.exit(0);
			break;
		case JOptionPane.CLOSED_OPTION:                     //JOptionPane. Close_OPTION (exit����)
			System.exit(0);
			break;
		}
	}
	
	//�x����炷���\�b�h
	public static void bell() {
		System.out.print("\007");
		System.out.flush();
	}
	
	public class MyJPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
		/***************** �e�ϐ���錾 ************************/
		/*ex ) ActionListener ��o�^������AactionPerformed���\�b�h��K���쐬����*/
		/* �S�̂̐ݒ�Ɋւ���ϐ� */
		Dimension dimOfPanel;
		Timer timer;
		Image imgMe, imgMeMissile, imgEnemy, imgEnemyS, imgEnemyS2, imgEnemyS3, imgEnemyMissile, imgEnemyMissileS, imgEnemyMissileS2, imgEnemyMissileS3;
		Image imgCloud, imgCloud2;
		
		/*** ���@�Ɋւ���ϐ��������� ***/
		
		boolean isMyAlive;                 //���@�̉摜 �\���ϐ�
		int numOfmyMissile = 3;            //���@�̃~�T�C����
		int myHeight, myWidth;
		int myX, myY, tempMyX;             //�ʒu��\���ix����y�����擾�j, �s�����2
        int gap = 100; 
		
		/** �����_���ϐ��p **/
		// �܂��͌Œ�l 8 ��ݒ�
		int randomValue = 8;
		int ranVa;
		int rank;
		
		// �O���[�o���ϐ��i�ǂ��̃��\�b�h������Q�Ɖj�Ƃ��āA
		// �ȉ��̂悤�ɕϐ��̘g�̂ݐ錾���A�ϐ��ւ̒l����́AactionPerformed���Ŏ��{
		int randomValue2;
		
		int[] myMissileXRm = new int[randomValue];
		int[] myMissileYRm = new int[randomValue];
		int[] myMissileSpeedY = new int[randomValue];
		boolean[] isMyMissileActiveRm = new boolean[randomValue]; 
	
		boolean isMylife = false;
		/*** ���@�Ɋւ���ϐ������܂� ***/
		
		/*** �G�@�Ɋւ���ϐ��������� ***/
		//�ʏ�G�@
		int numOfEnemy = 12;
		int numOfAlive = numOfEnemy;
		int enemyWidth, enemyHeight;
		int[] enemyX = new int[numOfEnemy];
		int[] enemyY = new int[numOfEnemy];
		int[] enemyMove = new int[numOfEnemy];
		int[] enemyMissileX = new int[numOfEnemy];
		int[] enemyMissileY = new int[numOfEnemy];
		int[] enemyMissileSpeed = new int[numOfEnemy];
		boolean[] isEnemyAlive = new boolean[numOfEnemy];
		boolean[] isEnemyMissileActive = new boolean[numOfEnemy];
		
		//�����G�@
		int numOfEnemyS = 4;
		int numOfAliveS = numOfEnemyS;
		int enemyWidthS, enemyHeightS;
		int[] enemyXS = new int[numOfEnemyS];
		int[] enemyYS = new int[numOfEnemyS];
		int[] enemyMoveS = new int[numOfEnemyS];
		int[] enemyMissileXS = new int[numOfEnemyS];
		int[] enemyMissileYS = new int[numOfEnemyS];
		int[] enemyMissileSpeedS = new int[numOfEnemyS];
		boolean[] isEnemyAliveS = new boolean[numOfEnemyS];
		boolean[] isEnemyMissileActiveS = new boolean[numOfEnemyS];
		
		//�����G�@�̏o���t���O
		boolean startTeki;
		
		//�����G�@2
		int numOfEnemyS2 = 2;
		int numOfAliveS2 = numOfEnemyS2;
		int enemyWidthS2, enemyHeightS2;
		int[] enemyXS2 = new int[numOfEnemyS2];
		int[] enemyYS2 = new int[numOfEnemyS2];
		int[] enemyMoveS2 = new int[numOfEnemyS2];
		int[] enemyMissileXS2 = new int[numOfEnemyS2];
		int[] enemyMissileYS2 = new int[numOfEnemyS2];
		int[] enemyMissileSpeedS2 = new int[numOfEnemyS2];
		boolean[] isEnemyAliveS2 = new boolean[numOfEnemyS2];
		boolean[] isEnemyMissileActiveS2 = new boolean[numOfEnemyS2];
		
		//�����G�@2�̏o���t���O
		boolean startTeki2;

		//�����G�@3
		int numOfEnemyS3 = 2;
		int numOfAliveS3 = numOfEnemyS3;
		int enemyWidthS3, enemyHeightS3;
		int[] enemyXS3 = new int[numOfEnemyS3];
		int[] enemyYS3 = new int[numOfEnemyS3];
		int[] enemyMoveS3 = new int[numOfEnemyS3];
		int[] enemyMissileXS3 = new int[numOfEnemyS3];
		int[] enemyMissileYS3 = new int[numOfEnemyS3];
		int[] enemyMissileSpeedS3 = new int[numOfEnemyS3];
		boolean[] isEnemyAliveS3 = new boolean[numOfEnemyS3];
		boolean[] isEnemyMissileActiveS3 = new boolean[numOfEnemyS3];
		
		//�����G�@3�̏o���t���O
		boolean startTeki3;
		
		/*** �G�@�Ɋւ���ϐ������܂� ***/
		
		/*** ��s�D�Ɋւ���ϐ��������� ***/
		//��s�D1 �ϐ�
		int cloudWidth, cloudHeight;
		int cloudX;
		int cloudY;
		int cloudMove;
		boolean isCloudAlive;
		
		//��s�D2 �ϐ�
		int cloud2Width, cloud2Height;
		int cloud2X;
		int cloud2Y;
		int cloud2Move;
		boolean isCloud2Alive;
		
		
		//��s�D �o���t���O
		boolean cloudflg = false;
		boolean cloud2flg = false;
		
		
		/*** ��s�D�Ɋւ���ϐ������܂� ***/

		//�X�R�A�Ɋւ���ϐ�
		int score = 0;
		int scoresum;
		
		/* �{�^���Ɋւ���ϐ� */
		JButton threeButton, oneButton, randomButton;
		boolean threeButtonClicked = false;
		boolean oneButtonClicked = false;
		boolean randomButtonClicked = false;
		boolean randomButtonClicked2 = false;

		/*************** �e�ϐ���錾�����܂� ******************/
		
		/******** �R���X�g���N�^�i�Q�[���J�n���̏������j********/
		public MyJPanel() {
			
			// �S�̂̐ݒ� 
			// �w�i�F�̐ݒ�
			setBackground(Color.black);
			addMouseListener(this);
			addMouseMotionListener(this);
			
			// �^�C�}�[�N���X�̃C���X�^���X��
			timer = new Timer(50, this);
			
			// �摜�̎�荞��(���@)
			imgMe = getImg("./image/jiki.png");
			myWidth = imgMe.getWidth(this);
			myHeight = imgMe.getHeight(this);
			
			// �摜�̎�荞��(���@�~�T�C�� random)
			imgMeMissile = getImg("./image/shabon1.jpg");
			for (int k=0; k<randomValue; k++) {
				myMissileXRm[k] = imgMeMissile.getWidth(this);
				myMissileYRm[k] = imgMeMissile.getHeight(this);
			}
			
			// �摜�̎�荞��(�G�@)
			// �ʏ�
			imgEnemy = getImg("./image/teki.png");
			enemyWidth = imgEnemy.getWidth(this);
			enemyHeight = imgEnemy.getHeight(this);
			
			// ����
			imgEnemyS = getImg("./image/teki2.png");
			enemyWidthS = imgEnemyS.getWidth(this);
			enemyHeightS = imgEnemyS.getHeight(this);
			
			// ����2
			imgEnemyS2 = getImg("./image/teki2.png");
			enemyWidthS2 = imgEnemyS2.getWidth(this);
			enemyHeightS2 = imgEnemyS2.getHeight(this);
			
			// ����3
			imgEnemyS3 = getImg("./image/teki2.png");
			enemyWidthS3 = imgEnemyS3.getWidth(this);
			enemyHeightS3 = imgEnemyS3.getHeight(this);
			
			
			// �摜�̎�荞��(�G�@�~�T�C��)
			imgEnemyMissile = getImg("./image/shabon2.jpg");
			
			// �摜�̎�荞��(�����G�@�~�T�C��)
			imgEnemyMissileS = getImg("./image/shabon6.png");
			imgEnemyMissileS2 = getImg("./image/shabon7.png");
			imgEnemyMissileS3 = getImg("./image/shabon8.png");
			
			
			// ���@�ƓG�@�̏�����
			initMyPlane();
			initEnemyPlane();
			initEnemyPlaneS();
			initEnemyPlaneS2();
			initEnemyPlaneS3();
		
			// ��s�D  https://ja.pngtree.com/freepng/clouds_766384.html
			imgCloud = getImg("./image/hikousen.png");
			cloudWidth = imgCloud.getWidth(this);
			cloudHeight = imgCloud.getHeight(this);
			
			
			imgCloud2 = getImg("./image/hikousen2.png");
			cloud2Width = imgCloud2.getWidth(this);
			cloud2Height = imgCloud2.getHeight(this);
			

			// ��s�D�̏�����
			initCloud();
			initCloud2();
			
			/***************** �{�^�� ***********************/
			//setBounds(int x, int y, int width, int height)
			
			oneButton = new JButton("��");
        	oneButton.setBounds(390, 530, 50, 25);
			oneButton.setBorder(new LineBorder(Color.magenta, 1, true));
        	oneButton.addActionListener(this);

			threeButton = new JButton("������");
        	threeButton.setBounds(450, 530, 50, 25);
			threeButton.setBorder(new LineBorder(Color.magenta, 1, true));
        	threeButton.addActionListener(this);
			
			randomButton = new JButton("����");
        	randomButton.setBounds(510, 530, 50, 25);
			randomButton.setBorder(new LineBorder(Color.magenta, 1, true));
        	randomButton.addActionListener(this);
			
			// add���\�b�h�Ŏ��ۂɃp�l����ɔz�u
			add(threeButton);
			add(oneButton);
			add(randomButton);
		}
		
		/* �C���q �߂�l�̌^ ���\�b�h��(�󂯎�����) {���\�b�h�̏���}
		/* �߂�l��Ԃ��Ȃ��ꍇ�͎��̂悤�ɋL�q���ČĂяo�� */
		   //���\�b�h��(����)
		
		/* �߂�l��Ԃ��ꍇ�͎��̂悤�ɋL�q���ČĂяo�� */
		   //�߂�l���i�[����I�u�W�F�N�g�ϐ��� = ���\�b�h��(����)
		
		/* �摜�t�@�C������ Image �N���X�ւ̕ϊ� */
		public Image getImg(String filename) {
			ImageIcon icon = new ImageIcon(filename);
			Image img = icon.getImage();
			return img;
		}
		
		/* ���@�̏����� ***************/
		public void initMyPlane() {
			myX = windowWidth / 2;
			myY = windowHeight - 110;
			tempMyX = windowWidth / 2; //�s�����2
			
			//random�p
			Random rand = new Random();
			
			for (int k=0; k<randomValue; k++) {
				isMyMissileActiveRm[k] = false;
				myMissileSpeedY[k] = rand.nextInt(10) + 10;  //�~�T�C�����̃X�s�[�h�𒲐� (10�`19�̃X�s�[�h�Ŕ��˂����)
			}
			
			isMyAlive = true;
		}
		
		
		/* �G�@�̏����� ***************/
		public void initEnemyPlane() {
			
			/****** �ʏ�G�@ ******/
			// 12�@�̂����ŏ���7�@�� y���W��50
			for (int i=0; i<7; i++) {
				enemyX[i] = 70*i;
				enemyY[i] = 50;
			}
			// �c��� y���W��100�ݒ�
			for (int i=7; i<numOfEnemy; i++) {
				enemyX[i] = 70*(i-6);
				enemyY[i] = 100;
			}
			
			for (int i=0; i<numOfEnemy; i++) {
				isEnemyAlive[i] = true;
				// 1 �ŉE�����Ɉړ� -1 ���ƍ������Ɉړ�
				enemyMove[i] = 1;
			}

			for (int i=0; i<numOfEnemy; i++) {
				isEnemyMissileActive[i] = true;
				enemyMissileX[i] = enemyX[i] + enemyWidth/2;
				enemyMissileY[i] = enemyY[i];
				
				/* �e�@�̃~�T�C���̃X�s�[�h��\�� enemyMissileSpeed[i]�̒l��ς��邱�ƂŁA
				   �����_������\���B���̂��߂Ɂu10 + (i%6)�v�Ƃ����v�Z����p���Ēl��ω������Ă��� */
				enemyMissileSpeed[i] = 5 + (i%6); 
			}
			/****** �ʏ�G�@ ******/
		}
			
		public void initEnemyPlaneS() {
			/****** �����G�@ ******/
			
			// 4�@�̂�����5����2�@�� y���W��50
			enemyXS[0] = -50;
			enemyXS[1] = -150;
			enemyXS[2] = -250;
			enemyXS[3] = -350;
			
			enemyYS[0] = 20;
			enemyYS[1] = 20;
			enemyYS[2] = 20;
			enemyYS[3] = 20;

			
			for (int i=0; i<numOfEnemyS; i++) {
				isEnemyAliveS[i] = true;
				// 1 �ŉE�����Ɉړ� -1 ���ƍ������Ɉړ�
				enemyMoveS[i] = 1;
			}

			for (int i=0; i<numOfEnemyS; i++) {
				isEnemyMissileActiveS[i] = true;
				enemyMissileXS[i] = enemyXS[i] + enemyWidthS/2;
				enemyMissileYS[i] = enemyYS[i];
				
				/* �e�@�̃~�T�C���̃X�s�[�h��\�� enemyMissileSpeed[i]�̒l��ς��邱�ƂŁA
				   �����_������\���B���̂��߂Ɂu10 + (i%6)�v�Ƃ����v�Z����p���Ēl��ω������Ă��� */
				enemyMissileSpeedS[i] = 5 + (i%6); 
			}
			/****** �����G�@ ******/
		}
		
		public void initEnemyPlaneS2() {
			/****** �����G�@2 ******/
			
			// 2�@�̂����ŏ���1�@�� y���W��50
			for (int i=0; i<1; i++) {
				enemyXS2[i] = -200;
				enemyYS2[i] = 30;
			}
			// �c��� y���W��100�ݒ�
			for (int i=1; i<numOfEnemyS2; i++) {
				enemyXS2[i] = -200;
				enemyYS2[i] = 80;
			}
			
			
			for (int i=0; i<numOfEnemyS2; i++) {
				isEnemyAliveS2[i] = true;
				// 1 �ŉE�����Ɉړ� -1 ���ƍ������Ɉړ�
				enemyMoveS2[i] = 1;
			}

			for (int i=0; i<numOfEnemyS2; i++) {
				isEnemyMissileActiveS2[i] = true;
				enemyMissileXS2[i] = enemyXS2[i] + enemyWidthS2/2;
				enemyMissileYS2[i] = enemyYS2[i];
				
				/* �e�@�̃~�T�C���̃X�s�[�h��\�� enemyMissileSpeed[i]�̒l��ς��邱�ƂŁA
				   �����_������\���B���̂��߂Ɂu10 + (i%6)�v�Ƃ����v�Z����p���Ēl��ω������Ă��� */
				enemyMissileSpeedS2[i] = 5 + (i%6); 
			}
			/****** �����G�@2 ******/
		}
		
		public void initEnemyPlaneS3() {
		
			/****** �����G�@3 ******/
			
			// 4�@�̂����ŏ���2�@�� y���W��50
			for (int i=0; i<1; i++) {
				enemyXS3[i] = -200;
				enemyYS3[i] = 70;
			}
			// �c��� y���W��100�ݒ�
			for (int i=1; i<numOfEnemyS3; i++) {
				enemyXS3[i] = -200;
				enemyYS3[i] = 120;
			}
			
			for (int i=0; i<numOfEnemyS3; i++) {
				isEnemyAliveS3[i] = true;
				// 1 �ŉE�����Ɉړ� -1 ���ƍ������Ɉړ�
				enemyMoveS3[i] = 1;
			}

			for (int i=0; i<numOfEnemyS3; i++) {
				isEnemyMissileActiveS3[i] = true;
				enemyMissileXS3[i] = enemyXS3[i] + enemyWidthS3/2;
				enemyMissileYS3[i] = enemyYS3[i];
				
				/* �e�@�̃~�T�C���̃X�s�[�h��\�� enemyMissileSpeed[i]�̒l��ς��邱�ƂŁA
				   �����_������\���B���̂��߂Ɂu10 + (i%6)�v�Ƃ����v�Z����p���Ēl��ω������Ă��� */
				enemyMissileSpeedS3[i] = 5 + (i%6); 
			}
			/****** �����G�@3 ******/
		}
		/* �G�@�̏����� �����܂� ******/
		
		
				
		/* ��s�D�̏����� ***************/
		public void initCloud() {
			
			// y���W��180
				cloudX = -200;
				cloudY = 80;

			isCloudAlive = true;
			// 1 �ŉE�����Ɉړ� -1 ���ƍ������Ɉړ�
			cloudMove = 1;

		}
		
		public void initCloud2() {
			
			// y���W��150
				cloud2X = -200;
				cloud2Y = 140;

			isCloud2Alive = true;
			// 1 �ŉE�����Ɉړ� -1 ���ƍ������Ɉړ�
			cloud2Move = 1;

		}
		
		/* ��s�D�̏����� �����܂� ******/
		
		
		/**************************************************************************/
		/*  paintComponent (�p�l����̕`��)                                       */
		/*                                                                        */
		/*  �{�^�����N���b�N���ꂽ�Ƃ��̏����� actionPerformed ���\�b�h���ɋL�q   */
    	/*  actionPerformed���\�b�h�ł́A�{�^���������ꂽ���Ƃ�F�����A           */
    	/*  ���̂��Ƃ� paintComponent ���\�b�h�ɓ`����                            */
		/*  drawString(String str, int x, int y)                                  */
		/**************************************************************************/
		
		public void paintComponent(Graphics g) {
			
			//http://switch-box.net/free-illustration-hanabi.html
			dimOfPanel = getSize();
			super.paintComponent(g);  /* (g)�ŏ����� */ /* timer�N���X���g�p����ہA�A���\�������̂ŏ��������� */
			
			//��ʑS�̂ɁA�w�i�摜�𖄂ߍ���
			//drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
			Image imghanabi = Toolkit.getDefaultToolkit().getImage("./image/hanabi.jpg");
			g.drawImage(imghanabi, 0, 0, 1000, 520, this);

			Image imgboard = Toolkit.getDefaultToolkit().getImage("./image/board.jpg");
			g.drawImage(imgboard, 0, 525, 1000, 50, this);
					
			
			//���t�擾
			Calendar cl = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy�NMM��dd��(E)H��m��s�b");

			/*** ��ʉ� �{�[�g���̐ݒ� ��������***/
			//�X�R�A�{�[�h�̕\�� & �w�i�ݒ�
			//fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
			g.setColor(Color.darkGray);
			g.fillRoundRect(80, 530, 140, 28, 20, 10); //1. �X�R�A
			g.fillRoundRect(230, 530, 100, 28, 20, 10);//2. ���˃{�^��
			g.fillRoundRect(680, 530, 300, 28, 20, 10);//3. ���t
			
			//1. ���� �X�R�A
			g.setColor(Color.green);
			Font fo1 = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo1);
			g.drawString("�X�R�A�F" + score, 90, 550);
			int scoresum = score;


			//GameOver���ɉ�ʕ\�����鏈��
			if (isMylife) {
				
				/*** �t�@�C�����o�͂Ɋւ��鏈�� ***/
				//�t�@�C����������
				try {
					File f = new File("score.txt");
					BufferedWriter bw = new BufferedWriter(new FileWriter(f));
					bw.write(sdf.format(cl.getTime()) +  "�X�R�A���_" + scoresum);
					bw.newLine();
 					bw.close();
				
				} catch (IOException e) {
				}
				
				String rankC2 = "���cGAMEOVER���c";
				//������𕶎��P�ʂɕ�������char�z��ɂ���
				char data[] = rankC2.toCharArray();
				//�������m�F����
        		int arr_num = data.length;
				
				int test1 = 40;
				
					for(int i = 0; i<= arr_num-1; i++){

					test1 = test1 + 60;
					
					Random rand = new Random();
					int R3=(int)(Math.random()*200);
					int G3=(int)(Math.random()*200);
					int B3=(int)(Math.random()*50);
 					g.setColor(new Color(R3,G3,B3));
					Font fo = new Font("Dialog",Font.BOLD,30);
					g.setFont(fo);
					g.drawString(Character.toString(data[i]), test1, 35);
					Font fo5 = new Font("Dialog",Font.PLAIN,20);
					g.setFont(fo5);
					g.setColor(Color.yellow);
					g.drawString("�ŏI�X�R�A�F" + score + "   ���ʃ��O�uscore.txt�v", 90, 510);

				}
			}
			
			Font fo = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo);
			
			g.setColor(Color.green);
			//2. ���� ���˃{�^��,���y�{�^��
			g.drawString("push!", 340, 550);
			
			//3. ���� ���t
			g.drawString(sdf.format(cl.getTime()), 685, 550);
			
			/*** ��ʉ� �{�[�g���̐ݒ肱���܂� ***/
			
			//�^�C�}�[�X�^�[�g
			timer.start();

			//���\�b�h�̌Ăяo��
			drawCloud(g);             //��s�D
			drawCloud2(g);             //��s�D
			drawMyPlane(g);           //���@
			drawMyMissileRm(g);       //���@�̃~�T�C��
			drawEnemyPlane(g);        //�G�@
			drawEnemyMissile(g);      //�G�@�̃~�T�C��
			drawEnemyPlaneS(g);       //�G�@(����)
			drawEnemyMissileS(g);     //�G�@�̃~�T�C��(����)
			drawEnemyPlaneS2(g);      //�G�@(����2)
			drawEnemyMissileS2(g);    //�G�@�̃~�T�C��(����2)
			drawEnemyPlaneS3(g);      //�G�@(����3)
			drawEnemyMissileS3(g);    //�G�@�̃~�T�C��(����3)
			
			/*** �p�l���Ɍ��鐯��\��  �����_���F ***/
			Random rand = new Random();
			int R=(int)(Math.random()*200);
			int G=(int)(Math.random()*200);
			int B=(int)(Math.random()*0);
 			g.setColor(new Color(R,G,B));
			Font fo4 = new Font("Dialog",Font.PLAIN,12);
			g.setFont(fo4);
			g.drawString("��", 960, 240);
			Font fo3 = new Font("Dialog",Font.PLAIN,8);
			g.setFont(fo3);
						
			int R2=(int)(Math.random()*200);
			int G2=(int)(Math.random()*100);
			int B2=(int)(Math.random()*200);
 			g.setColor(new Color(R2,G2,B2));
			g.drawString("��", 930, 260);
			/*** �p�l���Ɍ��鐯��\��  �����_���F ***/
			
			/***** ����I�ɏo�������邽�߂̏��� *****/
			Calendar cal;
			cal = Calendar.getInstance();  //Calender �I�u�W�F�N�g�̐���
		
			//�����G�@�o���t���O
			if (cal.get(Calendar.SECOND) == 0) {
				// �����Acal�̕b��0�b�Ȃ�
				initEnemyPlaneS();
				startTeki = true;
			}else if (cal.get(Calendar.SECOND) == 12) {
				// �����Acal�̕b��0�b�Ȃ�
				initEnemyPlaneS();
				startTeki = true;
			}else if (cal.get(Calendar.SECOND) == 32) {
				// �����Acal�̕b��0�b�Ȃ�
				initEnemyPlaneS();
				startTeki = true;
			}else if (cal.get(Calendar.SECOND) == 48) {
				// �����Acal�̕b��0�b�Ȃ�
				initEnemyPlaneS();
				startTeki = true;
			}
			
			
			if (cal.get(Calendar.SECOND) == 15) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 25) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 35) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 45) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 55) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 5) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS2();
				startTeki2 = true;
			}
			

			if (cal.get(Calendar.SECOND) == 10) {
				// �����Acal�̕b��40�b�Ȃ�
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 20) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 30) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 40) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 50) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 0) {
				// �����Acal�̕b��20�b�Ȃ�
				initEnemyPlaneS3();
				startTeki3 = true;
			}
			
			
			//��s�D�o���t���O
			if (cal.get(Calendar.SECOND) == 10) {
				initCloud();
				cloudflg = true;
			}
			
			if (cal.get(Calendar.SECOND) == 30) {
				initCloud2();
				cloud2flg = true;
			}
			
			/***** ����I�ɏo�������邽�߂̏��� *****/
		}
		
		/**************************************************************************/
		/* �@��̕`�悱������                                                     */
		/**************************************************************************/
		/****************** ���@�̕`�� ****************************/
		public void drawMyPlane(Graphics g) {
			
			if (isMyAlive) {
				if (Math.abs(tempMyX - myX) < gap) {
					if (myX < 0) {
						myX = 0;
					} else if (myX+myWidth > dimOfPanel.width) {
						myX = dimOfPanel.width - myWidth;
					}
					
					tempMyX = myX;
					g.drawImage(imgMe, tempMyX, myY, this);

				} else {
					g.drawImage(imgMe, tempMyX, myY, this);
				
				}
			}
		}
		
		/**************** ���@�̃~�T�C���̕`�� (1��, 3��, �����_��) ********************/
		public void drawMyMissileRm(Graphics g) {
			
			// �~�T�C���̈ʒu X                                // �~�T�C���̈ʒu X  ���L�͈ȉ��Ɠ���
			int sum = 2;                                       // myMissileXRm[1]  -= 2;
			for(int Rm =1; Rm < randomValue; Rm ++){           // myMissileXRm[2]  -= 1;
				myMissileXRm[Rm] -= sum;                       // myMissileXRm[3]  -= 0;
				sum = sum - 1;                                 // myMissileXRm[4]  -= -1;
			}                                                  // myMissileXRm[5]  -= -2;
			                                                   // myMissileXRm[6]  -= -3;
			// �~�T�C���̈ʒu Y                                // myMissileXRm[7]  -= -4;
			for(int Rm = 0; Rm < randomValue; Rm ++){
				myMissileYRm[Rm] -= myMissileSpeedY[Rm];
			}
			
			// �����_���� �\��
			g.setColor(Color.green);
			g.drawString("����F", 240, 550);
			if (randomButtonClicked2) {
				g.drawString("����F" + ranVa, 240, 550);
			}
			
			// actionPerformed �̃����_���{�^���ݒ艟���ɂ��A�������ꂽ�����_���l�irandomValue2�j�܂ŌJ��Ԃ�
			for(int k=0; k<randomValue2; k++){
			
				// �z��ŗ�O�����������ꍇ�ɃL���b�`����
				try {
					if (isMyMissileActiveRm[k]) {
						
						// �~�T�C���̔z�u
						g.drawImage(imgMeMissile, myMissileXRm[k], myMissileYRm[k], this);
						// ���@�̃~�T�C���̓G�@�e�@�ւ̓����蔻��
						for (int i=0; i<numOfEnemy; i++) {
							if (isEnemyAlive[i]) {
								if ((myMissileXRm[k] >= enemyX[i]) && 
									(myMissileXRm[k] <= enemyX[i]+enemyWidth) && 
									(myMissileYRm[k] >= enemyY[i]) && 
									(myMissileYRm[k] <= enemyY[i]+enemyHeight)) {
									isEnemyAlive[i] = false;
									isMyMissileActiveRm[k] = false;
									score ++;
									numOfAlive--;
									bell();
								}
							}
						}
						
						
						/***************** �����G�@�̏��� ******************/
						// ���@�̃~�T�C���̓G�@�e�@�ւ̓����蔻�� (����)
						for (int is=0; is<numOfEnemyS; is++) {
							if (isEnemyAliveS[is]) {
								if ((myMissileXRm[k] >= enemyXS[is]) && 
									(myMissileXRm[k] <= enemyXS[is]+enemyWidthS) && 
									(myMissileYRm[k] >= enemyYS[is]) && 
									(myMissileYRm[k] <= enemyYS[is]+enemyHeightS)) {
									isEnemyAliveS[is] = false;
									isMyMissileActiveRm[k] = false;
									score = score + 3;
									numOfAliveS--;
									bell();
								}
							}
						}
						
						
						/***************** ����2 �G�@�̏��� ******************/
						// ���@�̃~�T�C���̓G�@�e�@�ւ̓����蔻�� (����2)
						for (int is=0; is<numOfEnemyS2; is++) {
							if (isEnemyAliveS2[is]) {
								if ((myMissileXRm[k] >= enemyXS2[is]) && 
									(myMissileXRm[k] <= enemyXS2[is]+enemyWidthS2) && 
									(myMissileYRm[k] >= enemyYS2[is]) && 
									(myMissileYRm[k] <= enemyYS2[is]+enemyHeightS2)) {
									isEnemyAliveS2[is] = false;
									isMyMissileActiveRm[k] = false;
									score = score + 3;
									numOfAliveS2--;
									bell();
								}
							}
						}
						
						
						/***************** ����3 �G�@�̏��� ******************/
						// ���@�̃~�T�C���̓G�@�e�@�ւ̓����蔻�� (����3)
						for (int is=0; is<numOfEnemyS3; is++) {
							if (isEnemyAliveS3[is]) {
								if ((myMissileXRm[k] >= enemyXS3[is]) && 
									(myMissileXRm[k] <= enemyXS3[is]+enemyWidthS3) && 
									(myMissileYRm[k] >= enemyYS3[is]) && 
									(myMissileYRm[k] <= enemyYS3[is]+enemyHeightS3)) {
									isEnemyAliveS3[is] = false;
									isMyMissileActiveRm[k] = false;
									score = score + 3;
									numOfAliveS3--;
									bell();
								}
							}
						}
						
							
						// �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
						if (myMissileYRm[k] < 0) isMyMissileActiveRm[k] = false;
						}
				
					
					} catch (ArrayIndexOutOfBoundsException f) {

						System.out.println("�z��̗v�f���𒴂��Ă��܂�");
						System.exit(0);
				}
			}
		}
		
		/****************** �G�@�̕`�� ****************************/
		public void drawEnemyPlane(Graphics g) {
			// �G�@�̕`��
			for (int i=0; i<numOfEnemy; i++) {
				if (isEnemyAlive[i]) {
					if (enemyX[i] > dimOfPanel.width - enemyWidth) {
						//�E���璵�˕Ԃ�
						enemyMove[i] = -1;
					} else if (enemyX[i] < 0) {
						//�����璵�˕Ԃ�
						enemyMove[i] = 1;
					}
					enemyX[i] += enemyMove[i]*10;
					g.drawImage(imgEnemy, enemyX[i], enemyY[i], this);
				}
			}
		}
		
		/**************** �G�@�̃~�T�C���̕`�� *********************/
		public void drawEnemyMissile(Graphics g) {
			
			for (int i=0; i<numOfEnemy; i++) {
				// �~�T�C���̔z�u
				if (isEnemyMissileActive[i]) {
					enemyMissileY[i] += enemyMissileSpeed[i];
					g.drawImage(imgEnemyMissile, enemyMissileX[i], enemyMissileY[i], this);
				}
				
				// �G�@�̃~�T�C���̎��@�ւ̓����蔻�� (���s)
				if (isMyAlive && numOfAlive!=0) {
					if ((enemyMissileX[i] >= tempMyX) &&
						(enemyMissileX[i] <= tempMyX+myWidth) &&
						(enemyMissileY[i]+5 >= myY) 
						&& (enemyMissileY[i]+5 <= myY+myHeight)) {
						isEnemyMissileActive[i] = false;  // ���@�Ƀ~�T�C��������������G�@�̃~�T�C���摜������
						java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.�t���� beep��
						isMyAlive = false; // �~�T�C�������������玩�@�̉摜������
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "����̃V���{���ʂɓ�����܂���" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
				if (enemyMissileY[i] > dimOfPanel.height) {
					if (isEnemyAlive[i]) {
						enemyMissileX[i] = enemyX[i] + enemyWidth/2; 
						enemyMissileY[i] = enemyY[i];
					} else {
						isEnemyMissileActive[i] = false;
					}
				}
            }
				
		}
		
		/****************** ���� �G�@�̕`�� ****************************/
		public void drawEnemyPlaneS(Graphics g) {
			
			// �G�@�̕`��
			for (int i=0; i<numOfEnemyS; i++) {
				if (isEnemyAliveS[i] && startTeki) {
				
					if (enemyXS[i] > dimOfPanel.width - enemyWidthS) {
						//�E���璵�˕Ԃ�
						enemyMoveS[i] = -1;
					} else if (enemyXS[i] < 0) {
						//�����璵�˕Ԃ�
					//enemyMoveS[i] = 1;
					}
					enemyXS[i] += enemyMoveS[i]*20; // �G�@�̈ړ��X�s�[�h
					g.drawImage(imgEnemyS, enemyXS[i], enemyYS[i], this);
				}
			}
		}
		
		/**************** ���� �G�@�̃~�T�C���̕`�� *********************/
		public void drawEnemyMissileS(Graphics g) {
			
			for (int i=0; i<numOfEnemyS; i++) {

				// �~�T�C���̔z�u
				if (isEnemyMissileActiveS[i] && startTeki) {
					enemyMissileYS[i] += enemyMissileSpeedS[i];
					g.drawImage(imgEnemyMissileS, enemyMissileXS[i], enemyMissileYS[i], this);
				}

				// �����G�@�̃~�T�C���̎��@�ւ̓����蔻�� (���s)
				if (isMyAlive) {
					if ((enemyMissileXS[i] >= tempMyX) &&
						(enemyMissileXS[i] <= tempMyX+myWidth) &&
						(enemyMissileYS[i]+5 >= myY) 
						&& (enemyMissileYS[i]+5 <= myY+myHeight)) {
						isEnemyMissileActiveS[i] = false;  // ���@�Ƀ~�T�C��������������G�@�̃~�T�C���摜������
						isMyAlive = false;        // �~�T�C�������������玩�@�̉摜������
						//java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.�t���� beep��
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "����̃V���{���ʂɓ�����܂���" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
				if (enemyMissileYS[i] > dimOfPanel.height) {
					if (isEnemyAliveS[i]) {
						enemyMissileXS[i] = enemyXS[i] + enemyWidthS/2; 
						enemyMissileYS[i] = enemyYS[i];
					} else {
						isEnemyMissileActiveS[i] = false;
					}
				}
			}
		}
		
		
		/****************** ����2 �G�@�̕`�� ****************************/
		public void drawEnemyPlaneS2(Graphics g) {
			
			// �G�@�̕`��
			for (int i=0; i<numOfEnemyS2; i++) {
				if (isEnemyAliveS2[i] && startTeki2) {
				
					if (enemyXS2[i] > dimOfPanel.width - enemyWidthS2) {
						//�E���璵�˕Ԃ�
						enemyMoveS2[i] = -1;
					} else if (enemyXS2[i] < 0) {
						//�����璵�˕Ԃ�
					//	enemyMoveS2[i] = 1;
					}
					enemyXS2[i] += enemyMoveS2[i]*20; // �G�@�̈ړ��X�s�[�h
					g.drawImage(imgEnemyS2, enemyXS2[i], enemyYS2[i], this);
				}
			}
		}
		
		/**************** ����2 �G�@�̃~�T�C���̕`�� *********************/
		public void drawEnemyMissileS2(Graphics g) {
			
			for (int i=0; i<numOfEnemyS2; i++) {

				// �~�T�C���̔z�u
				if (isEnemyMissileActiveS2[i] && startTeki2) {
					enemyMissileYS2[i] += enemyMissileSpeedS2[i];
					g.drawImage(imgEnemyMissileS2, enemyMissileXS2[i], enemyMissileYS2[i], this);
				}

				// �����G�@�̃~�T�C���̎��@�ւ̓����蔻�� (���s)
				if (isMyAlive) {
					if ((enemyMissileXS2[i] >= tempMyX) &&
						(enemyMissileXS2[i] <= tempMyX+myWidth) &&
						(enemyMissileYS2[i]+5 >= myY) 
						&& (enemyMissileYS2[i]+5 <= myY+myHeight)) {
						isEnemyMissileActiveS2[i] = false;  // ���@�Ƀ~�T�C��������������G�@�̃~�T�C���摜������
						isMyAlive = false;        // �~�T�C�������������玩�@�̉摜������
						//java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.�t���� beep��
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "����̃V���{���ʂɓ�����܂���" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
				if (enemyMissileYS2[i] > dimOfPanel.height) {
					if (isEnemyAliveS2[i]) {
						enemyMissileXS2[i] = enemyXS2[i] + enemyWidthS2/2; 
						enemyMissileYS2[i] = enemyYS2[i];
					} else {
						isEnemyMissileActiveS2[i] = false;
					}
				}
			}
		}
		
		
		/****************** ����3 �G�@�̕`�� ****************************/
		public void drawEnemyPlaneS3(Graphics g) {
			
			// �G�@�̕`��
			for (int i=0; i<numOfEnemyS3; i++) {
				if (isEnemyAliveS3[i] && startTeki3) {
				
					if (enemyXS3[i] > dimOfPanel.width - enemyWidthS3) {
						//�E���璵�˕Ԃ�
						enemyMoveS3[i] = -1;
					} else if (enemyXS3[i] < 0) {
						//�����璵�˕Ԃ�
					//	enemyMoveS3[i] = 1;
					}
					enemyXS3[i] += enemyMoveS3[i]*20; // �G�@�̈ړ��X�s�[�h
					g.drawImage(imgEnemyS3, enemyXS3[i], enemyYS3[i], this);
				}
			}
		}
		
		/**************** ����3 �G�@�̃~�T�C���̕`�� *********************/
		public void drawEnemyMissileS3(Graphics g) {
			
			for (int i=0; i<numOfEnemyS3; i++) {

				// �~�T�C���̔z�u
				if (isEnemyMissileActiveS3[i] && startTeki3) {
					enemyMissileYS3[i] += enemyMissileSpeedS3[i];
					g.drawImage(imgEnemyMissileS3, enemyMissileXS3[i], enemyMissileYS3[i], this);
				}

				// �����G�@�̃~�T�C���̎��@�ւ̓����蔻�� (���s)
				if (isMyAlive) {
					if ((enemyMissileXS3[i] >= tempMyX) &&
						(enemyMissileXS3[i] <= tempMyX+myWidth) &&
						(enemyMissileYS3[i]+5 >= myY) 
						&& (enemyMissileYS3[i]+5 <= myY+myHeight)) {
						isEnemyMissileActiveS3[i] = false;  // ���@�Ƀ~�T�C��������������G�@�̃~�T�C���摜������
						isMyAlive = false;        // �~�T�C�������������玩�@�̉摜������
						//java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.�t���� beep��
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "����̃V���{���ʂɓ�����܂���" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
				if (enemyMissileYS3[i] > dimOfPanel.height) {
					if (isEnemyAliveS3[i]) {
						enemyMissileXS3[i] = enemyXS3[i] + enemyWidthS3/2; 
						enemyMissileYS3[i] = enemyYS3[i];
					} else {
						isEnemyMissileActiveS3[i] = false;
					}
				}
			}
		}
		
		
		/**************************************************************************/
		/* �@��̕`�悱���܂�                                                     */
		/**************************************************************************/
		
		/****************** ��s�D�̕`�� ****************************/
		public void drawCloud(Graphics g) {
						
			if (cloudflg) {
        		if (cloudX > dimOfPanel.width - cloudWidth) {
					//�E���璵�˕Ԃ�
					//cloudMove[i] = -1;
				} else if (cloudX < 0) {
					//�����璵�˕Ԃ�
					//cloudMove[i] = 1;
				}
				cloudX += cloudMove*2;
				g.drawImage(imgCloud, cloudX, cloudY, this);
			}
		}

		/****************** ��s�D�̕`�悱���܂� *********************/
		
		/****************** ��s�D2�̕`�� ****************************/
		public void drawCloud2(Graphics g) {
						
			if (cloud2flg) {
        		if (cloud2X > dimOfPanel.width - cloud2Width) {
					//�E���璵�˕Ԃ�
					//cloud2Move[i] = -1;
				} else if (cloud2X < 0) {
					//�����璵�˕Ԃ�
					//cloud2Move[i] = 1;
				}
				cloud2X += cloud2Move*2;
				g.drawImage(imgCloud2, cloud2X, cloud2Y, this);
			}
		}

		/****************** ��s�D2�̕`�悱���܂� *********************/
		
		public void actionPerformed(ActionEvent e) {
			/* �{�^�����N���b�N�������̏��� */
			/* ��莞�Ԃ��Ƃ̏����iActionListener �ɑ΂��鏈���j*/
		
			AudioClip ac;
				
			// 1���{�^��(��)�������ꂽ�Ƃ�
			if (e.getSource() == oneButton){
				randomValue2 = 1;
				oneButtonClicked = true;
				threeButtonClicked = false;
				randomButtonClicked = false;
				ac = Applet.newAudioClip(getClass().getResource("./wav/oto1.wav"));
				ac.play();
			}
			
			// 3���{�^��(������)�������ꂽ�Ƃ�
			if (e.getSource() == threeButton){
				randomValue2 = 3;
				oneButtonClicked = false;
				threeButtonClicked = true;
				randomButtonClicked = false;
				ac = Applet.newAudioClip(getClass().getResource("./wav/oto3.wav"));
				ac.play();
		
				for (int j=0; j<numOfmyMissile; j++) {
					isMyMissileActiveRm[j] = true;
				}
			}
			
			// random�{�^��(����)�������ꂽ�Ƃ�
			if (e.getSource() == randomButton){
				//randomValue2 = (int)(Math.random()*4)+10; //�����_���ϐ� Exception�ɂȂ����
				randomValue2 = (int)(Math.random()*4)+5; //�����_���ϐ���ݒ�(4�ȏ�8�ȉ��̗���)
				ranVa = randomValue2;
				randomButtonClicked = true;
				randomButtonClicked2 = true; 
				threeButtonClicked = false;
				oneButtonClicked = false;
				ac = Applet.newAudioClip(getClass().getResource("./wav/oto2.wav"));
				ac.play();
				
				for (int k=0; k<randomValue; k++) {
					isMyMissileActiveRm[k] = true;
				}	
			}
			
			/* paintComponent �Ăяo�� */
			repaint();
		}

		public void mouseClicked(MouseEvent e) {
			/* �}�E�X�{�^�����N���b�N�����Ƃ��̏��� */
		}
		
		public void mousePressed(MouseEvent e) {
			/* �}�E�X�{�^�����������Ƃ��̏��� */
			
			/*** ���@�摜�����݂��A�~�T�C�������˂���Ă��Ȃ������甭�˂��� */
			if (isMyAlive){

				// 3���{�^��(������)
				for (int j=0; j<numOfmyMissile; j++) {
					if (!isMyMissileActiveRm[j] && threeButtonClicked) {
						myMissileXRm[j] = tempMyX + myWidth/2; //�s�����2
						myMissileYRm[j] = myY;
						isMyMissileActiveRm[j] = true; 
					}		
				}
				
				// 1���{�^��(��)
				if (!isMyMissileActiveRm[0]) {
					myMissileXRm[0] = tempMyX + myWidth/2; //�s�����2
					myMissileYRm[0] = myY;
					isMyMissileActiveRm[0] = true;
				}
				
				// random�{�^��(����)
				for (int k=0; k<randomValue2; k++) {
					if (!isMyMissileActiveRm[k] && randomButtonClicked) {
						myMissileXRm[k] = tempMyX + myWidth/2; //�s�����2
						myMissileYRm[k] = myY;
						isMyMissileActiveRm[k] = true;
					}
				}
			}
			/*** ���@�摜�����݂��A�~�T�C�������˂���Ă��Ȃ������甭�˂��邱���܂� */
			
			
		}

		public void mouseReleased(MouseEvent e) {
			/* �}�E�X�{�^���𗣂����Ƃ��̏��� */
		}

		public void mouseExited(MouseEvent e) {
			/* �}�E�X���Ώۂ̃R���|�[�l���g����o���Ƃ��̏��� */
		}

		public void mouseEntered(MouseEvent e) {
			/* �}�E�X���Ώۂ̃R���|�[�l���g�ɓ������Ƃ��̏��� */
		}

		public void mouseMoved(MouseEvent e) {
			/* MouseMotionListener �ɑ΂��鏈�� */
			myX = e.getX();
		}

		public void mouseDragged(MouseEvent e) {
			//�s�����1 mouseMoved ���\�b�h�Ɠ��l�ɁCmouseDragged ���\�b�h�ɑ΂��Ă����̋L�q���s��
			myX = e.getX();
		}
	}

	
	public class MyJPanel2 extends JPanel implements ActionListener{
		//ActionListener ��o�^������AactionPerformed���\�b�h��K���쐬����
		/***************** �e�ϐ���錾 ************************/
		/* �S�̂̐ݒ�Ɋւ���ϐ� */
		Timer timer;
		JLabel label;
		int sec = 5;

		/******** �R���X�g���N�^�i�Q�[���J�n���̏������j********/
		public MyJPanel2() {
			
			// �S�̂̐ݒ� 
			// �w�i�F�̐ݒ�
			//setBackground(Color.black);
			setBackground(new Color(0,0,51));
			
			label = new JLabel("����", JLabel.LEFT);
			label.setPreferredSize(new Dimension(200,40));
			//label.setBorder(new LineBorder(Color.white, 3, true));
			add(label);

			timer = new Timer(1000, this);
			timer.start();

		}
		
		/**************************************************************************/
		/*  paintComponent (�p�l����̕`��)                                       */
		/**************************************************************************/
		
		public void paintComponent(Graphics g) {
						
			super.paintComponent(g);  /* (g)�ŏ����� */
			
			// drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
			// ��ʏ�̉摜�\���Ɋւ���ݒ�
			Image imgshabon = Toolkit.getDefaultToolkit().getImage("./image/shabon3.png");
			g.drawImage(imgshabon, 460, 0, 140, 110, this);

			Image imgshabon2 = Toolkit.getDefaultToolkit().getImage("./image/shabon4.png");
			g.drawImage(imgshabon2, 470, 65, 140, 110, this);
			//g.drawImage(imgshabon2, 450, 105, 140, 110, this);
			
			Image imgshabon4 = Toolkit.getDefaultToolkit().getImage("./image/shabon5.png");
			g.drawImage(imgshabon4, 465, 115, 30, 30, this);

			Image imgdougu = Toolkit.getDefaultToolkit().getImage("./image/dougu.png");
			g.drawImage(imgdougu, 450, 190, 125, 115, this);
			
			Image imgfukei3 = Toolkit.getDefaultToolkit().getImage("./image/shibafu.png");
			g.drawImage(imgfukei3, 0, 275, 595, 150, this);

			Image imgrimokon = Toolkit.getDefaultToolkit().getImage("./image/rimokon.jpg");
			g.drawImage(imgrimokon, 240, 182, 130, 23, this);

			// �J�E���g�_�E���̐��� �����T�C�Y�ύX
			g.setColor(Color.white);
			Font fo1 = new Font("Dialog",Font.PLAIN,16);
			g.setFont(fo1);
			
			// �����̑����ύX
			Graphics2D g2 = (Graphics2D)g;
			BasicStroke bs = new BasicStroke(4);
			g2.setStroke(bs);
			
			// �l�p���}�`��`��
			g.drawRect(30,10,250,30);
			g.drawString("�Q�[���J�n�܂ŁF", 50, 32);
			
			/** �Q�[���̐����R�����g **/
			Font fo2 = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo2);
			g.drawString("===================================", 20, 90);
			g.drawString("* ���̃Q�[���̓V���{���ʓ��ăQ�[���ł�", 20, 120);
			g.drawString("* �X�R�A�F�ʏ�@ 1�_  �����@(�r���ŏo��) 3�_", 20, 160);
			
			g.drawString("* �Q�[����ʉ��̃{�^����" , 20, 200);
			g.drawString("   ������V���{���ʂ̐���ς����܂�" , 20, 240);
			
			g.drawString("* ����̼���݋ʂ��������GameOver �ł�", 20, 280);
			/** �Q�[���̐����R�����g�����܂� **/

		}
		
		public void actionPerformed(ActionEvent e) {
			
			label.setForeground(Color.white); //�����F�̎w��
			label.setFont(new Font("Dialog", Font.PLAIN , 20)); //�t�H���g�̎w��
			label.setText(sec + " �b");
			if (sec >= 100) timer.stop();
			else            sec--;
			
		}
	}
	
	public class GameWindow extends JFrame{

		public GameWindow(String title, int width, int height) {
			super(title);                            //�^�C�g���̐ݒ�
			setDefaultCloseOperation(EXIT_ON_CLOSE); //��ʁ~�{�^�����������珈�����I������
			setSize(width,height);                   //��ʃT�C�Y�̐ݒ� 
			setLocationRelativeTo(null);             //��ʈʒu�̐ݒ�
			setResizable(false);
		}
	
		//��ʐ؂�ւ��p���\�b�h
		public void change(JPanel panel) {
			//ContentPane�ɂ͂ߍ��܂ꂽ�p�l�����폜
			getContentPane().removeAll();
		
			super.add(panel);   //�p�l���̒ǉ�
			validate();         //�X�V
			repaint();          //�ĕ`��
		}
		
	}
		
		
	public class TestAnim extends Frame {
		
		int x = 0;

		TestAnim() {
			this.setSize(300, 270);
			setVisible(true);
			setResizable(false);
			setTitle("��");
			setBackground(new Color(0,0,51));
			new MyThread(this, 0);
			new MyThread(this, 200);
		}

		public void redraw(int x) {
			this.x = x;
			repaint();
		}
		public void paint(Graphics g) {
			int xt[]= { 150,171,245,185,208,150,91,114,54,128 };
			int yt[]= { 60,130,129,171,240,197,240,171,129,130 };
			g.setColor(Color.blue);
			g.fillPolygon(xt,yt,10);
			
			g.setColor(Color.white);
			g.drawOval(x, 90, 30, 30);
			g.fillRect(10,90,30,30);
	
			Font fo2 = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo2);
			g.setColor(Color.cyan);
			g.drawString("GAME START!" , 20, 200);
			//g.drawString("����̼���݋ʂɂ������" , 20, 180);
			//g.drawString("GAME OVER" , 20, 200);
		}	
	
		class MyThread extends Thread {
			int x;
			TestAnim ta;
			
			MyThread(TestAnim ta, int x) {
				this.ta = ta;
				this.x = x;
				new Thread(this).start();
			}
		
			public void run() {
				while (true) {
					x = x + 1;
					if (x > 400) x = 0;
					ta.redraw(x);
					try {
						Thread.sleep(100);
					} catch (Exception e) { }
				}
			}
		}
	}

	public static Clip createClip(File path) {
		//�w�肳�ꂽURL�̃I�[�f�B�I���̓X�g���[�����擾
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
			
			//�t�@�C���̌`���擾
			AudioFormat af = ais.getFormat();
			
			//�P��̃I�[�f�B�I�`�����܂ގw�肵����񂩂�f�[�^���C���̏��I�u�W�F�N�g���\�z
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			
			//�w�肳�ꂽ Line.Info �I�u�W�F�N�g�̋L�q�Ɉ�v���郉�C�����擾
			Clip c = (Clip)AudioSystem.getLine(dataLine);
			
			//�Đ���������
			c.open(ais);
			
			return c;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
	}
}