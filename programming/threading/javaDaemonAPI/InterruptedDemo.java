
// p11-11
// 2017/08/10
public class InterruptedDemo {
	public static void main(String[] args) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("�ڥ���� 99999�@��");
					Thread.sleep(99999);
				} catch (InterruptedException ex) {
					System.out.println("��... �ڳQinterrupt... ���F");
				}
			}
		};
		thread.start();
		thread.interrupt();	// �D������I�sthread��interrupt()
	}
}
