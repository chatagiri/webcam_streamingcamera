/* Requires Webcam Capture API 
   URL(zip)  https://github.com/sarxos/webcam-capture/releases/download/webcam-capture-parent-0.3.10/webcam-capture-0.3.10-dist.zip
 */

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.net.*;
import javax.swing.*;
import javax.imageio.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;

public class Sender{

	static byte[] buf = new byte[100000]; // ImagePacketBuffer
	static DatagramSocket sendSocket = null;
	static InetSocketAddress sockaddr = null;
	static int sendSocketValue = 12776; //sending packet port
	static Webcam webcam = Webcam.getDefault();

	public static void main (String args[]) throws IOException {

		String webcamname = webcam.getName();
		System.out.println(webcamname);

		webcam.setViewSize(new Dimension (640,480));

		String opponentIp = args[0];
		int opponentPortNum = Integer.parseInt(args[1]);
			sendSocket = new DatagramSocket(sendSocketValue);
			sockaddr = new InetSocketAddress(opponentIp,opponentPortNum);
			SendThread st = new SendThread();
			st.start();	 
		}

	public static class SendThread extends Thread{
		public void run(){
			try{

				try{
					webcam.open();
					Thread.sleep(5000); // Wait for awaking Webcam
				}catch(InterruptedException e){
					System.out.println("InterruptedException occurred in ReceiveThread()");
				}

				while(true){
					/* image->Byte */
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					System.out.println("sending");
					ImageIO.write(webcam.getImage(), "jpg", baos);
					byte[] buf = baos.toByteArray();
					/* packetize&send */
					DatagramPacket packet = new DatagramPacket(buf, buf.length, sockaddr);
					sendSocket.send(packet);
						
				}
			}catch(IOException e){
				System.out.println("IOException accoured in SendThread()");
			}finally{
				sendSocket.close();
			}
		}
	}		
}

