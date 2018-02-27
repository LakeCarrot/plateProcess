/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package plateprocess;
import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprPlate;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.lang.Thread;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import edgeOffloading.OffloadingGrpc;
import edgeOffloading.OffloadingOuterClass.OffloadingRequest;
import edgeOffloading.OffloadingOuterClass.OffloadingReply;

public class PlateRecognizer {
	static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	public static void main(String[] args) {
		Alpr alpr = new Alpr("us", "openalpr.conf", "runtime_data");
		System.out.println("Start a new process!");
		// Set top N candidates returned to 20
		//alpr.setTopN(20);
		while(true) {
			try {
				long begin = System.currentTimeMillis();
				alpr.recognize("receive_1.jpg");
				long end = System.currentTimeMillis();
				double currentRate = 50370.0/(end-begin);
				updateInfo(currentRate);
			} catch(Exception e) {
				System.out.println("Something wrong!");
				continue;
			}
		}
	}
	private static void updateInfo(double rate) {
		ManagedChannel mChannel;
		mChannel = ManagedChannelBuilder.forAddress("172.28.142.176", 50050).usePlaintext(true).build();
		OffloadingGrpc.OffloadingBlockingStub stub = OffloadingGrpc.newBlockingStub(mChannel);
		String hostIP = System.getenv("HOSTIP");
		OffloadingRequest message = OffloadingRequest.newBuilder().setMessage(hostIP + ":" + "plate" + ":" + Double.toString(rate)).build();
		OffloadingReply reply = stub.startService(message);
	}
}
