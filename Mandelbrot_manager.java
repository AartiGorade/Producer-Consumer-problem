// original from: http://rosettacode.org/wiki/Mandelbrot_set#Java
// modified for color

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JFrame;


/**
* Pixel threads that calculates the iter value for each pixel
* set Color of the pixel
* @author Lakshmi Ravi
* @author Aarti Gorde
*
*/

class PixelThread extends Thread {
	
	Stack<Integer> xIndex;
	Stack<Integer> yIndex;
	static int LENGTH;
	static double ZOOM;
	static int MAX;
	Mandelbrot_manager mandelBrot;
	boolean inputOver;
	static Integer threadCount=0;
	private String info;
	boolean managerIsRunning;
	
	public PixelThread() {
		xIndex = new Stack<Integer>();
		yIndex = new Stack<Integer>();
		inputOver = false;
		managerIsRunning=false;
		this.info=null;
		threadCount++;
	}
	public PixelThread(int x, int y, Mandelbrot_manager m,String info){
		xIndex = new Stack<Integer>();
		yIndex = new Stack<Integer>();
		inputOver = false;
		managerIsRunning=false;
		xIndex.add(x); yIndex.add(y);mandelBrot=m;
		this.info=info;
		threadCount++;
	}
	
	public synchronized void setInputOver(){
		inputOver=true;
		notifyAll();
	}

	public static void setCommonProperties(int length, double zoom, int max){
		/**
		* @param length of the given image
		* @param zoom : zoom factor in the image
		* @param max : max pixels
		*/
		
		LENGTH=length; ZOOM=zoom;MAX=max;
	}
	
	public synchronized void addIndex(int x, int y) {
		/**
		* Add another pixel to this thread
		* @param x : x index of pixel
		* @param y :y index of pixel
		*/
		
		xIndex.add(x);
		yIndex.add(y);
		notifyAll();
	}

	public synchronized void run() {
		
		// Set flag True if Thread manager has started 
		if(this.info.equals("M") && (!managerIsRunning)){
			System.out.println("Started Thread manager ...");
			managerIsRunning=true;
			mandelBrot.createSet();
		}
		else{
		while (true) {
			if (xIndex.isEmpty()) {
				if (inputOver) {
					synchronized (mandelBrot) {
						threadCount--;
					}
					return;
				}
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//Distribute work among worker threads
			else if (!xIndex.isEmpty()) {
				int x = xIndex.pop();
				int y = yIndex.pop();
				double zx, zy, cX, cY;
				zx = zy = 0;
				cX = (x - LENGTH) / ZOOM;
				cY = (y - LENGTH) / ZOOM;
				int iter = 0;
				double tmp;
				while ((zx * zx + zy * zy < 10) && (iter < MAX - 1)) {
					tmp = zx * zx - zy * zy + cX;
					zy = 2.0 * zx * zy + cY;
					zx = tmp;
					iter++;
				}
				mandelBrot.setImageRGB(x, y, iter);
			}

		}
	}
	}
}

/**
* Symbolizes the Madel-brot image
* Initiates the thread to calculate iter value
*/

public class Mandelbrot_manager extends JFrame {

	private final int MAX = 5000;
	private final int LENGTH = 800;
	private final double ZOOM = 1000;
	private BufferedImage theImage;
	private int[] colors = new int[MAX];

	public Mandelbrot_manager() {
		super("Mandelbrot Set");
		initColors();
		setBounds(100, 100, LENGTH, LENGTH);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	public void setImageRGB(int x, int y, int iter) {
		if (iter > 0)
			theImage.setRGB(x, y, colors[iter]);
		else
			theImage.setRGB(x, y, iter | (iter << 8));
	}

	public void createSet() {
		/***
		 *  Starting worker threads as per Runtime.getRuntime().availableProcessors().
		 *  Worker threads will be active till full image is calculated.
		 *  
		 */
		
		theImage = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		int processorCount = Runtime.getRuntime().availableProcessors();
		//Create '4' (processor count ) pixel threads and initiate them
		PixelThread pixelThreads[] = new PixelThread[processorCount];
		PixelThread.setCommonProperties(LENGTH, ZOOM, MAX);
		for(int index=0; index < (processorCount); index++){
			pixelThreads[index] = new PixelThread(0,0,this,Integer.toString(index));
			pixelThreads[index].start();
		}
		System.out.println("Started "+processorCount+" worker threads ...");
		//for a given pair, (x,y) add it to one of the four pixel threads running
		double zx, zy, cX, cY;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int xyVal = x * LENGTH + y;
				int threadIndex = xyVal % processorCount;
				pixelThreads[threadIndex].addIndex(x,y);
			}
			//repaint();
		}
		for(int index=0;index<(processorCount);index++){
		pixelThreads[index].setInputOver();
		}
		while(PixelThread.threadCount != 1){
		}
		
		//Calling paint() method only once at end
		
		repaint();
	}

	public void initColors() {
		for (int index = 0; index < MAX; index++) {
			colors[index] = Color.HSBtoRGB(index / 256f, 1, index
					/ (index + 8f));
		}
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(theImage, 0, 0, this);
	}

	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		System.out.println("Started drawing MandelBrot set ... Please wait");
		Mandelbrot_manager aMandelbrot = new Mandelbrot_manager();
		aMandelbrot.setVisible(true);
		
		// Started Thread manager
		PixelThread threadManager=new PixelThread(0,0,aMandelbrot,"M");
		threadManager.start();
		
		// Waiting for Thread manager to die
		try {
			threadManager.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted,Try again!!!");
			System.exit(0);
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("MandelBrot set is Ready !!!");
		System.out.println("\nTotalTime taken: "+(totalTime/1000)+"seconds");	
		
	}
}