/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package platerecognition;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;
import platerecognition.PlateRecognitionGrpc;
import platerecognition.Platerecognition.PlateRecognitionRequest;
import platerecognition.Platerecognition.PlateRecognitionReply;
import platerecognizer.PlateRecognizer;

public class PlateServer {
  private static final Logger logger = Logger.getLogger(PlateServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50052;
    server = ServerBuilder.forPort(port)
        .addService(new PlateRecognitionImpl())
        .build()
        .start();
    logger.info("Plate Recognition Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        PlateServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final PlateServer server = new PlateServer();
    server.start();
    server.blockUntilShutdown();
  }

	static class PlateRecognitionImpl extends PlateRecognitionGrpc.PlateRecognitionImplBase {

		@Override 
	  public void offloading(PlateRecognitionRequest req, StreamObserver<PlateRecognitionReply> responseObserver) {
			PlateRecognitionReply reply = PlateRecognitionReply.newBuilder()
				.setMessage("You shall not pass!")
				.build();
			System.out.println("Plate? Plate!");
			PlateRecognizer plate = new PlateRecognizer();
			System.out.println("Debug: Successfully new a recognizer");
			plate.recognize();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}
	}
}