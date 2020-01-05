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

//Java Sound API clip を使用するためのimport
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
	
	// main メソッドは、プログラムを実行する時に最初に呼び出されるメソッド
	// このメソッド内に他のクラスのオブジェクトを作成する記述をしておくことでプログラムは動作する
	public static void main(String[] args) throws MalformedURLException, InterruptedException, Exception{
		new Ex_15_ShabonGame();
	}
	
	public Ex_15_ShabonGame() {

		int yesno = JOptionPane.showConfirmDialog(null, "ゲーム起動", "〇●シャボン玉ゲーム〇●", JOptionPane.YES_NO_OPTION);
        print(yesno);
		
		Dimension dimOfScreen = Toolkit.getDefaultToolkit().getScreenSize();

		// 説明画面を表示する
		GameWindow gw = new GameWindow("ゲームの説明",600, 400);
		gw.setVisible(true);
		gw.change(new MyJPanel2());
		
		try {
			Thread.sleep(6000); // 6秒処理をスリープ
		} catch (InterruptedException e) {
		}
		
		// 説明画面を非表示にする
		gw.setVisible(false);

		/* setBounds ウィンドウの幅と高さを表す部分を，windowWidth，windowHeight とし，数値の使用をなくす */
		setBounds(dimOfScreen.width/2 - windowWidth/2, dimOfScreen.height/2 - windowHeight/2, windowWidth, windowHeight); //after
		setResizable(false);
		setTitle("Software Development Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		MyJPanel panel= new MyJPanel();
		
		//レイアウトマネージャを無効にする（setBoundsが反映されないことの解決策）
		panel.setLayout(null);
		
		//メイン画面を再表示させる
		Container c = getContentPane();
		c.add(panel);
		setVisible(true);
		
		/*** 効果音の設定 ***/
		Clip clip = createClip(new File("./wav/tw043.wav"));
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		/*** 効果音の設定ここまで ***/
		
		/*** 別ウィンドウ表示 ***/
		//new TestAnim();

	}
	
	// シューティングゲーム開始 選択ボタンの処理
	private static void print(int result) {
		switch(result) {
		case JOptionPane.YES_OPTION:                        //JOptionPane. YES_OPTION
			break;
		case JOptionPane.NO_OPTION:                         //JOptionPane. No_OPTION (exitする)
			System.exit(0);
			break;
		case JOptionPane.CLOSED_OPTION:                     //JOptionPane. Close_OPTION (exitする)
			System.exit(0);
			break;
		}
	}
	
	//ベルを鳴らすメソッド
	public static void bell() {
		System.out.print("\007");
		System.out.flush();
	}
	
	public class MyJPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
		/***************** 各変数を宣言 ************************/
		/*ex ) ActionListener を登録したら、actionPerformedメソッドを必ず作成する*/
		/* 全体の設定に関する変数 */
		Dimension dimOfPanel;
		Timer timer;
		Image imgMe, imgMeMissile, imgEnemy, imgEnemyS, imgEnemyS2, imgEnemyS3, imgEnemyMissile, imgEnemyMissileS, imgEnemyMissileS2, imgEnemyMissileS3;
		Image imgCloud, imgCloud2;
		
		/*** 自機に関する変数ここから ***/
		
		boolean isMyAlive;                 //自機の画像 表示変数
		int numOfmyMissile = 3;            //自機のミサイル数
		int myHeight, myWidth;
		int myX, myY, tempMyX;             //位置を表す（x軸とy軸を取得）, 不具合解消2
        int gap = 100; 
		
		/** ランダム変数用 **/
		// まずは固定値 8 を設定
		int randomValue = 8;
		int ranVa;
		int rank;
		
		// グローバル変数（どこのメソッドからも参照可）として、
		// 以下のように変数の枠のみ宣言し、変数への値代入は、actionPerformed内で実施
		int randomValue2;
		
		int[] myMissileXRm = new int[randomValue];
		int[] myMissileYRm = new int[randomValue];
		int[] myMissileSpeedY = new int[randomValue];
		boolean[] isMyMissileActiveRm = new boolean[randomValue]; 
	
		boolean isMylife = false;
		/*** 自機に関する変数ここまで ***/
		
		/*** 敵機に関する変数ここから ***/
		//通常敵機
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
		
		//高速敵機
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
		
		//高速敵機の出現フラグ
		boolean startTeki;
		
		//高速敵機2
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
		
		//高速敵機2の出現フラグ
		boolean startTeki2;

		//高速敵機3
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
		
		//高速敵機3の出現フラグ
		boolean startTeki3;
		
		/*** 敵機に関する変数ここまで ***/
		
		/*** 飛行船に関する変数ここから ***/
		//飛行船1 変数
		int cloudWidth, cloudHeight;
		int cloudX;
		int cloudY;
		int cloudMove;
		boolean isCloudAlive;
		
		//飛行船2 変数
		int cloud2Width, cloud2Height;
		int cloud2X;
		int cloud2Y;
		int cloud2Move;
		boolean isCloud2Alive;
		
		
		//飛行船 出現フラグ
		boolean cloudflg = false;
		boolean cloud2flg = false;
		
		
		/*** 飛行船に関する変数ここまで ***/

		//スコアに関する変数
		int score = 0;
		int scoresum;
		
		/* ボタンに関する変数 */
		JButton threeButton, oneButton, randomButton;
		boolean threeButtonClicked = false;
		boolean oneButtonClicked = false;
		boolean randomButtonClicked = false;
		boolean randomButtonClicked2 = false;

		/*************** 各変数を宣言ここまで ******************/
		
		/******** コンストラクタ（ゲーム開始時の初期化）********/
		public MyJPanel() {
			
			// 全体の設定 
			// 背景色の設定
			setBackground(Color.black);
			addMouseListener(this);
			addMouseMotionListener(this);
			
			// タイマークラスのインスタンス化
			timer = new Timer(50, this);
			
			// 画像の取り込み(自機)
			imgMe = getImg("./image/jiki.png");
			myWidth = imgMe.getWidth(this);
			myHeight = imgMe.getHeight(this);
			
			// 画像の取り込み(自機ミサイル random)
			imgMeMissile = getImg("./image/shabon1.jpg");
			for (int k=0; k<randomValue; k++) {
				myMissileXRm[k] = imgMeMissile.getWidth(this);
				myMissileYRm[k] = imgMeMissile.getHeight(this);
			}
			
			// 画像の取り込み(敵機)
			// 通常
			imgEnemy = getImg("./image/teki.png");
			enemyWidth = imgEnemy.getWidth(this);
			enemyHeight = imgEnemy.getHeight(this);
			
			// 高速
			imgEnemyS = getImg("./image/teki2.png");
			enemyWidthS = imgEnemyS.getWidth(this);
			enemyHeightS = imgEnemyS.getHeight(this);
			
			// 高速2
			imgEnemyS2 = getImg("./image/teki2.png");
			enemyWidthS2 = imgEnemyS2.getWidth(this);
			enemyHeightS2 = imgEnemyS2.getHeight(this);
			
			// 高速3
			imgEnemyS3 = getImg("./image/teki2.png");
			enemyWidthS3 = imgEnemyS3.getWidth(this);
			enemyHeightS3 = imgEnemyS3.getHeight(this);
			
			
			// 画像の取り込み(敵機ミサイル)
			imgEnemyMissile = getImg("./image/shabon2.jpg");
			
			// 画像の取り込み(高速敵機ミサイル)
			imgEnemyMissileS = getImg("./image/shabon6.png");
			imgEnemyMissileS2 = getImg("./image/shabon7.png");
			imgEnemyMissileS3 = getImg("./image/shabon8.png");
			
			
			// 自機と敵機の初期化
			initMyPlane();
			initEnemyPlane();
			initEnemyPlaneS();
			initEnemyPlaneS2();
			initEnemyPlaneS3();
		
			// 飛行船  https://ja.pngtree.com/freepng/clouds_766384.html
			imgCloud = getImg("./image/hikousen.png");
			cloudWidth = imgCloud.getWidth(this);
			cloudHeight = imgCloud.getHeight(this);
			
			
			imgCloud2 = getImg("./image/hikousen2.png");
			cloud2Width = imgCloud2.getWidth(this);
			cloud2Height = imgCloud2.getHeight(this);
			

			// 飛行船の初期化
			initCloud();
			initCloud2();
			
			/***************** ボタン ***********************/
			//setBounds(int x, int y, int width, int height)
			
			oneButton = new JButton("●");
        	oneButton.setBounds(390, 530, 50, 25);
			oneButton.setBorder(new LineBorder(Color.magenta, 1, true));
        	oneButton.addActionListener(this);

			threeButton = new JButton("●●●");
        	threeButton.setBounds(450, 530, 50, 25);
			threeButton.setBorder(new LineBorder(Color.magenta, 1, true));
        	threeButton.addActionListener(this);
			
			randomButton = new JButton("★♪");
        	randomButton.setBounds(510, 530, 50, 25);
			randomButton.setBorder(new LineBorder(Color.magenta, 1, true));
        	randomButton.addActionListener(this);
			
			// addメソッドで実際にパネル上に配置
			add(threeButton);
			add(oneButton);
			add(randomButton);
		}
		
		/* 修飾子 戻り値の型 メソッド名(受け取る引数) {メソッドの処理}
		/* 戻り値を返さない場合は次のように記述して呼び出す */
		   //メソッド名(引数)
		
		/* 戻り値を返す場合は次のように記述して呼び出す */
		   //戻り値を格納するオブジェクト変数名 = メソッド名(引数)
		
		/* 画像ファイルから Image クラスへの変換 */
		public Image getImg(String filename) {
			ImageIcon icon = new ImageIcon(filename);
			Image img = icon.getImage();
			return img;
		}
		
		/* 自機の初期化 ***************/
		public void initMyPlane() {
			myX = windowWidth / 2;
			myY = windowHeight - 110;
			tempMyX = windowWidth / 2; //不具合解消2
			
			//random用
			Random rand = new Random();
			
			for (int k=0; k<randomValue; k++) {
				isMyMissileActiveRm[k] = false;
				myMissileSpeedY[k] = rand.nextInt(10) + 10;  //ミサイル数のスピードを調整 (10～19のスピードで発射される)
			}
			
			isMyAlive = true;
		}
		
		
		/* 敵機の初期化 ***************/
		public void initEnemyPlane() {
			
			/****** 通常敵機 ******/
			// 12機のうち最初の7機は y座標を50
			for (int i=0; i<7; i++) {
				enemyX[i] = 70*i;
				enemyY[i] = 50;
			}
			// 残りは y座標を100設定
			for (int i=7; i<numOfEnemy; i++) {
				enemyX[i] = 70*(i-6);
				enemyY[i] = 100;
			}
			
			for (int i=0; i<numOfEnemy; i++) {
				isEnemyAlive[i] = true;
				// 1 で右方向に移動 -1 だと左方向に移動
				enemyMove[i] = 1;
			}

			for (int i=0; i<numOfEnemy; i++) {
				isEnemyMissileActive[i] = true;
				enemyMissileX[i] = enemyX[i] + enemyWidth/2;
				enemyMissileY[i] = enemyY[i];
				
				/* 各機のミサイルのスピードを表す enemyMissileSpeed[i]の値を変えることで、
				   ランダム性を表現。そのために「10 + (i%6)」という計算式を用いて値を変化させている */
				enemyMissileSpeed[i] = 5 + (i%6); 
			}
			/****** 通常敵機 ******/
		}
			
		public void initEnemyPlaneS() {
			/****** 高速敵機 ******/
			
			// 4機のうち最5初の2機は y座標を50
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
				// 1 で右方向に移動 -1 だと左方向に移動
				enemyMoveS[i] = 1;
			}

			for (int i=0; i<numOfEnemyS; i++) {
				isEnemyMissileActiveS[i] = true;
				enemyMissileXS[i] = enemyXS[i] + enemyWidthS/2;
				enemyMissileYS[i] = enemyYS[i];
				
				/* 各機のミサイルのスピードを表す enemyMissileSpeed[i]の値を変えることで、
				   ランダム性を表現。そのために「10 + (i%6)」という計算式を用いて値を変化させている */
				enemyMissileSpeedS[i] = 5 + (i%6); 
			}
			/****** 高速敵機 ******/
		}
		
		public void initEnemyPlaneS2() {
			/****** 高速敵機2 ******/
			
			// 2機のうち最初の1機は y座標を50
			for (int i=0; i<1; i++) {
				enemyXS2[i] = -200;
				enemyYS2[i] = 30;
			}
			// 残りは y座標を100設定
			for (int i=1; i<numOfEnemyS2; i++) {
				enemyXS2[i] = -200;
				enemyYS2[i] = 80;
			}
			
			
			for (int i=0; i<numOfEnemyS2; i++) {
				isEnemyAliveS2[i] = true;
				// 1 で右方向に移動 -1 だと左方向に移動
				enemyMoveS2[i] = 1;
			}

			for (int i=0; i<numOfEnemyS2; i++) {
				isEnemyMissileActiveS2[i] = true;
				enemyMissileXS2[i] = enemyXS2[i] + enemyWidthS2/2;
				enemyMissileYS2[i] = enemyYS2[i];
				
				/* 各機のミサイルのスピードを表す enemyMissileSpeed[i]の値を変えることで、
				   ランダム性を表現。そのために「10 + (i%6)」という計算式を用いて値を変化させている */
				enemyMissileSpeedS2[i] = 5 + (i%6); 
			}
			/****** 高速敵機2 ******/
		}
		
		public void initEnemyPlaneS3() {
		
			/****** 高速敵機3 ******/
			
			// 4機のうち最初の2機は y座標を50
			for (int i=0; i<1; i++) {
				enemyXS3[i] = -200;
				enemyYS3[i] = 70;
			}
			// 残りは y座標を100設定
			for (int i=1; i<numOfEnemyS3; i++) {
				enemyXS3[i] = -200;
				enemyYS3[i] = 120;
			}
			
			for (int i=0; i<numOfEnemyS3; i++) {
				isEnemyAliveS3[i] = true;
				// 1 で右方向に移動 -1 だと左方向に移動
				enemyMoveS3[i] = 1;
			}

			for (int i=0; i<numOfEnemyS3; i++) {
				isEnemyMissileActiveS3[i] = true;
				enemyMissileXS3[i] = enemyXS3[i] + enemyWidthS3/2;
				enemyMissileYS3[i] = enemyYS3[i];
				
				/* 各機のミサイルのスピードを表す enemyMissileSpeed[i]の値を変えることで、
				   ランダム性を表現。そのために「10 + (i%6)」という計算式を用いて値を変化させている */
				enemyMissileSpeedS3[i] = 5 + (i%6); 
			}
			/****** 高速敵機3 ******/
		}
		/* 敵機の初期化 ここまで ******/
		
		
				
		/* 飛行船の初期化 ***************/
		public void initCloud() {
			
			// y座標を180
				cloudX = -200;
				cloudY = 80;

			isCloudAlive = true;
			// 1 で右方向に移動 -1 だと左方向に移動
			cloudMove = 1;

		}
		
		public void initCloud2() {
			
			// y座標を150
				cloud2X = -200;
				cloud2Y = 140;

			isCloud2Alive = true;
			// 1 で右方向に移動 -1 だと左方向に移動
			cloud2Move = 1;

		}
		
		/* 飛行船の初期化 ここまで ******/
		
		
		/**************************************************************************/
		/*  paintComponent (パネル上の描画)                                       */
		/*                                                                        */
		/*  ボタンがクリックされたときの処理は actionPerformed メソッド内に記述   */
    	/*  actionPerformedメソッドでは、ボタンが押されたことを認識し、           */
    	/*  そのことを paintComponent メソッドに伝える                            */
		/*  drawString(String str, int x, int y)                                  */
		/**************************************************************************/
		
		public void paintComponent(Graphics g) {
			
			//http://switch-box.net/free-illustration-hanabi.html
			dimOfPanel = getSize();
			super.paintComponent(g);  /* (g)で初期化 */ /* timerクラスを使用する際、連続表示されるので初期化する */
			
			//画面全体に、背景画像を埋め込み
			//drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
			Image imghanabi = Toolkit.getDefaultToolkit().getImage("./image/hanabi.jpg");
			g.drawImage(imghanabi, 0, 0, 1000, 520, this);

			Image imgboard = Toolkit.getDefaultToolkit().getImage("./image/board.jpg");
			g.drawImage(imgboard, 0, 525, 1000, 50, this);
					
			
			//日付取得
			Calendar cl = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日(E)H時m分s秒");

			/*** 画面下 ボート内の設定 ここから***/
			//スコアボードの表示 & 背景設定
			//fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
			g.setColor(Color.darkGray);
			g.fillRoundRect(80, 530, 140, 28, 20, 10); //1. スコア
			g.fillRoundRect(230, 530, 100, 28, 20, 10);//2. 発射ボタン
			g.fillRoundRect(680, 530, 300, 28, 20, 10);//3. 日付
			
			//1. 文字 スコア
			g.setColor(Color.green);
			Font fo1 = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo1);
			g.drawString("スコア：" + score, 90, 550);
			int scoresum = score;


			//GameOver時に画面表示する処理
			if (isMylife) {
				
				/*** ファイル入出力に関する処理 ***/
				//ファイル書き込み
				try {
					File f = new File("score.txt");
					BufferedWriter bw = new BufferedWriter(new FileWriter(f));
					bw.write(sdf.format(cl.getTime()) +  "スコア得点" + scoresum);
					bw.newLine();
 					bw.close();
				
				} catch (IOException e) {
				}
				
				String rankC2 = "☆彡GAMEOVER☆彡";
				//文字列を文字単位に分割してchar配列にする
				char data[] = rankC2.toCharArray();
				//長さを確認する
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
					g.drawString("最終スコア：" + score + "   結果ログ「score.txt」", 90, 510);

				}
			}
			
			Font fo = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo);
			
			g.setColor(Color.green);
			//2. 文字 発射ボタン,音楽ボタン
			g.drawString("push!", 340, 550);
			
			//3. 文字 日付
			g.drawString(sdf.format(cl.getTime()), 685, 550);
			
			/*** 画面下 ボート内の設定ここまで ***/
			
			//タイマースタート
			timer.start();

			//メソッドの呼び出し
			drawCloud(g);             //飛行船
			drawCloud2(g);             //飛行船
			drawMyPlane(g);           //自機
			drawMyMissileRm(g);       //自機のミサイル
			drawEnemyPlane(g);        //敵機
			drawEnemyMissile(g);      //敵機のミサイル
			drawEnemyPlaneS(g);       //敵機(高速)
			drawEnemyMissileS(g);     //敵機のミサイル(高速)
			drawEnemyPlaneS2(g);      //敵機(高速2)
			drawEnemyMissileS2(g);    //敵機のミサイル(高速2)
			drawEnemyPlaneS3(g);      //敵機(高速3)
			drawEnemyMissileS3(g);    //敵機のミサイル(高速3)
			
			/*** パネルに光る星を表示  ランダム色 ***/
			Random rand = new Random();
			int R=(int)(Math.random()*200);
			int G=(int)(Math.random()*200);
			int B=(int)(Math.random()*0);
 			g.setColor(new Color(R,G,B));
			Font fo4 = new Font("Dialog",Font.PLAIN,12);
			g.setFont(fo4);
			g.drawString("☆", 960, 240);
			Font fo3 = new Font("Dialog",Font.PLAIN,8);
			g.setFont(fo3);
						
			int R2=(int)(Math.random()*200);
			int G2=(int)(Math.random()*100);
			int B2=(int)(Math.random()*200);
 			g.setColor(new Color(R2,G2,B2));
			g.drawString("☆", 930, 260);
			/*** パネルに光る星を表示  ランダム色 ***/
			
			/***** 定期的に出現させるための処理 *****/
			Calendar cal;
			cal = Calendar.getInstance();  //Calender オブジェクトの生成
		
			//高速敵機出現フラグ
			if (cal.get(Calendar.SECOND) == 0) {
				// もし、calの秒が0秒なら
				initEnemyPlaneS();
				startTeki = true;
			}else if (cal.get(Calendar.SECOND) == 12) {
				// もし、calの秒が0秒なら
				initEnemyPlaneS();
				startTeki = true;
			}else if (cal.get(Calendar.SECOND) == 32) {
				// もし、calの秒が0秒なら
				initEnemyPlaneS();
				startTeki = true;
			}else if (cal.get(Calendar.SECOND) == 48) {
				// もし、calの秒が0秒なら
				initEnemyPlaneS();
				startTeki = true;
			}
			
			
			if (cal.get(Calendar.SECOND) == 15) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 25) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 35) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 45) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 55) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS2();
				startTeki2 = true;
			}else if (cal.get(Calendar.SECOND) == 5) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS2();
				startTeki2 = true;
			}
			

			if (cal.get(Calendar.SECOND) == 10) {
				// もし、calの秒が40秒なら
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 20) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 30) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 40) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 50) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS3();
				startTeki3 = true;
			}else if (cal.get(Calendar.SECOND) == 0) {
				// もし、calの秒が20秒なら
				initEnemyPlaneS3();
				startTeki3 = true;
			}
			
			
			//飛行船出現フラグ
			if (cal.get(Calendar.SECOND) == 10) {
				initCloud();
				cloudflg = true;
			}
			
			if (cal.get(Calendar.SECOND) == 30) {
				initCloud2();
				cloud2flg = true;
			}
			
			/***** 定期的に出現させるための処理 *****/
		}
		
		/**************************************************************************/
		/* 機器の描画ここから                                                     */
		/**************************************************************************/
		/****************** 自機の描画 ****************************/
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
		
		/**************** 自機のミサイルの描画 (1発, 3発, ランダム) ********************/
		public void drawMyMissileRm(Graphics g) {
			
			// ミサイルの位置 X                                // ミサイルの位置 X  左記は以下と同じ
			int sum = 2;                                       // myMissileXRm[1]  -= 2;
			for(int Rm =1; Rm < randomValue; Rm ++){           // myMissileXRm[2]  -= 1;
				myMissileXRm[Rm] -= sum;                       // myMissileXRm[3]  -= 0;
				sum = sum - 1;                                 // myMissileXRm[4]  -= -1;
			}                                                  // myMissileXRm[5]  -= -2;
			                                                   // myMissileXRm[6]  -= -3;
			// ミサイルの位置 Y                                // myMissileXRm[7]  -= -4;
			for(int Rm = 0; Rm < randomValue; Rm ++){
				myMissileYRm[Rm] -= myMissileSpeedY[Rm];
			}
			
			// ランダム数 表示
			g.setColor(Color.green);
			g.drawString("★♪：", 240, 550);
			if (randomButtonClicked2) {
				g.drawString("★♪：" + ranVa, 240, 550);
			}
			
			// actionPerformed のランダムボタン設定押下により、生成されたランダム値（randomValue2）まで繰り返す
			for(int k=0; k<randomValue2; k++){
			
				// 配列で例外が発生した場合にキャッチする
				try {
					if (isMyMissileActiveRm[k]) {
						
						// ミサイルの配置
						g.drawImage(imgMeMissile, myMissileXRm[k], myMissileYRm[k], this);
						// 自機のミサイルの敵機各機への当たり判定
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
						
						
						/***************** 高速敵機の処理 ******************/
						// 自機のミサイルの敵機各機への当たり判定 (高速)
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
						
						
						/***************** 高速2 敵機の処理 ******************/
						// 自機のミサイルの敵機各機への当たり判定 (高速2)
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
						
						
						/***************** 高速3 敵機の処理 ******************/
						// 自機のミサイルの敵機各機への当たり判定 (高速3)
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
						
							
						// ミサイルがウィンドウ外に出たときのミサイルの再初期化
						if (myMissileYRm[k] < 0) isMyMissileActiveRm[k] = false;
						}
				
					
					} catch (ArrayIndexOutOfBoundsException f) {

						System.out.println("配列の要素数を超えています");
						System.exit(0);
				}
			}
		}
		
		/****************** 敵機の描画 ****************************/
		public void drawEnemyPlane(Graphics g) {
			// 敵機の描画
			for (int i=0; i<numOfEnemy; i++) {
				if (isEnemyAlive[i]) {
					if (enemyX[i] > dimOfPanel.width - enemyWidth) {
						//右から跳ね返る
						enemyMove[i] = -1;
					} else if (enemyX[i] < 0) {
						//左から跳ね返る
						enemyMove[i] = 1;
					}
					enemyX[i] += enemyMove[i]*10;
					g.drawImage(imgEnemy, enemyX[i], enemyY[i], this);
				}
			}
		}
		
		/**************** 敵機のミサイルの描画 *********************/
		public void drawEnemyMissile(Graphics g) {
			
			for (int i=0; i<numOfEnemy; i++) {
				// ミサイルの配置
				if (isEnemyMissileActive[i]) {
					enemyMissileY[i] += enemyMissileSpeed[i];
					g.drawImage(imgEnemyMissile, enemyMissileX[i], enemyMissileY[i], this);
				}
				
				// 敵機のミサイルの自機への当たり判定 (改行)
				if (isMyAlive && numOfAlive!=0) {
					if ((enemyMissileX[i] >= tempMyX) &&
						(enemyMissileX[i] <= tempMyX+myWidth) &&
						(enemyMissileY[i]+5 >= myY) 
						&& (enemyMissileY[i]+5 <= myY+myHeight)) {
						isEnemyMissileActive[i] = false;  // 自機にミサイルが当たったら敵機のミサイル画像を消す
						java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.付属の beep音
						isMyAlive = false; // ミサイルが当たったら自機の画像を消す
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "相手のシャボン玉に当たりました" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// ミサイルがウィンドウ外に出たときのミサイルの再初期化
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
		
		/****************** 高速 敵機の描画 ****************************/
		public void drawEnemyPlaneS(Graphics g) {
			
			// 敵機の描画
			for (int i=0; i<numOfEnemyS; i++) {
				if (isEnemyAliveS[i] && startTeki) {
				
					if (enemyXS[i] > dimOfPanel.width - enemyWidthS) {
						//右から跳ね返る
						enemyMoveS[i] = -1;
					} else if (enemyXS[i] < 0) {
						//左から跳ね返る
					//enemyMoveS[i] = 1;
					}
					enemyXS[i] += enemyMoveS[i]*20; // 敵機の移動スピード
					g.drawImage(imgEnemyS, enemyXS[i], enemyYS[i], this);
				}
			}
		}
		
		/**************** 高速 敵機のミサイルの描画 *********************/
		public void drawEnemyMissileS(Graphics g) {
			
			for (int i=0; i<numOfEnemyS; i++) {

				// ミサイルの配置
				if (isEnemyMissileActiveS[i] && startTeki) {
					enemyMissileYS[i] += enemyMissileSpeedS[i];
					g.drawImage(imgEnemyMissileS, enemyMissileXS[i], enemyMissileYS[i], this);
				}

				// 高速敵機のミサイルの自機への当たり判定 (改行)
				if (isMyAlive) {
					if ((enemyMissileXS[i] >= tempMyX) &&
						(enemyMissileXS[i] <= tempMyX+myWidth) &&
						(enemyMissileYS[i]+5 >= myY) 
						&& (enemyMissileYS[i]+5 <= myY+myHeight)) {
						isEnemyMissileActiveS[i] = false;  // 自機にミサイルが当たったら敵機のミサイル画像を消す
						isMyAlive = false;        // ミサイルが当たったら自機の画像を消す
						//java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.付属の beep音
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "相手のシャボン玉に当たりました" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// ミサイルがウィンドウ外に出たときのミサイルの再初期化
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
		
		
		/****************** 高速2 敵機の描画 ****************************/
		public void drawEnemyPlaneS2(Graphics g) {
			
			// 敵機の描画
			for (int i=0; i<numOfEnemyS2; i++) {
				if (isEnemyAliveS2[i] && startTeki2) {
				
					if (enemyXS2[i] > dimOfPanel.width - enemyWidthS2) {
						//右から跳ね返る
						enemyMoveS2[i] = -1;
					} else if (enemyXS2[i] < 0) {
						//左から跳ね返る
					//	enemyMoveS2[i] = 1;
					}
					enemyXS2[i] += enemyMoveS2[i]*20; // 敵機の移動スピード
					g.drawImage(imgEnemyS2, enemyXS2[i], enemyYS2[i], this);
				}
			}
		}
		
		/**************** 高速2 敵機のミサイルの描画 *********************/
		public void drawEnemyMissileS2(Graphics g) {
			
			for (int i=0; i<numOfEnemyS2; i++) {

				// ミサイルの配置
				if (isEnemyMissileActiveS2[i] && startTeki2) {
					enemyMissileYS2[i] += enemyMissileSpeedS2[i];
					g.drawImage(imgEnemyMissileS2, enemyMissileXS2[i], enemyMissileYS2[i], this);
				}

				// 高速敵機のミサイルの自機への当たり判定 (改行)
				if (isMyAlive) {
					if ((enemyMissileXS2[i] >= tempMyX) &&
						(enemyMissileXS2[i] <= tempMyX+myWidth) &&
						(enemyMissileYS2[i]+5 >= myY) 
						&& (enemyMissileYS2[i]+5 <= myY+myHeight)) {
						isEnemyMissileActiveS2[i] = false;  // 自機にミサイルが当たったら敵機のミサイル画像を消す
						isMyAlive = false;        // ミサイルが当たったら自機の画像を消す
						//java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.付属の beep音
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "相手のシャボン玉に当たりました" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// ミサイルがウィンドウ外に出たときのミサイルの再初期化
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
		
		
		/****************** 高速3 敵機の描画 ****************************/
		public void drawEnemyPlaneS3(Graphics g) {
			
			// 敵機の描画
			for (int i=0; i<numOfEnemyS3; i++) {
				if (isEnemyAliveS3[i] && startTeki3) {
				
					if (enemyXS3[i] > dimOfPanel.width - enemyWidthS3) {
						//右から跳ね返る
						enemyMoveS3[i] = -1;
					} else if (enemyXS3[i] < 0) {
						//左から跳ね返る
					//	enemyMoveS3[i] = 1;
					}
					enemyXS3[i] += enemyMoveS3[i]*20; // 敵機の移動スピード
					g.drawImage(imgEnemyS3, enemyXS3[i], enemyYS3[i], this);
				}
			}
		}
		
		/**************** 高速3 敵機のミサイルの描画 *********************/
		public void drawEnemyMissileS3(Graphics g) {
			
			for (int i=0; i<numOfEnemyS3; i++) {

				// ミサイルの配置
				if (isEnemyMissileActiveS3[i] && startTeki3) {
					enemyMissileYS3[i] += enemyMissileSpeedS3[i];
					g.drawImage(imgEnemyMissileS3, enemyMissileXS3[i], enemyMissileYS3[i], this);
				}

				// 高速敵機のミサイルの自機への当たり判定 (改行)
				if (isMyAlive) {
					if ((enemyMissileXS3[i] >= tempMyX) &&
						(enemyMissileXS3[i] <= tempMyX+myWidth) &&
						(enemyMissileYS3[i]+5 >= myY) 
						&& (enemyMissileYS3[i]+5 <= myY+myHeight)) {
						isEnemyMissileActiveS3[i] = false;  // 自機にミサイルが当たったら敵機のミサイル画像を消す
						isMyAlive = false;        // ミサイルが当たったら自機の画像を消す
						//java.awt.Toolkit.getDefaultToolkit().beep();    //awt.Toolkit.付属の beep音
						AudioClip ac;
						ac = Applet.newAudioClip(getClass().getResource("./wav/owari.wav"));
						ac.play();
						isMylife = true;
						JOptionPane.showMessageDialog(null, "相手のシャボン玉に当たりました" + "\n" + "Game Over");
						System.exit(0);
					}
				}
				
				// ミサイルがウィンドウ外に出たときのミサイルの再初期化
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
		/* 機器の描画ここまで                                                     */
		/**************************************************************************/
		
		/****************** 飛行船の描画 ****************************/
		public void drawCloud(Graphics g) {
						
			if (cloudflg) {
        		if (cloudX > dimOfPanel.width - cloudWidth) {
					//右から跳ね返る
					//cloudMove[i] = -1;
				} else if (cloudX < 0) {
					//左から跳ね返る
					//cloudMove[i] = 1;
				}
				cloudX += cloudMove*2;
				g.drawImage(imgCloud, cloudX, cloudY, this);
			}
		}

		/****************** 飛行船の描画ここまで *********************/
		
		/****************** 飛行船2の描画 ****************************/
		public void drawCloud2(Graphics g) {
						
			if (cloud2flg) {
        		if (cloud2X > dimOfPanel.width - cloud2Width) {
					//右から跳ね返る
					//cloud2Move[i] = -1;
				} else if (cloud2X < 0) {
					//左から跳ね返る
					//cloud2Move[i] = 1;
				}
				cloud2X += cloud2Move*2;
				g.drawImage(imgCloud2, cloud2X, cloud2Y, this);
			}
		}

		/****************** 飛行船2の描画ここまで *********************/
		
		public void actionPerformed(ActionEvent e) {
			/* ボタンをクリックした時の処理 */
			/* 一定時間ごとの処理（ActionListener に対する処理）*/
		
			AudioClip ac;
				
			// 1発ボタン(●)が押されたとき
			if (e.getSource() == oneButton){
				randomValue2 = 1;
				oneButtonClicked = true;
				threeButtonClicked = false;
				randomButtonClicked = false;
				ac = Applet.newAudioClip(getClass().getResource("./wav/oto1.wav"));
				ac.play();
			}
			
			// 3発ボタン(●●●)が押されたとき
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
			
			// randomボタン(★♪)が押されたとき
			if (e.getSource() == randomButton){
				//randomValue2 = (int)(Math.random()*4)+10; //ランダム変数 Exceptionになるもの
				randomValue2 = (int)(Math.random()*4)+5; //ランダム変数を設定(4以上8以下の乱数)
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
			
			/* paintComponent 呼び出し */
			repaint();
		}

		public void mouseClicked(MouseEvent e) {
			/* マウスボタンをクリックしたときの処理 */
		}
		
		public void mousePressed(MouseEvent e) {
			/* マウスボタンを押したときの処理 */
			
			/*** 自機画像が存在し、ミサイルが発射されていなかったら発射する */
			if (isMyAlive){

				// 3発ボタン(●●●)
				for (int j=0; j<numOfmyMissile; j++) {
					if (!isMyMissileActiveRm[j] && threeButtonClicked) {
						myMissileXRm[j] = tempMyX + myWidth/2; //不具合解消2
						myMissileYRm[j] = myY;
						isMyMissileActiveRm[j] = true; 
					}		
				}
				
				// 1発ボタン(●)
				if (!isMyMissileActiveRm[0]) {
					myMissileXRm[0] = tempMyX + myWidth/2; //不具合解消2
					myMissileYRm[0] = myY;
					isMyMissileActiveRm[0] = true;
				}
				
				// randomボタン(★♪)
				for (int k=0; k<randomValue2; k++) {
					if (!isMyMissileActiveRm[k] && randomButtonClicked) {
						myMissileXRm[k] = tempMyX + myWidth/2; //不具合解消2
						myMissileYRm[k] = myY;
						isMyMissileActiveRm[k] = true;
					}
				}
			}
			/*** 自機画像が存在し、ミサイルが発射されていなかったら発射するここまで */
			
			
		}

		public void mouseReleased(MouseEvent e) {
			/* マウスボタンを離したときの処理 */
		}

		public void mouseExited(MouseEvent e) {
			/* マウスが対象のコンポーネントから出たときの処理 */
		}

		public void mouseEntered(MouseEvent e) {
			/* マウスが対象のコンポーネントに入ったときの処理 */
		}

		public void mouseMoved(MouseEvent e) {
			/* MouseMotionListener に対する処理 */
			myX = e.getX();
		}

		public void mouseDragged(MouseEvent e) {
			//不具合解消1 mouseMoved メソッドと同様に，mouseDragged メソッドに対しても次の記述を行う
			myX = e.getX();
		}
	}

	
	public class MyJPanel2 extends JPanel implements ActionListener{
		//ActionListener を登録したら、actionPerformedメソッドを必ず作成する
		/***************** 各変数を宣言 ************************/
		/* 全体の設定に関する変数 */
		Timer timer;
		JLabel label;
		int sec = 5;

		/******** コンストラクタ（ゲーム開始時の初期化）********/
		public MyJPanel2() {
			
			// 全体の設定 
			// 背景色の設定
			//setBackground(Color.black);
			setBackground(new Color(0,0,51));
			
			label = new JLabel("ｶｳﾝﾄ", JLabel.LEFT);
			label.setPreferredSize(new Dimension(200,40));
			//label.setBorder(new LineBorder(Color.white, 3, true));
			add(label);

			timer = new Timer(1000, this);
			timer.start();

		}
		
		/**************************************************************************/
		/*  paintComponent (パネル上の描画)                                       */
		/**************************************************************************/
		
		public void paintComponent(Graphics g) {
						
			super.paintComponent(g);  /* (g)で初期化 */
			
			// drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
			// 画面上の画像表示に関する設定
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

			// カウントダウンの数字 文字サイズ変更
			g.setColor(Color.white);
			Font fo1 = new Font("Dialog",Font.PLAIN,16);
			g.setFont(fo1);
			
			// 線幅の太さ変更
			Graphics2D g2 = (Graphics2D)g;
			BasicStroke bs = new BasicStroke(4);
			g2.setStroke(bs);
			
			// 四角い図形を描く
			g.drawRect(30,10,250,30);
			g.drawString("ゲーム開始まで：", 50, 32);
			
			/** ゲームの説明コメント **/
			Font fo2 = new Font("Dialog",Font.PLAIN,18);
			g.setFont(fo2);
			g.drawString("===================================", 20, 90);
			g.drawString("* このゲームはシャボン玉当てゲームです", 20, 120);
			g.drawString("* スコア：通常機 1点  高速機(途中で出現) 3点", 20, 160);
			
			g.drawString("* ゲーム画面下のボタンで" , 20, 200);
			g.drawString("   投げるシャボン玉の数を変えられます" , 20, 240);
			
			g.drawString("* 相手のｼｬﾎﾞﾝ玉があたるとGameOver です", 20, 280);
			/** ゲームの説明コメントここまで **/

		}
		
		public void actionPerformed(ActionEvent e) {
			
			label.setForeground(Color.white); //文字色の指定
			label.setFont(new Font("Dialog", Font.PLAIN , 20)); //フォントの指定
			label.setText(sec + " 秒");
			if (sec >= 100) timer.stop();
			else            sec--;
			
		}
	}
	
	public class GameWindow extends JFrame{

		public GameWindow(String title, int width, int height) {
			super(title);                            //タイトルの設定
			setDefaultCloseOperation(EXIT_ON_CLOSE); //画面×ボタンを押したら処理を終了する
			setSize(width,height);                   //画面サイズの設定 
			setLocationRelativeTo(null);             //画面位置の設定
			setResizable(false);
		}
	
		//画面切り替え用メソッド
		public void change(JPanel panel) {
			//ContentPaneにはめ込まれたパネルを削除
			getContentPane().removeAll();
		
			super.add(panel);   //パネルの追加
			validate();         //更新
			repaint();          //再描画
		}
		
	}
		
		
	public class TestAnim extends Frame {
		
		int x = 0;

		TestAnim() {
			this.setSize(300, 270);
			setVisible(true);
			setResizable(false);
			setTitle("☆");
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
			//g.drawString("相手のｼｬﾎﾞﾝ玉にあたると" , 20, 180);
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
		//指定されたURLのオーディオ入力ストリームを取得
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
			
			//ファイルの形式取得
			AudioFormat af = ais.getFormat();
			
			//単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
			
			//指定された Line.Info オブジェクトの記述に一致するラインを取得
			Clip c = (Clip)AudioSystem.getLine(dataLine);
			
			//再生準備完了
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