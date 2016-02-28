
public class candyWrappingPaperProducer extends Thread {
MyCustomQueue<String> wrapperPaperQueue;
int MAX_PAPER=300;
int paperInSheet=3;
int paperCount=0;

public candyWrappingPaperProducer(MyCustomQueue<String> wrapperPaperQueue,int MAX_PAPER) {
	this.wrapperPaperQueue =wrapperPaperQueue;this.MAX_PAPER=MAX_PAPER;
}


public void run(){
	while(true){
		if(wrapperPaperQueue.isProcessDone){
			return;
		}
		
		String wrapperPaper[] = new String[paperInSheet];
		//Create a wrapper sheet of 3 wrappers
		for(int i=0;i<paperInSheet;i++){
			wrapperPaper[i]="wrapperPaper"+paperCount++;
		}
		//Add them to wrapper-paper queue
		synchronized (wrapperPaperQueue) {
			//If the Paper queue may over flow : then wait
			while(wrapperPaperQueue.getCount()+paperInSheet > MAX_PAPER){
				try {
					wrapperPaperQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(wrapperPaperQueue.isProcessDone){
					return;
				}
			}
			//Add the wrapper paper one by one
			for(int index=0;index<paperInSheet;index++){
				wrapperPaperQueue.add(wrapperPaper[index]);
			}
			wrapperPaperQueue.notifyAll();
		}
	}
}
}
