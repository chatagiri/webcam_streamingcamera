/* 
   Requires Webcam Capture API 
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

public class Viewer{

	static Webcam webcam = Webcam.getDefault();
	static JFrame sessionFrame = null;
	static JPanel sessionPanel = null;
	static DatagramSocket recvSocket = null;
	static byte[] buf = new byte[100000]; // ImagePacketBuffer
	static int recvPortValue = 0; //sending packet port
	static InetSocketAddress sockaddr = null;


	public static void main (String args[]) throws IOException {

			recvPortValue = Integer.parseInt(args[0]);
			webcam.setViewSize(new Dimension (640,480));		
			recvSocket = new DatagramSocket(recvPortValue);
			CreateFrame();
			ReceiveThread rt = new ReceiveThread();
			rt.start();	 // Receiving stream earlyer to get opponent IP address
	}


	 static void CreateFrame(){
		sessionFrame = new JFrame("講座研まで飛ばしてるよ");
		sessionFrame.setSize(640,960);

	}

	
	public static class ReceiveThread extends Thread {

		public void run(){

			webcam.open();
			JLabel label = new JLabel();
			JPanel sessionPanel = new JPanel();
			sessionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			sessionPanel.add(new WebcamPanel(webcam));

			try{
				Thread.sleep(1000); //Wait for awaking Webcam
			}catch(InterruptedException e){
				System.out.println("InterruptedException occurred in ReceiveThread()");
			}
			
			sessionFrame.add(sessionPanel);
			sessionFrame.setVisible(true);	

			try{	
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				System.out.println("waiting for input");

				for(;;){

					recvSocket.receive(packet);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf);
					BufferedImage img = ImageIO.read(bais);		
					label.setIcon(new ImageIcon(img));
				}	
			}catch(Exception e){
				System.out.println("IOException accoured in ReceiveThread()");
			}finally{
				recvSocket.close();
			}
		}
	}		
}

