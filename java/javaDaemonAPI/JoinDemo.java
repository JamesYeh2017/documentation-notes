
import static java.lang.System.out;
// p11-12
// 2017/08/10
//
public class JoinDemo {
	public static void main(String[] args) 
		throws InterruptedException {
		System.out.println("Thread Main �}�l");
		
		Thread threadB = new Thread(() -> {
			System.out.println("Thread B �}�l");
			for (int i = 0; i < 5; i++) {
				System.out.println("Thread B ��������� " + Integer.toString(i) + " ��");
			}
			System.out.println("Thread B ����...");
		});
		
		threadB.start();
		threadB.join(); //Thread B �[�JMain thread�y�{
		
		System.out.println("Main thread�N����");
	}
}
