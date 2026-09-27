import java.util.concurrent.Semaphore;

class SharedValue {
	private int n = 0;
	private Semaphore semaphore = new Semaphore(1); // 1 for mutex

	public int get() throws InterruptedException {
		semaphore.acquire();
		try {
			System.out.println("Got: " + n);
			return n;
		} finally {
			semaphore.release();
		}
	}

	public void set(int n) throws InterruptedException {
		semaphore.acquire();
		try {
			this.n = n;
			System.out.println("Wrote: " + n);
		} finally {
			semaphore.release();
		}
	}
}

class Reader extends Thread {
	private SharedValue sharedValue;

	public Reader(SharedValue sharedValue) {
		super("Reader");
		this.sharedValue = sharedValue;
		System.out.println("Started reader");
	}

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			try {
				sharedValue.get();
			} catch (InterruptedException e) {
				System.out.println("Reader was interrupted");
				Thread.currentThread().interrupt();
				break;
			}
		}

		System.out.println("Reader finished");
	}
}

class Writer extends Thread {
	private SharedValue sharedValue;

	public Writer(SharedValue sharedValue) {
		super("Writer");
		this.sharedValue = sharedValue;
		System.out.println("Started writer");
	}

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			try {
				sharedValue.set(i);
			} catch (InterruptedException e) {
				System.out.println("Writer was interrupted");
				Thread.currentThread().interrupt();
				break;
			}
		}

		System.out.println("Writer finished");
	}
}

public class SemaphoreDemoReal {
	public static void main(String[] args) {
		SharedValue sharedValue = new SharedValue();
		Reader reader = new Reader(sharedValue);
		Writer writer = new Writer(sharedValue);
		reader.start();
		writer.start();

		try {
			reader.join();
			writer.join();
		} catch (InterruptedException e) {
			System.out.println("Main thread interrupted");
			Thread.currentThread().interrupt();
		}
		System.out.println("Main thread finished");
	}
}
