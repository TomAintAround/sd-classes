class NewThread implements Runnable {
	String name;
	Thread t;

	NewThread(String threadname) {
		name = threadname;
		t = new Thread(this, name);
		System.out.println("New thread: " + t);
		t.start(); // Start the thread
	}

	@Override
	public void run() { // entry point
		try {
			for (int i = 5; i > 0; i--) {
				System.out.println(name + ": " + i);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println(name + "Interrupted");
		}
		System.out.println(name + " exiting.");
	}
}

class MultiThreadDemo {
	public static void main(String args[]) {
		NewThread t1 = new NewThread("Sporting"); // create threads
		NewThread t2 = new NewThread("Benfica");
		NewThread t3 = new NewThread("Porto");

		try {
			t1.t.join();
			t2.t.join();
			t3.t.join();
		} catch (InterruptedException e) {
			System.out.println("Main thread Interrupted");
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Main thread Interrupted");
		}
		System.out.println("Main thread exiting...");
	}
}
