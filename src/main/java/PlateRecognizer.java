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
		String sessionID = args[0];
		Alpr alpr = new Alpr("us", "openalpr.conf", "runtime_data");
		System.out.println("Start a new process!");
		// Set top N candidates returned to 20
		//alpr.setTopN(20);
		long SessionBegin = System.currentTimeMillis();
		while(true) {
			try {
				long begin = System.currentTimeMillis();
				alpr.recognize("receive_1.jpg");
				long end = System.currentTimeMillis();
				double currentRate = 50370.0/(end-begin);
				updateInfo(currentRate, sessionID);
				/*
				if(end - SessionBegin >= 800 * 1000) {
					System.out.println("Finish this session!");
					break;
				}
				*/
			} catch(Exception e) {
				System.out.println("Something wrong!");
				continue;
			}
		}
	}
	private static void updateInfo(double rate, String sessionID) {
		ManagedChannel mChannel;
		mChannel = ManagedChannelBuilder.forAddress("172.28.136.3", 50050).usePlaintext(true).build();
		OffloadingGrpc.OffloadingBlockingStub stub = OffloadingGrpc.newBlockingStub(mChannel);
		String hostIP = System.getenv("HOSTIP");
		OffloadingRequest message = OffloadingRequest.newBuilder().setMessage(hostIP + ":" + sessionID + ":" + "plate" + ":" + Double.toString(rate)).build();
		OffloadingReply reply = stub.startService(message);
	}
}
