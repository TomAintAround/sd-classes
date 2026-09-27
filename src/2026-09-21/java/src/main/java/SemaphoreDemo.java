class Semaphore {
	private int value;

	public Semaphore(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Semaphore value cannot be negative");
		}
		this.value = value;
	}

	public final synchronized void doWait() throws InterruptedException {
		while (value <= 0) {
			wait();
		}

		value--;
	}

	public final synchronized void doSignal() {
		value++;
		notify();
	}
}

class SharedValue {
	private int n = 0;
	private Semaphore semaphore = new Semaphore(1); // 1 for mutex

	public int get() throws InterruptedException {
		semaphore.doWait();
		try {
			System.out.println("Got: " + n);
			return n;
		} finally {
			semaphore.doSignal();
		}
	}

	public void set(int n) throws InterruptedException {
		semaphore.doWait();
		try {
			this.n = n;
			System.out.println("Wrote: " + n);
		} finally {
			semaphore.doSignal();
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

public class SemaphoreDemo {
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
