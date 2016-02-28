class Box {
	String boxname;
	String[] wrappedCandy;
	int candyCount = 0;
	static int storageCapacity = 4;

	Box(String boxName) {
		wrappedCandy = new String[storageCapacity];
		for (int i = 0; i < storageCapacity; i++) {
			wrappedCandy[i] = "";
			this.boxname=boxName;
		}
	}
	public String getName(){
		return boxname;
	}
	public void addCandies(String candy) {
		wrappedCandy[candyCount++] = candy;
	}
}

public class BoxProducer extends Thread {
	MyCustomQueue<Box> boxQueue;
	int maxBoxes;
	int count = 0;

	public BoxProducer(MyCustomQueue<Box> boxArray,int maxBoxes) {
		this.boxQueue = boxArray;this.maxBoxes=maxBoxes;
	}

	public void run() {
		while (true) {
			if(boxQueue.isProcessDone){
				return;
			}
			
			Box mycandyBox = new Box("Box" + count++);
			// Check for number of candies and if it's greater than MAX add
			// candy to the queue
			synchronized (boxQueue) {
				//
				while (boxQueue.getCount() >= maxBoxes) {
					//System.out.println("Box Storage Full : waiting");
					try {
						boxQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(boxQueue.isProcessDone()){
						return;
					}
				}
				// Add box to the box-queue
				boxQueue.add(mycandyBox);
				boxQueue.notifyAll();
			}
		}
	}
}
