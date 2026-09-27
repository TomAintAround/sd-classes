import threading
from typing import override


class Semaphore:
    def __init__(self, value):
        if value < 0:
            raise ValueError("Semaphore value cannot be negative")

        self.value = value
        self.condition = threading.Condition()

    def doWait(self):
        with self.condition:
            while self.value <= 0:
                self.condition.wait()

            self.value -= 1

    def doSignal(self):
        with self.condition:
            self.value += 1
            self.condition.notify()


class SharedValue:
    def __init__(self):
        self.semaphore = Semaphore(1)  # 1 for mutex
        self.n = 0

    def get(self):
        self.semaphore.doWait()
        try:
            print(f"Got: {self.n}")
            return self.n
        finally:
            self.semaphore.doSignal()

    def set(self, n):
        self.semaphore.doWait()
        try:
            self.n = n
            print(f"Wrote: {n}")
        finally:
            self.semaphore.doSignal()


class Reader(threading.Thread):
    def __init__(self, sharedValue):
        super().__init__(name="Reader")
        self.sharedValue = sharedValue
        print("Started reader")

    @override
    def run(self):
        for _ in range(100):
            try:
                self.sharedValue.get()
            except Exception:
                print("Reader was interrupted")
                break

        print("Reader finished")


class Writer(threading.Thread):
    def __init__(self, sharedValue):
        super().__init__(name="Writer")
        self.sharedValue = sharedValue
        print("Started writer")

    @override
    def run(self):
        for i in range(100):
            try:
                self.sharedValue.set(i)
            except Exception:
                print("Writer was interrupted")
                break

        print("Writer finished")


class SemaphoreDemo:
    @staticmethod
    def main():
        sharedValue = SharedValue()
        reader = Reader(sharedValue)
        writer = Writer(sharedValue)
        reader.start()
        writer.start()

        try:
            reader.join()
            writer.join()
        except Exception:
            print("Main thread interrupted")

        print("Main thread finished")


if __name__ == "__main__":
    SemaphoreDemo.main()
