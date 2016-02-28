

public class TwoSyncMethods extends Thread{

	public synchronized void run(){
		System.out.println("RS");
		try {
			sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("RE");
	}
	
	public synchronized void someMethod(){
		System.out.println("SM");
		try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("SE");
	}
	
	public static void main(String[] args){
		TwoSyncMethods t1 = new TwoSyncMethods();
		t1.start();
		//System.out.println("Another Invokation");
		t1.someMethod();
	}
}
