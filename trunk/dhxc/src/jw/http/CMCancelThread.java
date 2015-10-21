package jw.http;
import java.io.*;

public class CMCancelThread extends Thread
{
	java.net.HttpURLConnection conn = null;
	InputStream is;
	OutputStream os;
	
	public CMCancelThread(java.net.HttpURLConnection connect, InputStream in, OutputStream out)
	{
		conn = connect;
		is = in;
		os = out;
		
		this.start();
	}
	
	synchronized public void run() 
	{
		System.out.println("Begin cancel");
		try
		{
			if(os != null)
				os.close();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			if(is != null)
				is.close();
		}
		catch(Exception e)
		{
			
		}
		try
		{
			if(conn != null)
				conn.disconnect();
		}
		catch(Exception e)
		{
			
		}
		System.out.println("End cancel");
		conn = null;		
		is = null;
		os = null;
	}
}
