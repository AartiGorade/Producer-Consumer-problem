
public class Consumer extends Thread{
	MyCustomQueue<String> candyQueue;
	MyCustomQueue<String> wrapperQueue;
	MyCustomQueue<Box> boxQueue;
	//30 boxes can be stored
	int boxStorage;
	int boxsCreated=0;
	
	Consumer(MyCustomQueue<String> candyQueue, MyCustomQueue<String> wrapperQueue, MyCustomQueue<Box> boxqueue,int boxStorage){
		this.candyQueue=candyQueue; this.wrapperQueue=wrapperQueue; this.boxQueue= boxqueue;this.boxStorage=boxStorage;
	}
	
	
	public void run(){
		while(boxsCreated < boxStorage){
		//Create wrapped candy array of storage capacity of the box
		//Add wrapped candies to the box
		//Print the box
		//Create 'n' such boxes
		String[] wrappedCandies= new String[Box.storageCapacity];
		for(int i=0;i<Box.storageCapacity;i++){
			String wrapper, candy;
			
			//Get the candy from candy queue
			synchronized (candyQueue) {
				while(candyQueue.getCount() == 0){
					System.out.println("waiting for candies!");
					try {
						candyQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				candy = candyQueue.remove();
				candyQueue.notifyAll();
			}
			
			//Get Wrapper from wrapper queue
			synchronized (wrapperQueue) {
				while(wrapperQueue.getCount() == 0){
					System.out.println("Waiting for wrappers");
					try {
						wrapperQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				wrapper=wrapperQueue.remove();	
				wrapperQueue.notifyAll();
			}
			
			wrappedCandies[i]=wrapper+"_ wrapped candy _ "+ candy;
		}
		Box currentbox;
		synchronized (boxQueue) {
			while(boxQueue.getCount()==0){
				System.out.println("Waiting for boxes");
				try {
					boxQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			currentbox=boxQueue.remove();
			boxQueue.notifyAll();
		}
		
		/*
		 *Display the box and the candies stored in it 
		 */
		System.out.println(currentbox.getName());
		for(int i=0;i<Box.storageCapacity;i++){
			currentbox.wrappedCandy[i]=wrappedCandies[i];
			System.out.println("\t"+wrappedCandies[i]);
		}
		System.out.println("\n");
		boxsCreated++;
		}
		synchronized (wrapperQueue) {
			wrapperQueue.setProcessDone();
			wrapperQueue.notifyAll();
		}
		synchronized (candyQueue) {
			candyQueue.setProcessDone();
			candyQueue.notifyAll();
		}
		synchronized (boxQueue) {
			boxQueue.setProcessDone();
			boxQueue.notifyAll();
		}
		
		System.out.println("\nCandy packing done!! :)");
		System.out.println("Total wrapped cadies packed boxes = "+ boxsCreated);
		System.out.println("\nUnused items available :");
		System.out.println("wrapping paper = "+wrapperQueue.getCount());
		System.out.println("cadies = "+candyQueue.getCount());
		System.out.println("Empty boxes = "+ boxQueue.getCount());
		
		
	}
	
	public static void main(String[] args){
		MyCustomQueue<String> candyqueue = new MyCustomQueue<String>();
		MyCustomQueue<String> wrapperqueue = new MyCustomQueue<String>();
		MyCustomQueue<Box> boxqueue = new MyCustomQueue<Box>();
		
		candyProducer candymachine = new candyProducer(candyqueue,100);
		candymachine.start();
		candyWrappingPaperProducer papermachine = new candyWrappingPaperProducer(wrapperqueue,5);
		papermachine.start();
		
		BoxProducer boxMaker = new BoxProducer(boxqueue,20);
		boxMaker.start();
		Consumer packingAgent = new Consumer(candyqueue,wrapperqueue,boxqueue,10);
		packingAgent.start();
	}
}
